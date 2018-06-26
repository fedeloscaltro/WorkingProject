package com.gibbo.salvatore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;

public class Util {
    public static void launchNavigation(Context context, LatLng destination){
        /*String destinantionIntent = "https://www.google.com/maps/dir/?api=1&origin="+myPos.latitude+", "+myPos.longitude+
                "&destination="+destination.latitude+", "+destination.longitude+"&travelmode=driving";*/
        //oppure

        String destinantionIntent2 = "google.navigation:q="+destination.latitude+", "+destination.longitude;

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destinantionIntent2));
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void pointToPosition(GoogleMap mGoogleMap, LatLng position) {
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(15).build();
        //Zoom in and animate the camera.
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static String writePosition(List<Address> addresses, String address, String city){
        if (addresses.size() > 0) {
            Address addr = addresses.get(0);
            if (addr.getThoroughfare() != null && !addr.getThoroughfare().equals("")) {
                address += addr.getThoroughfare() + " ";
            }
            if (addr.getSubThoroughfare() != null && !addr.getSubThoroughfare().equals("")) {
                address += addr.getSubThoroughfare() + ", ";
            }
            if (city != null && !city.equals("")) {
                address += city;
            } else {
                address += addr.getCountryName();
            }
        }
        return address;
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress){
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try{
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
        return p1;
    }

    public static double calculationByDistance(LatLng startP, LatLng endP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = startP.latitude;
        double lat2 = endP.latitude;
        double lon1 = startP.longitude;
        double lon2 = endP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    /*class LoadImage extends AsyncTask<Object, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(Object... objects) {
            return null;
        }
    }*/



    public static String md5(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }


}
