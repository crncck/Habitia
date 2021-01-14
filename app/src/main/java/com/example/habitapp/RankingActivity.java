package com.example.habitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.protobuf.UnmodifiableLazyStringList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class RankingActivity extends AppCompatActivity implements RankingFragment.OnUserSelected {

    ArrayList<User> users = new ArrayList<>();
    ImageView firstImage, secondImage, thirdImage;
    ProgressBar progressBar1, progressBar2, progressBar3;
    TextView firstName, secondName, thirdName;
    TextView first, second, third;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        firstImage = findViewById(R.id.imageView);
        secondImage = findViewById(R.id.imageView2);
        thirdImage = findViewById(R.id.imageView3);

        progressBar1 = findViewById(R.id.progress_bar);
        progressBar2 = findViewById(R.id.progress_bar2);
        progressBar3 = findViewById(R.id.progress_bar3);

        firstName = findViewById(R.id.firstNameText);
        secondName = findViewById(R.id.secondNameText);
        thirdName = findViewById(R.id.thirdNameText);

        first = findViewById(R.id.firstPositionText);
        second = findViewById(R.id.secondPositionText);
        third = findViewById(R.id.thirdPositionText);

        users = (ArrayList<User>) getIntent().getSerializableExtra("user_list");
        Collections.sort(users, Collections.reverseOrder());

        if (!users.isEmpty()) {
            User user = users.get(0);
            progressBar1.setProgress(user.getDone_percent());
            firstName.setText(user.getName()+" "+user.getSurname());
            if (user.getProfileUrl() != null) {
                Picasso.get().load(user.getProfileUrl()).into(firstImage);
            } else {
                firstImage.setImageResource(R.mipmap.add_habit_icon_foreground);
            }
            if (users.size() == 2 || users.size() > 2 ) {
                User user1 = users.get(1);
                progressBar2.setProgress(user1.getDone_percent());
                secondName.setText(user1.getName()+" "+user1.getSurname());
                if (user1.getProfileUrl() != null) {
                    Picasso.get().load(user1.getProfileUrl()).into(secondImage);
                } else {
                    secondImage.setImageResource(R.mipmap.add_habit_icon_foreground);
                }

                if (users.size() == 3 || users.size() > 3 ) {
                    User user2 = users.get(2);
                    progressBar3.setProgress(user2.getDone_percent());
                    thirdName.setText(user2.getName()+" "+user2.getSurname());
                    if (user2.getProfileUrl() != null) {
                        Picasso.get().load(user2.getProfileUrl()).into(thirdImage);
                    } else {
                        thirdImage.setImageResource(R.mipmap.add_habit_icon_foreground);
                    }
                    users.remove(2);
                } else {
                    thirdImage.setVisibility(ImageView.INVISIBLE);
                    thirdName.setVisibility(TextView.INVISIBLE);
                    progressBar3.setVisibility(ProgressBar.INVISIBLE);
                    third.setVisibility(TextView.INVISIBLE);
                }
                users.remove(1);
            } else {
                secondImage.setVisibility(ImageView.INVISIBLE);
                secondName.setVisibility(TextView.INVISIBLE);
                progressBar2.setVisibility(ProgressBar.INVISIBLE);
                second.setVisibility(TextView.INVISIBLE);
            }
            users.remove(0);
        } else {
            firstImage.setVisibility(ImageView.INVISIBLE);
            firstName.setVisibility(TextView.INVISIBLE);
            progressBar1.setVisibility(ProgressBar.INVISIBLE);
            third.setVisibility(TextView.INVISIBLE);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("sorted_list", users);
        RankingFragment rankingFragment = new RankingFragment();
        rankingFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_frame,rankingFragment).commit();
    }

    @Override
    public void userSelected(User user) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RankingActivity.this, HabitsActivity.class);
        startActivity(intent);
        finish();
    }
}