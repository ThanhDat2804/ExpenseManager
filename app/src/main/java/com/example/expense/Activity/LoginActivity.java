package com.example.expense.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense.R;
import com.example.expense.Tools.DBHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword; // Thêm TextView quên mật khẩu
    private ImageView ivLogo;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Sử dụng layout mới được tạo
        initializeUI();

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.validateLoginWithUsernameOrEmail(usernameOrEmail, password)) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nhấn "Đăng ký"
        tvRegister.setOnClickListener(v -> navigateToRegister());

        // Xử lý sự kiện nhấn "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initializeUI() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Ánh xạ nút quên mật khẩu
        ivLogo = findViewById(R.id.ivLogo);
        dbHelper = new DBHelper(this);

        // Đặt logo hoặc chỉnh sửa thêm nếu cần
        ivLogo.setImageResource(R.drawable.ic_launcher_foreground); // Đổi thành logo phù hợp
    }

    /**
     * Điều hướng đến màn hình chính
     */
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Điều hướng đến màn hình đăng ký
     */
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Điều hướng đến màn hình quên mật khẩu
     */
    private void navigateToForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
