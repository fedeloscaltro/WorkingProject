package com.gibbo.salvatore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterDistributoreActivity extends AppCompatActivity {

    private Button addToDB;
    private EditText usernameValue, mailValue, psswdValue, sedeValue;
    private CheckBox checkBenzaValue, checkDieselValue, checkGPLValue, checkMetanoValue, checkElettricoValue;
    /*private String keyName="Name";
    private String keyMail="Email";
    private String keyPsswd="Password";*/


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("/dispensers");
    final DatabaseReference refAddr = database.getReference("/addresses");
    private FirebaseAuth mAuth;
    final Map<String, Object> users = new HashMap<>();
    final Map<String, Object> addresses = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_distributore);

        mAuth = FirebaseAuth.getInstance();

        usernameValue = findViewById(R.id.signupNameD);
        mailValue = findViewById(R.id.signupEmailD);
        psswdValue = findViewById(R.id.signupPsswdD);
        sedeValue = findViewById(R.id.signupAddressD);

        checkBenzaValue = findViewById(R.id.checkBenza);
        checkDieselValue = findViewById(R.id.checkDiesel);
        checkElettricoValue = findViewById(R.id.checkElettrico);
        checkGPLValue = findViewById(R.id.checkGPL);
        checkMetanoValue = findViewById(R.id.checkMetano);


        //bottone che, una volta cliccato, aggiunge i dati sul db
        addToDB = (Button) findViewById(R.id.regDistributore);

        //gestione aggiunta utenti su db
        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mailValue.getText().toString();
                String psswd = psswdValue.getText().toString();

                //funzione per creazione account
                createAccount(mail, psswd);
            }
        });
    }

    //porta utente nella schermata principale
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

    //crea e aggiorna l'entità dell'utente
    private void updateUI(String mail, String password){

        String username = usernameValue.getText().toString();
        String sede = sedeValue.getText().toString();
        ArrayList<String> carburanti = new ArrayList<String>();
        if(checkBenzaValue.isChecked()){
            carburanti.add(checkBenzaValue.getText().toString());
        }
        if (checkMetanoValue.isChecked()){
            carburanti.add(checkMetanoValue.getText().toString());
        }
        if (checkGPLValue.isChecked()){
            carburanti.add(checkGPLValue.getText().toString());
        }
        if (checkElettricoValue.isChecked()){
            carburanti.add(checkElettricoValue.getText().toString());
        }
        if (checkDieselValue.isChecked()){
            carburanti.add(checkDieselValue.getText().toString());
        }

        //inserisco i dati nella mappa Hash
        users.put(username, new User(username, mail, password, sede, carburanti));
        addresses.put(username, new Addresses(sede));

        //aggiorno il DB
        ref.updateChildren(users);
        refAddr.updateChildren(addresses);
    }

    //crea l'account con l'ausilio di email e password
    private void createAccount(final String email, final String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //se la registrazione è andata bene aggiorna il db con le informazioni di sing-in
                        if (task.isSuccessful()) {
                            Log.d("#", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(email, password);
                            goToMainActivity();
                        } else {//se la registrazione è fallita, mostra un messaggio
                            Log.w("#", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterDistributoreActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}


