package com.example.habitapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    ImageView habitImage;
    TextView habitName;
    TextView habitDescription;
    Button checkBox;
    String doneCheck = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        habitImage = findViewById(R.id.habitImage);
        habitName = findViewById(R.id.habitName);
        habitDescription = findViewById(R.id.habitDescription);
        checkBox = findViewById(R.id.checkBox);

        //Gridviewdeki verileri detail activity içine aktarıyor
        Intent intent = getIntent();
        Habit SelectedHabit = (Habit) intent.getSerializableExtra("SelectedHabit");

        habitName.setText(SelectedHabit.getName());
        habitImage.setImageResource(Integer.parseInt(SelectedHabit.getPicId()));
        habitDescription.setText(SelectedHabit.getDescription());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneCheck = "true";
                Toast.makeText(DetailsActivity.this,"Congratulations!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                intent.putExtra("doneCheck", doneCheck);
                startActivity(intent);
            }
        });


    }






}