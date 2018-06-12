package com.example.user.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.chatapplication.models.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
 ;
class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    private List<Request> requestList;
    private Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    public RequestListAdapter(List<Request> requestList, Context context) {
     this.requestList = requestList;
     this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listview_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {

            holder.status.setText(requestList.get(position).getRequestType());
            final CircleImageView circleImageView = holder.circleImageView;
            final String user_id = requestList.get(position).UserID;

            DatabaseReference user_data = databaseReference.child(user_id);
            user_data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]{"View Profile", "Accept Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {


                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(context, UserProfileActivity.class);
                                    intent.putExtra("from_user_id", user_id);
                                    context.startActivity(intent);
                                    break;

                                case 1:
                                    Toast.makeText(context, "Request Accpeted", Toast.LENGTH_SHORT).show();
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

        return requestList.size();
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
