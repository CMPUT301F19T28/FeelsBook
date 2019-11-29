package com.cmput.feelsbook;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Creates a small dialog fragment that confirms that a user wishes to sign out or cancel
 * Brings user to login page upon ok requested and back to original activity upon cancel
 */

public class LogoutFragment extends DialogFragment {
    private OnLogoutListener listener;

    public interface OnLogoutListener{
        void onLogout();
    }

    /**
     * Checks to see if OnLogoutListener is implemented
     * in the activity/fragment LogoutFragment is used in
     * @param context - the activity/fragment context where LgoutFragment is called from
     * @throws RuntimeException - throws exception when OnLogoutListener is not
     * implemented.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilterFragment.OnMoodSelectListener) {
            listener = (LogoutFragment.OnLogoutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnLogoutListener");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.logout_message, null);

        build.setTitle("")
                .setView(view)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onLogout();
                    }
                });
        return build.create();
    }
}
