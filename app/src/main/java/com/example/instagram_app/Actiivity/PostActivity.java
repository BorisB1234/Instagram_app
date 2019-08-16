package com.example.instagram_app.Actiivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    ImageView close, image_added;
    TextView post;
    EditText description;
    Switch gpsBtn;
    String gpsLatitude="0";
    String gpsLongitude="0";
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        close=findViewById(R.id.close);
        image_added=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);
        gpsBtn=findViewById(R.id.save_gps);
        gpsBtn.setOnClickListener(v -> {
            if(gpsBtn.isChecked())
                getGpsLocation();
            else {
                gpsLatitude="0";
                gpsLongitude="0";
            }
        });
        close.setOnClickListener(view -> {
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        });
        post.setOnClickListener(v -> uploadImage());

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);

    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        Server.Storage.uploadImage(imageUri,getContentResolver(),true,myUrl -> {

                Server.Database.publishPost(myUrl, description.getText().toString(), Server.Auth.getUid(), gpsLatitude, gpsLongitude);
                progressDialog.dismiss();

                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
        },e -> e.ifPresent(e1 -> Toast.makeText(PostActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void getGpsLocation() {
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(PostActivity.this);
        if (ActivityCompat.checkSelfPermission(PostActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(PostActivity.this, location -> {
            if(location != null){
                gpsLatitude=String.valueOf(location.getLatitude());
                gpsLongitude=String.valueOf(location.getLongitude());
            }
        });
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri=result.getUri();

            image_added.setImageURI(imageUri);
        } else {
            Toast.makeText(this,"Something gone Wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
