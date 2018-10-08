package com.example.android.likeeatapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.example.android.likeeatapplication.Config.Constant;
import com.example.android.likeeatapplication.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgProfile;
    private TextInputEditText tvName;
    private TextInputEditText tvTtl;
    private TextInputEditText tvEmail;
    private TextInputEditText tvPass;
    private TextInputEditText tvPhone;
    private Button btnDaftar;
    private ProgressDialog pbDialog;
    private StorageReference refPhotoProfile;
    Uri photoUrl;
    String photoStringLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        tvName = (TextInputEditText) findViewById(R.id.name);
        tvEmail = (TextInputEditText) findViewById(R.id.email);
        tvTtl = (TextInputEditText) findViewById(R.id.tanggal);
        tvPass = (TextInputEditText) findViewById(R.id.password);
        tvPhone = (TextInputEditText) findViewById(R.id.phone);
        btnDaftar = (Button) findViewById(R.id.email_sign_in_button);
        btnDaftar.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        pbDialog = new ProgressDialog(this);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DateFragment();
        newFragment.show(getSupportFragmentManager(),
                getString(R.string.date_picker));
    }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        // Assign the concatenated strings to dateMessage.
        String dateMessage = (month_string + "/" + day_string + "/" + year_string);
        tvTtl.setText(dateMessage);
    }

    private void registerProcess() {
        if (isPic) {
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

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return refPhotoProfile.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        System.out.println("Upload " + downloadUri);
                        Toast.makeText(RegisterActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        if (downloadUri != null) {

                            photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            System.out.println("Upload " + photoStringLink);
                            createUser();

                        }

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                    Task<Uri> photoUrl = taskSnapshot.getStorage().getDownloadUrl();
//                    createUser();
//                }
//            });
        }
    }

    private void createUser() {
        final String email = tvEmail.getText().toString();
        String password = tvPass.getText().toString();
        final String phone = tvPhone.getText().toString();
        final String nama = tvName.getText().toString();
        final String ttl = tvTtl.getText().toString();

        Constant.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Constant.refUser.child(Constant.mAuth.getUid()).setValue(new User(
                                    Constant.mAuth.getUid(), nama, email, phone, photoStringLink, ttl));
                            // Sign in success, update UI with the signed-in user's information
                            pbDialog.dismiss();
                            Log.d("", "createUserWithEmail:success");
                            //Constant.currentUser = Constant.mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Berhasil mendaftar, Silahkan login!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            pbDialog.dismiss();
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    boolean isPic = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                if (tvName.getText().toString().isEmpty() || tvEmail.getText().toString().isEmpty()
                        || tvTtl.getText().toString().isEmpty() || !isPic) {
                    Toast.makeText(this, "Harap Lengkapi data!", Toast.LENGTH_SHORT).show();
                    return;
                }

                pbDialog.setMessage("Mendaftarkan..");
                pbDialog.setIndeterminate(true);
                pbDialog.show();
                registerProcess();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            com.esafirm.imagepicker.model.Image image = ImagePicker.getFirstImageOrNull(data);
            File imgFile = new File(image.getPath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgProfile.setImageBitmap(myBitmap);
                isPic = true;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
