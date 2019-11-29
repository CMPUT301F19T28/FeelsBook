package com.cmput.feelsbook;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts list of following requests received by the user.
 * List<String> list - contains users that requested to follow the current user
 * User user - current user used to display information
 */
public class FollowingRequests extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FollowUser> list;
    private User user;
    FirebaseFirestore db;
    CollectionReference cr;

    public FollowingRequests(User user) {
        db = FirebaseFirestore.getInstance();
        cr = db.collection("users").document(user.getUserName()).collection("followRequests");
        list = new ArrayList<>();
        this.user = user;
        fillList();
    }

    /**
     * Populates list attribute with all users that made follow requests pulled from the
     * user's database
     */
    public void fillList() {
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        list.add(document.toObject(FollowUser.class));
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_request_item, parent, false);
        return new RecyclerView.ViewHolder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView userName = holder.itemView.findViewById(R.id.username);
        TextView fullName = holder.itemView.findViewById(R.id.full_name);
        ImageView profilePic = holder.itemView.findViewById(R.id.profileImage);

        userName.setText(list.get(position).getUserName());
        fullName.setText(list.get(position).getName());

        byte[] photo = Base64.getDecoder().decode(list.get(position).getProfilePic());
        Bitmap bitmapProfilePicture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        profilePic.setImageBitmap(bitmapProfilePicture);

        Button accept = holder.itemView.findViewById(R.id.AcceptButton);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.acceptFollowRequest(list.get(position).getUserName());
                list.remove(position);
                notifyItemRemoved(position);
            }
        });

        Button decline = holder.itemView.findViewById(R.id.DenyButton);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.declineFollowRequest(list.get(position).getUserName());
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
