package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.applandeo.materialcalendarview.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    FirebaseUser activeUser;
    DocumentReference currentHabit;
    Date date;
    LocalDate currentDate;
    private FirebaseFirestore firebaseFirestore;
    List<EventDay> events = new ArrayList<>();

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
        CalendarView calendarView = findViewById(R.id.calendarView);

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        firebaseFirestore = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        this.habit = (Habit) intent.getSerializableExtra("SelectedHabit");

        habitName.setText(habit.getName());
        habitImage.setImageResource(Integer.parseInt(habit.getPicId()));
        habitDescription.setText(habit.getDescription());
        target = habit.getTarget();
        value = habit.getValue();
        done_percent = habit.getDone_percent();
        habitTarget.setText("/" + target + " " + habit.getType());

        if (value.equals("null")) {
            habitValue.setHint("Value");
        } else {
            habitValue.setText(value);
        }

        activeUser = FirebaseAuth.getInstance().getCurrentUser();
        currentHabit = firebaseFirestore.collection("users").document(activeUser.getUid()).collection("habits").document(habit.getId());
        CollectionReference collectionReference = currentHabit.collection("dates");
        DocumentReference documentReference = collectionReference.document();

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Calendar calendar = Calendar.getInstance();
                        Map<String, Object> data = snapshot.getData();
                        String dateValue = (String) data.get("date");
                        String[] items1 = dateValue.split("-");
                        int year= Integer.parseInt(items1[0]);
                        int month=Integer.parseInt(items1[1]) - 1;
                        int day=Integer.parseInt(items1[2]);

                        calendar.set(year,month,day);
                        events.add(new EventDay(calendar, R.drawable.calendar_icons));
                    }
                    calendarView.setEvents(events);
                }
            }
        });

        // Reset habit values next day
        if (date.getHours() == 0) {
            done_percent = "0";
            value = "null";
            currentHabit.update("done", "false");
            currentHabit.update("value", value);
            currentHabit.update("done_percent", done_percent);
        }


        habitValue.setOnEditorActionListener(new EditText.OnEditorActionListener() {
             @SuppressLint("ResourceAsColor")
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                         event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                     if (event == null || !event.isShiftPressed()) {
                         InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                         imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                         // The user is done with typing
                         if (habitValue.getText().toString().isEmpty()) {
                             habitValue.setError("Please enter a number");
                             habitValue.setBackgroundResource(R.drawable.error_style);
                             habitValue.setTextColor(R.color.black);
                         } else {
                             int enteredValue = Integer.parseInt(habitValue.getText().toString());
                             float tar = Integer.parseInt(target);
                             float percent = (enteredValue / tar) * 100;
                             done_percent = String.valueOf((int) percent);

                             if (Integer.parseInt(done_percent) == 100 || Integer.parseInt(done_percent) > 100) {
                                 currentHabit.update("done", "true");
                                 Toast.makeText(DetailsActivity.this, "Congratulations!", Toast.LENGTH_SHORT).show();

                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                     currentDate = LocalDate.now();
                                     collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                         @Override
                                         public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                             if (error != null) {
                                                 Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                             }

                                             if (value != null) {
                                                 for (DocumentSnapshot snapshot : value.getDocuments()) {
                                                     Map<String, Object> data = snapshot.getData();
                                                     String dateValue = (String) data.get("date");
                                                     if (!dateValue.equals(String.valueOf(currentDate))) {
                                                         Map<String, Object> dates = new HashMap<>();
                                                         dates.put("date", String.valueOf(currentDate));

                                                         documentReference.set(dates).addOnFailureListener(new OnFailureListener() {
                                                             @Override
                                                             public void onFailure(@NonNull Exception e) {
                                                                 Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                             }
                                                         });
                                                     }
                                                 }
                                             }
                                         }
                                     });
                                 }

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
                                 Toast.makeText(DetailsActivity.this, "You can still do this :)", Toast.LENGTH_SHORT).show();

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
                 }
                 return false;
             }
         }
        );

    }

}