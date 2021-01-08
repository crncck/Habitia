package com.example.habitapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FotoGalleryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<FotoGallery> fotoGalleries;

    public FotoGalleryAdapter(Activity activity, List<FotoGallery> fotoGalleries){
        mInflater =  (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fotoGalleries= fotoGalleries;
    }

    @Override
    public int getCount() {
        return fotoGalleries.size();
    }

    @Override
    public Object getItem(int i) {
        return fotoGalleries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;
        rowView = mInflater.inflate(R.layout.fotogallery_row,null);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.fotogallery_row);

        FotoGallery fotoGallery = fotoGalleries.get(i);

        imageView.setImageResource(fotoGallery.getPicId());

        return rowView;
    }
}
