package com.theandroiddev.riffking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jakub on 01.10.17.
 */

public class RepAdapter extends RecyclerView.Adapter<RepAdapter.ViewHolder> {
    private static final String TAG = "RepAdapter";
    DatabaseReference databaseReference;
    private List<Rep> reps;
    private Context context;
    private String currentUserId;
    private Rep rep;

    public RepAdapter(Context context, List<Rep> reps, DatabaseReference databaseReference, String currentUserId) {
        this.context = context;
        this.reps = reps;
        this.databaseReference = databaseReference;
        this.currentUserId = currentUserId;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_rep, parent, false);
        final ViewHolder holder = new ViewHolder(v);

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        setDatabase(position);
        setUserName(holder.repUserTv, holder.repUserIv, position);
        holder.repTitleTv.setText(reps.get(position).getTitle());
        holder.repDateTv.setText(reps.get(position).getDate());
        holder.repTv.setText(String.valueOf(reps.get(position).getRep()));


        holder.repUserIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

        holder.repUserTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != -1)
                    openProfileFragment(holder.getAdapterPosition());
            }
        });

    }

    private void setDatabase(final int position) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("reps").child(currentUserId).child(reps.get(position).getUserId()).getValue(Rep.class) != null) {
                    rep = dataSnapshot.child("reps").child(currentUserId).child(reps.get(position).getUserId()).getValue(Rep.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return reps.size();
    }

    public void setUserName(final TextView repUserTv, final CircularImageView repUserIv, final int position) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (position >= 0) {

                    repUserTv.setText(dataSnapshot.child("users").child(reps.get(position).getUserId()).child("name").getValue(String.class));
                    Picasso.with(context).load(dataSnapshot.child("users")
                            .child(reps.get(position).getUserId()).child("photoUrl").getValue(String.class))
                            .into(repUserIv);
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
        bundle.putString("USER_ID", reps.get(position).getUserId());
        bundle.putString("CURRENT_USER_ID", currentUserId); //Two same only in this case
        profileFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((HomeActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView repUserTv, repTitleTv, repDateTv, repTv;
        private final CircularImageView repUserIv;

        public ViewHolder(View itemView) {
            super(itemView);

            repUserTv = (TextView) itemView.findViewById(R.id.rep_user_tv);
            repTitleTv = (TextView) itemView.findViewById(R.id.rep_title_tv);
            repDateTv = (TextView) itemView.findViewById(R.id.rep_date_tv);
            repTv = (TextView) itemView.findViewById(R.id.rep_tv);
            repUserIv = (CircularImageView) itemView.findViewById(R.id.rep_user_iv);

        }


    }
}
