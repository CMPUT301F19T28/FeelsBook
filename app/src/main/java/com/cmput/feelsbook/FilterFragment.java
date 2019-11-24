package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private int posX = 30;
    private int posY = 30;

    private OnMoodSelectListener listener;

    private static boolean happyPressed = false;
    private static boolean sadPressed = false;
    private static boolean angryPressed = false;
    private static boolean sleepyPressed = false;
    private static boolean annoyedPressed = false;
    private static boolean sexyPressed = false;

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
        filterHappy.setChecked(happyPressed);
        filterHappy.setBackgroundColor(happyPressed ? ContextCompat.getColor(getContext(),R.color.yellow)
                : Color.parseColor("#F2F2F2"));
        filterHappy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterHappy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.yellow));
                    happyPressed = true;
                    listener.onSelect(MoodType.HAPPY);
                }
                else {
                    filterHappy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    happyPressed = false;
                    listener.onDeselect();
                }
            }
        });

        ToggleButton filterSad     = view.findViewById(R.id.sad_mood);
        filterSad.setChecked(sadPressed);
        filterSad.setBackgroundColor(sadPressed ? ContextCompat.getColor(getContext(),R.color.blue)
                : Color.parseColor("#F2F2F2"));
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
        filterAngry.setChecked(angryPressed);
        filterAngry.setBackgroundColor(angryPressed ? ContextCompat.getColor(getContext(),R.color.red)
                : Color.parseColor("#F2F2F2"));
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
        filterSleepy.setChecked(sleepyPressed);
        filterSleepy.setBackgroundColor(sleepyPressed ? ContextCompat.getColor(getContext(),R.color.purple)
                : Color.parseColor("#F2F2F2"));
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
        filterAnnoyed.setChecked(annoyedPressed);
        filterAnnoyed.setBackgroundColor(annoyedPressed ? ContextCompat.getColor(getContext(),R.color.orange)
                : Color.parseColor("#F2F2F2"));
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
        filterSexy.setChecked(sexyPressed);
        filterSexy.setBackgroundColor(sexyPressed ? ContextCompat.getColor(getContext(),R.color.pink)
                : Color.parseColor("#F2F2F2"));
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

        AlertDialog filterWindow = builder.create();
        filterWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        filterWindow.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        filterWindow.getWindow().setLayout(width,height);
        WindowManager.LayoutParams wlp = filterWindow.getWindow().getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.LEFT;
        wlp.x = posX;
        wlp.y = posY;
        filterWindow.show();

        return filterWindow;
    }

}
