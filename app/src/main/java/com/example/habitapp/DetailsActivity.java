package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private FirebaseFirestore firebaseFirestore;
    List<EventDay> events = new ArrayList<>();
    Habit habit;
    ImageView habitImage, deleteHabit;
    TextView habitName, habitDescription, habitTarget;
    EditText habitValue;
    String value, target, done_percent;
    FirebaseUser activeUser;
    DocumentReference currentHabit;
    Date date;
    LocalDate currentDate;
    MediaPlayer player;
    LayoutInflater layoutInflater;
    View layout;

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
        deleteHabit = findViewById(R.id.deleteHabit);
        CalendarView calendarView = findViewById(R.id.calendarView);

        layoutInflater = getLayoutInflater();
        layout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //It pulls and displays the habit information entered in AddHabitActivity.-gizem-
        Intent intent = getIntent();
        this.habit = (Habit) intent.getSerializableExtra("SelectedHabit");

        habitName.setText(habit.getName());
        habitImage.setImageResource(Integer.parseInt(habit.getPicId()));
        habitDescription.setText(habit.getDescription());
        target = habit.getTarget();
        value = habit.getValue();
        done_percent = habit.getDone_percent();
        habitTarget.setText("/" + target + " " + habit.getType());

        habitValue.setText(value);


        activeUser = FirebaseAuth.getInstance().getCurrentUser();
        currentHabit = firebaseFirestore.collection("users").document(activeUser.getUid()).collection("habits").document(habit.getId());
        CollectionReference collectionReference = currentHabit.collection("dates");
        DocumentReference documentReference = collectionReference.document();

        // Delete habit if user clicks on the the trash icon (Ceren)
        deleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setTitle(habitName.getText().toString() + " habit will be deleted.");
                builder.setMessage("Are you sure to delete?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentHabit.delete();
                        Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                        startActivity(intent);
                    }
                });

                // Set the alert dialog no button click listener
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Get the date information of the habit and show it on the calendar (Ceren)
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

        // Listener to track user while editing on edit text (Ceren)
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
                             // Get the value user entered and calculate how much of the habit is done
                             int enteredValue = Integer.parseInt(habitValue.getText().toString());
                             float tar = Integer.parseInt(target);
                             float percent = (enteredValue / tar) * 100;
                             done_percent = String.valueOf((int) percent);
                             currentHabit.update("done_percent", done_percent);

                             // If the percentage is equal to 100 or greater than it, show confetti animation, save date of the day to Firebase, and turn user to habit activity
                             if (Integer.parseInt(done_percent) == 100 || Integer.parseInt(done_percent) > 100) {
                                 currentHabit.update("done", "true");
                                 TextView text = (TextView) layout.findViewById(R.id.text);
                                 text.setText("Congratulations!");
                                 Toast toast = new Toast(getApplicationContext());
                                 toast.setGravity(Gravity.CENTER_VERTICAL, 0, 280);
                                 toast.setDuration(Toast.LENGTH_LONG);
                                 toast.setView(layout);
                                 toast.show();
                                 player=MediaPlayer.create(DetailsActivity.this,R.raw.sound);
                                 player.start();

                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                     currentDate = LocalDate.now();
                                     Map<String, Object> dates = new HashMap<>();
                                     dates.put("date", String.valueOf(currentDate));

                                     documentReference.set(dates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {

                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                                         startActivity(intent);
                                     }
                                 }, 3000);

                             } else {
                                 // The user entered a value less than target
                                 currentHabit.update("done", "false");
                                 TextView text = (TextView) layout.findViewById(R.id.text);
                                 text.setText("You can still do this :)");
                                 Toast toast = new Toast(getApplicationContext());
                                 toast.setGravity(Gravity.CENTER_VERTICAL, 0, 280);
                                 toast.setDuration(Toast.LENGTH_SHORT);
                                 toast.setView(layout);
                                 toast.show();

                                 new Handler().postDelayed(new Runnable() {
                                     @Override
                                     public void run() {
                                         Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
                                         startActivity(intent);
                                     }
                                 }, 1500);
                             }
                             currentHabit.update("value", habitValue.getText().toString());

                             return true;
                         }
                     }
                 }
                 return false;
             }
         }
        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailsActivity.this, HabitsActivity.class);
        startActivity(intent);
    }
}