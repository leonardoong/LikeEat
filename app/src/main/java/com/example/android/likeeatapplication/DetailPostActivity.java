package com.example.android.likeeatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.likeeatapplication.Adapter.CommentAdapter;
import com.example.android.likeeatapplication.Adapter.PostAdapter;
import com.example.android.likeeatapplication.Config.Constant;
import com.example.android.likeeatapplication.Model.Comment;
import com.example.android.likeeatapplication.Model.Like;
import com.example.android.likeeatapplication.Model.Post;
import com.example.android.likeeatapplication.Model.Rating;
import com.example.android.likeeatapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.tvbarthel.intentshare.IntentShare;

public class DetailPostActivity extends AppCompatActivity {

    private TextView mTitle, mPost, mAlamat, value;
    private ImageView mComment, mImagePost;

    private String str_id;

    private RatingBar avgRating;
    private Float ratingValue;

    private RecyclerView rvComment;
    private ArrayList<Comment> listComments;
    private CommentAdapter commentList;

    private ImageButton mLikeButton, mRating;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRating;
    private DatabaseReference databaseLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        rvComment = (RecyclerView) findViewById(R.id.recyclerViewComment);
        listComments = new ArrayList<>();

        mTitle = (TextView) findViewById(R.id.tv_title_detail);
        mPost = (TextView) findViewById(R.id.tv_detail_post);
        mAlamat = (TextView) findViewById(R.id.tx_alamat);

