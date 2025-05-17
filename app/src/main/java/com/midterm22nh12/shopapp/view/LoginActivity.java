package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.entity.user;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.view.CustomerHomeActivity;
import com.midterm22nh12.shopapp.view.RestaurantOwnerHomeActivity;
import com.midterm22nh12.shopapp.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private LoginViewModel loginViewModel; // 👈 ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signUpTextView = findViewById(R.id.textViewSignUp);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class); // 👈 lấy ViewModel

        loginButton.setOnClickListener(v -> handleLogin());

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 quan sát kết quả từ LiveData
        loginViewModel.validateUser(email, password).observe(this, validatedUser -> {
            if (validatedUser == null) {
                Toast.makeText(this, "Thông tin tài khoản không đúng, xin mời nhập lại", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userID", validatedUser.getId());
                editor.putString("gmail", validatedUser.getGmail());
                editor.putInt("role", validatedUser.getRole());
                editor.apply();

                Intent intent;
                if (validatedUser.getRole() == 0) {
                    intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, RestaurantOwnerHomeActivity.class);
                }
                intent.putExtra("user", validatedUser);
                startActivity(intent);
                finish();
            }
        });
    }
}
