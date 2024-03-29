package com.cmput.feelsbook;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput.feelsbook.post.MoodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adapter used in the dropdown mood spinner selector when adding or editing a mood event
 */

public class MoodTypeAdapter extends ArrayAdapter<MoodType> {

    private List<MoodType> list;

    public MoodTypeAdapter(@NonNull Context context, List<MoodType> list) {
        super(context, android.R.layout.simple_spinner_item, list);
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView listitem = (TextView) convertView;
        if(listitem == null)
            listitem = new TextView(parent.getContext());
        listitem.setText(list.get(position).getEmoticon());
        listitem.setTextSize(32);
        return listitem;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView listitem = (TextView) convertView;
        if(listitem == null)
            listitem = new TextView(parent.getContext());
        listitem.setText(list.get(position).getEmoticon());
        listitem.setTextSize(32);
        return listitem;
    }
}
