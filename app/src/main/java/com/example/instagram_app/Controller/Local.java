package com.example.instagram_app.Controller;


import android.os.AsyncTask;

import androidx.core.util.Consumer;

import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Post;

import java.util.List;

public class Local {

    public static class Database{
        static final AppLocalDbRepository db = ModelSql.db;

        public static void addPosts(final Consumer<Void> onComplete, final Post... posts) {
            new AsyncTask<Post,Void,Void>(){
                @Override
                protected Void doInBackground(final Post... posts) {
                    db.postDao().insertAll(posts);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    onComplete.accept(aVoid);
                }
            }.execute(posts);
        }

        public static void getAllPosts(final Consumer<List<Post>> listener) {
            new AsyncTask<Void,String,List<Post>>(){

                @Override
                protected List<Post> doInBackground(Void... strings) {
                    return db.postDao().getAll();
                }

                @Override
                protected void onPostExecute(List<Post> data) {
                    super.onPostExecute(data);
                    listener.accept(data);
                }


            }.execute();
        }
        public static void getAllComments(final Consumer<List<Comment>> listener) {
            new AsyncTask<Void,String,List<Comment>>(){

                @Override
                protected List<Comment> doInBackground(Void... strings) {
                    return db.commentDao().getAll();
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