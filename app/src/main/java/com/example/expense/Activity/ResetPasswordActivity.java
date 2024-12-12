package com.example.expense.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense.R;
import com.example.expense.Tools.DBHelper;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText etNewPassword, etConfirmPassword;
    Button btnResetPassword;
    ImageButton btnBackToLogin;
    DBHelper dbHelper;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Liên kết view
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        dbHelper = new DBHelper(this);

        // Lấy username từ Intent
        username = getIntent().getStringExtra("username");

        // Xử lý sự kiện đổi mật khẩu
        btnResetPassword.setOnClickListener(this::onResetClick);

        // Xử lý sự kiện quay lại màn hình Login
        btnBackToLogin.setOnClickListener(this::onBackToLoginClick);
    }

    /**
     * Xử lý khi người dùng nhấn nút Reset Password
     */
    private void onResetClick(View v) {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Both password fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đổi mật khẩu trong cơ sở dữ liệu
        if (dbHelper.updatePassword(username, newPassword)) {
            Toast.makeText(this, "Password reset successful.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút Back to Login
     */
    public void onBackToLoginClick(View view) {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Xử lý khi nhấn nút Back trên thanh ActionBar (nếu có)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
