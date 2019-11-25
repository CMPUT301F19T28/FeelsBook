package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.cmput.feelsbook.post.MoodType;

/**
 * Creates a small window used to filter out moods from a feed list.
 * int width, height, posX, posY - used to change the dimensions
 * and position of the popup window.
 * OnMoodSelectListener listener - used to relay information about which
 * mood is to be filtered
 * static boolean (mood)Pressed - used to keep track of the ToggleButton objects'
 * last clicked state
 *
 */
public class FilterFragment extends DialogFragment {
    private int width = 500;
    private int height = 500;
    private int posX = 550;
    private int posY = 135;

    private OnMoodSelectListener listener;
    private SharedPreferences prefs;
    private static String KEY_START = "started";

    private boolean happyPressed = false;
    private boolean sadPressed = false;
    private boolean angryPressed = false;
    private boolean sleepyPressed = false;
    private boolean annoyedPressed = false;
    private boolean sexyPressed = false;

    public interface OnMoodSelectListener{
        void onSelect(MoodType moodType);
        void onDeselect(MoodType moodType);
    }

    /**
     * Checks to see if OnMoodSelectListener is implemented
     * in the activity/fragment FilterFragment is used in
     * @param context - the activity/fragment context where FilterFragment is called from
     * @throws RuntimeException - throws exception when OnMoodSelectListener is not
     * implemented.
     */
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

    /**
     * Creates an instance of the filter window.
     * @return Returns the filter window to be used.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);
        TextView filterTitle = view.findViewById(R.id.mood_title);

        prefs = getActivity().getSharedPreferences("filterKey", Context.MODE_PRIVATE);

        ToggleButton filterHappy   = view.findViewById(R.id.happy_mood);
        happyPressed = prefs.getBoolean("happy",false);
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
                    listener.onDeselect(MoodType.HAPPY);
                }
                prefs.edit().putBoolean("happy",happyPressed).apply();
            }
        });

        ToggleButton filterSad     = view.findViewById(R.id.sad_mood);
        sadPressed = prefs.getBoolean("sad",false);
        filterSad.setChecked(sadPressed);
        filterSad.setBackgroundColor(sadPressed ? ContextCompat.getColor(getContext(),R.color.blue)
                : Color.parseColor("#F2F2F2"));
        filterSad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSad.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.blue));
                    sadPressed = true;
                    listener.onSelect(MoodType.SAD);
                }
                else {
                    filterSad.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    sadPressed = false;
                    listener.onDeselect(MoodType.SAD);
                }
                prefs.edit().putBoolean("sad",sadPressed).apply();
            }
        });

        ToggleButton filterAngry   = view.findViewById(R.id.angry_mood);
        angryPressed = prefs.getBoolean("angry",false);
        filterAngry.setChecked(angryPressed);
        filterAngry.setBackgroundColor(angryPressed ? ContextCompat.getColor(getContext(),R.color.red)
                : Color.parseColor("#F2F2F2"));
        filterAngry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterAngry.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));
                    angryPressed = true;
                    listener.onSelect(MoodType.ANGRY);
                }
                else {
                    filterAngry.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    angryPressed = false;
                    listener.onDeselect(MoodType.ANGRY);
                }
                prefs.edit().putBoolean("angry",angryPressed).apply();
            }
        });

        ToggleButton filterSleepy  = view.findViewById(R.id.sleepy_mood);
        sleepyPressed = prefs.getBoolean("sleepy",false);
        filterSleepy.setChecked(sleepyPressed);
        filterSleepy.setBackgroundColor(sleepyPressed ? ContextCompat.getColor(getContext(),R.color.purple)
                : Color.parseColor("#F2F2F2"));
        filterSleepy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSleepy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.purple));
                    sleepyPressed = true;
                    listener.onSelect(MoodType.SLEEPY);
                }
                else {
                    filterSleepy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    sleepyPressed = false;
                    listener.onDeselect(MoodType.SLEEPY);
                }
                prefs.edit().putBoolean("sleepy",sleepyPressed).apply();
            }
        });

        ToggleButton filterAnnoyed = view.findViewById(R.id.annoyed_mood);
        annoyedPressed = prefs.getBoolean("annoyed",false);
        filterAnnoyed.setChecked(annoyedPressed);
        filterAnnoyed.setBackgroundColor(annoyedPressed ? ContextCompat.getColor(getContext(),R.color.orange)
                : Color.parseColor("#F2F2F2"));
        filterAnnoyed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterAnnoyed.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.orange));
                    annoyedPressed = true;
                    listener.onSelect(MoodType.ANNOYED);
                }
                else {
                    filterAnnoyed.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    annoyedPressed = false;
                    listener.onDeselect(MoodType.ANNOYED);
                }
                prefs.edit().putBoolean("annoyed",annoyedPressed).apply();
            }
        });

        ToggleButton filterSexy    = view.findViewById(R.id.sexy_mood);
        sexyPressed = prefs.getBoolean("sexy",false);
        filterSexy.setChecked(sexyPressed);
        filterSexy.setBackgroundColor(sexyPressed ? ContextCompat.getColor(getContext(),R.color.pink)
                : Color.parseColor("#F2F2F2"));
        filterSexy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    filterSexy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.pink));
                    sexyPressed = true;
                    listener.onSelect(MoodType.SEXY);
                }
                else {
                    filterSexy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    sexyPressed = false;
                    listener.onDeselect(MoodType.SEXY);
                }
                prefs.edit().putBoolean("sexy",sexyPressed).apply();
            }
        });


        filterTitle.setText("Filter by mood: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.getContext().getTheme().applyStyle(R.style.FilterDialog, true);
        builder.setView(view);

        AlertDialog filterWindow = builder.create();
        filterWindow.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        filterWindow.show();

        return filterWindow;
    }

    /**
     * Determines the filter window's size and location.
     */
    @Override
    public void onResume(){
        super.onResume();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.START;
        wlp.x = posX;
        wlp.y = posY;
        wlp.width = width;
        wlp.height = height;
        window.setAttributes(wlp);
    }

    public void resetFilterButtons(){
        prefs.edit().clear().apply();
        this.happyPressed = false;
        this.sadPressed = false;
        this.angryPressed = false;
        this.sleepyPressed = false;
        this.annoyedPressed = false;
        this.sexyPressed = false;
    }

    public void checkPreferences(){
        if(prefs.contains(KEY_START)){
            Log.d("Filter", "called resetPreferences");
            prefs.edit().clear().apply();
        }
    }
}
