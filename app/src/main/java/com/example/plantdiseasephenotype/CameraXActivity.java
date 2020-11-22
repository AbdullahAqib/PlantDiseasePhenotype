package com.example.plantdiseasephenotype;

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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraXActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
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
    }

    private void takePhoto() {
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bm = imageProxyToBitmap(image);
//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                Intent intent = new Intent(getApplicationContext(), PredictionActivity.class);
                PredictionActivity.bitmap = bm;
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
}