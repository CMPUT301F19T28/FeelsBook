package com.cmput.feelsbook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Handles search function where a user can search for another user to send a follow request.
 */
public class SearchFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.test_search, null);
        EditText search = view.findViewById(R.id.search);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final User user = (User) getArguments().getSerializable("user");
        return builder.setView(view)
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.sendFollowRequest(getContext(),search.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    public static SearchFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        SearchFragment frag = new SearchFragment();
        frag.setArguments(args);
        return frag;

    }
}
