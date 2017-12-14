package se.ju.students.svni1493.foodcheckv2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private int MAPS_PERMISSION_CODE = 1;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    LocationManager locationManager;

    String[] mPlaceType=null;
    String[] mPlaceTypeName=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mPlaceType = getResources().getStringArray(R.array.place_type);
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        if(ContextCompat. checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Finding nearby stores...");
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //check if network provider is enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String str = adressList.get(0).getLocality()+", ";
                            str += adressList.get(0).getCountryName();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(str).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                            findPlaces(location);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
            }else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String str = adressList.get(0).getLocality() + ", ";
                            str += adressList.get(0).getCountryName();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(str).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                            findPlaces(location);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
            }
        }else {
            requestMapsPermission();
        }
    }

    private void requestMapsPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed so that you can see nearby stores.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MAPS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MAPS_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == MAPS_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Finding nearby stores...", Toast.LENGTH_SHORT).show();
                try {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                //check if network provider is enabled
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                                String str = adressList.get(0).getLocality()+", ";
                                str += adressList.get(0).getCountryName();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(str).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                                findPlaces(location);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
                }else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng latLng = new LatLng(latitude, longitude);
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                                String str = adressList.get(0).getLocality() + ", ";
                                str += adressList.get(0).getCountryName();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(str).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                                findPlaces(location);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exption downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }

    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            for(int i=0;i<list.size();i++){

                MarkerOptions markerOptions = new MarkerOptions();

                HashMap<String, String> hmPlace = list.get(i);

                double lat = Double.parseDouble(hmPlace.get("lat"));

                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");

                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                mMap.addMarker(markerOptions);
            }
        }
    }

    public void findPlaces(Location location) {

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String type = "grocery_or_supermarket";

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+latitude+","+longitude);
        sb.append("&radius=20000");
        sb.append("&types="+type);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCqhmeaLvCloG-dmn3FARTku0mP9MhTIfk");

        PlacesTask placesTask = new PlacesTask();

        placesTask.execute(sb.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap = googleMap;
    }
}
