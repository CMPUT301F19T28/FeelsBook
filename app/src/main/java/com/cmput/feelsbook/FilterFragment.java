package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.cmput.feelsbook.post.MoodType;

public class FilterFragment extends DialogFragment {
    private int width = 300;
    private int height = 300;
    private int pos_x = 30;
    private int pos_y = 30;

    private OnMoodSelectListener listener;

    public interface OnMoodSelectListener{
        void onSelect(MoodType moodType);
        void onDeselect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoodSelectListener) {
            listener = (OnMoodSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnMoodSelectListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);
        TextView filterTitle = view.findViewById(R.id.mood_title);

        ToggleButton filterHappy   = view.findViewById(R.id.happy_mood);
        filterHappy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterHappy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.yellow));
                    listener.onSelect(MoodType.HAPPY);
                }
                else {
                    filterHappy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });
        ToggleButton filterSad     = view.findViewById(R.id.sad_mood);
        filterSad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSad.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.blue));
                    listener.onSelect(MoodType.SAD);
                }
                else {
                    filterSad.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });
        ToggleButton filterAngry   = view.findViewById(R.id.angry_mood);
        filterAngry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterAngry.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));
                    listener.onSelect(MoodType.ANGRY);
                }
                else {
                    filterAngry.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });
        ToggleButton filterSleepy  = view.findViewById(R.id.sleepy_mood);
        filterSleepy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSleepy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.purple));
                    listener.onSelect(MoodType.SLEEPY);
                }
                else {
                    filterSleepy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });
        ToggleButton filterAnnoyed = view.findViewById(R.id.annoyed_mood);
        filterAnnoyed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterAnnoyed.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.orange));
                    listener.onSelect(MoodType.ANNOYED);
                }
                else {
                    filterAnnoyed.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });
        ToggleButton filterSexy    = view.findViewById(R.id.sexy_mood);
        filterSexy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSexy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.pink));
                    listener.onSelect(MoodType.SEXY);
                }
                else {
                    filterSexy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    listener.onDeselect();
                }
            }
        });


        filterTitle.setText("Filter by mood: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        AlertDialog filter_window = builder.create();
        filter_window.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        filter_window.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        filter_window.getWindow().setLayout(width,height);
        WindowManager.LayoutParams wlp = filter_window.getWindow().getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.LEFT;
        wlp.x = pos_x;
        wlp.y = pos_y;
        filter_window.show();
        return filter_window;

    }


}
