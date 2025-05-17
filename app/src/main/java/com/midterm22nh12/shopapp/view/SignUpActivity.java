package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, phoneEditText;
    private Spinner roleSpinner;
    private TextView loginTextView;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        phoneEditText = findViewById(R.id.editTextPhone);
        roleSpinner = findViewById(R.id.dropdownRole);
        loginTextView = findViewById(R.id.textViewLogin);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Khách hàng", "Chủ cửa hàng"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Observers
        viewModel.getMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show());

        viewModel.getNavigateToLogin().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Button listeners
        findViewById(R.id.buttonSignUp).setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();
            viewModel.signUp(name, email, password, phone, role);
        });

        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }
}
