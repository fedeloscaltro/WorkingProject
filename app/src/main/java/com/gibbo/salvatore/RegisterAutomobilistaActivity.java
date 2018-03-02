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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterAutomobilistaActivity extends AppCompatActivity {

    private Button addToDB;
    private EditText usernameValue, mailValue, psswdValue, ageValue;
    private String keyName="Name";
    private String keyMail="Email";
    private String keyPsswd="Password";
    private String keyAge="Age";


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("/drivers");
    private FirebaseAuth mAuth;
    final Map<String, Object> users = new HashMap<>();

    //final DatabaseReference usersRef = ref.child("drivers"); //DA CAMBIARE PER IL DISTRIBUTORE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_automobilista);

        mAuth = FirebaseAuth.getInstance();


        usernameValue = findViewById(R.id.signupNameA);
        mailValue = findViewById(R.id.signupEmailA);
        psswdValue = findViewById(R.id.signupPsswdA);
        ageValue = findViewById(R.id.signupAgeA);



        addToDB = (Button) findViewById(R.id.regAutomobilista);


        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mailValue.getText().toString();
                String psswd = psswdValue.getText().toString();


                createAccount(mail, psswd);

                /*users.put(username, new User(username, mail, psswd, age));

                ref.updateChildren(users);*/

                //goToMainActivity();
            }
        });
    }

    private void goToMainActivity(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser); per vedere se è loggato
    }

    private void updateUI(String mail, String psswd){

        String username = usernameValue.getText().toString();
        String age = ageValue.getText().toString();
        users.put(username, new User(username, mail, psswd, age));

        ref.updateChildren(users);
    }

    private void createAccount(final String email, final String password){
        /*controlli su email e password

        */

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("#", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(email, password);
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("#", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterAutomobilistaActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null, null);
                        }

                        // ...
                    }
                });
    }
}

