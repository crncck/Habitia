package com.example.habitapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

//connects fragmentlist with users arraylist -gizem-


public class MyRankingRecyclerViewAdapter extends RecyclerView.Adapter<MyRankingRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private RankingFragment.OnUserSelected listener;
    int selectedIndex;

    public MyRankingRecyclerViewAdapter(List<User> items, RankingFragment.OnUserSelected mListener) {
        mValues = items;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText((position+4)+ ". "+mValues.get(position).getName()+" "+mValues.get(position).getSurname());
        if (mValues.get(position).getProfileUrl()==null) {
            holder.mPicView.setImageResource(R.mipmap.user_icon);
        } else {
            holder.mPicView.setBackgroundResource(R.drawable.circle);
            Picasso.get().load(mValues.get(position).getProfileUrl()).into(holder.mPicView);
        }
        if (mValues.get(position).getDone_percent() == 0) {
            holder.mProgressBar.setProgress(0);
        } else {
            holder.mProgressBar.setProgress(mValues.get(position).getDone_percent());
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.userSelected(holder.mItem);
                    notifyItemChanged(selectedIndex);
                    selectedIndex = holder.getLayoutPosition();
                    notifyItemChanged(selectedIndex);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ImageView mPicView;
        public final ProgressBar mProgressBar;

        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mPicView = (ImageView)view.findViewById(R.id.pic);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}