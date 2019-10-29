package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.SocialSituation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * addMoodFragment is a fragment that is displayed as an alert dialog
 * this fragment works with interface activity_add_post
 * current implementation is only adding a mood not editing
 * Need to add button functionality vs current builder setbutton
 * Need to display in feed in mainActivity
 */
public class addMoodFragment extends DialogFragment{

    private EditText inputEditText;
    private OnFragmentInteractionListener listener;
    private MoodType moodType;
    private String reason;
    private Bitmap photo = null;
    private Mood mood;
    private Button positiveButton, backButton ;

    public interface OnFragmentInteractionListener {
        void onOkPressed(Mood newMood);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_post, null);
        inputEditText = view.findViewById(R.id.moodEditText);
        positiveButton = view.findViewById(R.id.imageButton2);
        backButton = view.findViewById(R.id.backButton);


        /**
         *   Retrieves the key "mood" from bundle and sets to edit text that pops up in the fragment
         *   Do not need for current sprint so commented out
         */
//        final Bundle bundle = getArguments();
//        if (bundle != null){
//            mood = (Mood) bundle.getSerializable("Mood");
//            inputEditText.setText(mood.getMoodType());
//        }

        /**
         * Alert Dialog builder for fragment
         * currently anticipates an editMood function by incorporating serializable
         * for the current sprint it is unnecessary since we are only adding mood
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Mood")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    /**
                     * commented out portion will be implemented in editMood sprint
                     * no need for in statement with addMood functionality
                     * @param dialogInterface
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // check if Mood is null for edit or add
//                        if (bundle != null) {
//                            // mood needs (moodtype,profilepic)
//                            mood.withReason(addMoodFragment.this.inputEditText.getText().toString());
//
//                        } else {
                            String reason = addMoodFragment.this.inputEditText.getText().toString();

                            listener.onOkPressed(new Mood(moodType,photo).withReason(reason));// add reason, situation, photo, location
                        }
                    }).create();

    }

    /**
     * New instance method
     * @param mood
     * @return fragment
     * for pulling a preexisting mood from a bundle
     * Will implement when editing a mood
     */
//    static addMoodFragment newInstance(Mood mood){
//        Bundle args = new Bundle(0);
//        args.putSerializable("Mood", Mood);
//        addMoodFragment fragment = new addMoodFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

}