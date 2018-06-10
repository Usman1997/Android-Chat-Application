package com.example.user.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class ChangeStatusActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressBar progressBar;
    Toolbar toolbar;
    Button ok, cancel;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String user_id;
    EditText status;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add About");


        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        ok = (Button) findViewById(R.id.OK_BTN);
        cancel = (Button) findViewById(R.id.Cancel_BTN);
        status = (EditText)findViewById(R.id.status);
        progressBar  = (ProgressBar)findViewById(R.id.progress);

        String u_status = getIntent().getStringExtra("status");
        status.setText(u_status);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.OK_BTN:
                progressBar.setVisibility(View.VISIBLE);
                UpdateStatus();
                break;

            case R.id.Cancel_BTN:
               finish();
        }
    }

    private void UpdateStatus() {
        String user_status = status.getText().toString();
        databaseReference.child("status").setValue(user_status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ChangeStatusActivity.this,"Status not Updated",Toast.LENGTH_SHORT).show();
            }
        });
     }
}
