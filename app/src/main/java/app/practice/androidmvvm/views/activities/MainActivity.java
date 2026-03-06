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

    private TextView postTitle;
    private TextView postBody;
    private EditText postIdInput;
    private EditText createUserIdInput;
    private EditText createTitleInput;
    private EditText createBodyInput;
    private PostsVM postsVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bindViews();
        postsVM = new ViewModelProvider(this).get(PostsVM.class);

        postIdInput.setText(String.valueOf(postsVM.getSelectedPostId()));
        postsVM.getPostTitle().observe(this, postTitle::setText);
        postsVM.getPostBody().observe(this, postBody::setText);

        setupButtonListeners();
        applyInsets();
    }

    private void bindViews() {
        postTitle = findViewById(R.id.tvPostTitle);
        postBody = findViewById(R.id.tvPostBody);
        postIdInput = findViewById(R.id.etPostId);
        createUserIdInput = findViewById(R.id.etCreateUserId);
        createTitleInput = findViewById(R.id.etCreateTitle);
        createBodyInput = findViewById(R.id.etCreateBody);
    }

    private void setupButtonListeners() {
        Button loadButton = findViewById(R.id.btnLoadPost);
        Button refreshButton = findViewById(R.id.btnRefreshPost);
        Button createButton = findViewById(R.id.btnCreatePost);
        Button updateButton = findViewById(R.id.btnUpdatePost);
        Button deleteButton = findViewById(R.id.btnDeletePost);

        loadButton.setOnClickListener(v -> onLoadClicked());
        refreshButton.setOnClickListener(v -> postsVM.onRefreshPostClicked());
        createButton.setOnClickListener(v -> onCreateClicked());
        updateButton.setOnClickListener(v -> onUpdateClicked());
        deleteButton.setOnClickListener(v -> onDeleteClicked());
    }

    private void onLoadClicked() {
        Integer postId = parsePostId(postIdInput);
        if (postId == null) {
            return;
        }

        postsVM.loadPost(postId);
        Toast.makeText(this, getString(R.string.posts_loading, postId), Toast.LENGTH_SHORT).show();
    }

    private void onCreateClicked() {
        Integer userId = parseUserId(createUserIdInput);
        if (userId == null) {
            return;
        }

        String titleValue = requireText(createTitleInput, R.string.posts_create_title_required);
        if (titleValue == null) {
            return;
        }

        String bodyValue = requireText(createBodyInput, R.string.posts_create_body_required);
        if (bodyValue == null) {
            return;
        }

        postsVM.createPost(userId, titleValue, bodyValue);
        Toast.makeText(this, R.string.posts_creating, Toast.LENGTH_SHORT).show();

        createTitleInput.setText("");
        createBodyInput.setText("");
    }

    private void onUpdateClicked() {
        Integer postId = parsePostId(postIdInput);
        if (postId == null) {
            return;
        }

        Integer userId = parseUserId(createUserIdInput);
        if (userId == null) {
            return;
        }

        String titleValue = requireText(createTitleInput, R.string.posts_create_title_required);
        if (titleValue == null) {
            return;
        }

        String bodyValue = requireText(createBodyInput, R.string.posts_create_body_required);
        if (bodyValue == null) {
            return;
        }

        postsVM.updatePost(userId, postId, titleValue, bodyValue);
        Toast.makeText(this, getString(R.string.posts_updating, postId), Toast.LENGTH_SHORT).show();
    }

    private void onDeleteClicked() {
        Integer postId = parsePostId(postIdInput);
        if (postId == null) {
            return;
        }

        postsVM.deletePost(postId);
        Toast.makeText(this, getString(R.string.posts_deleting, postId), Toast.LENGTH_SHORT).show();
        postIdInput.setText(String.valueOf(PostsVM.MIN_POST_ID));
    }

    private Integer parsePostId(EditText input) {
        String value = requireText(input, R.string.posts_id_required);
        if (value == null) {
            return null;
        }

        int postId;
        try {
            postId = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            input.setError(getString(R.string.posts_id_invalid));
            return null;
        }

        if (postId < PostsVM.MIN_POST_ID || postId > PostsVM.MAX_POST_ID) {
            input.setError(getString(R.string.posts_id_range, PostsVM.MIN_POST_ID, PostsVM.MAX_POST_ID));
            return null;
        }

        input.setError(null);
        return postId;
    }

    private Integer parseUserId(EditText input) {
        String value = requireText(input, R.string.posts_create_user_id_required);
        if (value == null) {
            return null;
        }

        int userId;
        try {
            userId = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            input.setError(getString(R.string.posts_create_user_id_invalid));
            return null;
        }

        if (userId < PostsVM.MIN_USER_ID) {
            input.setError(getString(R.string.posts_create_user_id_min, PostsVM.MIN_USER_ID));
            return null;
        }

        input.setError(null);
        return userId;
    }

    private String requireText(EditText input, int errorMessageResId) {
        String value = input.getText().toString().trim();
        if (value.isEmpty()) {
            input.setError(getString(errorMessageResId));
            return null;
        }

        input.setError(null);
        return value;
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return insets;
        });
    }
}
