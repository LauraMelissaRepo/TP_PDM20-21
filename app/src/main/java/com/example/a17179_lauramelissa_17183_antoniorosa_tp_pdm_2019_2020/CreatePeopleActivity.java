package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
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

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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

        // ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.createPeopleToolBarTitle);

        // View objects declared into variables
        this.namePersonCreate = findViewById(R.id.namePersonTextInputEdit);
        this.degreePersonCreate = findViewById(R.id.degreePersonTextInputEdit);
        this.imageView = findViewById(R.id.imageViewEdit);

        // Instantiate the variables with a empty string
        this.pictureTakenPath = "";
        this.lat = "";
        this.lng = "";

        // FloatingActionButton to take picture and listener. Use of the Dexter library to
        // verify if the permission to use camera is granted. If the permission is granted
        // we continue the action in listener and create a intent to capture the photo and
        // create a new image file to save the image into the app storage inside the device memory.
        // If the permission is denied we call a function used to inform the user.
        ExtendedFloatingActionButton takePictureButton = findViewById(R.id.takePictureEdit);
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

        // FloatingActionButton to get image and listener. Use of the Dexter library to
        // verify if the permission to read external storage is granted. If the permission is granted
        // we continue the action in listener and create a intent to pick the image.
        // If the permission is denied we call a function used to inform the user.
        ExtendedFloatingActionButton selectPictureButton = findViewById(R.id.selectPictureEdit);
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

        // FloatingActionButton to move user to map activity to save a location
        ExtendedFloatingActionButton mapButton = findViewById(R.id.locationEdit);
        mapButton.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), AddLocalToPeopleActivity.class);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        //FloatingActionButton to add a new person into firebase.
        ExtendedFloatingActionButton addButton = findViewById(R.id.editPeopleButton);
        addButton.setOnClickListener(v -> {
            // Get the string's form the EditText's with the person name and degree
            String namePerson = this.namePersonCreate.getText().toString();
            String degreePerson = this.degreePersonCreate.getText().toString();

            // If one of the fields was not introduced, show an alertDialog to inform user to
            // complete all fields
            if (namePerson.equals("") || degreePerson.equals("") || this.lat.equals("")
                    || this.lng.equals("") || this.pictureTakenPath.equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePeopleActivity.this);
                builder.setTitle(R.string.errorCreatePeopleTitleAlertDialog);
                builder.setMessage(R.string.errorCreatePeopleMessageAlertDialog);
                builder.setPositiveButton(R.string.errorCreatePeoplePositiveButtonAlertDialog,
                        (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            // If all fields have been entered, a new people object is created and
            // this new object is saved within a new document in the firebase
            else {
                People people = new People(namePerson,
                        degreePerson,
                        this.pictureTakenPath,
                        this.lat,
                        this.lng);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("peoples")
                        .add(people);
                new Thread(this::galleryAddPic).start();
                finish();
            }
        });
    }

    /**
     * Function called after certain activities are finished.
     * @param requestCode code responsible for identifying the activity.
     * @param resultCode code responsible for identifying how the activity ended,
     *                  successfully, unsuccessfully or if it was canceled.
     * @param data information returned by the closed activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Using thread to get a better performance.
        // If the closed activity was "taking a picture" and if it ended successfully,
        // a Uri is created from the file created for the image taken and we associate
        // the image taken with it. Then we recognize the Exif of the image and its orientation,
        // based on this orientation, the image is rotated and saved in a new bitmap.
        // Finally, the Glide library is used to insert the image into the
        // imageView and the path of the image is saved in a global variable
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                new Thread(() -> {
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
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                // Glide to load bitmap from thread. It waits and when the
                // resource is ready, load it into imageView
                Glide.with(getApplicationContext()).asBitmap().load(this.currentPhotoPath).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ImageView imageView = findViewById(R.id.imageViewEdit);
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
                this.pictureTakenPath = this.currentPhotoPath;
            }
        }
        // If the closed activity was "choose a photo from the gallery" and if it ended in success,
        // a Uri of the selected image is created, the path of the image is saved in a global
        // variable and the Glide library is used to introduce the image in a imageView.
        else if (requestCode == this.GALERY_REQUEST_CODE) {
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
                Glide.with(getApplicationContext()).load(selectedImage).into(this.imageView);
            }
        }
        // If the closed activity was "choose a location" and if it ended in success,
        // the latitude and longitude returned from the activity are kept in global variables.
        else if (requestCode == this.MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.lat = data.getStringExtra("lastMarkerLat");
                this.lng = data.getStringExtra("lastMarkerLng");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Funciton that creates the file for the photo taken.
     * @return the new file.
     */
    private Uri createFile() {
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

    /**
     * Function responsible for creating a path, name and extension of the file created for the image.
     * @return the image file.
     * @throws Exception
     */
    private File createImageFile() throws Exception {
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

    /**
     * Function responsible for inserting the image taken
     * into the created file and saving it to the application's storage
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(this.pictureTakenPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Function responsible for showing an alertDialog to the user
     * and inform the user that permissions are required to use the functionality.
     */
    private void showSettingsDialog() {
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

    /**
     * Function responsible for moving the user to the application's permissions
     */
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Function called to rotate the bitmap based on an angle.
     * @param source bitmap to be rotated.
     * @param angle to rotate the bitmap.
     * @return the new bitmap to insert into imageView.
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
