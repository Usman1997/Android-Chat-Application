package com.example.user.chatapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.user.chatapplication.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUserActivity extends AppCompatActivity{
    Toolbar toolbar;
    private List<Users> usersList;
    RecyclerView recyclerView;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users");

        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        usersList = new ArrayList<>();

        userRecyclerViewAdapter = new UserRecyclerViewAdapter(usersList,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(userRecyclerViewAdapter);



    }

    @Override
    public void onStart() {
        super.onStart();
        usersList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot UserList : dataSnapshot.getChildren()){

                    String user_id = UserList.getKey();
                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    usersList.add(users);
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
