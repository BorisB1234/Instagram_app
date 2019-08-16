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
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText fullname, username, bio;

    private Uri mImageUri;


    private User user;

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

        Server.Database.getUser(Server.Auth.getUid(), user -> {
            fullname.setText(user.getFullname());
            username.setText(user.getUsername());
            bio.setText(user.getBio());
            Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            this.user = user;
        }, e -> {
        });

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

    private void updateProfile(String fullname, String username, String bio) {

        final User utmp = user.clone();
        utmp.setFullname(fullname);
        utmp.setUsername(username);
        utmp.setBio(bio);

        Server.Database.updateUser(utmp, aVoid -> {
            Toast.makeText(EditProfileActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
            user = utmp;
            finish();
        }, e -> {
        });
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        Server.Storage.uploadImage(mImageUri, getContentResolver(), false, imageUrl -> {

            final User utmp = user.clone();
            utmp.setImageurl(imageUrl);
            Server.Database.updateUser(utmp, aVoid -> this.user = utmp, e -> {
            }); //TODO async pd.dismiss()

            pd.dismiss();

        }, e -> e.ifPresent(e1 -> Toast.makeText(EditProfileActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show()));
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