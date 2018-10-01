package com.example.android.likeeatapplication.Config;

import android.location.Location;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Leonardo on 4/21/2018.
 */

public class Constant {
    public final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public final static DatabaseReference refUser = database.getReference("user");
    public final static DatabaseReference refComment = database.getReference("comment");
    public final static DatabaseReference refPost = database.getReference("Post");

    public final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser;
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReference();

    public final static String App_ID = "52201e8f795927df6e7a2f7262c1a65d";
    //public static Location current_location = null;
}
