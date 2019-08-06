package com.example.instagram_app.Model;

import android.os.AsyncTask;

import androidx.core.util.Consumer;

import java.util.List;

public class PostAsyncDao{

    public static void getAllPosts(final Consumer<List<Post>> listener) {
        new AsyncTask<String,String,List<Post>>(){

            @Override
            protected List<Post> doInBackground(String... strings) {
                List<Post> list = ModelSql.db.postDao().getAll();
                return list;
            }

            @Override
            protected void onPostExecute(List<Post> data) {
                super.onPostExecute(data);
                listener.accept(data);

            }
        }.execute();

    }
}
