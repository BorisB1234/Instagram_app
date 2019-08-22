package com.example.instagram_app.Controller;

import android.os.AsyncTask;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.example.instagram_app.Controller.Dao.CommentDao;
import com.example.instagram_app.Controller.Dao.PostDao;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Local {

    public static class Database{
        static final ModelSql db = ModelSql.getInstance();

        public static void addPosts(final Consumer<Void> onComplete, final Post... posts) {
            final PostDao postDao = db.postDao();
            new AsyncTask<Post,Void,Void>(){
                @Override
                protected Void doInBackground(final Post... posts) {
                    postDao.insertAll(posts);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    onComplete.accept(aVoid);
                }
            }.execute(posts);
        }
        public static void addComments(final Consumer<Void> onComplete, final Comment... comments) {
            final CommentDao commentDao = db.commentDao();
            new AsyncTask<Comment,Void,Void>(){
                @Override
                protected Void doInBackground(final Comment... comments) {
                    commentDao.insertAll(comments);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    onComplete.accept(aVoid);
                }
            }.execute(comments);
        }

        public static void getLiveAllPosts(final Consumer<LiveData<List<Post>>> listener) {
            listener.accept(db.postDao().getAllPosts());
        }

        public static void getAllPosts(final Consumer<List<Post>> listener) {
            new AsyncTask<Void,String,List<Post>>(){

                @Override
                protected List<Post> doInBackground(Void... strings) {
                    List<Post> posts = db.postDao().getAll();
                    if (posts!=null)
                        return posts;
                    else return new ArrayList<>();
                }

                @Override
                protected void onPostExecute(List<Post> data) {
                    super.onPostExecute(data);
                    listener.accept(data);
                }


            }.execute();
        }

        public static void getLiveAllUsers(final Consumer<LiveData<List<User>>> listener) {
            listener.accept(db.userDao().getAllUsers());
        }

        public static void getAllUsers(final Consumer<List<User>> listener) {
            new AsyncTask<Void,String,List<User>>(){

                @Override
                protected List<User> doInBackground(Void... strings) {
                    List<User> users = db.userDao().getAll();
                    if (users!=null)
                        return users;
                    else return new ArrayList<>();
                }

                @Override
                protected void onPostExecute(List<User> data) {
                    super.onPostExecute(data);
                    listener.accept(data);
                }


            }.execute();
        }

        public static void getLiveAllComments(final Consumer<LiveData<List<Comment>>> listener) {
            listener.accept(db.commentDao().getAllComments());
        }
        public static void getAllComments(final Consumer<List<Comment>> listener) {
            new AsyncTask<Void,String,List<Comment>>(){

                @Override
                protected List<Comment> doInBackground(Void... strings) {
                    List<Comment> comments = db.commentDao().getAll();
                    if (comments!=null)
                        return comments;
                    else return new ArrayList<>();
                }

                @Override
                protected void onPostExecute(List<Comment> data) {
                    super.onPostExecute(data);
                    listener.accept(data);
                }

            }.execute();
        }


    }

}