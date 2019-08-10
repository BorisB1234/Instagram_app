package com.example.instagram_app.Actiivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Fragment.HomeFragment;
import com.example.instagram_app.Fragment.NotificationFragment;
import com.example.instagram_app.Fragment.ProfileFragment;
import com.example.instagram_app.Fragment.SearchFragment;
import com.example.instagram_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            menuItem -> {

                switch(menuItem.getItemId())
                {
                    case R.id.nav_home:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment=new SearchFragment();
                        break;
                    case R.id.nav_add:
                        selectedFragment=null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectedFragment=new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                        editor.putString("profileid", Server.Auth.getUid());
                        editor.apply();
                        selectedFragment=new ProfileFragment();
                        break;
                }
                if(selectedFragment!=null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };
}
