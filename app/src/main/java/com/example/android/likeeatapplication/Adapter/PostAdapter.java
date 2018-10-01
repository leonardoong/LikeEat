package com.example.android.likeeatapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.likeeatapplication.DetailPostActivity;
import com.example.android.likeeatapplication.Model.Post;
import com.example.android.likeeatapplication.R;

import java.util.ArrayList;
import java.util.List;

import fr.tvbarthel.intentshare.IntentShare;

/**
 * Created by Leonardo on 4/6/2018.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    Context context;
    List<Post> postList;



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername;
        public TextView mTitlePost;
        //public TextView mPost;
        public ImageView mImagePost;
        public CardView cardViewPost;
        public ImageView img_share;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsername= itemView.findViewById(R.id.tv_username);
            mTitlePost = itemView.findViewById(R.id.tv_title);
            //mPost = itemView.findViewById(R.id.tv_post);
            mImagePost=itemView.findViewById(R.id.img_post);
            cardViewPost= itemView.findViewById(R.id.cardViewPost);
            img_share = itemView.findViewById(R.id.img_share);
        }
    }

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_post,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        final Post post= postList.get(position);

        holder.mUsername.setText(post.getUsername());

        Glide.with(context)
                .load(post.getImagePost())
                .into(holder.mImagePost);

        holder.mTitlePost.setText(post.getTitlePost());

        //holder.mPost.setText(post.getPost());

        holder.cardViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.putExtra("id",post.getId());
                intent.putExtra("Username",post.getUsername());
                intent.putExtra("image",post.getImagePost());
                intent.putExtra("Title",post.getTitlePost());
                intent.putExtra("Post",post.getPost());
                intent.putExtra("Alamat", post.getAlamat());
                context.startActivity(intent);
            }
        });

        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentShare.with(context)
                        .chooserTitle("Select a sharing target : ")
                        .text(post.getImagePost())
                        .deliver();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
