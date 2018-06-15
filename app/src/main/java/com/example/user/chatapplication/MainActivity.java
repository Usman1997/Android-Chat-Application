package com.example.user.chatapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
 private android.support.v7.widget.Toolbar toolbar;
FirebaseAuth auth;

ViewPager mainPager;
PagerViewAdapter pagerViewAdapter;
TabLayout tabLayout;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");
        auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        mainPager = (ViewPager)findViewById(R.id.mainPager);
        mainPager.setOffscreenPageLimit(2);
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mainPager.setAdapter(pagerViewAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mainPager);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setSelectedTabIndicatorHeight((int) (4 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#7FFFFFFF"), Color.parseColor("#ffffff"));


    }

    @Override
    public void onStart() {
        super.onStart();
        if(auth.getCurrentUser() == null){
         SendToMainActivity();
        }
        else{
            databaseReference.child("online").setValue("true");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

        }


    }

    private void SendToMainActivity(){
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout) {

            DatabaseReference removeData = databaseReference.child("device_token");

            removeData.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    auth.signOut();
                    SendToMainActivity();
                }
            });




        }

        if(item.getItemId() == R.id.settings){
            startActivity(new Intent(MainActivity.this,AccountSettingsActivity.class));
        }
        if(item.getItemId() ==R.id.users){
            startActivity(new Intent(MainActivity.this,AllUserActivity.class));
        }
        return true;
    }
}
