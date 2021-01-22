package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPeopleActivity extends AppCompatActivity {

    private String documentID, lat, lng, name, degree, imgPath;
    public static final int CAMERA_REQUEST_CODE = 2000;
    public static final int GALERY_REQUEST_CODE = 1000;
    public static final int MAP_REQUEST_CODE = 3000;
    private File photoFile;
    private String currentPhotoPath;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_people);

        Bundle extras = getIntent().getExtras();
        this.documentID = extras.getString("DocumentID");
        this.lat = extras.getString("Lat");
        this.lng = extras.getString("Lng");
        this.name = extras.getString("Name");
        this.degree = extras.getString("Degree");
        this.imgPath = extras.getString("ImgPath");

        EditText namePersonEdit = findViewById(R.id.namePersonTextInputEdit);
        namePersonEdit.setText(this.name);

        EditText degreePersonEdit = findViewById(R.id.degreePersonTextInputEdit);
        degreePersonEdit.setText(this.degree);

        this.imageView = findViewById(R.id.imageViewEdit);
        this.imageView.setImageBitmap(getImage(this.imgPath));

        Button takePictureEdit = findViewById(R.id.takePictureEdit);
        takePictureEdit.setOnClickListener(v -> {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri photoUri = createFile();
                            intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intentTakePicture, CAMERA_REQUEST_CODE);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
        });

        Button selectPictureEdit = findViewById(R.id.selectPictureEdit);
        selectPictureEdit.setOnClickListener(v -> {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intentSelectPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentSelectPicture, GALERY_REQUEST_CODE);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        });

        Button locationEdit = findViewById(R.id.locationEdit);
        locationEdit.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), EditLocationPeople.class);
            intentMap.putExtra("Lat", this.lat);
            intentMap.putExtra("Lng", this.lng);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        Button editPeople = findViewById(R.id.editPeopleButton);
        editPeople.setOnClickListener(v -> {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fb.collection("peoples").document(this.documentID);
            documentReference.update("namePerson", String.valueOf(namePersonEdit.getText()));
            documentReference.update("degreePerson", String.valueOf(degreePersonEdit.getText()));
            documentReference.update("imgPath", String.valueOf(this.imgPath));
            documentReference.update("lat", String.valueOf(this.lat));
            documentReference.update("lng", String.valueOf(this.lng));
            finish();
        });

    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.fromFile(this.photoFile);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    androidx.exifinterface.media.ExifInterface ei = new androidx.exifinterface.media.ExifInterface(this.currentPhotoPath);
                    int orientation = ei.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch (orientation) {
                        case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }

                    this.imageView.setImageBitmap(rotatedBitmap);
                    this.imgPath = this.currentPhotoPath;
                    Toast.makeText(this, this.imgPath, Toast.LENGTH_LONG).show();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Last Path= " + this.imgPath, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == GALERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                this.imgPath = cursor.getString(columnIndex);
                this.imageView.setImageURI(selectedImage);
                Toast.makeText(this, this.imgPath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Last Path= " + this.imgPath, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.lat = data.getStringExtra("EditLat");
                this.lng = data.getStringExtra("EditLng");
            } else {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Bitmap getImage(String imgPath){
        File img = new File(imgPath);
        Bitmap rotatedBitmap;
        if (img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imgPath);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            return rotatedBitmap;
        }
        else{
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            rotatedBitmap = bitmap;
            return rotatedBitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Uri createFile () {
        this.photoFile = null;
        try {
            this.photoFile = createImageFile();
        } catch (Exception exception) {
            //Error ocurred while creating the File
        }
        //Continue only if the File was sucessfully created
        if (this.photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this,
                    "com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.fileprovider",
                    this.photoFile);
            return photoUri;
        }
        return null;
    }

    private File createImageFile () throws Exception {
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

    private void showSettingsDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPeopleActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings () {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}