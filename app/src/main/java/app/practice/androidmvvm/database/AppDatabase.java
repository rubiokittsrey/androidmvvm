package app.practice.androidmvvm.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import app.practice.androidmvvm.database.dao.GreetingDao;
import app.practice.androidmvvm.database.entities.GreetingEntity;

@Database(entities = {GreetingEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract GreetingDao greetingDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "android_mvvm.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
