package com.gibbo.salvatore;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuInflater;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //String[] accountData = new String[5];
    private FirebaseAuth mAuth;
    TextView textViewUsername;
    TextView textViewEmail;
    GoogleMap mGoogleMap;
    final int uniqueId = 45678;
    String EXTRA_NOTIFICATION_ID = "1232";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        String dispenserAdded = getIntent().getStringExtra("add_dispenser");
        if (dispenserAdded!=null) {
            /*Toast.makeText(MainActivity.this, dispenserAdded,
                    Toast.LENGTH_LONG).show();*/
            //geoLocate(dispenserAdded);
        }

        /*-------------------------------BLOCCO CODICE NOTIFICHE DA ELIMINARE------------------------------------------------------*/
        createNotificationChannel();

        Intent snoozeIntent = new Intent(this, AddDispenser.class);
        snoozeIntent.setAction("com.gibbo.salvatore.AddDispenser");
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);






        // Create an Intent for the activity you want to start
        Intent addDispenserIntent = new Intent(this, AddDispenser.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(addDispenserIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "12")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Vuoi aggiornare i prezzi di questo distributore?")
                .setContentText("Sei passato vicino a LUOGO, vuoi aggiornarne i prezzi?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Sei passato vicino a XXXXXXX LUOGO, vuoi aggiornarne i prezzi?"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.ic_notifications_black_24dp, "Sì",
                        snoozePendingIntent)
                .addAction(R.drawable.ic_notifications_black_24dp, "No",
                        snoozePendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setContentIntent(resultPendingIntent);


        final NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(this);

        /*-------------------------------------------------------------------------------------*/



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotificationChannel();

                notificationManager1.notify(uniqueId, builder.build());

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


            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mHeaderView = navigationView.getHeaderView(0);

        //https://stackoverflow.com/questions/43023042/android-firebase-database-fetch-username-by-user-id

        textViewUsername = mHeaderView.findViewById(R.id.accountUsername);
        textViewEmail= mHeaderView.findViewById(R.id.accountMail);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("/drivers");
        //final Query query = ref.orderByChild("name").equalTo(firebaseUser.getEmail());

        String s = getIntent().getStringExtra("user_data");

        textViewUsername.setText(s);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        //View i = (View) menu.findItem(R.id.action_settings);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refDr = database.getReference("/drivers");
        final DatabaseReference refDi = database.getReference("/dispensers");
        final FirebaseUser user = mAuth.getCurrentUser();
        //Toast.makeText(MainActivity.this, refAddr.getParent().toString(), Toast.LENGTH_LONG).show();

         MenuItem m = menu.findItem(R.id.action_settings);
         m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem menuItem) {
                 Query q = refDi.orderByChild("email").equalTo(user.getEmail());
                 q.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()) {
                             //HashMap<Object, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                             for (DataSnapshot o : dataSnapshot.getChildren()) {
                                 goToSettingsDistributore();

                             }
                         }
                     }
                      @Override
                      public void onCancelled(DatabaseError databaseError) {

                      }
                 });
                 Query q1 = refDr.orderByChild("email").equalTo(user.getEmail());
                 q1.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()) {
                             //HashMap<Oject, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                             for (DataSnapshot o : dataSnapshot.getChildren()) {
                                 goToSettings();
                             }
                         }
                     }
                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });

                 //Toast.makeText(MainActivity.this, refAddr.child("addresses").toString(), Toast.LENGTH_LONG).show();
                 return true;
             }
         });/*
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, refAddr.getParent().toString(), Toast.LENGTH_LONG).show();
                user.getEmail();
                //refAddr.getParent();

            }
        });*/
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
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference refDr = database.getReference("/drivers");
            final DatabaseReference refDi = database.getReference("/dispensers");
            final FirebaseUser user = mAuth.getCurrentUser();

            Query q = refDi.orderByChild("email").equalTo(user.getEmail());
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //HashMap<Object, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                        for (DataSnapshot o : dataSnapshot.getChildren()) {
                            goToSettingsDistributore();

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Query q1 = refDr.orderByChild("email").equalTo(user.getEmail());
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //HashMap<Oject, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                        for (DataSnapshot o : dataSnapshot.getChildren()) {
                            goToSettings();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //goToSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSettings(){
        final Intent intent = new Intent(this, SettingsAutomobilista.class);
        startActivity(intent);
    }

    public void goToAddDispenser(){
        final Intent intent = new Intent(this, AddDispenser.class);
        startActivity(intent);
    }

    public void goToSettingsDistributore(){
        final Intent intent = new Intent(this, SettingsDistributore.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Gestire gli elementi della navigation view cliccati qui.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            goToAddDispenser();
        } else if (id == R.id.nav_manage) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference refDr = database.getReference("/drivers");
            final DatabaseReference refDi = database.getReference("/dispensers");
            final FirebaseUser user = mAuth.getCurrentUser();

            Query q = refDi.orderByChild("email").equalTo(user.getEmail());
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //HashMap<Object, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                        for (DataSnapshot o : dataSnapshot.getChildren()) {
                            goToSettingsDistributore();

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Query q1 = refDr.orderByChild("email").equalTo(user.getEmail());
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //HashMap<Oject, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                        for (DataSnapshot o : dataSnapshot.getChildren()) {
                            goToSettings();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //goToSettings();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("12", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
