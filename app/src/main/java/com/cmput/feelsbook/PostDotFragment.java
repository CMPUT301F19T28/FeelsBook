package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;

public class PostDotFragment extends DialogFragment {

    private Button editPost;
    private Button deletePost;
    private AddMoodFragment.OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener{
        void edited();
        void deleted(Mood delete);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof AddMoodFragment.OnFragmentInteractionListener){
            listener = (AddMoodFragment.OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_dot_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        editPost.findViewById(R.id.edit_button);
        deletePost.findViewById(R.id.delete_button);

        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AddMoodFragment().show(getFragmentManager(),"Edit selected");
                Toast.makeText(getContext(), "Edit Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Delete selected",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return builder
                .setView(view)
//                .setTitle("A")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", null )
                .create();
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
            // This is for the spinner if the top header is selected "Choose a mood" then no mood will be posted , not implemented yet
//                            if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Choose a mood")){
//                                Toast.makeText(getActivity(),spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                            }


//                        String moodText = input.getText().toString();
//                        Object selectedMood = spinner.getSelectedItem();
//                        MoodType selected_type = MoodType.class.cast(selectedMood);
//
//                        if(!moodText.isEmpty()){
//                            listener.onSubmit(new Mood(selected_type, null).withReason(moodText));
//                        }else{
//                            Toast.makeText(getContext(), "Must fill required text",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }

//                   })


    }
}
