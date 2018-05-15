package com.gibbo.salvatore;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //String[] accountData = new String[5];
    private FirebaseAuth mAuth;
    TextView textViewUsername;
    TextView textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        /*RegisterAutomobilistaActivity activity = new RegisterAutomobilistaActivity();
        //accountData = activity.getData();

        TextView accountUsername = findViewById(R.id.nav_view).findViewById(R.id.accountUsername);
        TextView accountMail = findViewById(R.id.nav_view).findViewById(R.id.accountMail);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null) {
            accountUsername.setText(user.getDisplayName());
            accountMail.setText(user.getEmail());
        }*/


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creazione finestra di dialogo per inserire un nuovo distributore
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog ); //android.R.style.Theme_Material_Dialog_Alert
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                }
                //View dialogView = View.inflate(MainActivity.this, );
                builder.setTitle("Aggiungi un distributore").setMessage("Sei sicuro di voler aggiungere la posizione di un distributore?")
                        .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                goToAddDispenser();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        }).setIcon(android.R.drawable.ic_input_add).show();


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mHeaderView = navigationView.getHeaderView(0);

        //https://stackoverflow.com/questions/43023042/android-firebase-database-fetch-username-by-user-id

        textViewUsername = (TextView) mHeaderView.findViewById(R.id.accountUsername);
        textViewEmail= (TextView) mHeaderView.findViewById(R.id.accountMail);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("/drivers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //dataSnapshot.getRef().getKey();
                textViewUsername.setText(dataSnapshot.getChildren().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //textViewUsername.setText(firebaseUser.getUid());
        textViewEmail.setText(firebaseUser.getEmail());
    }

    //per settare il titolo dell'Activity
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        setTitle("Salvatore");
        return super.onCreateView(name, context, attrs);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            goToSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSettings(){
        final Intent intent = new Intent(this, SettingsAutomobilista.class);
        startActivity(intent);
    }

    private void goToAddDispenser(){
        final Intent intent = new Intent(this, AddDispenser.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Gestire gli elementi della navigation view cliccati qui.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            goToAddDispenser();
        } else if (id == R.id.nav_manage) {
            goToSettings();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
