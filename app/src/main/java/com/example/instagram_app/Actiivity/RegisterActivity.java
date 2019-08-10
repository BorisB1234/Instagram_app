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
import com.example.instagram_app.R;

public class RegisterActivity extends AppCompatActivity {
    EditText username,fullname,email,password;
    Button register;
    TextView txt_login;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);

        txt_login.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));
        register.setOnClickListener(v -> {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Please wait..");
            pd.show();

            String str_username=username.getText().toString();
            String str_fullname=fullname.getText().toString();
            String str_email=email.getText().toString();
            String str_password=password.getText().toString();

            if(TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
            {
                Toast.makeText(RegisterActivity.this,"All fields are required!",Toast.LENGTH_SHORT).show();
            }else if(str_password.length()<6)
            {
                Toast.makeText(RegisterActivity.this,"Password must have 6 characters",Toast.LENGTH_SHORT).show();
            }else
            {
                register(str_username,str_fullname,str_email,str_password);
            }
        });
    }
    private void register(final String username, final String fullname, String email, String password)
    {
        Server.Auth.SignUp(username, fullname, email, password, aVoid -> {
            pd.dismiss();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }, e -> {
            pd.dismiss();
            Toast.makeText(RegisterActivity.this,"you can't register with this email or password",Toast.LENGTH_SHORT).show();
        });

    }
}