        mImagePost = (ImageView) findViewById(R.id.img_detailpost);
        mComment = (ImageView) findViewById(R.id.img_comment);

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new GridLayoutManager(DetailPostActivity.this, 1));
        commentList = new CommentAdapter(DetailPostActivity.this, listComments);
        rvComment.setAdapter(commentList);

        mLikeButton = (ImageButton) findViewById(R.id.img_like);
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseLike.keepSynced(true);

        mRating = (ImageButton) findViewById(R.id.img_rating);
        avgRating = (RatingBar) findViewById(R.id.avgRating);

        Intent intent = getIntent();
        str_id = intent.getStringExtra("id");
        String str_username = intent.getStringExtra("Username");
        final String str_image = intent.getStringExtra("image");
        String str_title = intent.getStringExtra("Title");
        String str_description = intent.getStringExtra("Post");
        String str_alamat = intent.getStringExtra("Alamat");

        Glide.with(DetailPostActivity.this).load(str_image).into(mImagePost);

        final DatabaseReference databaseComment = Constant.refPost.child(str_id).child("comment");
        databaseRating = Constant.refPost.child(str_id).child("rating");
        databaseLike = Constant.refPost.child(str_id).child("likes");
        mTitle.setText(str_title);
        mPost.setText(str_description);
        mAlamat.setText(str_alamat);

        loadComment();
        loadRating();

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailPostActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
                final EditText ed_comment = (EditText) mView.findViewById(R.id.editComment);
                Button mOK = (Button) mView.findViewById(R.id.buttonOK);

                mOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = ed_comment.getText().toString();
                        if (!comment.isEmpty()) {
                            Constant.refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.child(Constant.mAuth.getUid()).getValue(User.class);
                                    String textReview = ed_comment.getText().toString().trim();
                                    if (!TextUtils.isEmpty(textReview)) {

                                        String id = databaseComment.push().getKey();
                                        long timestamp = System.currentTimeMillis();

                                        Comment track = new Comment(id, user.getName(), textReview, (0 - timestamp), user.getProfile_pic());
                                        databaseComment.child(id).setValue(track);
                                        Toast.makeText(DetailPostActivity.this, "Komentar Terkirim", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DetailPostActivity.this, "Masukkan Komentar", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(DetailPostActivity.this, "Masukkan Komentar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                //dialog.dismiss();
            }
        });

        mRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailPostActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_rating, null);
                final RatingBar rb = (RatingBar) mView.findViewById(R.id.ratingBar);
                rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float a, boolean b) {
                        ratingValue = a;
                    }
                });

                Button mSubmit = (Button) mView.findViewById(R.id.submitButton);

                mSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View a) {
                        if (ratingValue>0) {
                            Constant.refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.child(Constant.mAuth.getUid()).getValue(User.class);
                                    //String textReview = ed_comment.getText().toString().trim();
                                    String id = databaseRating.push().getKey();
                                    long timestamp = System.currentTimeMillis();

                                    Rating rating = new Rating(id, user.getName(), ratingValue);
                                    databaseRating.child(id).setValue(rating);
                                    Toast.makeText(DetailPostActivity.this, "Terima kasih atas rating yang anda berikan", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(DetailPostActivity.this, "Tolong, berikan rating dari anda", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        setLikeButton();

        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProcessLike = true;
                if (mProcessLike) {

                    Constant.refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.child(Constant.mAuth.getUid()).getValue(User.class);
                            //String textReview = ed_comment.getText().toString().trim();
                            String id = databaseLike.push().getKey();
                            long timestamp = System.currentTimeMillis();

                            Like like = new Like(id, user.getName());
                            databaseLike.child(id).setValue(like);
                            updateLike();
                            Toast.makeText(DetailPostActivity.this, "Terima kasih atas like anda", Toast.LENGTH_SHORT).show();
                            mProcessLike = false;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    /**private void pushNotifMessage() {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(  "608156307629@gcm.googleapis.com")
                .setMessageId(Integer.toString(1))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());
    }*/

    private void setLikeButton() {

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot.child(str_id).hasChild(mAuth.getCurrentUser().getUid())) {

                        mLikeButton.setImageResource(R.drawable.ic_thumb_up_red_24dp);

                    } else {

                        mLikeButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);

                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnReport) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailPostActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_report, null);
            Button mOK = (Button) mView.findViewById(R.id.buttonOK);
            Button mNO = (Button) mView.findViewById(R.id.buttonNo);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            mNO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Toast.makeText(DetailPostActivity.this, "Ga jadi Report ya", Toast.LENGTH_SHORT);
                }
            });
            mOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateReport();
                    dialog.dismiss();
                    finish();
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }


    private void loadComment() {
        listComments.clear();
        Constant.refPost.orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.child(str_id).getValue(Post.class);
                        try {
                            HashMap<String, Comment> comment = post.getComment();
                            for (Map.Entry<String, Comment> entry : comment.entrySet()) {
                                Comment commentModel = entry.getValue();
                                listComments.add(commentModel);
                            }

                            commentList.notifyDataSetChanged();
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = Constant.mAuth.getCurrentUser();
        if (user == null) {
            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailPostActivity.this, "Login terlebih dahulu untuk comment", Toast.LENGTH_SHORT).show();
                }
            });

            mRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailPostActivity.this, "Login terlebih dahulu untuk rating", Toast.LENGTH_SHORT).show();
                }
            });

            mLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailPostActivity.this, "Login terlebih dahulu untuk like", Toast.LENGTH_SHORT).show();;
                }
            });
        }
    }

    float ratingCount = 0;
    float ratingAvg = 0;
    float totalRating = 0;

    private void loadRating() {
        databaseRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    Rating rating = ds.getValue(Rating.class);
                    ratingAvg += rating.getRating();
                    ratingCount++;
                }

                totalRating = ratingAvg/ratingCount;
                avgRating.setRating(totalRating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateReport() {
        Constant.refPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post user = dataSnapshot.child(str_id).getValue(Post.class);
                String id = databaseRating.push().getKey();
                long timestamp = System.currentTimeMillis();
                try {
                    long report = user.getReport() + 1;

                    Constant.refPost.child(str_id).child("report").setValue(report);
                    Toast.makeText(DetailPostActivity.this, "Terima kasih atas laporan anda", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Constant.refPost.child(str_id).child("report").setValue(1);
                    Toast.makeText(DetailPostActivity.this, "Terima kasih atas laporan anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateLike() {
        Constant.refPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post user = dataSnapshot.child(str_id).getValue(Post.class);
                String id = databaseLike.push().getKey();
                long timestamp = System.currentTimeMillis();
                try {
                    long report = user.getLike() + 1;

                    Constant.refPost.child(str_id).child("like").setValue(report);
                    mLikeButton.setImageResource(R.drawable.ic_thumb_up_red_24dp);
                    Toast.makeText(DetailPostActivity.this, "Terima kasih atas like anda", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Constant.refPost.child(str_id).child("like").setValue(1);
                    Toast.makeText(DetailPostActivity.this, "Terima kasih atas like anda", Toast.LENGTH_SHORT).show();
                    mLikeButton.setImageResource(R.drawable.ic_thumb_up_red_24dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
