package com.example.instagram_app.Controller;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.MyApplication;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Controller.Dao.CommentDao;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Controller.Dao.NotificationDao;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Controller.Dao.PostDao;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.Controller.Dao.UserDao;

@Database(entities = {Post.class, Comment.class, Notification.class, User.class}, version = 21)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract NotificationDao notificationDao();
    public abstract UserDao userDao();
}

class ModelSql {
    final static AppLocalDbRepository db = Room.databaseBuilder(MyApplication.getContext(),
            AppLocalDbRepository.class,
            "database.db")
            .fallbackToDestructiveMigration()
            .build();

}
