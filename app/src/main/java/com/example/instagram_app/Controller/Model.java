package com.example.instagram_app.Controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.instagram_app.Model.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Model extends ViewModel {

    final private static Model instance = new Model();


    public static Model getInstance() {
        return instance;
    }
    private Model() { }

    private PostListData postListData=new PostListData();

    public LiveData<List<Post>> getPosts() {
        return postListData;
    }

    class PostListData extends MutableLiveData<List<Post>> {
        @Override
        protected void onActive() {
            super.onActive();
            Server.Database.getAllPostsFromAllUsers(posts -> {
                Post[] array = posts.toArray(new Post[0]);

                //TODO check doubles in local db when insert the same agrs
                Log.d("gil server", Arrays.toString(array));

                for (Post post : posts) {
                    Local.Database.addPosts(aVoid -> {Log.d("gil Local", "post insert db "+ post.getPostid());},post);
                }

            }, e -> {});
            Local.Database.getLiveAllPosts(listLiveData -> {
                List<Post> value = listLiveData.getValue();
                Log.d("gil local", value+"");

                if (value!=null)
                    setValue(value);
                else setValue(new ArrayList<>());
            });
        }
        @Override
        protected void onInactive() {
            super.onInactive();
            //TODO close session with firebase
//            modelFirebase.cancellGetAllPosts();
            Log.d("TAG","cancellGetAllPosts");
        }
        public PostListData() {
            super();
            setValue(new LinkedList<>());
            Local.Database.getAllPosts(posts -> setValue(posts));
        }
    }
    
}
