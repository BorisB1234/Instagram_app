package com.example.instagram_app.Actiivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;

import java.util.function.Consumer;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button login;
    TextView txt_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        txt_signup=findViewById(R.id.txt_signup);

        txt_signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
        login.setOnClickListener(v -> {
            final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
            pd.setMessage("please wait...");
            pd.show();

            String str_email=email.getText().toString();
            String str_password=password.getText().toString();

            if(TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
            {
                Toast.makeText(LoginActivity.this,"All fields are required!!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Server.Auth.SignIn(str_email, str_password, aVoid -> Server.Database.getCurrentUser(new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        pd.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }, e -> pd.dismiss()), e -> {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
