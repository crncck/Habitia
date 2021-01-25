package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddHabitActivity extends AppCompatActivity {

    Button button;
    EditText addName, addDescription, addTarget;
    ImageView addImage;
    TextView addHabitText;
    FotoGallery SelectedFoto;
    Boolean bool = false;
    String habitName, habitDescription, habitImage, habitTarget;
    String habitDone = "false", habitValue = "0", habitType = "count", habitDonePercent = "0";
    LayoutInflater layoutInflater;
    View layout;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private String userID;
    private ArrayList<TypeItem> mTypeList;
    private ItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        initList();

        addHabitText = findViewById(R.id.addHabitText);
        addName = findViewById(R.id.addName);
        addDescription = findViewById(R.id.addDescription);
        addImage = findViewById(R.id.addImage);
        addTarget = findViewById(R.id.addTarget);
        button = findViewById(R.id.addButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        layoutInflater = getLayoutInflater();
        layout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        //pulls the habit icon chosen by the user and checks if the user has selected icon with bool.-gizem-
        Intent intent = getIntent();
        SelectedFoto = (FotoGallery) intent.getSerializableExtra("SelectedFoto");
        bool = intent.getBooleanExtra("bool", false);

        if (bool == true) {
            addImage.setImageResource(SelectedFoto.getPicId());
        }

        addTarget.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        // The user is done with typing
                        return true;
                    }
                }
                return false;
            }
        });

        Spinner spinnerTypes = findViewById(R.id.typeSpinner);
        mAdapter = new ItemAdapter(this, mTypeList);
        spinnerTypes.setAdapter(mAdapter);
        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypeItem clickedItem = (TypeItem) parent.getItemAtPosition(position);
                habitType = clickedItem.getTypeName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initList() {
        mTypeList = new ArrayList<>();
        mTypeList.add(new TypeItem("count", R.mipmap.count_icon_foreground));
        mTypeList.add(new TypeItem("steps", R.mipmap.steps_icon_foreground));
        mTypeList.add(new TypeItem("m", R.mipmap.meter_icon_foreground));
        mTypeList.add(new TypeItem("sec", R.mipmap.second_icon_foreground));
        mTypeList.add(new TypeItem("min", R.mipmap.minute_icon_foreground));
        mTypeList.add(new TypeItem("hr", R.mipmap.hour_icon_foreground));
        mTypeList.add(new TypeItem("Cal", R.mipmap.calorie_icon_foreground));
    }


    @SuppressLint("ResourceAsColor")
    public void addHabit(View view){

        habitName = addName.getText().toString();
        habitDescription = addDescription.getText().toString();
        habitTarget = addTarget.getText().toString();

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
        } else if (habitTarget.isEmpty()) {
            addTarget.setError("Add a target value");
            addTarget.setBackgroundResource(R.drawable.error_style);
            addTarget.setTextColor(R.color.black);
        } else {
            habitImage = String.valueOf(SelectedFoto.getPicId());
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
            DocumentReference documentReference2 = documentReference.collection("habits").document();
            Map<String, Object> habit = new HashMap<>();
            habit.put("name", habitName);
            habit.put("image_id", habitImage);
            habit.put("description", habitDescription);
            habit.put("done", habitDone);
            habit.put("target", habitTarget);
            habit.put("value", habitValue);
            habit.put("type", habitType);
            habit.put("done_percent", habitDonePercent);

            documentReference2.set(habit).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Habit is created");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 980);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();                }
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