package com.gibbo.salvatore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterAutomobilistaActivity extends AppCompatActivity {

    private Button addToDB;
    private EditText usernameValue, mailValue, psswdValue, dateOfBirth;
    private RadioButton maleBtnA, femaleBtnA;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("/drivers");
    private FirebaseAuth mAuth;
    final Map<String, Object> users = new HashMap<>();
    private static RadioGroup radioButtonGroup;
    //Firebase.AuthStateListener mAuthListener;


    private int mYear,mMonth,mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_automobilista);

        mAuth = FirebaseAuth.getInstance();

        maleBtnA = findViewById(R.id.maleBtnA);
        usernameValue = findViewById(R.id.signupNameA);
        mailValue = findViewById(R.id.signupEmailA);
        psswdValue = findViewById(R.id.signupPsswdA);

        dateOfBirth = findViewById(R.id.signupAgeA);

        radioButtonGroup = (RadioGroup) findViewById(R.id.gender_radio_group);


        //bottone che, una volta cliccato, aggiunge i dati sul db
        addToDB = (Button) findViewById(R.id.regAutomobilista);

        ConstraintLayout cL = (ConstraintLayout) findViewById(R.id.constraintRegisterAutomobilista);
        cL.setBackground(new ImageLoader(this).doInBackground());

        //gestione aggiunta utenti su db
        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mailValue.getText().toString();
                String psswd = psswdValue.getText().toString();
                String newPsswd = Util.md5(psswd);

                //funzione per creazione account
                createAccount(mail, newPsswd);
            }
        });


        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // myCalendar.add(Calendar.DATE, 0);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateOfBirth.setText(sdf.format(myCalendar.getTime()));
            }


        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(RegisterAutomobilistaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox

                                if (year < mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (monthOfYear < mMonth && year == mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                                    view.updateDate(mYear,mMonth,mDay);

                                dateOfBirth.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();

            }
        });

        /*mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull AuthData authData) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    //User is signed in
                    //NOTE: this Activity should get open when the user is not signed in, otherwise
                    //the user will receive another verification email
                    sendVerificationEmail();
                } else {
                    //User is signed out

                }
            }
        };*/

    }

    //porta utente nella schermata principale
    private void goToMainActivity(){
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_data", usernameValue.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser); per vedere se è loggato
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
                            updateDB(email, password);
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("Registration", String.valueOf(task.isSuccessful()));
                                }
                            });
                            //mAuthListener.notify();

                            goToMainActivity();
                        } else {//se la registrazione è fallita, mostra un messaggio
                            Log.w("#", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterAutomobilistaActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //crea e aggiorna l'entità dell'utente
    private void updateDB(String mail, String password){

        String gender;
        String username = usernameValue.getText().toString();
        String birthday = dateOfBirth.getText().toString();
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        if (((RadioButton) findViewById(radioButtonID)).getText().equals("Uomo")){
            gender="Uomo";
        } else {
            gender="Donna";
        }

        //inserisco i dati nella mappa Hash
        users.put(username, new User(username, mail, password, birthday, gender));

        //aggiorno il DB
        ref.updateChildren(users);
    }

    /*public void getData(){

        String gender;
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        if (((RadioButton) findViewById(radioButtonID)).getText().equals("Uomo")){
            gender="Uomo";
        } else {
            gender="Donna";
        }

        String[] data = new String[5];
        String[] values = {usernameValue.getText().toString(), mailValue.getText().toString(), psswdValue.getText().toString(), dateOfBirth.getText().toString(), gender};

        for (int i=0; i<data.length; i++){
            data[i]= values[i];
        }

        Bundle bundle = new Bundle();
        bundle.putString("dataAccount", data.toString());
        // set Fragmentclass Arguments
        MapsFragment fragobj = new MapsFragment();
        fragobj.setArguments(bundle);

        /*String gender;
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        if (((RadioButton) findViewById(radioButtonID)).getText().equals("Uomo")){
            gender="Uomo";
        } else {
            gender="Donna";
        }

        String[] data = new String[5];
        String[] values = {usernameValue.getText().toString(), mailValue.getText().toString(), psswdValue.getText().toString(), dateOfBirth.getText().toString(), gender};

        for (int i=0; i<data.length; i++){
            data[i]= values[i];
        }

    }*/

    /*private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //email sent

                            //after email is sent just logout the user and finish this activity
                            /*FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegisterAutomobilistaActivity.this, getClass()));
                            finish();*/
                            /*Toast.makeText(RegisterAutomobilistaActivity.this, "EMAIL VERIFIED", Toast.LENGTH_LONG);
                        } else {
                            //email not sent, so display message and restart the activity or do whatever you wish to do

                                //restart this acrivity
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0,0);
                                startActivity(getIntent());
                        }
                    }
                });
    }*/
}

