package com.example.user.chatapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.chatapplication.models.Friends;
import com.example.user.chatapplication.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private List<Request> requestList;
    RecyclerView recyclerView;
    RequestListAdapter requestListAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference requestDatabase;
    FirebaseAuth auth;
    public RequestFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView  = (RecyclerView)view.findViewById(R.id.recycleView);
        auth = FirebaseAuth.getInstance();
        requestDatabase = databaseReference.child(auth.getCurrentUser().getUid()).child("FriendRequest");

        requestList = new ArrayList<>();

        requestListAdapter = new RequestListAdapter(requestList,getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(requestListAdapter);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        requestList.clear();

        requestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  for(DataSnapshot RequestList:dataSnapshot.getChildren()){

                      String key = RequestList.getKey();
                      Request request = RequestList.getValue(Request.class).WithId(key);

                      if(!request.getRequestType().equals("sent")){
                          requestList.add(request);
                      }

                      requestListAdapter.notifyDataSetChanged();
                  }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
