package com.example.instagram_app.Controller;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.MyApplication;
import com.example.instagram_app.Controller.Dao.CommentDao;
import com.example.instagram_app.Controller.Dao.NotificationDao;
import com.example.instagram_app.Controller.Dao.PostDao;
import com.example.instagram_app.Controller.Dao.UserDao;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Model.User;

@Database(entities = {Post.class, Comment.class, Notification.class, User.class}, version = 2)
public abstract class ModelSql extends RoomDatabase{

    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract NotificationDao notificationDao();
    public abstract UserDao userDao();


    private static ModelSql instance;
    public static synchronized ModelSql getInstance() {
        if (instance==null){
            instance = Room.databaseBuilder(MyApplication.getContext(),
                    ModelSql.class,
                    "database.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }



    private static RoomDatabase.Callback callback=new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsync().execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}