package com.example.expense.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense.R;
import com.example.expense.Tools.DBHelper;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etForgotUsername, etForgotEmail;
    Button btnVerifyForgotPassword;
    ImageButton btnBackToLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Liên kết view
        etForgotUsername = findViewById(R.id.etForgotUsername);
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnVerifyForgotPassword = findViewById(R.id.btnVerifyForgotPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin); // Nút quay lại
        dbHelper = new DBHelper(this);

        // Xử lý sự kiện xác minh
        btnVerifyForgotPassword.setOnClickListener(this::onVerifyClick);

        // Xử lý sự kiện quay lại màn hình Login
        btnBackToLogin.setOnClickListener(this::onBackToLoginClick);
    }

    /**
     * Xử lý khi người dùng nhấn nút "Verify"
     */
    private void onVerifyClick(View v) {
        String username = etForgotUsername.getText().toString().trim();
        String email = etForgotEmail.getText().toString().trim();

        // Kiểm tra thông tin hợp lệ
        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Username and Email are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác minh username và email
        if (dbHelper.isUsernameOrEmailExists(username, email)) {
            // Chuyển sang màn hình ResetPassword
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra("username", username); // Truyền username sang màn hình Reset
            startActivity(intent);
            finish(); // Đóng màn hình hiện tại
        } else {
            Toast.makeText(this, "Invalid Username or Email.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút "Back to Login"
     */
    private void onBackToLoginClick(View v) {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng màn hình ForgotPassword
    }
}
