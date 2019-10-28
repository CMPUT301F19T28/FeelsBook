package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.cmput.feelsbook.post.Mood;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class addMoodFragment extends DialogFragment{

    private EditText inputEditText;
    private OnFragmentInteractionListener listener;
    private Mood mood;

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


        /**
         *   Retrieves the key "ride" from bundle and sets to edit text that pops up in the fragment
         */

        final Bundle bundle = getArguments();
        if (bundle != null){
            mood = (Mood) bundle.getSerializable("Mood");
            inputEditText.setText(mood.getMoodType());
        }

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

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // check if Ride is null for edit or add
                        if (bundle != null) {
                            // Need to display Ride and distance name in fragment
                            mood.setMoodType(addMoodFragment.this.inputEditText.getText().toString());

                        } else {
                            String moodType = addMoodFragment.this.inputEditText.getText().toString();

                            listener.onOkPressed(new Mood(moodType));// add reason, situation, photo, location
                        }
                    }}).create();

    }

    /**
     * New instance method
     * @param mood
     * @return fragment
     */
    static addMoodFragment newInstance(Mood mood){
        Bundle args = new Bundle(0);
        args.putSerializable("Mood", Mood);
        addMoodFragment fragment = new addMoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

}