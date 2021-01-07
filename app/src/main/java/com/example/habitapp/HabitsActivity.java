package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HabitsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    GridView gridview;
    final public List<Habit> habitsList = new ArrayList<>();
    TextView habitsText;
    static CircularImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        gridview = findViewById(R.id.gridview);
        habitsText = findViewById(R.id.habitsText);
        profileImage = findViewById(R.id.profileImage);
        profileImage.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        getDataFromFireStore();


        HabitsAdapter adapter = new HabitsAdapter(HabitsActivity.this, habitsList);
        gridview.setAdapter(adapter);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HabitsActivity.this, DetailsActivity.class);
                intent.putExtra("SelectedHabit", habitsList.get(i));
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.habit_options_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_habit) {

            Intent intentToHabit = new Intent(HabitsActivity.this, AddHabitActivity.class);
            startActivity(intentToHabit);

        } else if (item.getItemId() == R.id.sign_out) {

            firebaseAuth.signOut();

            Intent intentToLogin = new Intent(HabitsActivity.this, LoginActivity.class);
            startActivity(intentToLogin);


        }else if (item.getItemId() == R.id.settings){
            Intent intentToSettings = new Intent(HabitsActivity.this,SettingsActivity.class);
            startActivity(intentToSettings);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    public void getDataFromFireStore() {

        CollectionReference collectionReference = firebaseFirestore.collection("users");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(HabitsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        if (snapshot.getId().equals(userID)) {
                            Map<String, Object> data = snapshot.getData();
                            String downloadUrl = (String) data.get("profile_image");

                            if (downloadUrl != null) {
                                profileImage.setVisibility(View.VISIBLE);
                                Picasso.get().load(downloadUrl).into(profileImage);
                            }
                        }

                    }

                }

            }
        });


        DocumentReference document = collectionReference.document(userID);
        CollectionReference collectionReference2 = document.collection("habits");

        collectionReference2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(HabitsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();
                        String habitId = snapshot.getId();
                        String name = (String) data.get("name");
                        String description = (String) data.get("description");
                        String image_id = (String) data.get("image_id");
                        String done = (String) data.get("done");
                        String target = (String) data.get("target");
                        String habit_value = (String) data.get("value");
                        String type = (String) data.get("type");
                        String done_percent = (String) data.get("done_percent");


                        habitsList.add(new Habit(habitId, name, image_id, description, done, target, habit_value, type, done_percent));

                        HabitsAdapter adapter = new HabitsAdapter(HabitsActivity.this, habitsList);
                        gridview.setAdapter(adapter);
                    }
                }
            }
        });
    }
}