package com.example.instagram_app.Controller.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.instagram_app.Model.Notification;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("select * from Notification")
    List<Notification> getAll();

    @Query("select * from Notification")
    LiveData<List<Notification>> getAllNotifications();

    @Query("select * from Notification where userid = :id")
    Notification get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Notification... Notifications);

    @Insert
    void insert(List<Notification> Notifications);

    @Update
    void update(Notification Notification);

    @Delete
    void delete(Notification Notification);

    @Query("Delete From Notification")
    void deleteAllNotifications();
}
