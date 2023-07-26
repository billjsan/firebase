package com.recife.ifpe.willian.praticafb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recife.ifpe.willian.praticafb.model.User;

public class SignUpActivity extends AppCompatActivity {


    private Button btRegister;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth fbAuth;
    private FirebaseAuthListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);

        findViewsById();
        btRegister.setOnClickListener(view -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Você deve preencher os campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        String msg = task.isSuccessful() ? "SIGN UP OK!": "SIGN UP ERROR!";
                        Toast.makeText(SignUpActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {
                            User tempUser = new User(name, email);
                            DatabaseReference drUsers = FirebaseDatabase.
                                    getInstance().getReference("users");
                            drUsers.child(mAuth.getCurrentUser().getUid()).
                                    setValue(tempUser);
                        }
                    });
        });
    }

    private void findViewsById() {
        btRegister = findViewById(R.id.login_login_button);
        etName = findViewById(R.id.edit_name);
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