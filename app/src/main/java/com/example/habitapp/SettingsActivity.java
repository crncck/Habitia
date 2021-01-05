package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    Button doneButton;
    ImageView addProfileImage;

    EditText userNameEditText;
    EditText userSurnameEditText;

    Bitmap selectedImage;
    Uri imageData;
    String imageDownloadUrl;

    String name;
    String surname;
    String downloadUrl;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private String userID;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addProfileImage = findViewById(R.id.addProfileImage);
        userNameEditText = findViewById(R.id.userNameEditText);
        userSurnameEditText = findViewById(R.id.userSurnameEditText);

        doneButton = findViewById(R.id.doneButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        userID = fAuth.getCurrentUser().getUid();

        registerForContextMenu(addProfileImage);

        getDataFromFireStore();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.long_press_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_picture:
                downloadUrl = null;
                addProfileImage.setImageResource(R.mipmap.add_habit_icon_foreground);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void addProfileImage(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageData = data.getData();

            try {

                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    addProfileImage.setImageBitmap(selectedImage);

                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    addProfileImage.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void doneButton (View view) {

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();


        if (imageData != null) {

            String imageName = "images/" + userID + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Download URL
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();

                            user.put("profile_image", imageDownloadUrl);
                            user.put("name", userNameEditText.getText().toString());
                            user.put("surname", userSurnameEditText.getText().toString());

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SettingsActivity.this, "Profile settings changed successfully", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else {
            user.put("profile_image", downloadUrl);
            user.put("name", userNameEditText.getText().toString());
            user.put("surname", userSurnameEditText.getText().toString());

            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SettingsActivity.this, "Profile settings changed successfully", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });

        }

        //profileImage.setImageBitmap(selectedImage2);
        Intent intent = new Intent(SettingsActivity.this, HabitsActivity.class);
        startActivity(intent);


    }

    public void getDataFromFireStore() {
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(SettingsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        if (snapshot.getId().equals(userID)) {
                            Map<String, Object> data = snapshot.getData();

                            name = (String) data.get("name");
                            surname = (String) data.get("surname");
                            downloadUrl = (String) data.get("profile_image");

                            userNameEditText.setText(name);
                            userSurnameEditText.setText(surname);

                            // If user does not add profile image show template image
                            if (downloadUrl == null) {
                                addProfileImage.setImageResource(R.mipmap.add_habit_icon_foreground);                            } else {
                                Picasso.get().load(downloadUrl).into(addProfileImage);
                                Picasso.get().load(downloadUrl).into(HabitsActivity.profileImage);

                            }



                        }

                    }

                }

            }
        });
    }




}