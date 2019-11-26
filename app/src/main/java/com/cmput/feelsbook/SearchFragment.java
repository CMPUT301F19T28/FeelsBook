package com.cmput.feelsbook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Handles search function where a user can search for another user to send a follow request.
 */
public class SearchFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Client client = new Client("CZHQW4KJVA","394205d7e7f173719c08f3e187b2a77b");
        Index index = client.getIndex("users");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.test_search, null);
        EditText search = view.findViewById(R.id.search);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final User user = (User) getArguments().getSerializable("user");
        return builder.setView(view)
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index.searchAsync(new Query(search.getText().toString()), new CompletionHandler() {
                            @Override
                            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                                if(jsonObject != null) {
                                    try {
                                        JSONArray arr = jsonObject.getJSONArray("hits");
                                        for (int i = 0; i<arr.length(); i++) {
                                            FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(arr.getJSONObject(i).getString("username"))
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful() && task.getResult() != null) {
                                                                DocumentSnapshot doc = task.getResult();
                                                                FollowUser fu = new FollowUser(task.getResult().getId(), (String) doc.getData().get("name"), (Bitmap) doc.getData().get("profilePic"));
                                                            }
                                                        }
                                                    });
                                        }
                                    }catch (JSONException ex) {
                                            Log.d("Search", "JsonException");
                                    }
                                }
                            }
                        });
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
