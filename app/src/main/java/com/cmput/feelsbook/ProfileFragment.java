package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    /**
     * TO-DO:
     * - convert to Activity class
     * - correctly display profile-relevant numbers (followers, following, posts...)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_screen, container, false);
    }
}