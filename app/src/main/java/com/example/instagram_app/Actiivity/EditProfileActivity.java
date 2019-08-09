package com.example.instagram_app.Actiivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText fullname, username, bio;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        Server.Database.getUser(Server.Auth.getUid(),user -> {
            fullname.setText(user.getFullname());
            username.setText(user.getUsername());
            bio.setText(user.getBio());
            Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
        },e -> {});

        close.setOnClickListener(view -> finish());

        save.setOnClickListener(view -> updateProfile(fullname.getText().toString(),
                username.getText().toString(),
                bio.getText().toString()));

        View.OnClickListener onClickListener = view -> CropImage.activity()
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);
        tv_change.setOnClickListener(onClickListener);

        image_profile.setOnClickListener(onClickListener);
    }

    private void updateProfile(String fullname, String username, String bio){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", fullname);
        map.put("username", username);
        map.put("bio", bio);

        Server.Database.updateUser(Server.Auth.getUid(),map,aVoid -> {
            Toast.makeText(EditProfileActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
            finish();
        }, e -> {});
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        Server.Storage.uploadImage(mImageUri, getContentResolver(),false,s -> {
            if(s.equals("Failed")||s.equals("No image selected"))
            {
                Toast.makeText(EditProfileActivity.this, s, Toast.LENGTH_SHORT).show();
            }else {

                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("imageurl", ""+s);

                Server.Database.updateUser(Server.Auth.getUid(),map1,aVoid -> {},e -> {});

                pd.dismiss();

            }
        },e -> {});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}