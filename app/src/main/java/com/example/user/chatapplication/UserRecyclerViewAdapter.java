package com.example.user.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.user.chatapplication.models.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private List<Users> usersList;
    private Context context;

    public UserRecyclerViewAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
         holder.name.setText(usersList.get(position).getName());
         holder.status.setText(usersList.get(position).getStatus());
         final CircleImageView circleImageView = holder.circleImageView;

         if(!usersList.get(position).getImage().equals("default")){



             Picasso.get().load(usersList.get(position).getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                 @Override
                 public void onSuccess() {
                     holder.progressBar.setVisibility(View.INVISIBLE);
                 }

                 @Override
                 public void onError(Exception e) {
                     Picasso.get().load(usersList.get(position).getImage()).into(circleImageView);
                     holder.progressBar.setVisibility(View.INVISIBLE);
                 }
             });






             }


       final String user_id = usersList.get(position).User_id;
       final String user_name =usersList.get(position).getName();
       final String user_status = usersList.get(position).getStatus();
       final String user_image = usersList.get(position).getImage();
        holder.view.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context,UserProfileActivity.class);
               intent.putExtra("from_user_id",user_id);
               intent.putExtra("user_name",user_name);
               intent.putExtra("user_status",user_status);
               intent.putExtra("user_image",user_image);
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView circleImageView;
        private TextView name,status;
        private ProgressBar progressBar;
        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            name = (TextView)view.findViewById(R.id.listview_name);
            status = (TextView)view.findViewById(R.id.listview_status);
            progressBar = (ProgressBar)view.findViewById(R.id.progress);
        }
    }
}
