package com.example.webdrive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class Profile extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Uri imageUri;
    public TabLayout tabLayout ;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button lessonsButton = findViewById(R.id.button2);
        Button roadSignsButton = findViewById(R.id.button3);
        ImageButton settings = findViewById(R.id.settings);
        tabLayout = findViewById(R.id.tab_layout);
        profileImageView = findViewById(R.id.blank_profile);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profile_pictures");
        lessonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, LessonsActivity.class);
                startActivity(intent);
            }
        });

        roadSignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, RoadSignsActivity.class);
                startActivity(intent);
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the settings button is clicked, start the Settings activity
                Intent intent = new Intent(Profile.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            saveImageToLocalStorage(imageUri);
          //  uploadImageToFirebase();
        }
    }

    protected void saveImageToLocalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File file = new File(getFilesDir(), "profile_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            FileChannel source = ((FileInputStream) inputStream).getChannel();
            FileChannel destination = outputStream.getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            inputStream.close();
            outputStream.close();
            Toast.makeText(this, "Image saved locally", Toast.LENGTH_SHORT).show();
            Log.d("SaveImage", "Image saved successfully to internal storage");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image locally", Toast.LENGTH_SHORT).show();
            Log.e("SaveImage", "Failed to save image to internal storage: " + e.getMessage());
        }
    }


//    private void uploadImageToFirebase() {
//        if (imageUri != null && isFirebaseSetupValid()) {
//            StorageReference imageRef = storageReference.child("profile_image.jpg");
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] imageData = baos.toByteArray();
//                imageRef.putBytes(imageData)
//                        .addOnSuccessListener(taskSnapshot -> {
//                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                String imageUrl = uri.toString();
//                                databaseReference.child("profile_picture_url").setValue(imageUrl)
//                                        .addOnSuccessListener(aVoid -> {
//                                            Toast.makeText(Profile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(Profile.this, "Failed to update profile picture URL", Toast.LENGTH_SHORT).show();
//                                        });
//                            });
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//                        });
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(Profile.this, "Error processing image", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(Profile.this, "Invalid image or Firebase setup", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private boolean isFirebaseSetupValid() {
//        return storageReference != null && databaseReference != null;
//    }
}
