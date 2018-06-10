package com.example.user.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.ForkJoinPool;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText email, password, name;
    Button submit, login;
    ProgressBar progressBar;


    FirebaseAuth auth;
    DatabaseReference database;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        storageReference = FirebaseStorage.getInstance().getReference().child("images").child("user.png");

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference().child("Users");
        progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        login = (Button) findViewById(R.id.login);

        submit.setOnClickListener(this);
        login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Register();
                break;

            case R.id.login:
                finish();

                break;
                }
    }

    private void Register() {


        final String user_name = name.getText().toString();
        String EmailTxt = email.getText().toString();
        String PassTxt = password.getText().toString();


        if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(EmailTxt) && !TextUtils.isEmpty(PassTxt)) {
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(EmailTxt, PassTxt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String user_id = auth.getCurrentUser().getUid();
                        String token_id =  FirebaseInstanceId.getInstance().getToken();

                       final DatabaseReference user_data = database.child(user_id);
                        user_data.child("name").setValue(user_name);
                        user_data.child("status").setValue("Hey There! I am using WhatsApp");
                        user_data.child("thumb_image").setValue("default");
                        user_data.child("device_token").setValue(token_id);
                        user_data.child("image").setValue("default").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });




                        } else {
                        Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
            });
        }
    }
}


