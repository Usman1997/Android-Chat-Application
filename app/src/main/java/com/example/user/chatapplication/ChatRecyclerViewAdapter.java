package com.example.user.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaExtractor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.chatapplication.models.Conv;
import com.example.user.chatapplication.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerViewAdapter  extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private List<Conv> chatList;
    Context context;


    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    FirebaseAuth auth;

    public ChatRecyclerViewAdapter(List<Conv> chatList,Context context) {
        this.chatList = chatList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(auth.getCurrentUser().getUid());
        mUsersDatabase.keepSynced(true);

        final String key = chatList.get(position).key;

        Query lastMessageQuery = mMessageDatabase.child(key).limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data = dataSnapshot.child("message").getValue().toString();
                holder.setMessage(data, chatList.get(position).isSeen());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUsersDatabase.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String userName = (String)dataSnapshot.child("name").getValue();
                String image = (String)dataSnapshot.child("image").getValue();

                holder.setName(userName);
                holder.setImage(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("from_user_id",key);
                context.startActivity(intent);
            }
        });






    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView circleImageView;
        private TextView name;
        private TextView message;
        private ProgressBar progressBar;




        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            name = (TextView)view.findViewById(R.id.listview_name);
            message = (TextView)view.findViewById(R.id.listview_status);
            progressBar = (ProgressBar)view.findViewById(R.id.progress);

            }

            public  void setMessage(String msg,boolean isSeen){

             message.setText(msg);
             if(isSeen){
                 message.setTypeface(message.getTypeface(), Typeface.NORMAL);
             }else{
                message.setTypeface(message.getTypeface(),Typeface.BOLD);
             }

             }

             public void setName(String UserName){

               name.setText(UserName);
             }
             public void setImage(final String ImageUrl) {

                 if (!ImageUrl.equals("default")) {
                     Picasso.get().load(ImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                         @Override
                         public void onSuccess() {
                             progressBar.setVisibility(View.INVISIBLE);

                         }

                         @Override
                         public void onError(Exception e) {
                             Picasso.get().load(ImageUrl).into(circleImageView);
                             progressBar.setVisibility(View.INVISIBLE);
                         }
                     });
                 }
             }


    }
}