package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private String userID;
    private FirebaseFirestore firebaseFirestore;
    EditText nameEditText, surnameEditText, emailEditText, passwordEditText;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);

    }

    public void toLogin (View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUpClicked (View view) {

        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty()) {
            nameEditText.setError("Enter your name");
            nameEditText.setBackgroundResource(R.drawable.error_style);
        } else if (surname.isEmpty()) {
            surnameEditText.setError("Enter your surname");
            surnameEditText.setBackgroundResource(R.drawable.error_style);
        } else if(password.isEmpty() && email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please enter your information", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Invalid email address.", Toast.LENGTH_LONG).show();
        } else {

            fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    userID = fAuth.getCurrentUser().getUid();

                    Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_LONG);

                    // Save name and surname of the user
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                    Map<String, Object> users = new HashMap<>();
                    users.put("pictureUrl", null);
                    users.put("name", name);
                    users.put("surname", surname);

                    documentReference.set(users);

                    Intent intent = new Intent(SignUpActivity.this, HabitsActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();

                }
            });

        }
    }


}