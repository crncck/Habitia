package com.example.habitapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class DetailsActivity extends AppCompatActivity {

    Habit habit;
    ImageView habitImage;
    TextView habitName, habitDescription, habitTarget;
    EditText habitValue;
    String value;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        habitImage = findViewById(R.id.habitImage);
        habitName = findViewById(R.id.habitName);
        habitDescription = findViewById(R.id.habitDescription);
        habitValue = findViewById(R.id.addValue);
        habitTarget = findViewById(R.id.targetText);
        checkBox = findViewById(R.id.checkBox);
        final KonfettiView konfettiView = findViewById(R.id.konfettiView);

        Intent intent = getIntent();
        this.habit = (Habit) intent.getSerializableExtra("SelectedHabit");

        habitName.setText(habit.getName());
        habitImage.setImageResource(Integer.parseInt(habit.getPicId()));
        habitDescription.setText(habit.getDescription());
        habitTarget.setText("/"+ habit.getTarget() + " " + habit.getType());
        value = habit.getValue();
        checkBox.setChecked(habit.isDone());

        if (value.equals("null")) {
            habitValue.setText("Value");
        } else {
            habitValue.setText(value);
        }




        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Konfetti animation
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.BLUE)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .addSizes(new Size(12, 5f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);

                Toast.makeText(DetailsActivity.this,"Congratulations!",Toast.LENGTH_SHORT).show();

                FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference currentHabit = FirebaseFirestore.getInstance().collection("users").document(activeUser.getUid()).collection("habits").document(habit.getId());

                currentHabit.update("done", "true");
                currentHabit.update("value", habitValue.getText().toString());


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }
        });
    }
}