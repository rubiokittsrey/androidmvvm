package app.practice.androidmvvm.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.practice.androidmvvm.database.dao.GreetingDao;
import app.practice.androidmvvm.database.entities.GreetingEntity;

public class GreetingRepo {

    private final GreetingDao greetingDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GreetingRepo(GreetingDao greetingDao) {
        this.greetingDao = greetingDao;
    }

    public LiveData<String> observeGreetingMessage() {
        return Transformations.map(
                greetingDao.observeGreeting(),
                entity -> entity == null ? "Loading greeting..." : entity.getMessage()
        );
    }

    public void refreshGreeting() {
        executor.execute(() -> {
            GreetingEntity current = greetingDao.getGreeting();
            int nextCount = current == null ? 1 : current.getCount() + 1;
            String nextMessage = "Hello from MVVM (update #" + nextCount + ")";
            greetingDao.save(new GreetingEntity(1, nextMessage, nextCount));
        });
    }
}
