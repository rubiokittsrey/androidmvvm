package app.practice.androidmvvm.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import app.practice.androidmvvm.database.AppDatabase;
import app.practice.androidmvvm.repositories.GreetingRepo;

public class GreetingVM extends AndroidViewModel {

    private final GreetingRepo greetingRepo;
    private final LiveData<String> greetingMessage;

    public GreetingVM(@NonNull Application application) {
        super(application);
        greetingRepo = new GreetingRepo(
                AppDatabase.getInstance(application).greetingDao()
        );
        greetingMessage = greetingRepo.observeGreetingMessage();
        greetingRepo.refreshGreeting();
    }

    public LiveData<String> getGreetingMessage() {
        return greetingMessage;
    }

    public void onRefreshClicked() {
        greetingRepo.refreshGreeting();
    }
}
