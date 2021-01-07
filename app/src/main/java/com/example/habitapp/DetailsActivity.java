package com.example.habitapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import java.util.Date;
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
    String value, target;
    String done_percent;
    CheckBox checkBox;
    FirebaseUser activeUser;
    DocumentReference currentHabit;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        date = new Date();
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
        target = habit.getTarget();
        value = habit.getValue();
        done_percent = habit.getDone_percent();
        habitTarget.setText("/" + target + " " + habit.getType());
        checkBox.setChecked(habit.isDone());
        checkBox.setClickable(false);

        if (value.equals("null")) {
            habitValue.setHint("Value");
        } else {
            habitValue.setText(value);
        }

        activeUser = FirebaseAuth.getInstance().getCurrentUser();
        currentHabit = FirebaseFirestore.getInstance().collection("users").document(activeUser.getUid()).collection("habits").document(habit.getId());

        // Reset habit values next day
        if (date.getHours() == 0) {
            done_percent = "0";
            value = "null";
            currentHabit.update("done", "false");
            currentHabit.update("value", value);
            currentHabit.update("done_percent", done_percent);
        }

        habitValue.setOnEditorActionListener(new EditText.OnEditorActionListener() {
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                         event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                     if (event == null || !event.isShiftPressed()) {
                         InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                         imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                         // The user is done with typing
                         int enteredValue = Integer.parseInt(habitValue.getText().toString());
                         float tar = Integer.parseInt(target);
                         float percent = (enteredValue / tar) * 100;
                         done_percent = String.valueOf((int) percent);

                         if (Integer.parseInt(done_percent) == 100 || Integer.parseInt(done_percent) > 100) {
                             currentHabit.update("done", "true");
                             Toast.makeText(DetailsActivity.this, "Congratulations!", Toast.LENGTH_SHORT).show();
                             checkBox.setChecked(habit.isDone());

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

                             new Handler().postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(intent);
                                 }
                             }, 3000);

                         } else {
                             currentHabit.update("done", "false");
                             checkBox.setChecked(habit.isDone());

                             new Handler().postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     startActivity(intent);
                                 }
                             }, 1500);
                         }
                         currentHabit.update("value", habitValue.getText().toString());
                         currentHabit.update("done_percent", done_percent);

                         return true;
                     }
                 }
                 return false;
             }
         }
        );

    }

}