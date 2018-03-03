package com.example.android.likeeatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail, mPass, mRePass;
    private Button mDaftar;

    private ProgressBar progressBarReg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (EditText)findViewById(R.id.regEmail);
        mPass = (EditText)findViewById(R.id.regPassword);
        mRePass = (EditText)findViewById(R.id.regRePassword);
        mDaftar = (Button)findViewById(R.id.register);

        progressBarReg = (ProgressBar)findViewById(R.id.progressBar2);
        progressBarReg.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        mDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();
                String rePass = mRePass.getText().toString();

                progressBarReg.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(rePass)){
                    if(pass.equals(rePass)){
                        mAuth.createUserWithEmailAndPassword(email,pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            sendToMain();
                                        }else{
                                            String error = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Password yang anda masukkan berbeda", Toast.LENGTH_SHORT).show();
                    }
                    progressBarReg.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
