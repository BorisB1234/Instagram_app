package com.example.instagram_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.instagram_app.Adapter.CommentAdapter;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.User;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CommentsActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(this, postid);
        recyclerView.setAdapter(commentAdapter);


        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                }else {
                    addcomment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addcomment(){

        Server.Database.addComment(postid, addcomment.getText().toString(),
                new Consumer<Comment>() {
            @Override
            public void accept(Comment comment) {
                addNotifications(comment);
                addcomment.setText("");
            }
        }, new Consumer<Optional<Exception>>() {
            @Override
            public void accept(Optional<Exception> e) {
                //TODO
            }
        });

    }

    private void addNotifications(final Comment comment) {
        Notification notification=new Notification(Server.Auth.getUid(),
                "commented: "+comment.getComment(),
                postid,true);

        Server.Database.addNotification(comment.getPublisher(), notification,
                new Consumer<Void>() {
            @Override
            public void accept(Void aVoid) {

            }
        }, new Consumer<Optional<Exception>>() {
            @Override
            public void accept(Optional<Exception> e) {

            }
        });
    }

    private void getImage(){

        Server.Database.getCurrentUser(new Consumer<User>() {
            @Override
            public void accept(User user) {
                Glide.with(getApplicationContext())
                        .load(user.getImageurl()).into(image_profile);
            }
        }, new Consumer<Optional<Exception>>() {
            @Override
            public void accept(Optional<Exception> e) {
                e.ifPresent(new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void readComments(){
        Server.Database.getAllComments(postid, new Consumer<List<Comment>>() {
            @Override
            public void accept(List<Comment> comments) {
                commentAdapter.setmComment(comments);
            }
        }, new Consumer<Optional<Exception>>() {
            @Override
            public void accept(Optional<Exception> e) {

            }
        });
    }

}
