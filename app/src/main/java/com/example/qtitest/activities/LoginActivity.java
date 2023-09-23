package com.example.qtitest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qtitest.R;
import com.example.qtitest.data.LoginRequest;
import com.example.qtitest.data.LoginResponse;
import com.example.qtitest.databinding.ActivityLoginBinding;
import com.example.qtitest.domain.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("logged", false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.emailTil.setHelperText(getResources().getString(R.string.required));
                binding.emailTil.setHelperTextEnabled(true);
                if (TextUtils.isEmpty(binding.passwordEt.getText().toString())) {
                    binding.passwordTil.setHelperText(getResources().getString(R.string.required));
                    binding.passwordTil.setHelperTextEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(binding.emailEt.getText().toString()) && !Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.getText().toString()).matches()) {
                    binding.emailTil.setHelperText(getResources().getString(R.string.invalid_email_format));
                    binding.emailTil.setHelperTextEnabled(true);
                } else if (!TextUtils.isEmpty(binding.emailEt.getText().toString())) {
                    binding.emailTil.setHelperText("");
                    binding.emailTil.setHelperTextEnabled(false);
                } else {
                    binding.emailTil.setHelperText(getResources().getString(R.string.required));
                    binding.emailTil.setHelperTextEnabled(true);
                    if (TextUtils.isEmpty(binding.passwordEt.getText().toString()) || binding.passwordEt.getText().toString().length() < 8) {
                        binding.passwordTil.setHelperText(getResources().getString(R.string.required));
                        binding.passwordTil.setHelperTextEnabled(true);
                    }
                }
            }
        });

        binding.passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.passwordTil.setHelperText(getResources().getString(R.string.required));
                binding.passwordTil.setHelperTextEnabled(true);
                if (TextUtils.isEmpty(binding.emailEt.getText().toString())) {
                    binding.emailTil.setHelperText(getResources().getString(R.string.required));
                    binding.emailTil.setHelperTextEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(binding.passwordEt.getText().toString()) && binding.passwordEt.getText().toString().length() > 8) {
                    binding.passwordTil.setHelperText("");
                    binding.passwordTil.setHelperTextEnabled(false);
                } else {
                    binding.passwordTil.setHelperText(getResources().getString(R.string.required));
                    binding.passwordTil.setHelperTextEnabled(true);
                }
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.passwordEt.getText().toString())
                        && binding.passwordEt.getText().toString().length() > 8
                        && !TextUtils.isEmpty(binding.emailEt.getText().toString())
                        && Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.getText().toString()).matches()) {
                    login(binding.emailEt.getText().toString(), binding.passwordEt.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Call<LoginResponse> loginResponseCall = APIClient.getService().loginUser(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    sharedPreferences.edit().putBoolean("logged", true).apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("data", loginResponse));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

    }
}