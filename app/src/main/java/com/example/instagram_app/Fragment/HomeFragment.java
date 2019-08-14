package com.example.instagram_app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.Adapter.PostAdapter;
import com.example.instagram_app.Controller.Model;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;

    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        progressBar = view.findViewById(R.id.progress_circular);

        checkFollowing();

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        Server.Database.getFollow(Server.Auth.getUid(),false,strings -> {
            followingList.clear();
            followingList.addAll(strings);
            readPosts();
        },e -> {});
    }

    private void readPosts(){
        Log.d("TAG10","readPosts");

        Model.getInstance().getPosts().observe(this, posts -> {
            Log.d("TAG10","observe");
            Log.d("TAG10",posts.toString());

            postLists.clear();
            for(Post post:posts)
            {
                Log.d("TAG10",post.toString());

                if(post.getPublisher().equals(Server.Auth.getUid())){
                    postLists.add(post);
                }
                for(String id:followingList){
                    if(post.getPublisher().equals(id)){
                        postLists.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });



//        Server.Database.getAllPostsFromAllUsers(posts -> {
//            postLists.clear();
//            for(Post post:posts)
//            {
//                if(post.getPublisher().equals(Server.Auth.getUid())){
//                    postLists.add(post);
//                }
//                for(String id:followingList){
//                    if(post.getPublisher().equals(id)){
//                        postLists.add(post);
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);
//            }
//        },e -> {});

    }
}
