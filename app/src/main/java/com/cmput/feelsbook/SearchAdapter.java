package com.cmput.feelsbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FollowUser> list;
    private User user;

    public SearchAdapter(User user) {
        list = new ArrayList<>();
        this.user = user;
    }

    public void addUser(FollowUser user) {
        list.add(user);
        notifyItemInserted(list.size()-1);
    }

    public void clearList() {
        list = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_item, parent,false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView username = holder.itemView.findViewById(R.id.username);
        TextView fullname = holder.itemView.findViewById(R.id.full_name);
        ImageView profilePic = holder.itemView.findViewById(R.id.profileImage);

        username.setText(list.get(position).getUserName());
        fullname.setText(list.get(position).getName());

        Button send = holder.itemView.findViewById(R.id.toggleButton);
        send.setText("Send");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendFollowRequest(view.getContext(), list.get(position).getUserName());
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
