package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    EditText emailText, passwordText;
    String email, password;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        signInButton = findViewById(R.id.signInButton);
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            // To restart habit values next day (Ceren)
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            int lastTimeStarted = settings.getInt("last_time_started", -1);
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_YEAR);
            if (today != lastTimeStarted) {
                LoginActivity.HabitRunnable runnable = new LoginActivity.HabitRunnable();
                new Thread(runnable).start();
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("last_time_started", today);
                editor.commit();
            }

            Intent intent = new Intent(LoginActivity.this, HabitsActivity.class);
            startActivity(intent);
            finish();
        }

        // Track edit text (Ceren)
        passwordText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        // The user is done with typing
                        signInButton.callOnClick();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // Get users information and try to sign (Ceren)
    public void signInClicked (View view) {

        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        if (password.isEmpty() && email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your e-mail", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent = new Intent(LoginActivity.this, HabitsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void toSignUp (View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    class HabitRunnable implements Runnable {

        @Override
        public void run() {
            CollectionReference collectionReference = firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("habits");

            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }

                    if (value != null) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            DocumentReference documentReference = collectionReference.document(snapshot.getId());
                            documentReference.update("done", "false");
                            documentReference.update("value", "0");
                            documentReference.update("done_percent", "0");

                        }
                    }
                }
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}