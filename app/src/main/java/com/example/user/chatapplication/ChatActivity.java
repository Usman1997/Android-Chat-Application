package com.example.user.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.user.chatapplication.models.Messages;
import com.example.user.chatapplication.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    String VisitedUserID;
    DatabaseReference RoofData = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference UserDatabase;
    TextView name,lastseen;
    CircleImageView circleImageView;

    ImageButton add,sent;
    EditText message;
    FirebaseAuth auth;
    String CurrentUserID;


    private List<Messages> messagesList;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MessageRecyclerViewAdapter messageRecyclerViewAdapter;

    private static final int PAGES_TO_LOAD=10;
    private int CurrentPage=1;


    //new Sol

    private int itemPos = 0;
    private String mLastKey="";
    private String prevKey="";
    LinearLayoutManager linearLayoutManager;


    //For image messaging
    private static final int Gallery_Pick = 1;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        add = (ImageButton)findViewById(R.id.add);
        sent = (ImageButton)findViewById(R.id.sent);
        message = (EditText)findViewById(R.id.message);
        sent.setOnClickListener(this);
        add.setOnClickListener(this);


        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        CurrentUserID = auth.getCurrentUser().getUid();
        VisitedUserID = getIntent().getStringExtra("from_user_id");


        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        messagesList = new ArrayList<>();

        messageRecyclerViewAdapter = new MessageRecyclerViewAdapter(messagesList,this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messageRecyclerViewAdapter);


        loadMessages();
        RoofData.child("Chat").child(CurrentUserID).child(VisitedUserID).child("seen").setValue(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        name = (TextView)findViewById(R.id.name);
        lastseen = (TextView)findViewById(R.id.last_seen);
        circleImageView = (CircleImageView)findViewById(R.id.bar_image);

       UserDatabase = databaseReference.child(VisitedUserID);
        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name1 = (String)dataSnapshot.child("name").getValue();
                String image1 = (String)dataSnapshot.child("image").getValue();
                String online = (String)dataSnapshot.child("online").getValue().toString();

                if(online.equals("true")){
                    lastseen.setText("online");
                }else{
                    GetTimeAgo getTime = new GetTimeAgo();
                    long last_seen = Long.parseLong(online);
                    String lastSeenTime = getTime.getTimeAgo(last_seen,getApplicationContext());
                    lastseen.setText(lastSeenTime);
                }
                name.setText(name1);

                if(!image1.equals("default")){
                    Picasso.get().load(image1).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        RoofData.child("Chat").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(!dataSnapshot.hasChild(VisitedUserID)){
                     Map ChatMap = new HashMap();
                     ChatMap.put("seen",false);
                     ChatMap.put("timestamp", ServerValue.TIMESTAMP);

                     Map ChatUserMap = new HashMap();
                     ChatUserMap.put("Chat/"+CurrentUserID+"/"+VisitedUserID,ChatMap);
                     ChatUserMap.put("Chat/"+VisitedUserID+"/"+CurrentUserID,ChatMap);

                     RoofData.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                              if(databaseError !=null){
                                  Log.d("CHAT_LOG",databaseError.getMessage().toString());
                              }
                         }
                     });

                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sent:
                message();
                break;

            case R.id.add:
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),Gallery_Pick);
                break;
        }
    }

    private void message() {
        String msg = message.getText().toString();
        if(!TextUtils.isEmpty(msg)){
            String cuurent_user_ref = "messages/"+CurrentUserID+"/"+ VisitedUserID;
            String visited_user_ref = "messages/"+VisitedUserID+"/"+CurrentUserID;

            DatabaseReference user_message_push = RoofData.child("messages").child(CurrentUserID).child(VisitedUserID).push();

            String push_id = user_message_push.getKey();

            final Map messageMap = new HashMap();
            messageMap.put("message",msg);
            messageMap.put("seen",false);
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("type","text");
            messageMap.put("from",CurrentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(cuurent_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(visited_user_ref+"/"+push_id,messageMap);

            message.setText("");

            RoofData.child("Chat").child(CurrentUserID).child(VisitedUserID).child("seen").setValue(true);
            RoofData.child("Chat").child(CurrentUserID).child(VisitedUserID).child("timestamp").setValue(ServerValue.TIMESTAMP);

            RoofData.child("Chat").child(VisitedUserID).child(CurrentUserID).child("seen").setValue(false);
            RoofData.child("Chat").child(VisitedUserID).child(CurrentUserID).child("timestamp").setValue(ServerValue.TIMESTAMP);



            RoofData.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                 @Override
                 public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                   if(databaseError!=null){
                       Log.d("CHAT_LOG",databaseError.getMessage().toString());
                   }
                 }
             });

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                CurrentPage++;
                itemPos = 0;
                LoadMoreMessages();
            }
        });


    }


public void LoadMoreMessages(){

    DatabaseReference messageRef =  RoofData.child("messages").child(CurrentUserID).child(VisitedUserID);
    Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
    messageQuery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Messages messages = dataSnapshot.getValue(Messages.class);

            String messageKey = dataSnapshot.getKey();

           if(!prevKey.equals(messageKey)){
               messagesList.add(itemPos++,messages);
           }else{
               prevKey = mLastKey;
           }
            if(itemPos==1) {

                mLastKey = messageKey;

            }

            messageRecyclerViewAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setRefreshing(false);
            linearLayoutManager.scrollToPositionWithOffset(10,0);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}


    public void loadMessages(){

        DatabaseReference messageRef =  RoofData.child("messages").child(CurrentUserID).child(VisitedUserID);
        Query messageQuery = messageRef.limitToLast(CurrentPage * PAGES_TO_LOAD);

           messageQuery.addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   Messages messages = dataSnapshot.getValue(Messages.class);

                   itemPos++;

                   if(itemPos==1){
                      String messageKey = dataSnapshot.getKey();
                      mLastKey = messageKey;
                      prevKey = messageKey;
                   }

                   messagesList.add(messages);
                   messageRecyclerViewAdapter.notifyDataSetChanged();
                   recyclerView.scrollToPosition(messagesList.size()-1);
                   swipeRefreshLayout.setRefreshing(false);

               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               }

               @Override
               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

               }

               @Override
               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK){
            Uri imageURI = data.getData();

            final String current_user_ref = "messages/"+CurrentUserID+"/"+ VisitedUserID;
            final String visited_user_ref = "messages/"+VisitedUserID+"/"+CurrentUserID;

            DatabaseReference user_message_push = RoofData.child("messages").child(CurrentUserID).child(VisitedUserID).push();

           final String push_id = user_message_push.getKey();

            final StorageReference filepath = storageReference.child("messages_image").child(push_id + ".jpg");

            filepath.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String download_uri = uri.toString();

                                final Map messageMap = new HashMap();
                                messageMap.put("message",download_uri);
                                messageMap.put("seen",false);
                                messageMap.put("time",ServerValue.TIMESTAMP);
                                messageMap.put("type","image");
                                messageMap.put("from",CurrentUserID);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                                messageUserMap.put(visited_user_ref+"/"+push_id,messageMap);

                                message.setText("");

                                RoofData.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if(databaseError!=null){
                                            Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                        }
                                    }
                                });

                            }
                        });
                    }

                }
            });


        }
    }
}
