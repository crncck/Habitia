package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;


public class AddHabitActivity extends AppCompatActivity {

    Button button;
    EditText addName;
    EditText addDescription;
    ImageView addImage;
    TextView addHabitText;
    FotoGallery SelectedFoto;
    Boolean bool = false;

    String habitId;
    String habitName;
    String habitDescription;
    String habitImage;
    String habitDone = "false";

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        addHabitText = findViewById(R.id.addHabitText);
        addName = findViewById(R.id.addName);
        addDescription = findViewById(R.id.addDescription);
        addImage = findViewById(R.id.addImage);

        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        SelectedFoto = (FotoGallery) intent.getSerializableExtra("SelectedFoto");
        bool = intent.getBooleanExtra("bool", false);

        if (bool == true) {
            addImage.setImageResource(SelectedFoto.getPicId());
        }

    }


    @SuppressLint("ResourceAsColor")
    public void addHabit(View view){

        habitName = addName.getText().toString();
        habitDescription = addDescription.getText().toString();

        if (bool == false) {
            Toast.makeText(AddHabitActivity.this, "Please select an icon", Toast.LENGTH_LONG).show();
        } else if (habitName.isEmpty()) {
            addName.setError("Add name of the habit");
            addName.setBackgroundResource(R.drawable.error_style);
            addName.setTextColor(R.color.black);
        } else if (habitDescription.isEmpty()) {
            addDescription.setError("Add description of the habit");
            addDescription.setBackgroundResource(R.drawable.error_style);
            addDescription.setTextColor(R.color.black);
        } else {
            habitImage = String.valueOf(SelectedFoto.getPicId());
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
            DocumentReference documentReference2 = documentReference.collection("habits").document();
            Map<String, Object> habit = new HashMap<>();
            habit.put("name", habitName);
            habit.put("image_id", habitImage);
            habit.put("description", habitDescription);
            habit.put("done", habitDone);

            documentReference2.set(habit).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddHabitActivity.this, "Habit is created", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddHabitActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });



            Intent intent = new Intent(AddHabitActivity.this,HabitsActivity.class);
            startActivity(intent);
        }

    }


    public void addImage(View view) {


        Intent intent = new Intent(AddHabitActivity.this,FotoGalleryActivity.class);
        startActivity(intent);


    }
}