package app.practice.androidmvvm.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import app.practice.androidmvvm.database.entities.GreetingEntity;

@Dao
public interface GreetingDao {

    @Query("SELECT * FROM greetings WHERE id = 1 LIMIT 1")
    LiveData<GreetingEntity> observeGreeting();

    @Query("SELECT * FROM greetings WHERE id = 1 LIMIT 1")
    GreetingEntity getGreeting();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(GreetingEntity entity);
}
