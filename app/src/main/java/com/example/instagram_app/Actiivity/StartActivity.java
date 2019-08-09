package com.example.instagram_app.Actiivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.R;

public class StartActivity extends AppCompatActivity {
    Button login,register;

    @Override
    protected void onStart() {
        super.onStart();

        if(Server.Auth.getUid()!=null)
        {
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        login.setOnClickListener(v -> startActivity(new Intent(StartActivity.this,LoginActivity.class)));
        register.setOnClickListener(v -> startActivity(new Intent(StartActivity.this,RegisterActivity.class)));
    }
}
