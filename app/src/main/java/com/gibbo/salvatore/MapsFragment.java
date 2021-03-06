package com.gibbo.salvatore;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MapsFragment.class.getSimpleName();
    public static final int MY_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    public String addressDispenser, prezziDispenser;
    public boolean switchPreference;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    ArrayList<Double> results = new ArrayList<Double>();
    int i=0;
    Double minDistance = 0.0;
    SwitchPreference switchFilter = null;

    final int uniqueId = 45678;
    String EXTRA_NOTIFICATION_ID = "1232";
    //String[] accountData;

    //private OnFragmentInteractionListener mListener;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference refAddr = database.getReference("/addresses");

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        i=0;

        if (getArguments() != null) {
            addressDispenser = getArguments().getString("indirizzo");
            prezziDispenser = getArguments().getString("prezziSignUp");
            //switchPreference = getArguments().getBoolean("switchPreference");
        }

        if (checkFinePermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_ACCESS_FINE_LOCATION);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng newPos = new LatLng(location.getLatitude(), location.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(newPos);

                    mGoogleMap.addMarker(markerOptions);
                }
            }
        };


    }

    private boolean checkFinePermission(Context context, String accessPermission) {
        return ContextCompat.checkSelfPermission(context, accessPermission)
                != PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            //mMapView.getMapAsync(this);
        }
    }

    public void startMap(){
        mMapView.getMapAsync(this);
    }

    public void searchAddressesWithFilters(SharedPreferences setting){
        Toast.makeText(this.getActivity(), "ricerca con filtri", 2000).show();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Query q = refAddr.orderByChild("carburanti").equalTo(sp.getString("favFuel", ""));
                q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //HashMap<Object, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                    for (DataSnapshot o : dataSnapshot.getChildren()) {
                        Addresses addresses = o.getValue(Addresses.class);
                        //String values = o.getValue(String.class);
                        //Toast.makeText(getActivity(), addresses.getAddress(), Toast.LENGTH_LONG).show();
                        retrieveMarkers(addresses.getAddress(), mGoogleMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchAddressesWithoutFilters(){
        Toast.makeText(this.getActivity(), "ricerca senza filtri", 2000).show();
        Query q = refAddr.orderByChild("address").startAt("");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //HashMap<Object, Addresses> map = ((HashMap<Object, Addresses>) dataSnapshot.getValue());
                    for (DataSnapshot o : dataSnapshot.getChildren()) {
                        Addresses addresses = o.getValue(Addresses.class);
                        //String values = o.getValue(String.class);
                        //Toast.makeText(getActivity(), addresses.getAddress(), Toast.LENGTH_LONG).show();
                        retrieveMarkers(addresses.getAddress(), mGoogleMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void aggiornaMappa(){
        if(mGoogleMap != null) mGoogleMap.clear();
        if(!MainActivity.automobilista) {
            searchAddressesWithoutFilters();
        }else{
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            if(sp.getBoolean("switchFilter", false)){
                searchAddressesWithFilters(sp);
            }else {
                searchAddressesWithoutFilters();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);




        /*----------------------------------------BLOCCO CODICE NOTIFICHE ----------------------------------------------------*/
        /*createNotificationChannel();

        Intent snoozeIntent = new Intent(getContext(), AddDispenser.class);
        snoozeIntent.setAction("com.gibbo.salvatore.AddDispenser");
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(getContext(), 0, snoozeIntent, 0);

        // Create an Intent for the activity you want to start
        Intent addDispenserIntent = new Intent(getContext(), AddDispenser.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntentWithParentStack(addDispenserIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "12")
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


        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        notificationManager.notify(uniqueId, builder.build());*/

        /*----------------------------------------------------------------------------------------------------------*/


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            createLocationRequest();
        }

        aggiornaMappa();
        /*MarkerOptions markerOptions = new MarkerOptions()
                .position(Util.getLocationFromAddress(getContext(), positionFromRegisterDistributoreActivity))
                .title("posizione ricevuta");

        Marker marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Util.pointToPosition(mGoogleMap, marker.getPosition());

        LatLng address = getLocationFromAddress(this, yourAddressString("Street Number, Street, Suburb, State, Postcode");*/

        //gestisco azioni quando si clicca sulla mappa
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapClick(final LatLng latLng) {
                //googleMap.clear(); //elimino i marker precedenti
                String address, city;

                Geocoder geocoder;
                List<Address> addresses;

                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                //seleziono la località da far apparire sopra il marker
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    address = "";
                    city = addresses.get(0).getLocality();

                    address = Util.writePosition(addresses, address, city);

                    Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dispensericon);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(address)
                            .icon(BitmapDescriptorFactory.fromBitmap(icon));

                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.showInfoWindow();

                    Util.pointToPosition(mGoogleMap, marker.getPosition());
                    //ins. richiesta

                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog); //android.R.style.Theme_Material_Dialog_Alert
                    } else {
                        builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog);
                    }
                    //final String finalAddress = address;
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                            String address;
                            final String city;

                            Geocoder geocoder;
                            final List<Address> addresses;

                            geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                                address = "";
                                city = addresses.get(0).getLocality();

                                address = Util.writePosition(addresses, address, city);
                                builder.setTitle("Distributore").setMessage("Aggiungere un distributore in " + address + " o iniziare la navigazione?")
                                        .setNeutralButton("Annulla", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                marker.remove();
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .setPositiveButton("Naviga", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                Util.launchNavigation(getContext(), latLng);
                                            }
                                        })
                                        .setNegativeButton("Aggiungi", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                final Intent intent = new Intent(getContext(), AddDispenser.class);
                                                final String addr;
                                                intent.putExtra("dispenserToAdd", addresses.get(0).getAddressLine(0));
                                                startActivity(intent);
                                            }
                                        })
                                        .setIcon(R.drawable.dispensericon).show();
                                return true;
                            } catch (IOException e) {
                                return false;
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        String dispenserAdded = getActivity().getIntent().getStringExtra("add_dispenser");
        String prices = getActivity().getIntent().getStringExtra("prices");
        if (dispenserAdded != null) {
            geoLocate(dispenserAdded, googleMap, prices);
        }

        String prices_distributore = getActivity().getIntent().getStringExtra("prices_distributore");
        if (addressDispenser != null){
            geoLocate(addressDispenser, googleMap, prices_distributore);
        }

        /*if (addressDispenser != null) {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses;


            List<Address> list = new ArrayList<>();

            try {
                list = geocoder.getFromLocationName(addressDispenser, 1);
            } catch (IOException e) {
                Log.e("#", "geolocate: IOException " + e.getMessage());
            }
            if (list.size() > 0) {
                Address addressList = list.get(0);

                //Toast.makeText(getActivity(), "Location found: "+ addressList, Toast.LENGTH_LONG).show();

                //int DEFAULT_ZOOM = 17;

                //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, googleMap);
                LatLng latLng = new LatLng(addressList.getLatitude(), addressList.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(addressDispenser + " " + prezziDispenser + "CIAO");

                Marker marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();

                Util.pointToPosition(mGoogleMap, marker.getPosition());
            }
        }*/
    }

    /*private Location getLastKnownLocation(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }*/
    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        final SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //quando la richiesta di ottenere la posizione attuale va a buon fine
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //prendo la posizione attuale
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                //faccio il controllo dei permessi
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //trovo il valore della location
                    assert locationManager != null;
                    final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                    //if(location != null)
                    //richiedo tutti gli indirizzi memorizzati nel DB
                    Query q = refAddr.orderByChild("address").startAt("");
                    q.addListenerForSingleValueEvent(new ValueEventListener(){
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot o : dataSnapshot.getChildren()) {
                                    //per ogni indirizzo
                                    Addresses addresses = o.getValue(Addresses.class);

                                    Geocoder geocoder = new Geocoder(getActivity());
                                    List<Address> list = new ArrayList<>();



                                    try{
                                        //controllo il nome e prendo un solo risultato
                                        list = geocoder.getFromLocationName(addresses.getAddress(), 1);
                                    } catch (IOException e){
                                        Log.e("#", "geolocate: IOException "+e.getMessage());
                                    }

                                    if (list.size()> 0) {
                                        Address addressList = list.get(0);
                                        //creo un oggetto LatLng per identificare il risultato della query
                                        LatLng latLngQuery = new LatLng(addressList.getLatitude(), addressList.getLongitude());
                                        //creo un oggetto LatLng per identificare la posizione attuale
                                        LatLng latLngLivePosition = new LatLng(location.getLatitude(), location.getLongitude());
                                        Double result = Util.calculationByDistance(latLngLivePosition, latLngQuery);

                                        /*SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                        SharedPreferences.Editor editor = mPreferences.edit();

                                        boolean notificationEnabled = mPreferences.getBoolean("switchPreference", );*/
                                        //se la distanza tra i 2 punti è minore di 100 m

                                        result *= 10;
                                        if (result<=5) {// && switchPreference
                                            if (i==0) {
                                                minDistance = result;
                                                createNotificationChannel();
                                                i++;

                                                // Creo un Intent per l'activity che voglio iniziare
                                                Intent addDispenserIntent = new Intent(getContext(), AddDispenser.class);
                                                addDispenserIntent.putExtra("distributore_da_modificare", "Via Balzac 12, Reggio Emilia");
                                                // Creo il TaskStackBuilder e aggiungo l'intent, che fa l'inflate del back stack
                                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                                                stackBuilder.addNextIntentWithParentStack(addDispenserIntent);
                                                // Ottengo il PendingIntent che contiene l'intero back stack
                                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                                                //addresses.getAddress()
                                                //creo la notifica
                                                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "12")
                                                        .setSmallIcon(R.mipmap.ic_launcher)
                                                        .setContentTitle("Vuoi aggiornare i prezzi di questo distributore?")
                                                        .setContentText("Sei passato vicino a " + "Via Balzac 12, Reggio Emilia" + ", vuoi aggiornarne i prezzi?")
                                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                                .bigText("Sei passato vicino a " + "Via Balzac 12, Reggio Emilia" + ", vuoi aggiornarne i prezzi?"))
                                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                                        .setAutoCancel(true)
                                                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                                        .setVisibility(Notification.VISIBILITY_PUBLIC);
                                                builder.setContentIntent(resultPendingIntent);

                                                final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                                                //la visualizzo
                                                notificationManager.notify(uniqueId, builder.build());
                                            } else {
                                                if (result < minDistance) {
                                                    //creo il canale per la notifica
                                                    createNotificationChannel();
                                                    i++;

                                                    // Creo un Intent per l'activity che voglio iniziare
                                                    Intent addDispenserIntent = new Intent(getContext(), AddDispenser.class);
                                                    addDispenserIntent.putExtra("distributore_da_modificare", "Via Balzac 12, Reggio Emilia");
                                                    // Creo il TaskStackBuilder e aggiungo l'intent, che fa l'inflate del back stack
                                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                                                    stackBuilder.addNextIntentWithParentStack(addDispenserIntent);
                                                    // Ottengo il PendingIntent che contiene l'intero back stack
                                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                                    //creo la notifica
                                                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "12")
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle("Vuoi aggiornare i prezzi di questo distributore?")
                                                            .setContentText("Sei passato vicino a " + "Via Balzac 12, Reggio Emilia" + ", vuoi aggiornarne i prezzi?")
                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                    .bigText("Sei passato vicino a " + "Via Balzac 12, Reggio Emilia" + ", vuoi aggiornarne i prezzi?"))
                                                            .setPriority(NotificationCompat.PRIORITY_MAX)
                                                            .setAutoCancel(true)
                                                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                                            .setVisibility(Notification.VISIBILITY_PUBLIC);
                                                    builder.setContentIntent(resultPendingIntent);

                                                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                                                    //la visualizzo
                                                    notificationManager.notify(uniqueId, builder.build());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                //LatLng address = Util.getLocationFromAddress(this, yourAddressString("Street Number, Street, Suburb, State, Postcode"));


                Log.i(TAG, "le impostazioni solo abilitate correttamente");
                MapsFragment.mRequestingLocationUpdates = true;
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
        return mLocationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_REQUEST_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
        /*MainActivity mainActivity = new MainActivity();
        if (mainActivity.getIntent().getStringExtra("add_dispenser") != null) {
            String dispenserData = mainActivity.getIntent().getStringExtra("add_dispenser");
            Toast.makeText(getContext(), dispenserData,
                    Toast.LENGTH_LONG).show();
        }*/
        if(mGoogleMap != null) aggiornaMappa();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    private void geoLocate(String dispenserAdded, GoogleMap googleMap, String prices){
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        String address, city;

        Log.d("geoLocate: ", "geolocating");

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        List<Address> addresses;

        try{
            list = geocoder.getFromLocationName(dispenserAdded, 1);
        } catch (IOException e){
            Log.e("#", "geolocate: IOException "+e.getMessage());
        }

        if (list.size()> 0){
            Address addressList = list.get(0);

            //Toast.makeText(getActivity(), "Location found: "+ addressList, Toast.LENGTH_LONG).show();

            //int DEFAULT_ZOOM = 17;

            //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, googleMap);
            LatLng latLng = new LatLng(addressList.getLatitude(), addressList.getLongitude());

            try{
                //seleziono la località da far apparire sopra il marker
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                address = "";
                city = addresses.get(0).getLocality();

                address = Util.writePosition(addresses, address, city);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(address + " " + prices)
                            .position(latLng);
                    googleMap.addMarker(markerOptions).showInfoWindow();
                Util.pointToPosition(googleMap, latLng);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void retrieveMarkers(String n_position, GoogleMap googleMap){
        //String address, city;

        Log.d("geoLocate: ", "geolocating");

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        List<Address> addresses;

        try{
            list = geocoder.getFromLocationName(n_position, 1);
        } catch (IOException e){
            Log.e("#", "geolocate: IOException "+e.getMessage());
        }

        if (list.size()> 0){
            Address addressList = list.get(0);
            LatLng latLng = new LatLng(addressList.getLatitude(), addressList.getLongitude());

            /*try{
                //seleziono la località da far apparire sopra il marker
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                address = "";
                city = addresses.get(0).getLocality();

                address = Util.writePosition(addresses, address, city);*/

                MarkerOptions markerOptions = new MarkerOptions()
                        .title(n_position)
                        .position(latLng);
                googleMap.addMarker(markerOptions).showInfoWindow();
        }
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
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}