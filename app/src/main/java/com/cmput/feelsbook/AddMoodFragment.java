package com.cmput.feelsbook;
import com.cmput.feelsbook.post.MoodType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import com.cmput.feelsbook.post.Mood;


public class AddMoodFragment extends DialogFragment {

    private EditText input;
    private ImageButton dp;
    private OnFragmentInteractionListener listener;
//    public static final String INPUT = "com.cmput.feelsbook.AddMoodFragment.input";

    public interface OnFragmentInteractionListener{
        void onSubmit(Mood newMood);
        void edited();
        void deleted(Mood delete);
    }

    @Override
    // makes sure the activity the fragment attaches to implements OnFragmentInteractionListener
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    //when a new instance of the fragment is called passes a Mood into the fragment
    // for when a Mood is being edited
    public static AddMoodFragment newInstance(Mood mood){
        Bundle args = new Bundle();
        args.putSerializable("mood", mood);

        AddMoodFragment fragment = new AddMoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_post, null);
        input = view.findViewById(R.id.editText);
        dp = view.findViewById(R.id.profileImage); //need to get profile pic

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
