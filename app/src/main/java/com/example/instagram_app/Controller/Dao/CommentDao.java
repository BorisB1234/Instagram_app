package com.example.instagram_app.Controller.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.instagram_app.Model.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("select * from Comment")
    List<Comment> getAll();

    @Query("select * from Comment")
    LiveData<List<Comment>> getAllComments();

    @Query("select * from Comment where commentid = :id")
    Comment get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Comment... Comments);

    @Insert
    void insert(List<Comment> Comments);

    @Update
    void update(Comment Comment);

    @Query("Delete From Comment")
    void deleteAllComments();

    @Delete
    void delete(Comment Comment);
}
