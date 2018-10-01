package com.example.android.likeeatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.likeeatapplication.Config.Constant;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgProfile;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvTTL;
    private TextView tvPhone;
    private LinearLayout llLogout;
    private LinearLayout llCariMakanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvTTL = (TextView) findViewById(R.id.tvTtl);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        llLogout = (LinearLayout) findViewById(R.id.llLogout);
        llCariMakanan = (LinearLayout) findViewById(R.id.llCariMakanan);
        llLogout.setOnClickListener(this);
        llCariMakanan.setOnClickListener(this);

        if(Constant.currentUser!=null) {
            loadProfile();
        }
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String name = prefs.getString("nama", "");
        String profile_pic = prefs.getString("profile_pic", "");
        String email = prefs.getString("email", "");
        String ttl = prefs.getString("ttl", "");
        String phone = prefs.getString("phone", "");

        Picasso.get().load(profile_pic).placeholder(R.drawable.ic_launcher_foreground).into(imgProfile);

        tvName.setText(name);
        tvEmail.setText(email);
        tvTTL.setText(ttl);
        tvPhone.setText(phone);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Constant.currentUser!=null) {
            loadProfile();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnEdit) {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLogout:
                Constant.currentUser = null;
                Constant.mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.llCariMakanan:
                startActivity(new Intent(ProfileActivity.this, SearchPost.class));
                break;
        }
    }
}