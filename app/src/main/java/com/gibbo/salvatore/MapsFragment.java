package com.gibbo.salvatore;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = MapsFragment.class.getSimpleName();
    public static final int MY_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    public String positionFromRegisterDistributoreActivity, addressDispenser;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    //String[] accountData;

    //private OnFragmentInteractionListener mListener;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressDispenser = getArguments().getString("indirizzo");
        }
        //positionFromRegisterDistributoreActivity = getArguments().getString("indirizzo");
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
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        /*RegisterAutomobilistaActivity activity = (RegisterAutomobilistaActivity) getActivity();
        accountData = activity.getData();*/
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            createLocationRequest();
        }

        /*MarkerOptions markerOptions = new MarkerOptions()
                .position(Util.getLocationFromAddress(getContext(), positionFromRegisterDistributoreActivity))
                .title("posizione ricevuta");

        Marker marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Util.pointToPosition(mGoogleMap, marker.getPosition());*/

        //LatLng address = getLocationFromAddress(this, yourAddressString("Street Number, Street, Suburb, State, Postcode");

        //gestisco azioni quando si clicca sulla mappa
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                googleMap.clear(); //elimino i marker precedenti
                String address, city;

                Geocoder geocoder;
                List<Address> addresses;

                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                //seleziono la località da far apparire sopra il marker
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    address = "";
                    city = addresses.get(0).getLocality();
                    if (addresses.size() > 0) {
                        Address addr = addresses.get(0);
                        if (addr.getThoroughfare() != null && addr.getThoroughfare() != ""){
                            address += addr.getThoroughfare() + " ";
                        }
                        if(addr.getSubThoroughfare() != null && addr.getSubThoroughfare() != ""){
                            address += addr.getSubThoroughfare() + ", ";
                        }
                        if (city != null && city != ""){
                            address += city;
                        } else {
                            address += addr.getCountryName();
                        }

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(address);

                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.showInfoWindow();

                        Util.pointToPosition(mGoogleMap, marker.getPosition());
                        //ins. richiesta

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog ); //android.R.style.Theme_Material_Dialog_Alert
                        } else {
                            builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog);
                        }
                        builder.setTitle("Navigazione").setMessage("Iniziare la navigazione verso "+address+"?")
                                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        Util.launchNavigation(getContext(), latLng);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_map).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_REQUEST_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission was denied. Display an error message.
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
        String A = getActivity().getIntent().getStringExtra("add_dispenser");
        if (A != null) {
            Toast.makeText(getContext(), A, Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }


}