package app.practice.androidmvvm.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.HashMap;
import java.util.Map;

import app.practice.androidmvvm.api.EndPoints;
import app.practice.androidmvvm.database.AppDatabase;
import app.practice.androidmvvm.repositories.PostsRepo;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostsVM extends AndroidViewModel {

    private static final int DEFAULT_POST_ID = 1;
    public static final int MIN_POST_ID = 1;
    public static final int MAX_POST_ID = 100;
    public static final int MIN_USER_ID = 1;
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private final PostsRepo postsRepo;
    private final MutableLiveData<Integer> selectedPostId = new MutableLiveData<>(DEFAULT_POST_ID);
    private final LiveData<String> postTitle;
    private final LiveData<String> postBody;

    public PostsVM(@NonNull Application application) {
        super(application);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EndPoints endPoints = retrofit.create(EndPoints.class);

        postsRepo = new PostsRepo(
                AppDatabase.getInstance(application).postDao(),
                endPoints
        );
        postTitle = Transformations.switchMap(selectedPostId, postsRepo::observePostTitle);
        postBody = Transformations.switchMap(selectedPostId, postsRepo::observePostBody);
        postsRepo.refreshPost(DEFAULT_POST_ID);
    }

    public LiveData<String> getPostTitle() {
        return postTitle;
    }

    public LiveData<String> getPostBody() {
        return postBody;
    }

    public int getSelectedPostId() {
        Integer postId = selectedPostId.getValue();
        return postId == null ? DEFAULT_POST_ID : postId;
    }

    public void loadPost(int postId) {
        selectedPostId.setValue(postId);
        postsRepo.refreshPost(postId);
    }

    public void onRefreshPostClicked() {
        postsRepo.refreshPost(getSelectedPostId());
    }

    public void createPost(int userId, String title, String body) {
        Map<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("title", title);
        post.put("body", body);

        postsRepo.createPost(post, createdPost -> {
            if (createdPost != null) {
                selectedPostId.setValue(createdPost.getId());
            }
        });
    }

    public void updatePost(int userId, int postId, String title, String body) {
        Map<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("id", postId);
        post.put("title", title);
        post.put("body", body);

        postsRepo.updatePost(postId, post, updatedPost -> {
            if (updatedPost != null) {
                selectedPostId.setValue(updatedPost.getId());
            }
        });
    }

    public void deletePost(int id) {
        postsRepo.deletePost(id, success -> {
            if (success) {
                selectedPostId.setValue(DEFAULT_POST_ID);
                postsRepo.refreshPost(DEFAULT_POST_ID);
            }
        });
    }
}
