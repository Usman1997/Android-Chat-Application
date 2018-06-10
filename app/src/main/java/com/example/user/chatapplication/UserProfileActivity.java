package com.example.user.chatapplication;

import android.app.NotificationManager;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    TextView name, status, friends;
    Button send, decline;
    ImageView imageView;


    int send_state;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    String CurrentUserID;
    String VisitedUserID;
    FirebaseAuth auth;
    DatabaseReference CurrentUserData, VisitedUserData,NotificationDatabase;

    //0 for Not Friend
    //1 For Friend
    //2 Request sent
    //3 Request received

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        name = (TextView) findViewById(R.id.name);
        status = (TextView) findViewById(R.id.status);
        friends = (TextView) findViewById(R.id.total_friends);
        imageView = (ImageView) findViewById(R.id.image);
        send = (Button) findViewById(R.id.send_request);
        decline = (Button) findViewById(R.id.decline_request);

        VisitedUserID = getIntent().getStringExtra("from_user_id");

        databaseReference.child(VisitedUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UserName = (String)dataSnapshot.child("name").getValue();
                String UserStatus = (String)dataSnapshot.child("status").getValue();
                final String UserImage = (String)dataSnapshot.child("image").getValue();

                name.setText(UserName);


                name.setText(UserName);
                status.setText(UserStatus);
                if (!UserImage.equals("default")) {

                    Picasso.get().load(UserImage).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(UserImage).into(imageView);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //state
        send_state =0;

        //Working for database
        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        CurrentUserData = databaseReference.child(CurrentUserID);
        VisitedUserData = databaseReference.child(VisitedUserID);





        send.setOnClickListener(this);
        decline.setOnClickListener(this);


      checkRequestState();





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.send_request:

                if (send_state == 0) {

                    SendFriendRequest();


                } else if (send_state == 1) {

                    UnFriend();

                } else if (send_state == 2) {
                    CancelFriendRequest();
                } else if (send_state == 3){
                    AcceptFriendRequest();
                }


                    break;

            case R.id.decline_request:
                DeclineFriendRequest();
                break;
        }
    }



    private void SendFriendRequest() {

        CurrentUserData.child("FriendRequest").child(VisitedUserID).child("RequestType").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    VisitedUserData.child("FriendRequest").child(CurrentUserID).child("RequestType").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            NotificationDatabase = databaseReference.child(VisitedUserID).child("Notifications").push();
                            NotificationDatabase.child("from").setValue(CurrentUserID);
                            NotificationDatabase.child("type").setValue("request").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    send_state = 2;
                                    send.setText("Cancel Friend Request");
                                    decline.setEnabled(false);
                                    decline.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UserProfileActivity.this, "Friend Request sent", Toast.LENGTH_SHORT).show();
                                }
                            });





                        }
                    });

                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    private void CancelFriendRequest() {
        CurrentUserData.child("FriendRequest").child(VisitedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    VisitedUserData.child("FriendRequest").child(CurrentUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           if(send_state==2){
                               send.setText("Send Freind Request");
                               send_state = 0;
                           }
                           else if(send_state==1){
                               send.setText("UnFriend");
                           }


                        }
                    });

                } else {
                    Toast.makeText(UserProfileActivity.this, "Error in deleting Friend  Request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void AcceptFriendRequest() {

                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                CurrentUserData.child("Friends").child(VisitedUserID).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if(task.isSuccessful()){
                            VisitedUserData.child("Friends").child(CurrentUserID).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    send.setText("UnFriend");
                                    send_state = 1;
                                    CancelFriendRequest();
                                    decline.setEnabled(false);
                                    decline.setVisibility(View.INVISIBLE);
                                }
                            });
                        }else{
                            Toast.makeText(UserProfileActivity.this, "Error in accepting Request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void checkRequestState(){
        databaseReference.child(CurrentUserID).child("FriendRequest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(VisitedUserID)) {
                    String req_type = dataSnapshot.child(VisitedUserID).child("RequestType").getValue().toString();
                    if (req_type.equals("sent")) {
                        send.setText("Cancel Freind Request");
                        send_state = 2;
                        decline.setEnabled(false);
                        decline.setVisibility(View.INVISIBLE);
                    } else if (req_type.equals("received")) {
                        send.setText("Accept Freind Request");
                        send_state = 3;
                        decline.setEnabled(true);
                        decline.setVisibility(View.VISIBLE);
                    }
                }else{
                    checkFriends();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void UnFriend() {
        CurrentUserData.child("Friends").child(VisitedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  VisitedUserData.child("Friends").child(CurrentUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                       send.setText("Sent Friend Request");
                       send_state=0;
                          decline.setEnabled(false);
                          decline.setVisibility(View.INVISIBLE);
                      }
                  });
              }else{
                  Toast.makeText(UserProfileActivity.this, "Error in accepting Request", Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

    private void checkFriends(){
        databaseReference.child(CurrentUserID).child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(VisitedUserID)){
                    send.setText("UnFriend");
                    send_state=1;
                    decline.setEnabled(false);
                    decline.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void DeclineFriendRequest() {
        CurrentUserData.child("FriendRequest").child(VisitedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    VisitedUserData.child("FriendRequest").child(CurrentUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                                send.setText("Send Friend Request");
                                send_state = 0;
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);


                        }
                    });

                } else {
                    Toast.makeText(UserProfileActivity.this, "Error in declining Friend Request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child(CurrentUserID).child("online").setValue("false");
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.child(CurrentUserID).child("online").setValue("true");
    }
}
