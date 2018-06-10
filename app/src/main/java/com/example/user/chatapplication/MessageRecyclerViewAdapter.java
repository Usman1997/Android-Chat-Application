package com.example.user.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.user.chatapplication.models.Messages;
import com.example.user.chatapplication.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRecyclerViewAdapter  extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private List<Messages> messagesList;

    FirebaseAuth auth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    Context context;


    public MessageRecyclerViewAdapter(List<Messages> messagesList,Context context) {
        this.messagesList = messagesList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {
        final CircleImageView circleImageView = holder.circleImageView;


        auth = FirebaseAuth.getInstance();
        String CurrentUserID = auth.getCurrentUser().getUid();
        Messages c = messagesList.get(position);

        String from_user = c.getFrom();
        String type = c.getType();

        databaseReference.child(from_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             final String Image = (String) dataSnapshot.child("image").getValue();
             String name = (String)dataSnapshot.child("name").getValue();
              if(!Image.equals("default")){
                  Picasso.get().load(Image).into(holder.circleImageView, new Callback() {
                      @Override
                      public void onSuccess() {

                      }

                      @Override
                      public void onError(Exception e) {
                          Picasso.get().load(Image).into(holder.circleImageView);
                      }
                  });
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










        if(!from_user.equals("")) {
            if (from_user.equals(CurrentUserID)) {
                holder.message.setBackgroundColor(Color.WHITE);
                holder.message.setTextColor(Color.BLACK);

            } else {
                holder.message.setBackgroundResource(R.drawable.button_color_background);
                holder.message.setTextColor(Color.WHITE);
            }
        }

        if(type.equals("text")){
            holder.message.setText( messagesList.get(position).getMessage());
//            holder.imageView.setVisibility(View.INVISIBLE);
        }else{
//            holder.message.setVisibility(View.INVISIBLE);
//            Picasso.get().load(messagesList.get(position).getMessage()).into(holder.imageView);
//            holder.imageView.getLayoutParams().height=150;
        }







    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView circleImageView;
        private TextView message;


        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            message = (TextView)view.findViewById(R.id.listview_message);


        }
    }
}

