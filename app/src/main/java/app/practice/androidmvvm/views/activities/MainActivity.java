package app.practice.androidmvvm.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import app.practice.androidmvvm.R;
import app.practice.androidmvvm.viewmodels.GreetingVM;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView messageText = findViewById(R.id.tvMessage);
        Button refreshButton = findViewById(R.id.btnRefresh);
        GreetingVM greetingVM = new ViewModelProvider(this).get(GreetingVM.class);

        greetingVM.getGreetingMessage().observe(this, messageText::setText);
        refreshButton.setOnClickListener(v -> greetingVM.onRefreshClicked());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
