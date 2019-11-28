package com.cmput.feelsbook;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Following extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FollowUser> list;
    private User user;

    public Following(User user) {
        this.user = user;
        list = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUserName())
                .collection("following")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                   if(e == null) {
                       for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                           switch (doc.getType()) {
                               case ADDED:
                                   list.add(doc.getDocument().toObject(FollowUser.class));
                                   notifyItemInserted(doc.getNewIndex());
                                   break;
                               case REMOVED:
                                   if(list.size() > 0 && list.get(doc.getOldIndex()).getUserName().equals(doc.getDocument().getId())) {
                                       list.remove(doc.getOldIndex());
                                       notifyItemRemoved(doc.getOldIndex());
                                       notifyItemRangeChanged(doc.getOldIndex(), getItemCount());
                                   }
                                   break;
                           }
                       }
                   }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_item, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView username = holder.itemView.findViewById(R.id.username);
        TextView fullname = holder.itemView.findViewById(R.id.full_name);
        ImageView profilePic = holder.itemView.findViewById(R.id.profileImage);

        username.setText(list.get(position).getUserName());
        fullname.setText(list.get(position).getName());
        profilePic.setImageBitmap(list.get(position).profilePicBitmap());

        Button remove = holder.itemView.findViewById(R.id.removeButton);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.removeFollower(list.get(position).getUserName());
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
