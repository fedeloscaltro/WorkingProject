package com.gibbo.salvatore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddDispenser extends AppCompatActivity {

    EditText indirizzo;
    Button addDispenserButton;
    String defaultCompagnia = "Seleziona una compagnia";
    String indirizzoValue = "";

    private EditText benzaPrice, dieselPrice, gplPrice, metanoPrice, elettricitàPrice;
    private CheckBox checkBenzaValue, checkDieselValue, checkGPLValue, checkMetanoValue, checkElettricoValue;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("addresses");

    final Map<String, Object> addresses = new HashMap<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispenser);

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

        indirizzo = findViewById(R.id.indirizzo);

        String s = getIntent().getStringExtra("dispenserToAdd");
        if (s != null){
            indirizzo.setText(s);
        }

        //Toast.makeText(AddDispenser.this, user.getEmail(), Toast.LENGTH_LONG).show();


        //get the spinner from the xml.
        final Spinner dropdown = (Spinner) findViewById(R.id.compagnia);
        //create a list of items for the spinner.
        final String[] items = new String[]{defaultCompagnia, "Agip Eni", "Pompe Bianche", "Q8", "Api-lp", "Esso", "Total Erg", "Tamoil",
                "Repsol", "Retitalia", "Europam", "KEROPETROL", "Costantin", "Giap", "Ego", "San Marco Petroli", "Shell", "Carrefour",
                "GNP", "Energas", "EnerCoop", "Metanauto", "Iren"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);


        addDispenserButton = (Button) findViewById(R.id.addDispenserButton);

        addDispenserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indirizzoValue = indirizzo.getText().toString();
                String compagniaValue = dropdown.getSelectedItem().toString();

                checkValidityFields(indirizzoValue, compagniaValue);
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

    private void checkValidityFields(String ragioneSocialeValue, String compagniaValue){

        if (ragioneSocialeValue.equals("") && compagniaValue.equals(defaultCompagnia)){
            Toast.makeText(AddDispenser.this, "Indirizzo non valido",
                    Toast.LENGTH_LONG).show();
        }else if (compagniaValue.equals(defaultCompagnia) && !ragioneSocialeValue.equals("")){
            Toast.makeText(AddDispenser.this, "Compagnia non valida",
                    Toast.LENGTH_LONG).show();
        } else {
            goToMainActivity();
        }
    }

    private void goToMainActivity(){
        final Intent intent = new Intent(this, MainActivity.class);

        String username = user.getEmail();
        int i =username.indexOf(".");
        String substring = username.substring(i, username.length());
        username = username.replace(substring, "");

        String indirizzoValue = indirizzo.getText().toString();
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

        addresses.put(username, new Addresses(indirizzoValue, carburanti, prezzi));
        Addresses a = new Addresses(indirizzoValue, carburanti, prezzi);
        ref.updateChildren(addresses);

        String[] array = {username, indirizzoValue};
        //intent.putExtra("add_dispenser", array);
        intent.putExtra("add_dispenser", indirizzoValue);
        startActivity(intent);
    }

}