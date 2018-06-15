package com.example.user.chatapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.chatapplication.models.Friends;
import com.example.user.chatapplication.models.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {


    private RecyclerView recyclerView;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public RequestFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = (RecyclerView) mMainView.findViewById(R.id.recycleView);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mRequestDatabase = mUsersDatabase.child(mCurrent_user_id).child("FriendRequest");
        mRequestDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mRequestDatabase.orderByChild("RequestType").equalTo("received");
        FirebaseRecyclerAdapter<Request,ViewHolder> requestRecycleAdapter = new FirebaseRecyclerAdapter<Request,ViewHolder>(

                Request.class,
                R.layout.activity_listview_item,
                ViewHolder.class,
                conversationQuery


        ) {
            @Override
            protected void populateViewHolder(final ViewHolder holder, Request request, int position) {

                    holder.status.setText(request.getRequestType());
                    final CircleImageView circleImageView = holder.circleImageView;
                    final String list_user_id = getRef(position).getKey();

                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            String name = (String) dataSnapshot.child("name").getValue();
                            final String image = (String) dataSnapshot.child("image").getValue();
                            holder.name.setText(name);

                            if (!image.equals("default")) {
                                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).into(circleImageView);
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                            holder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]{"View Profile", "Accept Request"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Select Options");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {


                                            switch (i) {
                                                case 0:
                                                    Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                                    intent.putExtra("from_user_id", list_user_id);
                                                    startActivity(intent);
                                                    break;

                                                case 1:
                                                    Toast.makeText(getContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
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

        recyclerView.setAdapter(requestRecycleAdapter);

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
