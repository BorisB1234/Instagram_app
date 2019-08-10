package com.example.instagram_app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_app.Adapter.MyFotoAdapter;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Actiivity.EditProfileActivity;
import com.example.instagram_app.Actiivity.FollowersActivity;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Actiivity.OptionsActivity;
import com.example.instagram_app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {
    ImageView image_profile,options;
    TextView posts,following,followers,username,fullname,bio;
    Button edit_profile;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<Post> postList_saves;

    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<Post> postList;

    String profileid;

    ImageButton my_fotos,saved_fotos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        following = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(),postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mysaves();

        if(profileid.equals(Server.Auth.getUid()))
        {
            edit_profile.setText("Edit Profile");
        }
        else
        {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(v -> {
            String btn = edit_profile.getText().toString();
            if (btn.equals("Edit Profile")) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            } else if (btn.equals("follow")) {
                Server.Database.follow(Server.Auth.getUid(), profileid,true);

                addNotifications();

            } else if (btn.equals("following")) {
                Server.Database.follow(Server.Auth.getUid(), profileid,false);

            }
        });

        options.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OptionsActivity.class);
            startActivity(intent);
        });

        my_fotos.setOnClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView_saves.setVisibility(View.GONE);
        });

        saved_fotos.setOnClickListener(v -> {
            recyclerView.setVisibility(View.GONE);
            recyclerView_saves.setVisibility(View.VISIBLE);
        });

        followers.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id",profileid);
            intent.putExtra("title","followers");
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id",profileid);
            intent.putExtra("title","following");
            startActivity(intent);
        });

        return view;
    }

    private void addNotifications() {
        Notification notification = new Notification(Server.Auth.getUid(), "started following you", "", false);
        Server.Database.addNotification(profileid,notification,aVoid -> {},e -> {});
    }

    private void userInfo()
    {
        Server.Database.getUser(profileid,user -> {
            if(getContext()==null)
            {
                return;
            }
            Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
            username.setText(user.getUsername());
            fullname.setText(user.getFullname());
            bio.setText(user.getBio());

        },e -> {});
    }
    private void checkFollow()
    {
        Server.Database.isFollowing(Server.Auth.getUid(),profileid,aBoolean -> {
            if(aBoolean){
                edit_profile.setText("following");
            }else {
                edit_profile.setText("follow");
            }
        },e -> {});
    }
    private void getFollowers(){
        Server.Database.getFollow(profileid,true,strings ->
                followers.setText(""+strings.size()), e -> {});

        Server.Database.getFollow(profileid,false,strings ->
                following.setText(""+strings.size()), e -> {});

    }
    private void getNrPosts(){
        Server.Database.getAllPosts(profileid,posts1 -> {posts.setText(""+posts1.size());},e -> {});
    }

    private void myFotos(){
        Server.Database.getAllPosts(profileid,posts1 -> {
            postList.clear();
            postList.addAll(posts1);
            Collections.reverse(postList);
            myFotoAdapter.notifyDataSetChanged();},e -> {});
    }

    private void mysaves(){
        mySaves=new ArrayList<>();

        Server.Database.getAllSaves(Server.Auth.getUid(),posts1 -> mySaves.addAll(posts1), e -> {});

        Server.Database.getAllPostsFromAllUsers(posts1 -> {
            postList_saves.clear();
            for (Post post:posts1){
                for(String save:mySaves){
                    if(post.getPostid().equals(save)){
                        postList_saves.add(post);
                    }
                }
            }
            myFotoAdapter_saves.notifyDataSetChanged();

        },e -> {});
    }
}
