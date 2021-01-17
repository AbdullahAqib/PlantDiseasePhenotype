package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantdiseasephenotype.utils.ModelClasses;
import com.example.plantdiseasephenotype.utils.ModelClassesLinks;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.models.Upload;
import com.example.plantdiseasephenotype.network.DeepLearningAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private static int RESULT_LOAD_IMAGE = 1;

    public Uri uri = null;
    public Uri salmap_uri = null;
    public static String LOG_TAG = "PredictionActivity";
    int imageHeight, imageWidth;

    TextView textView, learnMore;
    ImageView imageView;
    Button button;
    ProgressBar progressBar;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.result_text);
        learnMore = findViewById(R.id.learn_more);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressbar);

        imageView.setOnClickListener(this);
        button.setOnClickListener(this);

        if (getIntent().hasExtra("uri")) {
            uri = Uri.parse(getIntent().getStringExtra("uri"));
            updateImageBitmap(uri);
            button.setText("Diagnose My Plant");
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("history");

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_prediction);
        navbar.setOnNavigationItemSelectedListener(this);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            uri = selectedImage;
//            imageView.setImageURI(selectedImage);
//
//            detectButton.setOnClickListener(this);
//        }
//    }

    public static String fetchModelFile(Context context, String modelName) throws IOException {
        File file = new File(context.getFilesDir(), modelName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(modelName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(ActivityCompat.checkSelfPermission(PredictionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                startActivity(new Intent(getApplicationContext(), CameraXActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_prediction:
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                pickImageFromGallery();
                break;
            case R.id.button:
                if(button.getText().equals("Open Gallery")){
                    imageView.performClick();
                }else if(button.getText().equals("Diagnose My Plant")) {
                    detectImage();
                }else if(button.getText().equals("View Saliency Map")){
                    if(salmap_uri==null) {
                        callAPI();
                    }else{
                        updateImageBitmap(salmap_uri);
                        button.setText("View Original Image");
                    }
                }else if(button.getText().equals("View Original Image")){
                    updateImageBitmap(uri);
                    button.setText("View Saliency Map");
                }
        }
    }

    private void callAPI() {
        progressBar.setVisibility(View.VISIBLE);
        File file = new File(uri.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part img =
                MultipartBody.Part.createFormData("img", file.getName(), requestBody);
                // "img" is what server is expecting
        DeepLearningAPI.DeepLearningService apiService = DeepLearningAPI.getDeepLearningService();
        Call<ResponseBody> call = apiService.getSaliencyMap(img);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String filename = "salmap.png"; // no reason for it to be unique
                        try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
                            fos.write(response.body().bytes());
                        }
                        File f = new File(getApplicationContext().getFilesDir(), filename);
                        salmap_uri = Uri.fromFile(f);

                        //Read the image as Bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), salmap_uri);

                        //Here we reshape the image into 299*299
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);

                        salmap_uri = getImageUri(getApplicationContext(), bitmap);

                        progressBar.setVisibility(View.GONE);

                        // all that just to do this
                        updateImageBitmap(salmap_uri);

                        button.setText("View Original Image");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void detectImage() {

        Bitmap bitmap = null;
        Module module = null;

        progressBar.setVisibility(View.VISIBLE);
        //Getting the image from the image view
        try {

            getDropboxIMGSize(uri);

            //Read the image as Bitmap
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            //Here we reshape the image into 299*299
            bitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true);

            //Loading the model file.
            module = Module.load(fetchModelFile(PredictionActivity.this, "plant_disease_model.pt"));

        } catch (IOException e) {
            finish();
        }

        //Input Tensor
        final Tensor input = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
        );

        //Calling the forward of the model to run our input
//        final IValue[] outputTuple = module.forward(IValue.from(input)).toTuple();
//
//        final Tensor output = outputTuple[0].toTensor();

        if (module == null) {
            Toast.makeText(this, "Model not found. Please download the model.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Tensor output = module.forward(IValue.from(input)).toTensor();

        final float[] score_arr = output.getDataAsFloatArray();

        // Fetch the index of the value with maximum score
        float max_score = -Float.MAX_VALUE;
        int ms_ix = -1;
        for (int i = 0; i < score_arr.length; i++) {
            if (score_arr[i] > max_score) {
                max_score = score_arr[i];
                ms_ix = i;
            }
        }

        //Fetching the name from the list based on the index
        String detected_class = ModelClasses.MODEL_CLASSES[ms_ix];
        String link = ModelClassesLinks.MODEL_CLASSES_Links[ms_ix];

        //Writing the detected class in to the text view of the layout
        textView.setText(detected_class);

        if (!link.isEmpty()) {
            learnMore.setVisibility(View.VISIBLE);

            learnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("url", link);
                    startActivity(intent);
                    imageView.setImageResource(R.drawable.camera_icon);
                    textView.setText("");
                    learnMore.setVisibility(View.GONE);
                }
            });
        }

        progressBar.setVisibility(View.GONE);

        button.setText("View Saliency Map");

//        uploadFile(detected_class, uri);
    }

    private void pickImageFromGallery() {
        textView.setText("");
        learnMore.setVisibility(View.GONE);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                salmap_uri = null;
                updateImageBitmap(uri);
                button.setText("Diagnose My Plant");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
        Log.i("img", String.valueOf(imageWidth)+"x"+String.valueOf(imageHeight));
    }

    public void updateImageBitmap(Uri uri){
        imageView.setImageURI(uri);
    }

    private void uploadFile(String title, Uri uri) {

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + ".jpg");
            mUploadTask = fileReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PredictionActivity.this, "History Updated", Toast.LENGTH_LONG).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadUrl = uri;
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    Upload upload = new Upload(title,
                                                downloadUrl.toString(), firebaseUser.getUid());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    mDatabaseRef.child(uploadId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PredictionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
    }

}