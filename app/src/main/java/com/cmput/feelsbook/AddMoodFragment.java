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
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment for adding or editing a Mood
 */
public class AddMoodFragment extends DialogFragment {

    private EditText input;
    private OnFragmentInteractionListener listener;
    private Bitmap picture;
//    private Bitmap dp;

    private static final int REQUEST_IMAGE_CAPTURE = 1;


    public interface OnFragmentInteractionListener{
        void onSubmit(Post newMood);
        void edited();
        void deleted(Post delete);
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

//    public static AddMoodFragment newInstance(Mood mood) {

    public static AddMoodFragment newInstance(Post mood){
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
        Spinner socialSpinner = view.findViewById(R.id.social_spinner);

        MoodType moodTypes[] = {MoodType.HAPPY, MoodType.SAD,MoodType.ANGRY, MoodType.ANNOYED,MoodType.SLEEPY, MoodType.SEXY};
        ArrayList<MoodType> moodList = new ArrayList<MoodType>();
        moodList.addAll(Arrays.asList(moodTypes));
        ArrayAdapter<MoodType> moodTypeAdapter = new ArrayAdapter<MoodType>(getActivity(), android.R.layout.simple_spinner_item, moodList);
        moodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(moodTypeAdapter);

        //creates social situation spinner drop down menu
        SocialSituation socialSits[] = {SocialSituation.ALONE, SocialSituation.ONEPERSON, SocialSituation.SEVERAL, SocialSituation.CROWD };
        ArrayList<SocialSituation> socialSitList = new ArrayList<SocialSituation>();
        socialSitList.addAll(Arrays.asList(socialSits));
        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<SocialSituation>(getActivity(), android.R.layout.simple_spinner_item, socialSitList);
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSpinner.setAdapter(socialAdapter);
        socialSpinner.setVisibility(View.GONE); //sets the view to be gone because it is optional


        //if the social situatiion button is pressed then shows the drop down
        final Button socialBttn = view.findViewById(R.id.social_situation_button);
        socialBttn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socialSpinner.setVisibility(View.VISIBLE);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            //For editing mood
            final Mood editMood = (Mood) getArguments().getSerializable("mood");
            input.setText(editMood.getReason());
            for(int i = 0; i < moodTypes.length; i++){
                if(moodTypes[i] == editMood.getMoodType()){
                    spinner.setSelection(i);
                }
            }

            //checks to see if the editmood has a social situation
            // if makes dropdown visible and sets the social situation
            if(editMood.hasSituation()){
                for(int i = 0; i < socialSits.length; i++){
                    if(socialSits[i] == editMood.getSituation()){
                        socialSpinner.setVisibility(View.VISIBLE);
                        socialSpinner.setSelection(i);
                    }
                }
            }
            /**
             *  Camera button in case of edit mood, if there is a picture already attached to mood then
             *  user is unable to attach a new photo. If there is no picture attached to a previous post then
             *  used is able to attach a picture
             */

            Button cameraButtonEdit = view.findViewById(R.id.add_picture_button);
            cameraButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editMood.hasPhoto()) {
                        Toast.makeText(getContext(), "Can not change photo",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });
            /**
             * Builder for Edit and delete post
             */
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

                            MoodType newSelectedType = MoodType.class.cast(spinner.getSelectedItem());
                            SocialSituation selectedSocial = SocialSituation.class.cast(socialSpinner.getSelectedItem());

                            if(!newMoodReason.isEmpty()){

                                if(socialSpinner.getVisibility() == View.VISIBLE){
                                    editMood.setSituation(selectedSocial);
                                }

                                editMood.setReason(newMoodReason);
                                editMood.setMoodType(newSelectedType);
                                if (!editMood.hasPhoto()){
//                                    editMood.withPhoto(picture);
                                }
                                listener.edited();

                            }else{
                                Toast.makeText(getContext(), "Must fill required text",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).create();
        } catch (Exception e) {

            /**
             * Camera button in case of add mood, used is able to attach a picture to post/mood
             */
            Button cameraButtonAdd = view.findViewById(R.id.add_picture_button);
            cameraButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
            });
            /**
            * Builder for Add post
            */
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
//                                Mood newMood = new Mood(selected_type, null).withReason(moodText).withPhoto(picture);
//                                listener.onSubmit(newMood);

                            } else {

                                SocialSituation selectedSocial = SocialSituation.class.cast(socialSpinner.getSelectedItem());

                                if (!moodText.isEmpty()) {

                                    if (socialSpinner.getVisibility() == View.VISIBLE) {
                                        listener.onSubmit(new Mood(selected_type, null).withReason(moodText).withSituation(selectedSocial));
                                    } else {
                                        listener.onSubmit(new Mood(selected_type, null).withReason(moodText));
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Must fill required text",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).create();
        }
    }

    /**
     * Activity result for Camera activity, gets bitmap photo taken during activity and attaches to bitmap variable 'picture'
     * @param requestCode
     * @param resultCode
     * @param data
     */
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