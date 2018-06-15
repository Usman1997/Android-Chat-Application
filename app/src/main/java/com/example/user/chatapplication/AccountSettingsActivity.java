package com.example.user.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.nio.InvalidMarkException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    CircleImageView image;
    TextView name,status;
    Button update_image;
    ImageButton status_update;
    public static final int pick_image = 1;
    private Uri uri;

    String user_id;
    FirebaseAuth auth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference user_data;
    StorageReference storageReference;

    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_setting);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        image = (CircleImageView)findViewById(R.id.image);
        name = (TextView)findViewById(R.id.name);
        status = (TextView)findViewById(R.id.status);
        status_update = (ImageButton)findViewById(R.id.update_status);
        update_image  = (Button)findViewById(R.id.update_image);

        image.setOnClickListener(this);
        status_update.setOnClickListener(this);
        update_image.setOnClickListener(this);

        uri = null;
        progressBar = (ProgressBar)findViewById(R.id.progress);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("images");
         user_id = auth.getCurrentUser().getUid();

         user_data = databaseReference.child(user_id);
         user_data.keepSynced(true);

         user_data.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String User_name = (String)dataSnapshot.child("name").getValue();
                 String User_status = (String)dataSnapshot.child("status").getValue();
                 final String user_image = (String)dataSnapshot.child("image").getValue();
                 name.setText(User_name);
                 status.setText(User_status);
                 if(!user_image.equals("default")){

                     Picasso.get().load(user_image).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                         @Override
                         public void onSuccess() {
                             progressBar.setVisibility(View.INVISIBLE);
                             image.setVisibility(View.VISIBLE);
                         }

                         @Override
                         public void onError(Exception e) {
                             Picasso.get().load(user_image).into(image);
                             progressBar.setVisibility(View.INVISIBLE);
                         }
                     });


                 }else{
                     progressBar.setVisibility(View.INVISIBLE);
                     image.setVisibility(View.VISIBLE);
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
           case R.id.image:
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Selected Picture"), pick_image);
               break;


            case R.id.update_status:
                String User_Status = status.getText().toString();
                Intent intent1 = new Intent(AccountSettingsActivity.this,ChangeStatusActivity.class);
                intent1.putExtra("status",User_Status);
                startActivity(intent1);

                break;

            case R.id.update_image:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pick_image && resultCode == RESULT_OK) {
            uri = data.getData();
            CropImage.activity(uri).setAspectRatio(1,1).setMinCropWindowSize(500,500)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                image.setVisibility(View.INVISIBLE);


                Uri resultUri = result.getUri();

                final String UserID = auth.getCurrentUser().getUid();
                final StorageReference filepath = storageReference.child(UserID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful()){

                             filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {

                                     String image_uri = uri.toString();
                                     DatabaseReference user_data = databaseReference.child(UserID);
                                     user_data.child("image").setValue(image_uri);
                                     progressBar.setVisibility(View.INVISIBLE);
                                     image.setVisibility(View.VISIBLE);
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(AccountSettingsActivity.this,"Fail in Retrieving image",Toast.LENGTH_SHORT).show();
                                     progressBar.setVisibility(View.INVISIBLE);
                                     image.setVisibility(View.VISIBLE);
                                 }
                             });
                         }else{
                             Toast.makeText(AccountSettingsActivity.this,"Fail in Uploading image",Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(View.INVISIBLE);
                         }
                    }
                });

        }
        }
    }



}
