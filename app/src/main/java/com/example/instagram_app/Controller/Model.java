package com.example.instagram_app.Controller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Post;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Model extends ViewModel {

    final private static Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }
    private Model() { }

    //private PostListData postListData=new PostListData();

    public LiveData<List<Post>> getPosts() {
        //return postListData;
        return new PostListData();
    }
    public LiveData<List<Comment>> getComments() {
        //return postListData;
        return new CommentListData();
    }
    class CommentListData extends MutableLiveData<List<Comment>> {
        @Override
        protected void onActive() {
            super.onActive();

            Server.Database.getAllCommentsOfAllPosts(comments -> {
                Comment[] array = comments.toArray(new Comment[0]);

                //TODO check doubles in local db when insert the same agrs
            for(List<Comment> comment : comments) {
                for (Comment comment2: comment) {
                    Local.Database.addComments(aVoid -> {
                    }, comment2);
                }
            }

            }, e -> {
            });
            Local.Database.getLiveAllComments(listLiveData -> {
                List<Comment> value = listLiveData.getValue();

                if (value != null)
                    setValue(value);
//                else setValue(new ArrayList<>());
            });
        }
        @Override
        protected void onInactive() {
            super.onInactive();

        }
        public CommentListData() {
            super();
            setValue(new LinkedList<>());
            Local.Database.getAllComments(comments -> setValue(comments));
        }
    }

    class PostListData extends MutableLiveData<List<Post>> {
        @Override
        protected void onActive() {
            super.onActive();

            Server.Database.getAllPostsFromAllUsers(posts -> {
                Post[] array = posts.toArray(new Post[0]);

                //TODO check doubles in local db when insert the same agrs
                for (Post post : posts) {
                    Local.Database.addPosts(aVoid -> {},post);
                }

            }, e -> {});
            Local.Database.getLiveAllPosts(listLiveData -> {
                List<Post> value = listLiveData.getValue();

                if (value!=null)
                    setValue(value);
//                else setValue(new ArrayList<>());
            });
        }
        @Override
        protected void onInactive() {
            super.onInactive();

//            modelFirebase.cancellGetAllPosts();
        }
        public PostListData() {
            super();
            setValue(new LinkedList<>());
            Local.Database.getAllPosts(posts -> setValue(posts));
        }
    }
    
}
