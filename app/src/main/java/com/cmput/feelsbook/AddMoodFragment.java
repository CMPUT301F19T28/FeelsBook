package com.cmput.feelsbook;
import com.cmput.feelsbook.post.MoodType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import com.cmput.feelsbook.post.Mood;

/**
 * Fragment for adding or editing a Mood
 */
public class AddMoodFragment extends DialogFragment {

    private EditText input;
    private Bitmap dp;
    private OnFragmentInteractionListener listener;


    public interface OnFragmentInteractionListener{
        void onSubmit(Mood newMood);
        void edited();
        void deleted(Mood delete);
    }

    /**
     * Attaches to context and ensures it implements OnFragmentInteractionListener
     * else it throws an exception
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Returns a new instance of the fragment and passes a mood to be edited
     * Used for when a Mood needs to be edited
     * @param mood
     *      object that will be edited
     * @return
     *      a fragment object
     */
    public static AddMoodFragment newInstance(Mood mood){
        Bundle args = new Bundle();
        args.putSerializable("mood", mood);

        AddMoodFragment fragment = new AddMoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Builds a dialog when the fragment is called
     * @param savedInstanceState
     * @return
     *      returns a dialog where a mood can be created or edited and passed back to the homepage
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_post, null);
        input = view.findViewById(R.id.edit_text);
        //dp.setImage; //need to get profile pic

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try{
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
                            if(!newMoodReason.isEmpty()){
                                listener.edited();
                            }else{
                                Toast.makeText(getContext(), "Must fill required text",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).create();
        }catch(Exception e){
            return builder
                    .setView(view)
                    .setTitle("Add Post")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String moodText = input.getText().toString();

                            if(!moodText.isEmpty()){
                                listener.onSubmit(new Mood(MoodType.HAPPY, null).withReason(moodText));
                            }else{
                                Toast.makeText(getContext(), "Must fill required text",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).create();

        }
    }



}
