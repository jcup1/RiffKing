package com.theandroiddev.riffking;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;

/**
 * Created by jakub on 18.07.17.
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder>
        implements AsyncResponse {
    private static final String TAG = "ThreadAdapter";
    DatabaseReference mDatabase;
    User user;
    private ArrayList<Thread> threads;
    private Context context;


    public ThreadAdapter(Context context, ArrayList<Thread> threads, DatabaseReference mDatabase) {
        this.context = context;
        this.threads = threads;
        this.mDatabase = mDatabase;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_thread,
                parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.getSingleTitle().setText(threads.get(position).getTitle());
        setThreadCreator((position), holder.nickTv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
                openThread(holder.getAdapterPosition());
            }
        });

        holder.statsTv.setText(initStats(threads.get(position)));
        holder.dateTv.setText(initDate(threads.get(position)));


        new GetThumbnail(threads.get(position).getVideoUrl(), holder.singleThumbnail).execute();

    }

    private String initStats(Thread thread) {

        return thread.getViews() + " views " + thread.getLikes() + " likes";

    }

    private String initDate(Thread thread) {

        return thread.getDate();

    }


    private void openThread(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("threadId", String.valueOf(threads.get(position).getId()));

        android.support.v4.app.Fragment threadFragment = new ThreadFragment();
        threadFragment.setArguments(bundle);
        try {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_home, threadFragment).addToBackStack("stack1");
            transaction.commit();

        } catch (ClassCastException e) {
            Log.e(TAG, "cant get fragment manager");
        }
    }


    @Override
    public int getItemCount() {
        return threads.size();
    }

    @Override
    public void processFinish(Bitmap output) {


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setThreadCreator(final int position, final TextView tv) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("users").child(threads.get(position).getUserId()).getValue(User.class);
                tv.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //TODO HANDLE DATABASE JOINING
        // mDatabase
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView singleTitle, statsTv, nickTv;
        private final TextView dateTv;
        private final ImageView singleThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            singleTitle = (TextView) itemView.findViewById(R.id.text_view);
            nickTv = (TextView) itemView.findViewById(R.id.nick_tv);
            singleThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_img);
            statsTv = (TextView) itemView.findViewById(R.id.stats_tv);
            dateTv = (TextView) itemView.findViewById(R.id.date_tv);

        }

        public TextView getSingleTitle() {
            return singleTitle;
        }
    }


}
