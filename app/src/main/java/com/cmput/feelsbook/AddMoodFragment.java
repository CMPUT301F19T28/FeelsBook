package com.cmput.feelsbook;
import com.cmput.feelsbook.post.MoodType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cmput.feelsbook.post.Mood;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment for adding or editing a Mood
 */
public class AddMoodFragment extends DialogFragment {

    private EditText input;
    private Bitmap dp;
    private OnFragmentInteractionListener listener;
    private Bitmap picture;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public interface OnFragmentInteractionListener {
        void onSubmit(Mood newMood);

        void edited();

        void deleted(Mood delete);
    }

    /**
     * Attaches to context and ensures it implements OnFragmentInteractionListener
     * else it throws an exception
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Returns a new instance of the fragment and passes a mood to be edited
     * Used for when a Mood needs to be edited
     *
     * @param mood object that will be edited
     * @return a fragment object
     */
    public static AddMoodFragment newInstance(Mood mood) {
        Bundle args = new Bundle();
        args.putSerializable("mood", mood);
        AddMoodFragment fragment = new AddMoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Builds a dialog when the fragment is called
     *
     * @param savedInstanceState
     * @return returns a dialog where a mood can be created or edited and passed back to the homepage
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_post, null);

        /**
         * spinner currently displays text for moodtype, want to display emoji
         */
        input = view.findViewById(R.id.editText);
        Spinner spinner = view.findViewById(R.id.mood_spinner);
        MoodType moodTypes[] = {MoodType.HAPPY, MoodType.SAD, MoodType.ANGRY, MoodType.ANNOYED, MoodType.SLEEPY, MoodType.SEXY};
        ArrayList<MoodType> moodList = new ArrayList<MoodType>();
        moodList.addAll(Arrays.asList(moodTypes));
        ArrayAdapter<MoodType> moodTypeAdapter = new ArrayAdapter<MoodType>(getActivity(), android.R.layout.simple_spinner_item, moodList);
        moodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(moodTypeAdapter);
        /**
         * button for opening camera to take picture
         * photo stored as "photo"
         */

        Button cameraButton = view.findViewById(R.id.add_picture_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) !=  null)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            //For editing mood
            final Mood editMood = (Mood) getArguments().getSerializable("mood");
            input.setText(editMood.getReason());
            return builder
                    .setView(view)
                    .setTitle("Edit Post")
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            listener.deleted(editMood);
                        }
                    })
                    .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String newMoodReason = input.getText().toString();
                            if (!newMoodReason.isEmpty()) {
                                listener.edited();
                            } else {
                                Toast.makeText(getContext(), "Must fill required text",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).create();
        } catch (Exception e) {
            return builder
                    .setView(view)
                    .setTitle("Add Post")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String moodText = input.getText().toString();
                            Object selectedMood = spinner.getSelectedItem();
                            MoodType selected_type = MoodType.class.cast(selectedMood);


                            if (!moodText.isEmpty()) {
                                Mood newMood = new Mood(selected_type, null).withReason(moodText).withPhoto(picture);
                                listener.onSubmit(newMood);

                            } else {
                                Toast.makeText(getContext(), "Must fill required text",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).create();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                picture = (Bitmap) data.getExtras().get("data");
            }
        }

    }
}