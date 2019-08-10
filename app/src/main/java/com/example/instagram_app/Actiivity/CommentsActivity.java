package com.example.instagram_app.Actiivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_app.Adapter.CommentAdapter;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.R;

public class CommentsActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;
    String publisherPostId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        post.setOnClickListener(view -> {
            if(addcomment.getText().toString().equals("")){
                Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
            }else {
                addcomment();
            }
        });

        getImage();
        readComments();
    }

    private void addcomment(){
        Server.Database.getPost(postid,post1 -> publisherPostId=post1.getPublisher(), e -> {});

        Server.Database.addComment(postid, addcomment.getText().toString(),
                comment -> {
                    addNotifications(comment);
                    addcomment.setText("");
                }, e -> {});
    }

    private void addNotifications(final Comment comment) {

        Notification notification=new Notification(Server.Auth.getUid(),
                "commented: "+comment.getComment(),
                postid,true);

        Server.Database.addNotification(publisherPostId, notification,
                aVoid -> {}, e -> {});
    }

    private void getImage(){

        Server.Database.getCurrentUser(user -> Glide.with(getApplicationContext())
                .load(user.getImageurl()).into(image_profile),
                e -> e.ifPresent(Throwable::printStackTrace));
    }

    private void readComments(){
        Server.Database.getAllComments(postid, comments -> commentAdapter.setmComment(comments), e -> {});
    }

}
