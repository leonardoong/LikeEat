package com.example.android.likeeatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.likeeatapplication.Config.Constant;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin, mDaftar, mLoginFb;
    private ProgressBar loginProgress;
    private TextView mLupaPass;
    private ProgressDialog pbDialog;

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private static final String TAG = "FACELOG";
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login);
        mDaftar = (Button) findViewById(R.id.daftar);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress);
        mLupaPass = (TextView)findViewById(R.id.lupaPassword);
        mLoginFb = (Button)findViewById(R.id.login_fb);
        pbDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regis = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regis);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String str_email = mEmail.getText().toString();
                final String pass = mPassword.getText().toString();

                if (!TextUtils.isEmpty(str_email) && !TextUtils.isEmpty(pass)) {
                    loginProgress.setVisibility(View.VISIBLE);
                    pbDialog.setIndeterminate(true);
                    pbDialog.show();
                    pbDialog.setMessage("Harap tunggu");
                    mAuth.signInWithEmailAndPassword(str_email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Constant.refUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    String email = (String) ds.child("email").getValue();
                                                    String nama = (String) ds.child("name").getValue();
                                                    String profile_pic = (String) ds.child("profile_pic").getValue();
                                                    String ttl = (String) ds.child("ttl").getValue();
                                                    String phone = (String) ds.child("phone").getValue();
                                                    if (email.equals(str_email)) {
                                                        SharedPreferences.Editor editor = getSharedPreferences("userSession", MODE_PRIVATE).edit();
                                                        editor.putString("email", email);
                                                        editor.putString("nama", nama);
                                                        editor.putString("profile_pic", profile_pic);
                                                        editor.putString("ttl", ttl);
                                                        editor.putString("phone", phone);
                                                        editor.apply();

                                                        pbDialog.dismiss();
                                                        Log.d("", "signInWithEmail:success");
                                                        FirebaseUser curUser = Constant.mAuth.getCurrentUser();
                                                        Constant.currentUser = curUser;
                                                        sendToMain();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w("", "Failed to read value.", databaseError.toException());
                                                pbDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        String error = task.getException().getMessage();
                                        Log.w("", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                                        pbDialog.dismiss();
                                    }

                                    loginProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                }else{
                    Toast.makeText(LoginActivity.this,getString(R.string.notify_user_pass),Toast.LENGTH_SHORT).show();
                }

            }
        });

        mLupaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_lupa_pass,null);
                final EditText mEmail = (EditText)mView.findViewById(R.id.editEmail);
                Button mReset = (Button)mView.findViewById(R.id.buttonReset);

                mReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = mEmail.getText().toString();
                        if(!email.isEmpty()) {
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(LoginActivity.this, com.example.android.likeeatapplication.R.string.password_baru_sent, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, com.example.android.likeeatapplication.R.string.gagal_reset, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(LoginActivity.this, getString(R.string.masukkan_email), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                //dialog.dismiss();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_fb);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if(acct != null){
                FirebaseUser user = mAuth.getCurrentUser();
                Constant.currentUser = user;
                SharedPreferences.Editor editor = getSharedPreferences("userSession", MODE_PRIVATE).edit();
                editor.putString("email", acct.getEmail());
                editor.putString("nama", acct.getDisplayName());
                editor.putString("profile_pic", acct.getPhotoUrl().toString());
                editor.putString("ttl", "Ubah tanggal lahir");
                editor.putString("phone", "Ubah no Telpon");
                editor.apply();
            }
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(Object o) {
        sendToMain();
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithFacebook:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Constant.currentUser = user;
                            SharedPreferences.Editor editor = getSharedPreferences("userSession", MODE_PRIVATE).edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("nama", user.getDisplayName());
                            editor.putString("profile_pic", user.getPhotoUrl().toString());
                            editor.putString("ttl", "Ubah Tanggal Lahir");
                            editor.putString("phone", "Ubah no Telpon");
                            editor.apply();
                            sendToMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //ngecek user sudah login atau belum
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser != null) {
            sendToMain();
        }else if(acct != null){
            SharedPreferences.Editor editor = getSharedPreferences("userSession", MODE_PRIVATE).edit();
            editor.putString("email", acct.getEmail());
            editor.putString("nama", acct.getDisplayName());
            editor.putString("profile_pic", acct.getPhotoUrl().toString());
            editor.putString("ttl", "Ubah tanggal lahir");
            editor.putString("phone", "Ubah no Telpon");
            editor.apply();
            sendToMain();
        }
    }

    private void sendToMain() {
        //Toast.makeText(LoginActivity.this, getString(R.string.berhasil_login),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
