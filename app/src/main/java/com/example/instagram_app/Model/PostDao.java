package com.example.instagram_app.Model;

import android.os.AsyncTask;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {
    @Query("select * from Post")
    List<Post> getAll();

    @Query("select * from Post")
    LiveData<List<Post>> getAllPosts();

    @Query("select * from Post where postid = :id")
    Post get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... Posts);

    @Insert
    void insert(List<Post> Posts);

    @Update
    void update(Post Post);

    @Delete
    void delete(Post Post);

    @Query("Delete From Post")
    void deleteAllPosts();
}

