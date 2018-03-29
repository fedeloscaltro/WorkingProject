package com.gibbo.salvatore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText emailValue, passwordValue;

    private FirebaseAuth mAuth;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        final Button login = (Button) findViewById(R.id.login);
        final TextView register = (TextView) findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                goToLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistration();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        emailValue.findViewById(R.id.signInMail);
        passwordValue.findViewById(R.id.passwordSignIn);
    }
    private void goToLogin(){
        final Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToRegistration(){
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser currentUser){
        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String mail = currentUser.getEmail();
            boolean emailVerified = currentUser.isEmailVerified();
            String uid = currentUser.getUid();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //crea l'account con l'ausilio di email e password
    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("#", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("#", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
