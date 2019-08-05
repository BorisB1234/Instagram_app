package com.example.instagram_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.User;

import java.util.Optional;
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


        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Server.Auth.SignIn(str_email, str_password, new Consumer<Void>() {
                        @Override
                        public void accept(Void aVoid) {
                            Server.Database.getCurrentUser(new Consumer<User>() {
                                @Override
                                public void accept(User user) {
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }, new Consumer<Optional<Exception>>() {
                                @Override
                                public void accept(Optional<Exception> e) {
                                    pd.dismiss();
                                }
                            });
                        }
                    }, new Consumer<Optional<Exception>>() {
                        @Override
                        public void accept(Optional<Exception> e) {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }
}
