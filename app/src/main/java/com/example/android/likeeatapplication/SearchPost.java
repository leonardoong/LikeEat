package com.example.android.likeeatapplication;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.likeeatapplication.Adapter.PostAdapter;
import com.example.android.likeeatapplication.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchPost extends AppCompatActivity {

    private EditText etCari;
    private RecyclerView rvFood;
    private ArrayList<Post> postList;
    private PostAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);

        etCari = (EditText) findViewById(R.id.etCari);
        rvFood = (RecyclerView) findViewById(R.id.rvFood);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        postList = new ArrayList<>();
        LinearLayoutManager llManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFood.setLayoutManager(llManager);
        mAdapter = new PostAdapter(this, postList);
        rvFood.setAdapter(mAdapter);


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFood(etCari.getText().toString());
            }
        });

        etCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadFood(etCari.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadFood(final String filter) {
        postList.clear();
        swipeRefresh.setRefreshing(true);
        DatabaseReference refPost = FirebaseDatabase.getInstance().getReference("Post");
        refPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post model = ds.getValue(Post.class);
                    if(model.getTitlePost().contains(filter)) {
                        postList.add(model);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }
}
