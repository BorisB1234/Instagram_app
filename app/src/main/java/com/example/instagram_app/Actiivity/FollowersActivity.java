package com.example.instagram_app.Actiivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.Adapter.UserAdapter;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    String id;
    String title;

    private List<String> idlist;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList=new ArrayList<>();
        userAdapter=new UserAdapter(this,userList,false);
        recyclerView.setAdapter(userAdapter);

        idlist=new ArrayList<>();

        switch(title)
        {
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
        }
    }
    private void getFollowers()
    {
        Server.Database.getFollow(id,true,strings -> {
            idlist.clear();
            idlist.addAll(strings);
            showUsers();
        },e -> {});
    }
    private void getFollowing()
    {
        Server.Database.getFollow(id,false,strings -> {
            idlist.clear();
            idlist.addAll(strings);
            showUsers();
        },e -> {});
    }
    private void getLikes()
    {
        Server.Database.getLikes(id,strings -> {
            idlist.clear();
            idlist.addAll(strings);
            showUsers();
        },e -> {});
    }
    private void showUsers()
    {
        Server.Database.getAllUsers(users -> {
            userList.clear();
            for(User user: users) {
                for (String id : idlist) {
                    if (user.getId().equals(id)) {
                        userList.add(user);
                    }
                }
            }
            userAdapter.notifyDataSetChanged();
        },e -> {});
    }
}
