package com.example.instagram_app;

import androidx.annotation.NonNull;
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

import com.example.instagram_app.Controller.OnComplete;
import com.example.instagram_app.Controller.OnFailed;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;

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
                    Server.Auth.SignIn(str_email, str_password, new OnComplete<Void>() {
                        @Override
                        public void onComplete(Void aVoid) {
                            Server.Database.getCurrentUser(new OnComplete<User>() {
                                @Override
                                public void onComplete(User user) {
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }, new OnFailed<Optional<Exception>>() {
                                @Override
                                public void onFailed(Optional<Exception> e) {
                                    pd.dismiss();
                                }
                            });
                        }
                    }, new OnFailed<Optional<Exception>>() {
                        @Override
                        public void onFailed(Optional<Exception> e) {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }
}
