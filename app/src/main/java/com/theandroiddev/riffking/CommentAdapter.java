package com.theandroiddev.riffking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jakub on 29.09.17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private static final String TAG = "RepAdapter";
    protected Helper helper;
    DatabaseReference databaseReference;
    Comment comment;
    private List<Comment> comments;
    private Context context;
    private String currentUserId;
    private boolean[] liked;

    public CommentAdapter(Context context, List<Comment> comments, DatabaseReference databaseReference, String currentUserId) {
        this.context = context;
        this.comments = comments;
        this.databaseReference = databaseReference;
        liked = new boolean[100];
        helper = new Helper(context);
        this.currentUserId = currentUserId;

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
                holder.likeIv, position);
        setUserName(holder.userTv, holder.userIv, holder.getAdapterPosition());
        holder.contentTv.setText(comments.get(position).getContent());
        holder.dateTv.setText(comments.get(position).getDate());
        holder.likesTv.setText(String.valueOf(comments.get(position).getLikes()));

        if (!comments.get(position).getUserId().equals(currentUserId)) {
            holder.likeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (holder.getAdapterPosition() != -1) {
                        handleLike(comments.get(holder.getAdapterPosition()).getThreadId(), comments.get(holder.getAdapterPosition()).getId(),
                                holder.likeIv, holder.getAdapterPosition());

                }

                }
            });
        } else {
            helper.setLikeInactive(holder.likeIv);
            Log.d(TAG, "onBindViewHolderLike: inactive");
        }


        holder.userIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

        holder.userTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

    }

    private void handleLike(String threadId, final String commentId, final ImageView likeIv, int position) {
        Log.e(TAG, "handleLike: thre" + threadId + "comm" + commentId);
        if (!liked[position]) {
            helper.transaction(databaseReference.child("comments").child(threadId).child(commentId).child("likes"), 1);
            helper.transaction(databaseReference.child("users").child(comments.get(position).getUserId()).child("likes"), 1);
            databaseReference.child("commentLikes").child(currentUserId).child(commentId).setValue(true);

        } else {
            helper.transaction(databaseReference.child("comments").child(threadId).child(commentId).child("likes"), -1);
            helper.transaction(databaseReference.child("users").child(comments.get(position).getUserId()).child("likes"), -1);
            databaseReference.child("commentLikes").child(currentUserId).child(commentId).removeValue();

        }
    }

    private void setDatabase(String threadId, final String commentId, final ImageView likeIv, final int position) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!currentUserId.equals(comments.get(position).getUserId())) {

                    if (dataSnapshot.child("commentLikes").child(currentUserId).child(commentId).getValue(Boolean.class) != null) {
                        Log.d(TAG, "onDataChangeComment: " + "already liked");
                        liked[position] = true;
                        helper.setLiked(likeIv);

                    } else {
                        Log.d(TAG, "onDataChangeComment: not liked!");
                        liked[position] = false;
                        helper.setUnliked(likeIv);

                    }

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setUserName(final TextView userTv, final ImageView userIv, final int position) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Change color of creator's comment
                if (position >= 0) {

                    if (dataSnapshot.child("threads").child(comments.get(position).getThreadId()).child("userId").getValue(String.class) != null) {
                        if (dataSnapshot.child("threads").child(comments.get(position).getThreadId()).child("userId").getValue(String.class).equals(comments.get(position).getUserId())) {
                            helper.highlightUser(userTv);
                        }
                    }

                    userTv.setText(dataSnapshot.child("users").child(comments.get(position).getUserId()).child("name").getValue(String.class));
                    Picasso.with(context).load(dataSnapshot.child("users")
                            .child(comments.get(position).getUserId()).child("photoUrl").getValue(String.class))
                            .into(userIv);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void openProfileFragment(int position) {

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", comments.get(position).getUserId());
        bundle.putString("CURRENT_USER_ID", currentUserId); //Two same only in this case
        profileFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((HomeActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userTv, contentTv, dateTv, likesTv;
        private final CircularImageView userIv;
        private final ImageView likeIv;

        public ViewHolder(View itemView) {
            super(itemView);

            userTv = (TextView) itemView.findViewById(R.id.comment_user_tv);
            contentTv = (TextView) itemView.findViewById(R.id.comment_content_tv);
            dateTv = (TextView) itemView.findViewById(R.id.comment_date_tv);
            likesTv = (TextView) itemView.findViewById(R.id.comment_likes_number_tv);

            userIv = (CircularImageView) itemView.findViewById(R.id.comment_user_iv);
            likeIv = (ImageView) itemView.findViewById(R.id.comment_like_iv);

        }


    }
}
