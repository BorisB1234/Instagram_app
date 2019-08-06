package com.example.instagram_app.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.MyApplication;

@Database(entities = {Post.class}, version = 20, exportSchema = false)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
}

public class ModelSql {
    static public AppLocalDbRepository db = Room.databaseBuilder(MyApplication.getContext(),
            AppLocalDbRepository.class,
            "database.db")
            .fallbackToDestructiveMigration()
            .build();


}
