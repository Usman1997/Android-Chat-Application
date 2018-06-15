package com.example.user.chatapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.chatapplication.models.Friends;
import com.example.user.chatapplication.models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {




    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.recycleView);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsDatabase = mUsersDatabase.child(mCurrent_user_id).child("Friends");
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, ViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, ViewHolder>(

                Friends.class,
                R.layout.activity_listview_item,
                ViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final ViewHolder friendsViewHolder, Friends friends, int i) {

                friendsViewHolder.status.setText(friends.getDate());
                final CircleImageView circleImageView = friendsViewHolder.circleImageView;
                final String list_user_id = getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = (String)dataSnapshot.child("name").getValue();
                        final String image  = (String)dataSnapshot.child("image").getValue();

                        friendsViewHolder.name.setText(name);

                        if(!image.equals("default")){
                            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    friendsViewHolder.progressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).into(circleImageView);
                                    friendsViewHolder.progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        friendsViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {


                                        switch (i){
                                            case 0:
                                                Intent intent = new Intent(getContext(),UserProfileActivity.class);
                                                intent.putExtra("from_user_id",list_user_id);
                                                startActivity(intent);
                                                break;

                                            case 1:
                                                Intent intent1 = new Intent(getContext(),ChatActivity.class);
                                                intent1.putExtra("from_user_id",list_user_id);
                                                startActivity(intent1);
                                                break;
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);





}





    public static class ViewHolder extends RecyclerView.ViewHolder{

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




