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
import androidx.fragment.app.Fragment;

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

    private ToggleButton filterHappy;
    private ToggleButton filterAngry;
    private ToggleButton filterSad;
    private ToggleButton filterAnnoyed;
    private ToggleButton filterSleepy;
    private ToggleButton filterSexy;

    public interface OnMoodSelectListener{
        void onSelect(MoodType moodType);
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
        prefs = getActivity().getSharedPreferences("filterkey", Context.MODE_PRIVATE);
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

        filterHappy = view.findViewById(R.id.happy_mood);
        filterHappy.setOnCheckedChangeListener((compoundButton, b) -> {
                if(b)
                    filterHappy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.yellow));
                else
                    filterHappy.setBackgroundColor(Color.parseColor("#F2F2F2"));
                listener.onSelect(MoodType.HAPPY);
        });

        filterAngry = view.findViewById(R.id.angry_mood);
        filterAngry.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                filterAngry.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));
            else
                filterAngry.setBackgroundColor(Color.parseColor("#F2F2F2"));
            listener.onSelect(MoodType.ANGRY);
        });

        filterSad = view.findViewById(R.id.sad_mood);
        filterSad.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                filterSad.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.blue));
            else
                filterSad.setBackgroundColor(Color.parseColor("#F2F2F2"));
            listener.onSelect(MoodType.SAD);
        });

        filterSleepy = view.findViewById(R.id.sleepy_mood);
        filterSleepy.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                filterSleepy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.purple));
            else
                filterSleepy.setBackgroundColor(Color.parseColor("#F2F2F2"));
            listener.onSelect(MoodType.SLEEPY);
        });

        filterAnnoyed = view.findViewById(R.id.annoyed_mood);
        filterAnnoyed.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                filterAnnoyed.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.orange));
            else
                filterAnnoyed.setBackgroundColor(Color.parseColor("#F2F2F2"));
            listener.onSelect(MoodType.ANNOYED);
        });

        filterSexy = view.findViewById(R.id.sexy_mood);
        filterSexy.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                filterSexy.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.purple));
            else
                filterSexy.setBackgroundColor(Color.parseColor("#F2F2F2"));
            listener.onSelect(MoodType.SEXY);
        });

        filterHappy.setChecked(prefs.getBoolean("happy", false));
        filterAngry.setChecked(prefs.getBoolean("angry", false));
        filterAnnoyed.setChecked(prefs.getBoolean("annoyed", false));
        filterSad.setChecked(prefs.getBoolean("sad", false));
        filterSexy.setChecked(prefs.getBoolean("sexy", false));
        filterSleepy.setChecked(prefs.getBoolean("sleepy", false));


        filterTitle.setText("Filter by mood: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.getContext().getTheme().applyStyle(R.style.FilterDialog, true);
        builder.setView(view);

        AlertDialog filterWindow = builder.create();
        filterWindow.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        filterWindow.show();

        return filterWindow;
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.edit().putBoolean("happy", filterHappy.isChecked()).apply();
        prefs.edit().putBoolean("angry", filterAngry.isChecked()).apply();
        prefs.edit().putBoolean("annoyed", filterAnnoyed.isChecked()).apply();
        prefs.edit().putBoolean("sad", filterSad.isChecked()).apply();
        prefs.edit().putBoolean("sexy", filterSexy.isChecked()).apply();
        prefs.edit().putBoolean("sleepy", filterSleepy.isChecked()).apply();
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

    public void reset() {
        prefs.edit().putBoolean("happy", false).apply();
        prefs.edit().putBoolean("angry", false).apply();
        prefs.edit().putBoolean("annoyed", false).apply();
        prefs.edit().putBoolean("sad", false).apply();
        prefs.edit().putBoolean("sexy", false).apply();
        prefs.edit().putBoolean("sleepy", false).apply();
    }
}
