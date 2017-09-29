package com.theandroiddev.riffking;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jakub on 29.09.17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private static final String TAG = "CommentAdapter";
    DatabaseReference databaseReference;
    Comment comment;
    private List<Comment> comments;
    private Context context;
    private User user;
    private boolean[] liked;

    public CommentAdapter(Context context, List<Comment> comments, DatabaseReference databaseReference) {
        this.context = context;
        this.comments = comments;
        this.databaseReference = databaseReference;
        liked = new boolean[100];

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment, parent, false);
        final ViewHolder holder = new ViewHolder(v);

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        setDatabase(comments.get(holder.getAdapterPosition()).getThreadId(), comments.get(holder.getAdapterPosition()).getId(),
                comments.get(holder.getAdapterPosition()).getUserId(), holder.likeIv, position);
        setUserName(holder.userTv, holder.userIv, holder.getAdapterPosition());
        holder.contentTv.setText(comments.get(position).getContent());
        holder.dateTv.setText(comments.get(position).getDate());
        holder.likesTv.setText(String.valueOf(comments.get(position).getLikes()))
        ;
        holder.likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position != -1)
                    handleLike(comments.get(holder.getAdapterPosition()).getThreadId(), comments.get(holder.getAdapterPosition()).getId(),
                            comments.get(holder.getAdapterPosition()).getUserId(), holder.likeIv, holder.getAdapterPosition());

            }
        });

    }

    private void handleLike(String threadId, final String commentId, final String userId, final ImageView likeIv, int position) {
        if (!liked[position]) {
            addCommentLike(threadId, commentId, userId, likeIv);
            databaseReference.child("commentLikes").child(userId).child(commentId).setValue(true);

        } else {
            removeCommentLike(threadId, commentId, userId, likeIv);
            databaseReference.child("commentLikes").child(userId).child(commentId).removeValue();
        }
    }

    private void setDatabase(String threadId, final String commentId, final String userId, final ImageView likeIv, final int position) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("commentLikes").child(userId).child(commentId).getValue(Boolean.class) != null) {
                    Log.d(TAG, "onDataChangeComment: " + "already liked");
                    liked[position] = true;
                    likeIv.setColorFilter(Color.BLUE);

                } else {
                    Log.d(TAG, "onDataChangeComment: not liked!");
                    liked[position] = false;
                    likeIv.setColorFilter(Color.BLACK);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //TODO ADD COMMENT LIKE
    private void addCommentLike(String threadId, final String commentId, final String userId, final ImageView likeIv) {
        //TODO ATTACH TO ONCLICK AND CHECK IS USER LOGGED IN...

        databaseReference.child("comments").child(threadId).child(commentId).child("likes").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int likes = mutableData.getValue(Integer.class);

                likes++;

                // Set value and report transaction success
                mutableData.setValue(likes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    //TODO ADD COMMENT LIKE
    private void removeCommentLike(String threadId, final String commentId, final String userId, final ImageView likeIv) {
        //TODO ATTACH TO ONCLICK AND CHECK IS USER LOGGED IN...


        databaseReference.child("comments").child(threadId).child(commentId).child("likes").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int likes = mutableData.getValue(Integer.class);

                likes--;

                // Set value and report transaction success
                mutableData.setValue(likes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setUserName(final TextView userTv, final ImageView userIv, final int adapterPosition) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTv.setText(dataSnapshot.child("users").child(comments.get(adapterPosition).getUserId()).child("name").getValue(String.class));
                Picasso.with(context).load(dataSnapshot.child("users")
                        .child(comments.get(adapterPosition).getUserId()).child("photoUrl").getValue(String.class))
                        .into(userIv);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userTv, contentTv, dateTv, likesTv;
        private final ImageView userIv, likeIv;

        public ViewHolder(View itemView) {
            super(itemView);

            userTv = (TextView) itemView.findViewById(R.id.comment_user_tv);
            contentTv = (TextView) itemView.findViewById(R.id.comment_content_tv);
            dateTv = (TextView) itemView.findViewById(R.id.comment_date_tv);
            likesTv = (TextView) itemView.findViewById(R.id.comment_likes_number_tv);

            userIv = (ImageView) itemView.findViewById(R.id.comment_user_iv);
            likeIv = (ImageView) itemView.findViewById(R.id.comment_like_iv);

        }


    }
}
