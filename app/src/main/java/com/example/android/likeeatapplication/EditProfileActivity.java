package com.example.android.likeeatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.android.likeeatapplication.Config.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgProfile;
    private EditText tvName;
    private TextView tvEmail;
    private EditText tvTTL;
    private EditText tvPhone;
    private Button btnUpdate;
    private StorageReference refPhotoProfile;
    private ProgressDialog pbDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Update Profile");

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        tvName = (EditText) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvTTL = (EditText) findViewById(R.id.tvTtl);
        tvPhone = (EditText) findViewById(R.id.tvPhone);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        pbDialog = new ProgressDialog(this);

        if(Constant.currentUser!=null) {
            loadProfile();
        }
    }

    String email;
    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String name = prefs.getString("nama", "");
        String profile_pic = prefs.getString("profile_pic", "");
        email = prefs.getString("email", "");
        String ttl = prefs.getString("ttl", "");
        String phone = prefs.getString("phone", "");

        Picasso.get().load(profile_pic).into(imgProfile);
        tvName.setText(name);
        tvEmail.setText(email);
        tvTTL.setText(ttl);
        tvPhone.setText(phone);
    }

    Uri photoUrl;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpdate:
                if(tvName.getText().toString().isEmpty() || tvTTL.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Harap lengkapi data", Toast.LENGTH_SHORT).show();
                    return;
                }

                pbDialog.setMessage("Updating..");
                pbDialog.setIndeterminate(true);
                pbDialog.show();

                if(isPicChange) {
                    refPhotoProfile = Constant.storageRef.child("food/" + System.currentTimeMillis() + ".jpg");
                    StorageReference photoImagesRef = Constant.storageRef.child("food/" + System.currentTimeMillis() + ".jpg");
                    refPhotoProfile.getName().equals(photoImagesRef.getName());
                    refPhotoProfile.getPath().equals(photoImagesRef.getPath());

                    imgProfile.setDrawingCacheEnabled(true);
                    imgProfile.buildDrawingCache();
                    Bitmap bitmap = imgProfile.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = refPhotoProfile.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Task<Uri> photoUrl = taskSnapshot.getStorage().getDownloadUrl();
                            prosesUpdate();
                        }
                    });
                } else {
                    prosesUpdate();
                }


                break;
            case R.id.imgProfile:
                ImagePicker.create(this)
                        .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Folder") // folder selection title
                        .toolbarImageTitle("Tap to select") // image selection title
                        .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                        .single() // single mode
                        .limit(1) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        .enableLog(false) // disabling log
                        .start(); // start image picker activity with request code
                break;
        }
    }

    private void prosesUpdate() {
        final String name = tvName.getText().toString();
        final String ttl = tvTTL.getText().toString();
        final String phone = tvPhone.getText().toString();

        Constant.refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emails = (String) ds.child("email").getValue();

                    if(emails.equals(email)) {
                        Constant.refUser.child(ds.getKey()).child("name").setValue(name);
                        Constant.refUser.child(ds.getKey()).child("ttl").setValue(ttl);
                        Constant.refUser.child(ds.getKey()).child("phone").setValue(phone);
                        if(isPicChange) Constant.refUser.child(ds.getKey()).child("profile_pic").setValue(photoUrl.toString());

                        pbDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Update berhasil!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
                //showProgress(false);
            }
        });
    }

    boolean isPicChange = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            File imgFile = new File(image.getPath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgProfile.setImageBitmap(myBitmap);
                isPicChange = true;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}