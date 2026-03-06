package app.practice.androidmvvm.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import app.practice.androidmvvm.R;
import app.practice.androidmvvm.viewmodels.PostsVM;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView postTitle = findViewById(R.id.tvPostTitle);
        TextView postBody = findViewById(R.id.tvPostBody);
        EditText postIdInput = findViewById(R.id.etPostId);
        EditText createUserIdInput = findViewById(R.id.etCreateUserId);
        EditText createTitleInput = findViewById(R.id.etCreateTitle);
        EditText createBodyInput = findViewById(R.id.etCreateBody);
        Button loadButton = findViewById(R.id.btnLoadPost);
        Button refreshButton = findViewById(R.id.btnRefreshPost);
        Button createButton = findViewById(R.id.btnCreatePost);
        Button updateButton = findViewById(R.id.btnUpdatePost);
        Button deleteButton = findViewById(R.id.btnDeletePost);

        PostsVM postsVM = new ViewModelProvider(this).get(PostsVM.class);
        postIdInput.setText(String.valueOf(postsVM.getSelectedPostId()));

        postsVM.getPostTitle().observe(this, postTitle::setText);
        postsVM.getPostBody().observe(this, postBody::setText);

        loadButton.setOnClickListener(v -> {
            String value = postIdInput.getText().toString().trim();
            if (value.isEmpty()) {
                postIdInput.setError(getString(R.string.posts_id_required));
                return;
            }

            int postId;
            try {
                postId = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                postIdInput.setError(getString(R.string.posts_id_invalid));
                return;
            }

            if (postId < PostsVM.MIN_POST_ID || postId > PostsVM.MAX_POST_ID) {
                postIdInput.setError(getString(R.string.posts_id_range, PostsVM.MIN_POST_ID, PostsVM.MAX_POST_ID));
                return;
            }

            postIdInput.setError(null);
            postsVM.loadPost(postId);
            Toast.makeText(this, getString(R.string.posts_loading, postId), Toast.LENGTH_SHORT).show();
        });

        refreshButton.setOnClickListener(v -> postsVM.onRefreshPostClicked());

        createButton.setOnClickListener(v -> {
            String userIdValue = createUserIdInput.getText().toString().trim();
            String titleValue = createTitleInput.getText().toString().trim();
            String bodyValue = createBodyInput.getText().toString().trim();

            if (userIdValue.isEmpty()) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_required));
                return;
            }
            createUserIdInput.setError(null);

            int userId;
            try {
                userId = Integer.parseInt(userIdValue);
            } catch (NumberFormatException e) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_invalid));
                return;
            }

            if (userId < PostsVM.MIN_USER_ID) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_min, PostsVM.MIN_USER_ID));
                return;
            }
            createUserIdInput.setError(null);

            if (titleValue.isEmpty()) {
                createTitleInput.setError(getString(R.string.posts_create_title_required));
                return;
            }
            createTitleInput.setError(null);

            if (bodyValue.isEmpty()) {
                createBodyInput.setError(getString(R.string.posts_create_body_required));
                return;
            }
            createBodyInput.setError(null);

            postsVM.createPost(userId, titleValue, bodyValue);
            Toast.makeText(this, R.string.posts_creating, Toast.LENGTH_SHORT).show();

            createTitleInput.setText("");
            createBodyInput.setText("");
        });

        updateButton.setOnClickListener(v -> {
            String postIdValue = postIdInput.getText().toString().trim();
            String userIdValue = createUserIdInput.getText().toString().trim();
            String titleValue = createTitleInput.getText().toString().trim();
            String bodyValue = createBodyInput.getText().toString().trim();

            if (postIdValue.isEmpty()) {
                postIdInput.setError(getString(R.string.posts_id_required));
                return;
            }
            postIdInput.setError(null);

            int postId;
            try {
                postId = Integer.parseInt(postIdValue);
            } catch (NumberFormatException e) {
                postIdInput.setError(getString(R.string.posts_id_invalid));
                return;
            }

            if (postId < PostsVM.MIN_POST_ID || postId > PostsVM.MAX_POST_ID) {
                postIdInput.setError(getString(R.string.posts_id_range, PostsVM.MIN_POST_ID, PostsVM.MAX_POST_ID));
                return;
            }
            postIdInput.setError(null);

            if (userIdValue.isEmpty()) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_required));
                return;
            }
            createUserIdInput.setError(null);

            int userId;
            try {
                userId = Integer.parseInt(userIdValue);
            } catch (NumberFormatException e) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_invalid));
                return;
            }

            if (userId < PostsVM.MIN_USER_ID) {
                createUserIdInput.setError(getString(R.string.posts_create_user_id_min, PostsVM.MIN_USER_ID));
                return;
            }
            createUserIdInput.setError(null);

            if (titleValue.isEmpty()) {
                createTitleInput.setError(getString(R.string.posts_create_title_required));
                return;
            }
            createTitleInput.setError(null);

            if (bodyValue.isEmpty()) {
                createBodyInput.setError(getString(R.string.posts_create_body_required));
                return;
            }
            createBodyInput.setError(null);

            postsVM.updatePost(userId, postId, titleValue, bodyValue);
            Toast.makeText(this, getString(R.string.posts_updating, postId), Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            String postIdValue = postIdInput.getText().toString().trim();
            if (postIdValue.isEmpty()) {
                postIdInput.setError(getString(R.string.posts_id_required));
                return;
            }
            postIdInput.setError(null);

            int postId;
            try {
                postId = Integer.parseInt(postIdValue);
            } catch (NumberFormatException e) {
                postIdInput.setError(getString(R.string.posts_id_invalid));
                return;
            }

            if (postId < PostsVM.MIN_POST_ID || postId > PostsVM.MAX_POST_ID) {
                postIdInput.setError(getString(R.string.posts_id_range, PostsVM.MIN_POST_ID, PostsVM.MAX_POST_ID));
                return;
            }
            postIdInput.setError(null);

            postsVM.deletePost(postId);
            Toast.makeText(this, getString(R.string.posts_deleting, postId), Toast.LENGTH_SHORT).show();
            postIdInput.setText(String.valueOf(PostsVM.MIN_POST_ID));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
