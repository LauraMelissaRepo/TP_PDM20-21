package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
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

public class CreatePeopleActivity extends AppCompatActivity {

    private EditText namePersonCreate, degreePersonCreate;
    private ImageView imageView;
    public static final int CAMERA_REQUEST_CODE = 2000;
    public static final int GALERY_REQUEST_CODE = 1000;
    public static final int MAP_REQUEST_CODE = 3000;
    private String lat, lng;
    private String currentPhotoPath, pictureTakenPath;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_people);

        this.namePersonCreate = findViewById(R.id.namePersonTextInputEdit);
        this.degreePersonCreate = findViewById(R.id.degreePersonTextInputEdit);
        this.imageView = findViewById(R.id.imageViewEdit);
        this.pictureTakenPath = "";
        this.lat = "";
        this.lng = "";

        Button takePictureButton = findViewById(R.id.takePictureEdit);
        takePictureButton.setOnClickListener(v -> Dexter.withActivity(this)
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
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {

                    }
                }).check());

        Button selectPictureButton = findViewById(R.id.selectPictureEdit);
        selectPictureButton.setOnClickListener(v -> Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intentSelectPicture = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intentSelectPicture, GALERY_REQUEST_CODE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check());

        Button mapButton = findViewById(R.id.locationEdit);
        mapButton.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), AddLocalToPeopleActivity.class);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        Button addButton = findViewById(R.id.editPeopleButton);
        addButton.setOnClickListener(v -> {
            String namePerson = this.namePersonCreate.getText().toString();
            String degreePerson = this.degreePersonCreate.getText().toString();

            if (namePerson.equals("")
                    || degreePerson.equals("")
                    || this.lat.equals("")
                    || this.lng.equals("")
                    || this.pictureTakenPath.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePeopleActivity.this);

                builder.setTitle(R.string.errorCreatePeopleTitleAlertDialog);
                builder.setMessage(R.string.errorCreatePeopleMessageAlertDialog);
                builder.setPositiveButton(R.string.errorCreatePeoplePositiveButtonAlertDialog,
                        (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else{
                    People people = new People(namePerson,
                            degreePerson,
                            this.pictureTakenPath,
                            this.lat,
                            this.lng);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("peoples")
                            .add(people);
                    galleryAddPic();
                    finish();
                }
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
                        ExifInterface ei = new ExifInterface(this.currentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch (orientation) {
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

                        this.imageView.setImageBitmap(rotatedBitmap);
                        this.pictureTakenPath = this.currentPhotoPath;
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            } else if (requestCode == GALERY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    this.pictureTakenPath = cursor.getString(columnIndex);
                    this.imageView.setImageURI(selectedImage);
                }
            } else if (requestCode == MAP_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    this.lat = data.getStringExtra("lastMarkerLat");
                    this.lng = data.getStringExtra("lastMarkerLng");
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
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
                return FileProvider.getUriForFile(this,
                        "com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.fileprovider",
                        this.photoFile);
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

        private void galleryAddPic () {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(this.pictureTakenPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

        private void showSettingsDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreatePeopleActivity.this);
            builder.setTitle(R.string.showSettingDialogTitle);
            builder.setMessage(R.string.showSettingsDialogMessage);
            builder.setPositiveButton(R.string.showSettingsDialogPositiveButton,
                    (dialog, which) -> {
                dialog.cancel();
                openSettings();
            });
            builder.setNegativeButton(R.string.showSettingsDialogNegativeButton,
                    (dialog, which) -> dialog.cancel());
            builder.show();

        }

        // navigating user to app settings
        private void openSettings () {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        }

        public static Bitmap rotateImage (Bitmap source,float angle){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        }
    }