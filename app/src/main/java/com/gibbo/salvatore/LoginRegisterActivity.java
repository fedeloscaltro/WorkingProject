package com.gibbo.salvatore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText emailValue, passwordValue;
    private TextView statusTextView;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        setContentView(R.layout.activity_login_register);
        final Button login = (Button) findViewById(R.id.login);
        final Button register = (Button) findViewById(R.id.signin);
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String email = emailValue.getText().toString();
                String password = passwordValue.getText().toString();
                signIn(email, password);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistration();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        emailValue = findViewById(R.id.signInMail);
        passwordValue = findViewById(R.id.passwordSignIn);
        statusTextView = findViewById(R.id.status);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void goToMainActivity(){
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
    }

    private void goToRegistration(){
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    //crea l'account con l'ausilio di email e password
    private void signIn(final String email, final String password) {

        Log.d("#", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("#", "signInWithEmail:success");
                            goToMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("#", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginRegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            statusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailValue.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailValue.setError("Required.");
            valid = false;
        } else {
            emailValue.setError(null);
        }

        String password = passwordValue.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordValue.setError("Required.");
            valid = false;
        } else {
            passwordValue.setError(null);
        }

        return valid;
    }

    public void showProgressDialog(){
        //progressBar.setMax(100);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressDialog(){
        progressBar.setVisibility(View.INVISIBLE);
    }
}

