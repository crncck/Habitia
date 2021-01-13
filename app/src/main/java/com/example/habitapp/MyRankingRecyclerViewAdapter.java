package com.example.habitapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MyRankingRecyclerViewAdapter extends RecyclerView.Adapter<MyRankingRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final RankingFragment.OnRankingListInteractionListener mListener;

    public MyRankingRecyclerViewAdapter(List<User> users, RankingFragment.OnRankingListInteractionListener mListener) {
        this.mValues = users;
        this.mListener = mListener;
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
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mPicView.setImageResource(mValues.get(position).getProPicId());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onDepartmentSelected(holder.mItem);
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

        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mPicView = (ImageView)view.findViewById(R.id.pic);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}