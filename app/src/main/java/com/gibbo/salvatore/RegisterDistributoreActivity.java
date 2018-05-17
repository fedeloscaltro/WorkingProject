package com.gibbo.salvatore;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.firebase.client.Query;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterDistributoreActivity extends AppCompatActivity {

    private static final String TAG = RegisterDistributoreActivity.class.getSimpleName();
    private Button addToDB;
    private EditText usernameValue, mailValue, psswdValue, sedeValue, benzaPrice, dieselPrice, gplPrice, metanoPrice, elettricitàPrice;
    private CheckBox checkBenzaValue, checkDieselValue, checkGPLValue, checkMetanoValue, checkElettricoValue;


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

        benzaPrice = findViewById(R.id.benzaPrice);
        dieselPrice = findViewById(R.id.dieselPrice);
        gplPrice = findViewById(R.id.gplPrice);
        metanoPrice = findViewById(R.id.metanoPrice);
        elettricitàPrice = findViewById(R.id.elettricitàPrice);
        benzaPrice.setEnabled(false);
        dieselPrice.setEnabled(false);
        gplPrice.setEnabled(false);
        metanoPrice.setEnabled(false);
        elettricitàPrice.setEnabled(false);

        //bottone che, una volta cliccato, aggiunge i dati sul db
        addToDB = (Button) findViewById(R.id.regDistributore);


        ConstraintLayout cL = (ConstraintLayout) findViewById(R.id.constraintRegisterDistributore);
        cL.setBackground(new ImageLoader(this).doInBackground());

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

        checkBenzaValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBenzaValue.isChecked()){
                    benzaPrice.setEnabled(true);
                } else {
                    benzaPrice.setEnabled(false);
                }
            }
        });

        checkDieselValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDieselValue.isChecked()){
                    dieselPrice.setEnabled(true);
                } else {
                    dieselPrice.setEnabled(false);
                }
            }
        });

        checkMetanoValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMetanoValue.isChecked()){
                    metanoPrice.setEnabled(true);
                } else {
                    metanoPrice.setEnabled(false);
                }
            }
        });

        checkGPLValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGPLValue.isChecked()){
                    gplPrice.setEnabled(true);
                } else {
                    gplPrice.setEnabled(false);
                }
            }
        });

        checkElettricoValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkElettricoValue.isChecked()){
                    elettricitàPrice.setEnabled(true);
                } else {
                    elettricitàPrice.setEnabled(false);
                }
            }
        });
    }

    //porta utente nella schermata principale
    private void goToMainActivity(){
        queryDB(refAddr);
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_data", usernameValue.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putString("indirizzo", sedeValue.getText().toString());
        // set MyFragment Arguments
        MapsFragment myObj = new MapsFragment();
        myObj.setArguments(bundle);
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
        ArrayList<String> prezzi = new ArrayList<>();

        if(checkBenzaValue.isChecked()){
            carburanti.add(checkBenzaValue.getText().toString());
            prezzi.add(benzaPrice.getText().toString());
        }
        if (checkMetanoValue.isChecked()){
            carburanti.add(checkMetanoValue.getText().toString());
            prezzi.add(metanoPrice.getText().toString());
        }
        if (checkGPLValue.isChecked()){
            carburanti.add(checkGPLValue.getText().toString());
            prezzi.add(gplPrice.getText().toString());
        }
        if (checkElettricoValue.isChecked()){
            carburanti.add(checkElettricoValue.getText().toString());
            prezzi.add(elettricitàPrice.getText().toString());
        }
        if (checkDieselValue.isChecked()){
            carburanti.add(checkDieselValue.getText().toString());
            prezzi.add(dieselPrice.getText().toString());
        }

        //inserisco i dati nella mappa Hash
        users.put(username, new User(username, mail, password, sede));
        addresses.put(username, new Addresses(sede, carburanti, prezzi));

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
                            getLocationFromAddress(RegisterDistributoreActivity.this, sedeValue.getText().toString());
                            goToMainActivity();
                        } else {//se la registrazione è fallita, mostra un messaggio
                            Log.w("#", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterDistributoreActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Nullable
    private LatLng getLocationFromAddress(Context context, String strAddress){
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng position = null;

        try{
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            position = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
        return position;
    }

    public void queryDB(DatabaseReference refAddresses){
        com.google.firebase.database.Query query = refAddresses.child("addresses").child("address").equalTo(sedeValue.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Bundle bundle = new Bundle();
                    Addresses ad = (Addresses) dataSnapshot.getValue(false);
                    bundle.putString("indirizzo", ad.getAddress());
                    MapsFragment mapsFragment = new MapsFragment();
                    mapsFragment.setArguments(bundle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}


