package com.gibbo.salvatore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Button automobilista = (Button) findViewById(R.id.automobilista_button);
        final Button distributore = (Button) findViewById(R.id.distributore_button);
        automobilista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAutomobilista();
            }
        });
        distributore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDistributore();
            }
        });
    }
    private void goToAutomobilista(){
        final Intent intent = new Intent(this, RegisterAutomobilistaActivity.class);
        startActivity(intent);
    }
    private void goToDistributore(){
        final Intent intent = new Intent(this, RegisterDistributoreActivity.class);
        startActivity(intent);
    }
}
