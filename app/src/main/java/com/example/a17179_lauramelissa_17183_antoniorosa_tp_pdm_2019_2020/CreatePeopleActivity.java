package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePeopleActivity extends AppCompatActivity {

    private EditText namePersonCreate;
    private EditText degreePersonCreate;
    private ImageView imageView;
    public static final int CAMERA_REQUEST_CODE = 2000;
    public static final int GALERY_REQUEST_CODE = 1000;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_people);

        this.namePersonCreate = findViewById(R.id.namePersonCreateEdit);
        this.degreePersonCreate = findViewById(R.id.degreePersonCreateEdit);
        this.imageView = findViewById(R.id.imageView);

        Button takePictureButton = findViewById(R.id.takePicture);
        takePictureButton.setOnClickListener(v -> {
            Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intentTakePicture.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (Exception exception){
                    //Error ocurred while creating the File
                }
                //Continue only if the File was sucessfully created
                if(photoFile != null){
                    Uri photoUri = FileProvider.getUriForFile(this,
                            "com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.fileprovider",
                            photoFile);
                    intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intentTakePicture, CAMERA_REQUEST_CODE);
                    galleryAddPic();
                }
            }
        });

        Button selectPictureButton = findViewById(R.id.selectPicture);
        selectPictureButton.setOnClickListener(v -> {
            Intent intentSelectPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentSelectPicture, GALERY_REQUEST_CODE);
        });

        Button addButton = findViewById(R.id.createPeopleButton);
        addButton.setOnClickListener(v -> {
            String namePerson = this.namePersonCreate.getText().toString();
            String degreePerson = this.degreePersonCreate.getText().toString();
            String path = "/avatarDefault.png";

            People people = new People(0, namePerson, degreePerson, path);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("peoples")
                    .add(people)
                    .addOnSuccessListener(this, documentReference -> {
                        Toast.makeText(getApplicationContext(), "Chegou", Toast.LENGTH_LONG).show();
                    });
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bitmap bitmap = data.getParcelableExtra("data");
                this.imageView.setImageBitmap(bitmap);
            }
            else {
                Toast.makeText(this, "404 Pic Not Found", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == GALERY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                this.imageView.setImageURI(selectedImage);
            }
            else {
                Toast.makeText(this, "404 Pic Not Found", Toast.LENGTH_LONG).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File createImageFile() throws Exception{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        //Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(this.currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}