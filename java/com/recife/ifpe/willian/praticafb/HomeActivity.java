package com.recife.ifpe.willian.praticafb;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.recife.ifpe.willian.praticafb.model.Message;
import com.recife.ifpe.willian.praticafb.model.User;

public class HomeActivity extends AppCompatActivity {

    private Button btSair;
    private FirebaseAuth fbAuth;
    private FirebaseAuthListener authListener;
    private ViewGroup vgChat;
    private Button btEnviar;
    private EditText etMensagem;
    private DatabaseReference drUser;
    private DatabaseReference drChat;
    private TextView txWelcome;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);
        setContentView(R.layout.activity_home);
        setTitle("Pratica07");
        findViews();
        btSair.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                mAuth.signOut();
            } else {
                Toast.makeText(HomeActivity.this, "Erro!", Toast.LENGTH_SHORT).show();
            }
        });

        btEnviar.setOnClickListener(view -> {
            String message = etMensagem.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Nada para enviar!", Toast.LENGTH_SHORT).show();
                return;
            }
            etMensagem.setText("");
            drChat.push().setValue(new Message(user.getName(), message));
        });

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
        FirebaseUser fbUser = fbAuth.getCurrentUser();
        drUser = fbDB.getReference("users/" + fbUser.getUid());
        drChat = fbDB.getReference("chat");
        drUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tempUser = dataSnapshot.getValue(User.class);
                if (tempUser != null) {
                    HomeActivity.this.user = tempUser;
                    txWelcome.setText("Welcome " + tempUser.getName() + "!");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        drChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                showMessage(message);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void findViews() {
        vgChat = findViewById(R.id.chat_area);
        btSair = findViewById(R.id.sair);
        btEnviar = findViewById(R.id.botao_enviar);
        etMensagem = findViewById(R.id.edit_message);
        txWelcome = findViewById(R.id.bem_vindo);
    }

    private void showMessage(Message message) {
        TextView tvMsg = new TextView(this);
        tvMsg.setText(message.getName() + ": " + message.getText());
        tvMsg.setTextSize(27);
        vgChat.addView(tvMsg);
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