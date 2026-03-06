package app.practice.androidmvvm.repositories;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.practice.androidmvvm.api.EndPoints;
import app.practice.androidmvvm.database.dao.PostDao;
import app.practice.androidmvvm.database.entities.PostEntity;
import retrofit2.Response;

public class PostsRepo {

    public interface CreatePostCallback {
        void onComplete(PostEntity postEntity);
    }

    public interface UpdatePostCallback {
        void onComplete(PostEntity postEntity);
    }

    public interface DeletePostCallback {
        void onComplete(Boolean success);
    }

    private final PostDao postDao;
    private final EndPoints endPoints;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public PostsRepo(PostDao postDao, EndPoints endPoints) {
        this.postDao = postDao;
        this.endPoints = endPoints;
    }

    public LiveData<String> observePostTitle(int postId) {
        return Transformations.map(
                postDao.observePost(postId),
                entity -> entity == null ? "Loading post title..." : entity.getTitle()
        );
    }

    public LiveData<String> observePostBody(int postId) {
        return Transformations.map(
                postDao.observePost(postId),
                entity -> entity == null ? "Loading post body..." : entity.getBody()
        );
    }

    private PostEntity destructureResponse(Response<Map<String, Object>> response) {
        // TODO: handle body == error message
        if (!response.isSuccessful() || response.body() == null) {
            return null;
        }

        Map<String, Object> body = response.body();
        Object id = body.get("id");
        Object userId = body.get("userId");
        Object title = body.get("title");
        Object postBody = body.get("body");

        if (!(id instanceof Number) || !(userId instanceof Number)) {
            return null;
        }

        return new PostEntity(
                ((Number) id).intValue(),
                ((Number) userId).intValue(),
                title == null ? "" : String.valueOf(title),
                postBody == null ? "" : String.valueOf(postBody)
        );
    }

    public void refreshPost(int postId) {
        executor.execute(() -> {
            try {
                Response<Map<String, Object>> response = endPoints.getPost(postId).execute();
                PostEntity postEntity = destructureResponse(response);
                if (postEntity != null) {
                    postDao.save(postEntity);
                }
            } catch (Exception ignored) {
            }
        });
    }

    public void createPost(Map<String, Object> post, CreatePostCallback callback) {
        executor.execute(() -> {
            PostEntity postEntity = null;
            try {
                Response<Map<String, Object>> response = endPoints.createPost(post).execute();
                postEntity = destructureResponse(response);
                if (postEntity != null) {
                    postDao.save(postEntity);
                }
            } catch (Exception ignored) {}

            if (callback != null) {
                PostEntity finalPostEntity = postEntity;
                mainHandler.post(() -> callback.onComplete(finalPostEntity));
            }
        });
    }

    public void updatePost(Integer id, Map<String, Object> post, UpdatePostCallback callback) {
        executor.execute(() -> {
            PostEntity postEntity = null;
            try {
                Response<Map<String, Object>> response = endPoints.updatePost(id, post).execute();
                postEntity = destructureResponse(response);
                if (postEntity != null) {
                    int updatedRows = postDao.update(postEntity);
                    if (updatedRows == 0) {
                        postDao.save(postEntity);
                    }
                }
            } catch (Exception ignored) {}

            if (callback != null) {
                PostEntity finalPostEntity = postEntity;
                mainHandler.post(() -> callback.onComplete(finalPostEntity));
            }
        });
    }

    public void deletePost(Integer id, DeletePostCallback callback) {
        executor.execute(() -> {
            boolean success = false;
            try {
                Response<Void> response = endPoints.deletePost(id).execute();
                success = response.isSuccessful();
                if (success) {
                    postDao.delete(id);
                }
            } catch (Exception ignored) {}

            if (callback != null) {
                boolean finalSuccess = success;
                mainHandler.post(() -> callback.onComplete(finalSuccess));
            }
        });
    }
}
