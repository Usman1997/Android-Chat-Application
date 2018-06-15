package com.example.user.chatapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.chatapplication.models.Conv;
import com.example.user.chatapplication.models.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {


    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public ChatsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.recycleView);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.activity_listview_item,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {



                final String key = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(key).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mUsersDatabase.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {





                        final String userName = (String)dataSnapshot.child("name").getValue();
                        String image = (String)dataSnapshot.child("image").getValue();

                        convViewHolder.setName(userName);
                        convViewHolder.setImage(image);

                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("from_user_id",key);
                                startActivity(chatIntent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mConvList.setAdapter(firebaseConvAdapter);

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private CircleImageView circleImageView;
        private TextView name;
        private TextView message;
        private ProgressBar progressBar;

        public ConvViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            circleImageView = (CircleImageView)mView.findViewById(R.id.listview_image);
            name = (TextView)mView.findViewById(R.id.listview_name);
            message = (TextView)mView.findViewById(R.id.listview_status);
            progressBar = (ProgressBar)mView.findViewById(R.id.progress);


        }


        public  void setMessage(String msg,boolean isSeen) {

            message.setText(msg);
            if (isSeen) {
                message.setTypeface(message.getTypeface(), Typeface.NORMAL);
            } else {
                message.setTypeface(message.getTypeface(), Typeface.BOLD);
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
