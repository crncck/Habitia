package com.example.habitapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class HabitsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Habit> habits;

    public HabitsAdapter(Activity activity,List<Habit> habits){
        mInflater =  (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.habits= habits;
    }

    @Override
    public int getCount() {
        return habits.size();
    }

    @Override
    public Object getItem(int i) {
        return habits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;
        rowView = mInflater.inflate(R.layout.gridrow,null);
        TextView textView = (TextView)rowView.findViewById(R.id.customtextview);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.customimageview);
        ProgressBar progressBar = (ProgressBar)rowView.findViewById(R.id.progress_bar);

        Habit habit = habits.get(i);

        textView.setText(habit.getName());
        imageView.setImageResource(Integer.parseInt(habit.getPicId()));
        progressBar.setProgress(Integer.parseInt(habit.getDone_percent()));

        return rowView;


    }
}