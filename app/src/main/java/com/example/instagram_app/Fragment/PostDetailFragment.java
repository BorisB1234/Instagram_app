package com.example.instagram_app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.Adapter.PostAdapter;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Actiivity.MapsActivity;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.R;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private String gpsLatitude="0";
    private String gpsLongitude="0";

    Button mapLocBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid=preferences.getString("postid","none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);
        mapLocBtn=view.findViewById(R.id.mapLocation);

        Server.Database.getPost(postid,post -> {
            gpsLatitude=post.getGpsLatitude();
            gpsLongitude=post.getGpsLongitude();
            if((gpsLongitude.equals("0")&&gpsLatitude.equals("0")||gpsLongitude.equals("")||gpsLatitude.equals("")))
            {
                mapLocBtn.setVisibility(View.GONE);
            }
            else{
                mapLocBtn.setVisibility(View.VISIBLE);
            }

            postList.clear();
            if (post!=null)
                postList.add(post);
            postAdapter.notifyDataSetChanged();
        },e -> {});

        mapLocBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("gpsLongitude",gpsLongitude);
            intent.putExtra("gpsLatitude",gpsLatitude);

            startActivity(intent);
        });
        return view;
    }
}
