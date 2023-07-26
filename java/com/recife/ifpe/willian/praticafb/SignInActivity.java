package com.recife.ifpe.willian.praticafb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private Button btRegister;
    private Button btLogin;
    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth fbAuth;
    private FirebaseAuthListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViewsById();

        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);

        btRegister.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        btLogin.setOnClickListener(view -> {
            String login = etEmail.getText().toString();
            String passwd = etPassword.getText().toString();
            if (login.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Você deve preencher os campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(login, passwd)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
//                            startActivity(new Intent(SignInActivity.this,
//                                    HomeActivity.class));
                        } else {
                            Toast.makeText(SignInActivity.this, "SIGN IN ERROR!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void findViewsById() {
        btRegister = findViewById(R.id.login_register_button);
        btLogin = findViewById(R.id.login_login_button);
        etEmail = findViewById(R.id.login_edit_email);
        etPassword = findViewById(R.id.login_edit_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        fbAuth.removeAuthStateListener(authListener);
    }
}