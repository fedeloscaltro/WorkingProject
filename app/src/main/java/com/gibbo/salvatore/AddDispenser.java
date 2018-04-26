package com.gibbo.salvatore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddDispenser extends AppCompatActivity {

    EditText ragioneSociale;
    Button addDispenserButton;
    String defaultCompagnia = "Seleziona una compagnia";
    String ragioneSocialeValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispenser);

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

        ragioneSociale = findViewById(R.id.ragioneSociale);

        addDispenserButton = (Button) findViewById(R.id.addDispenserButton);

        addDispenserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ragioneSocialeValue = ragioneSociale.getText().toString();
                String compagniaValue = dropdown.getSelectedItem().toString();

                checkValidityFields(ragioneSocialeValue, compagniaValue);
            }
        });
    }

    private void goToMainActivity(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void checkValidityFields(String ragioneSocialeValue, String compagniaValue){

        if (ragioneSocialeValue.equals("") && compagniaValue.equals(defaultCompagnia)){
            Toast.makeText(AddDispenser.this, "Ragione sociale non valida",
                    Toast.LENGTH_LONG).show();
        }else if (compagniaValue.equals(defaultCompagnia) && !ragioneSocialeValue.equals("")){
            Toast.makeText(AddDispenser.this, "Compagnia non valida",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AddDispenser.this, "Tutto ok",
                    Toast.LENGTH_LONG).show();
            //goToMainActivity();
        }
    }
}
