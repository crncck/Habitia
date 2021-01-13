package com.example.habitapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitapp.dummy.DummyContent;

import java.util.ArrayList;

public class RankingFragment extends Fragment {

    private static final String ARG_USERS = "users";
    private OnRankingListInteractionListener mListener;
    private ArrayList<User> users;
    MyRankingRecyclerViewAdapter mAdapter;


    @SuppressWarnings("unused")
    public static RankingFragment newInstance(int columnCount) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USERS, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            users = (ArrayList<User>)getArguments().getSerializable(ARG_USERS);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_franking_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            mAdapter = new MyRankingRecyclerViewAdapter(users, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof OnRankingListInteractionListener) {
            mListener = (OnRankingListInteractionListener) context;
        } else {    
            throw new RuntimeException(context.toString()
                    + " must implement OnNoteListInteractionListener");
        }


        super.onAttach(context);
    }

    public void setDepartments(ArrayList<User> users) {
        this.users.clear();
        this.users.addAll(users);
        mAdapter.notifyDataSetChanged();
    }



    public interface OnRankingListInteractionListener {
        void onDepartmentSelected(User user);
    }

}