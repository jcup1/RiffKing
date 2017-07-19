package com.example.grazyna.riffking;

import android.content.Context;
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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jakub on 18.07.17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<Thread> threads;
    private Context context;

    public CustomAdapter(Context context, ArrayList<Thread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item,
                parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.getTextView().setText(threads.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
                openThread(position);
            }
        });

    }

    private void openThread(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("position", String.valueOf(position));

        android.support.v4.app.Fragment threadFragment = new ThreadFragment();
        threadFragment.setArguments(bundle);
        try {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_home, threadFragment).addToBackStack("state2");
            transaction.commit();

        } catch (ClassCastException e) {
            Log.e(TAG, "cant get fragment manager");
        }
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView videoImg;

        public ViewHolder(View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked");
                }
            });
            textView = (TextView) itemView.findViewById(R.id.textView);
            videoImg = (ImageView) itemView.findViewById(R.id.imageView);
        }

        public TextView getTextView() {
            return textView;
        }
    }


}
