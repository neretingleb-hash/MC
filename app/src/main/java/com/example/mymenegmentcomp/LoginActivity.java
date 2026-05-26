package com.example.mymenegmentcomp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etPersonalAccount;
    private EditText etPassword;
    private Button btnLogin;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        sharedPrefs = getSharedPreferences("uk_app_prefs", Context.MODE_PRIVATE);
        checkSavedLogin();
        setupClickListeners();
    }

    private void initViews() {
        etPersonalAccount = findViewById(R.id.etPersonalAccount);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkSavedLogin() {
        String savedAccount = sharedPrefs.getString("personal_account", null);
        String savedPassword = sharedPrefs.getString("password", null);
        boolean isRemembered = sharedPrefs.getBoolean("remember_me", false);

        if (isRemembered && savedAccount != null && savedPassword != null) {
            etPersonalAccount.setText(savedAccount);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String account = etPersonalAccount.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (account.isEmpty()) {
                etPersonalAccount.setError("Введите лицевой счет");
                etPersonalAccount.requestFocus();
            } else if (password.isEmpty()) {
                etPassword.setError("Введите пароль");
                etPassword.requestFocus();
            } else {
                performLogin(account, password);
            }
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Свяжитесь с УК для восстановления пароля", Toast.LENGTH_LONG).show()
        );
    }

    private void performLogin(String account, String password) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnLogin.setText("Вход...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("Войти");

            if (account.length() >= 5 && password.length() >= 3) {
                if (cbRememberMe.isChecked()) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("personal_account", account);
                    editor.putString("password", password);
                    editor.putBoolean("remember_me", true);
                    editor.apply();
                }

                // Сохраняем текущего пользователя глобально
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("current_user", account);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("personal_account", account);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Неверный лицевой счет или пароль", Toast.LENGTH_LONG).show();
                etPassword.setText("");
                etPassword.requestFocus();
            }
        }, 1500);
    }
}