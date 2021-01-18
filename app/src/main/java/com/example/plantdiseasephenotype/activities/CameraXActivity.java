package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.dialogs.CameraInstructionsDialog;
import com.example.plantdiseasephenotype.dialogs.ConfirmEmailDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraXActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final Executor executor = Executors.newSingleThreadExecutor();

    private PreviewView viewFinder;
    private Button takePhoto;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        viewFinder = findViewById(R.id.viewFinder);
        takePhoto = findViewById(R.id.camera_capture_button);

        if (requiredPermissionsGranted()) {
            Log.i("CameraXActivity", "all the permissions granted!");
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        takePhoto.setOnClickListener(l -> {
            takePhoto();
        });

        // setup bottom navbar
        BottomNavigationView navbar = (BottomNavigationView) findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_camera);
        navbar.setOnNavigationItemSelectedListener(this);

        cameraInstrcutionsDialog();
    }

    private void takePhoto() {
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bm = imageProxyToBitmap(image);

                Uri tempUri = getImageUri(getApplicationContext(), bm);

                CropImage.activity(tempUri)
                        .start(CameraXActivity.this);

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // copied from below.
    // https://gist.github.com/moshimore/dfe5cf0216a520a8fef55ebe58a7ebe4#file-mainactivity-java-L134
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindUseCases(cameraProvider);
                } catch (ExecutionException | InterruptedException ignored) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
        imageCapture = new ImageCapture.Builder().setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).build();

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
        } catch (Exception e) {
            Log.e("CameraXActivity", "Binding failed", e);
        }
    }

    private boolean requiredPermissionsGranted() {
        // returns true if all the permissions we specified are granted.
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (requiredPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // send them back to home
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                this.finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Intent intent = new Intent(getApplicationContext(), PredictionActivity.class);
                intent.putExtra("uri", resultUri.toString());
                startActivity(intent);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                return true;
            case R.id.nav_prediction:
                startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_blogs:
                startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
        }
        return false;
    }

    private void cameraInstrcutionsDialog() {

        CameraInstructionsDialog dialog = new CameraInstructionsDialog(CameraXActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}