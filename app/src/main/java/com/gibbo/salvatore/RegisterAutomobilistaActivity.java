package com.gibbo.salvatore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterAutomobilistaActivity extends AppCompatActivity {

    private Button addToDB;
    private EditText usernameValue, mailValue, psswdValue, ageValue;
    private String keyName="Nome";
    private String keyMail="Email";
    private String keyPsswd="Password";
    private String keyAge="Age";


    //private Firebase mRootRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference();

    final DatabaseReference usersRef = ref.child("drivers"); //DA CAMBIARE PER IL DISTRIBUTORE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_automobilista);




        usernameValue = findViewById(R.id.signupNameA);
        mailValue = findViewById(R.id.signupEmailA);
        psswdValue = findViewById(R.id.signupPsswdA);
        ageValue = findViewById(R.id.signupAgeA);

        final Map<String, Object> users = new HashMap<>();

        addToDB = (Button) findViewById(R.id.regAutomobilista);


        usersRef.keepSynced(true);

        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameValue.getText().toString();
                String mail = mailValue.getText().toString();
                String psswd = psswdValue.getText().toString();
                String age = ageValue.getText().toString();

                users.put(username, new User(username, mail, psswd, age));
                usersRef.setValue(users);

                usersRef.push();

                //ref.updateChildren(users);
                goToMainActivity();
            }
        });


        /*nameValue = findViewById(R.id.signupNameA);
        mailValue = findViewById(R.id.signupEmailA);
        psswdValue = findViewById(R.id.signupPsswdA);
        ageValue = findViewById(R.id.signupAgeA);

        mRootRef = new Firebase("https://workingproject-880c3.firebaseio.com/"+contatore); //connetto il DB Firebase all'app

        addToDB = (Button) findViewById(R.id.regAutomobilista);

        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contatore++;
                String name = nameValue.getText().toString();
                String mail = mailValue.getText().toString();
                String psswd = psswdValue.getText().toString();
                String age = ageValue.getText().toString();

                Firebase childNameRef = mRootRef.child(keyName);
                childNameRef.setValue(name);
                Firebase childMailRef = mRootRef.child(keyMail);
                childMailRef.setValue(mail);
                Firebase childPsswdRef = mRootRef.child(keyPsswd);
                childPsswdRef.setValue(psswd);
                Firebase childAgeRef = mRootRef.child(keyAge);
                childAgeRef.setValue(age);

                goToMainActivity();
            }
        });*/
    }

    private void goToMainActivity(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

