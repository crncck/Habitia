package com.example.habitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;




public class FotoGalleryActivity extends AppCompatActivity {

    final public  List<FotoGallery> fotogallerylist = new ArrayList<>();
    TextView galleryText;
    GridView gridView;
    boolean bool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_gallery);

        galleryText = findViewById(R.id.galleryText);
        gridView = findViewById(R.id.gridview);


        fotogallerylist.add(new FotoGallery(R.mipmap.yoga_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.work_laptop_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.weightlifting_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.trekking_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.training_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.trainers_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.track_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.to_do_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.tennis_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.tea_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.swim_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.egg_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.study_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.search_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.running_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.pill_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.permanent_job_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.new_job_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.muscle_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.healthy_food_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.nature_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.run_exercise_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.laptop_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.dumbbell_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.write_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.chess_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.chef_hat_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.books_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.apple_foreground));
        fotogallerylist.add(new FotoGallery(R.mipmap.beach_foreground));


        FotoGalleryAdapter adapter = new FotoGalleryAdapter(FotoGalleryActivity.this,fotogallerylist);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FotoGalleryActivity.this,AddHabitActivity.class);
                intent.putExtra("SelectedFoto",fotogallerylist.get(i));
                intent.putExtra("bool", bool);
                startActivity(intent);
            }
        });
    }

}