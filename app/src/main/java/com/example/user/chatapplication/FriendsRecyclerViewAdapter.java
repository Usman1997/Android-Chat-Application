package com.example.user.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.chatapplication.models.Friends;
import com.example.user.chatapplication.models.Users;
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

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>{
    private List<Friends> friendsList;
    private Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    public FriendsRecyclerViewAdapter(List<Friends> friendsList,Context context ) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.status.setText(friendsList.get(position).getDate());
        final CircleImageView circleImageView = holder.circleImageView;
        final String user_id = friendsList.get(position).User_id;

        DatabaseReference user_data = databaseReference.child(user_id);
        user_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String)dataSnapshot.child("name").getValue();
                final String image  = (String)dataSnapshot.child("image").getValue();
                holder.name.setText(name);

                if(!image.equals("default")){
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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {


                        switch (i){
                            case 0:
                                Intent intent = new Intent(context,UserProfileActivity.class);
                                intent.putExtra("from_user_id",user_id);
                                context.startActivity(intent);
                                break;

                            case 1:
                                Intent intent1 = new Intent(context,ChatActivity.class);
                                intent1.putExtra("from_user_id",user_id);

                                context.startActivity(intent1);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return friendsList.size();
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

