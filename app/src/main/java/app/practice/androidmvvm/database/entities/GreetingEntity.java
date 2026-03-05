package app.practice.androidmvvm.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "greetings")
public class GreetingEntity {

    @PrimaryKey
    private int id;

    private String message;

    private int count;

    public GreetingEntity(int id, String message, int count) {
        this.id = id;
        this.message = message;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }
}
