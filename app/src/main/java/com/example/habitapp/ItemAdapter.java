package com.example.habitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<TypeItem> {

    public ItemAdapter(Context context, ArrayList<TypeItem> typeList) {
        super(context, 0, typeList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.type_spinner_row, parent, false);
        }
        ImageView imageViewType = convertView.findViewById(R.id.image_view_type);
        TextView textViewType = convertView.findViewById(R.id.text_view_type);
        TypeItem currentItem = getItem(position);
        if (currentItem != null) {
            imageViewType.setImageResource(currentItem.getTypeIcon());
            textViewType.setText(currentItem.getTypeName());
        }
        return convertView;
    }

}
