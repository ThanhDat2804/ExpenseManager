package com.example.expense.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense.Model.UserModel;
import com.example.expense.R;
import com.example.expense.Tools.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername, etPassword, etEmail, etPhone, etAddress;
    Button btnRegister;
    Button btnBackToLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liên kết các view
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Gán sự kiện click cho nút "Đăng ký"
        btnRegister.setOnClickListener(v -> onClick(v));

        // Xử lý sự kiện khi nhấn nút quay lại login
        btnBackToLogin.setOnClickListener(v -> {
            // Chuyển sang màn hình LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void onClick(View v) {
        // Lấy dữ liệu từ form
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Xác thực dữ liệu
        if (!validateInput(username, password, email, phone, address)) {
            return;
        }

        // Kiểm tra username hoặc email trùng lặp
        int checkResult = checkDuplicates(username, email);
        switch (checkResult) {
            case 1:
                Toast.makeText(this, "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                return;
            case 2:
                Toast.makeText(this, "Email already exists. Please choose a different email.", Toast.LENGTH_SHORT).show();
                return;
            case 3:
                Toast.makeText(this, "Both Username and Email already exist. Please use different values.", Toast.LENGTH_SHORT).show();
                return;
            case 0:
                break; // Không có lỗi, tiếp tục
        }

        // Tạo đối tượng UserModel với dữ liệu từ form
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        // Lấy ngày giờ hiện tại cho created_at và updated_at
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        user.setCreated_at(currentDate);
        user.setUpdated_at(currentDate);

        // Lưu dữ liệu vào cơ sở dữ liệu
        if (dbHelper.registerUser(user)) {
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Kiểm tra username và email trùng lặp
    private int checkDuplicates(String username, String email) {
        boolean usernameExists = dbHelper.isUsernameExists(username);
        boolean emailExists = dbHelper.isEmailExists(email);

        if (usernameExists && emailExists) {
            return 3; // Cả username và email đều trùng
        } else if (usernameExists) {
            return 1; // Chỉ username trùng
        } else if (emailExists) {
            return 2; // Chỉ email trùng
        }
        return 0; // Không có trùng lặp
    }

    // Hàm xác thực dữ liệu đầu vào
    private boolean validateInput(String username, String password, String email, String phone, String address) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        // Biểu thức chính quy cho email hợp lệ
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Quay lại activity trước đó
    }
}
