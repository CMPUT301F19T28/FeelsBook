package com.cmput.feelsbook;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts list of following requests received by the user.
 * List<String> list - contains users that requested to follow the current user
 * User user - current user used to display information
 */
public class FollowingRequests extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> list;
    private User user;

    public FollowingRequests(User user) {
        list = new ArrayList<>();
        this.user = user;
        fillList();
    }

    /**
     * Populates list attribute with all users that made follow requests pulled from the
     * user's database
     */
    public void fillList() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUserName())
                .collection("info")
                .document("followingRequests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        list.addAll(task.getResult().getData().keySet());
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_request, parent, false);
        return new RecyclerView.ViewHolder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.user_name);
        textView.setText(list.get(position));

        Button button = holder.itemView.findViewById(R.id.follow_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put(list.get(position),"temp");
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUserName())
                        .collection("info")
                        .document("followers")
                        .set(data, SetOptions.merge());
                data = new HashMap<>();
                data.put(user.getUserName(),"temp");
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(list.get(position))
                        .collection("info")
                        .document("following")
                        .set(data, SetOptions.merge());
                data = new HashMap<>();
                data.put(list.get(position), FieldValue.delete());
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUserName())
                        .collection("info")
                        .document("followingRequests")
                        .update(data);
                list.remove(position);
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
