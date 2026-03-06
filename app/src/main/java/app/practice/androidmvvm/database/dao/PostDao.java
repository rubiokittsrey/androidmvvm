package app.practice.androidmvvm.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import app.practice.androidmvvm.database.entities.PostEntity;

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    LiveData<PostEntity> observePost(int postId);

    @Query("DELETE FROM posts WHERE id = :postId")
    void delete(int postId);

    @Update
    int update(PostEntity post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(PostEntity postEntity);
}
