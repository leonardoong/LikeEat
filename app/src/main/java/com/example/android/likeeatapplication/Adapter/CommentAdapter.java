package com.example.android.likeeatapplication.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.likeeatapplication.Config.Constant;
import com.example.android.likeeatapplication.Model.Comment;
import com.example.android.likeeatapplication.Model.User;
import com.example.android.likeeatapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by Leonardo on 4/21/2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<Comment> commentList;
//    RoundedBitmapDrawable rounded;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mUsername;
        TextView mComment;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_avatar);
            mUsername = itemView.findViewById(R.id.username);
            mComment = itemView.findViewById(R.id.comment);
        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        holder.mUsername.setText(comment.getUsername());
        holder.mComment.setText(comment.getComment());
        Picasso.get().load(comment.getProfile_pic()).placeholder(R.drawable.ic_profile).into(holder.imageView);
    }

//    private void setupImageRounded() {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);
//        rounded = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
//        rounded.setCircular(true);
//
//    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

}