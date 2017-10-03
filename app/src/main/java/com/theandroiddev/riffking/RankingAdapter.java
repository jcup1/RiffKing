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

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private static final String TAG = "RepAdapter";
    protected Helper helper;
    Comment comment;
    private DatabaseReference databaseReference;
    private List<User> users;
    private Context context;
    private String currentUserId;

    RankingAdapter(Context context, List<User> users, DatabaseReference databaseReference, String currentUserId) {
        this.context = context;
        this.users = users;
        this.databaseReference = databaseReference;
        helper = new Helper(context);
        this.currentUserId = currentUserId;
        Log.d(TAG, "USERSTEST" + users.toString());


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_ranking, parent, false);
        final ViewHolder holder = new ViewHolder(v);

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        setDatabase(users.get(holder.getAdapterPosition()).getId(), position);

        setUser(holder.rankingUserTv, holder.rankingUserIv, holder.rankingRepTv, holder.getAdapterPosition());

        holder.rankingUserIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

        holder.rankingUserTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

    }

    private void setDatabase(final String userId, final int position) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void setUser(final TextView userTv, final ImageView userIv, final TextView repTv, final int position) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (currentUserId.equals(users.get(position).getId())) {
                    helper.highlightUser(userTv);
                }
                Log.d(TAG, "USERSTESTTT: " + position + " " + users.size());
                userTv.setText(dataSnapshot.child("users").child(users.get(position).getId()).child("name").getValue(String.class));
                repTv.setText(String.valueOf(dataSnapshot.child("users").child(users.get(position).getId()).child("reps").getValue(Integer.class)));
                Picasso.with(context).load(dataSnapshot.child("users")
                        .child(users.get(position).getId()).child("photoUrl").getValue(String.class))
                        .into(userIv);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void openProfileFragment(int position) {

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", users.get(position).getId());
        bundle.putString("CURRENT_USER_ID", currentUserId);
        profileFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((HomeActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView rankingUserTv, rankingRepTv;
        private final CircularImageView rankingUserIv;

        ViewHolder(View itemView) {
            super(itemView);

            rankingUserTv = (TextView) itemView.findViewById(R.id.ranking_user_tv);
            rankingRepTv = (TextView) itemView.findViewById(R.id.ranking_rep_tv);
            rankingUserIv = (CircularImageView) itemView.findViewById(R.id.ranking_user_iv);

        }


    }
}
