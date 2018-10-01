package com.example.android.likeeatapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.likeeatapplication.Model.PlaceInfo;
import com.example.android.likeeatapplication.Model.Post;
import com.example.android.likeeatapplication.Model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    EditText mTitlePost, mPost, mAlamat;
    ImageView imageView;
    Button mChooseImage;
    DatabaseReference databaseFood;
    FirebaseAuth mAuth;

    private Uri imageUri;
    private StorageReference mStorage;
    Query databaseUser;
    private PlaceInfo mPlace;

    private static final String TAG = "PostActivity";
    private static final int ERROR_DIALOG_REQUEST= 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageUri = null;

        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference().child("images");

        databaseFood = FirebaseDatabase.getInstance().getReference(MainActivity.table1);

        databaseUser = FirebaseDatabase.getInstance().getReference(MainActivity.table3);

        mTitlePost = (EditText) findViewById(R.id.et_title_post);
        mPost = (EditText) findViewById(R.id.ed_post);
        imageView = findViewById(R.id.img_post);
        mAlamat = (EditText) findViewById(R.id.ed_alamat);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String tx_alamat = extras.getString("Alamat");
            mAlamat.setText(tx_alamat);
        }

        mAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maps = new Intent(PostActivity.this, MapsActivity.class);
                startActivity(maps);
            }
        });

        mChooseImage = findViewById(R.id.btn_choose_image);
        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        if(isServiesOK()){
            mAlamat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent maps = new Intent(PostActivity.this, MapsActivity.class);
                    startActivity(maps);
                }
            });
        }
    }

    public void add(View view) {

        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.child(mAuth.getUid()).getValue(User.class);

                final String name = user.getName();
                final String title = mTitlePost.getText().toString();
                final String postMessage = mPost.getText().toString();
                final String id = databaseFood.push().getKey();
                final String userId = mAuth.getUid();
                final String alamat = mAlamat.getText().toString();
                final long timestamp = System.currentTimeMillis();

                if (imageUri != null && !TextUtils.isEmpty(name)) {

                    final StorageReference ref = mStorage.child(id + ".jpg");


                    ref.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                String str_downloadUri = downloadUri.toString();
                                Post post = new Post(id, userId, name, str_downloadUri, title, postMessage, alamat, 0 - timestamp, null, 0, 0);
                                databaseFood.child(id).setValue(post);
                            } else {
                                // Handle failures
                                // ...
                                Toast.makeText(PostActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//                    image.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
//
//                            if (uploadTask.isSuccessful()) {
//                                //Task<Uri> download_url = uploadTask.getResult().getDownloadUrl().toString();
//                                String download_url = uploadTask.getResult().getDownloadUrl().toString();
//                                Post post = new Post(id, userId, name, download_url, title, postMessage, alamat, 0 - timestamp, null, 0, 0);
//                                databaseFood.child(id).setValue(post);
//
//                            } else {
//                                Toast.makeText(PostActivity.this, "Error : " + uploadTask.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });

                    //displaying a success toast
                    Toast.makeText(PostActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    //if the value is not given displaying a toast
                    Toast.makeText(PostActivity.this, "Please Fill the form and choose image", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    public boolean isServiesOK(){
        Log.d(TAG, "Cek google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PostActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG,"Google service berjalan lancar");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"Terjadi error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PostActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"Google services tidak dapat dibuka", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

