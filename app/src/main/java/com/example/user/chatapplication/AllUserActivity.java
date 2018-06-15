package com.example.user.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class AllUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView recyclerView;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Users");

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersDatabase.keepSynced(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users,ViewHolder>(

                Users.class,
                R.layout.activity_listview_item,
                ViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(final ViewHolder holder, final Users users,final  int position) {

                holder.name.setText(users.getName());
                holder.status.setText(users.getStatus());
                final CircleImageView circleImageView = holder.circleImageView;
                final String user_id = getRef(position).getKey();

                if(!users.getImage().equals("default")) {


                    Picasso.get().load(users.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(users.getImage()).into(circleImageView);
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                final String user_name =users.getName();
                final String user_status = users.getStatus();
                final String user_image = users.getImage();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(AllUserActivity.this,UserProfileActivity.class);
                        intent.putExtra("from_user_id",user_id);
                        intent.putExtra("user_name",user_name);
                        intent.putExtra("user_status",user_status);
                        intent.putExtra("user_image",user_image);
                        startActivity(intent);

                    }
                });

            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);


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
