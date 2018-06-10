package com.example.user.chatapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.chatapplication.models.Friends;
import com.example.user.chatapplication.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {

    private List<Friends> friendsList;
    RecyclerView recyclerView;
    FriendsRecyclerViewAdapter friendsRecyclerViewAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference friendsDatabase;
    FirebaseAuth auth;
    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        auth = FirebaseAuth.getInstance();
        friendsDatabase = databaseReference.child(auth.getCurrentUser().getUid()).child("Friends");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycleView);
        friendsList = new ArrayList<>();

        friendsRecyclerViewAdapter = new FriendsRecyclerViewAdapter(friendsList,getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        recyclerView.setAdapter(friendsRecyclerViewAdapter);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        friendsList.clear();
        friendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot FriendList : dataSnapshot.getChildren()){

                    String user_id = FriendList.getKey();
                    Friends friends = FriendList.getValue(Friends.class).WithId(user_id);
                    friendsList.add(friends);
                    friendsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
