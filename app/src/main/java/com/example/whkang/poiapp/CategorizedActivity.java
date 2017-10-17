package com.example.whkang.poiapp;


import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CategorizedActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Marker mCurrentMarker = null;
    private Marker mPlacesMarker = null;
    private AppCompatActivity mActivity;

    LatLng mCurrentPosition;

    ArrayList<String> photoRefer = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<JSONObject> location = new ArrayList<JSONObject>();
    private String API_KEY = "AIzaSyB6F0yi1E2MZWBAlqeM2hRLlDczEAkBmOg";
    JSONObject mJsonObject = new JSONObject();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    boolean mLocationPermissionGranted = false;
    boolean mRequestingLocationUpdates = false;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    String mType = null;

    //디폴트 위치, Seoul
    LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

    //Google Place URL API
    String jsonUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    LocationRequest mLocationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("lisa", "onCreate");
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mType = null;
            } else {
                mType = extras.getString("TYPE");
            }
        }

        setContentView(R.layout.activity_categorized);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


        mCurrentPosition = DEFAULT_LOCATION;
        mActivity = this;

        jsonUrl = jsonUrl+mCurrentPosition.latitude+","+mCurrentPosition.longitude+"&radius=5000&type="+mType+"&key="+API_KEY;
        makeJsonData(jsonUrl);
    }



    private void makeJsonData(final String url)
    {
        Log.d("lisa", "makeJsonData : "+url);

        URL AsyUrl=null;
        new AsyncTask<String, String, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                Log.d("lisa", "makeJsonData doInBackground");
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    return new JSONObject(buffer.toString());
                }

                catch(Exception ex)
                {
                    Log.e("lisa", "yourDataTask", ex);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject response)
            {
                Log.d("lisa", "makeJsonData onPostExecute");
                if(response != null)
                {
                    mJsonObject = response;
                    getPOIInformation();
                    showPoiInformation();
                    Log.e("lisa", "Success: " + response.toString() );
                }
            }
        }.execute(url);
    }

    private  void getPOIInformation()
    {
        Log.d("lisa", "getPOIInformation");
        JSONArray photos = null;

        String PhotoAPI = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&key="+API_KEY+"&photoreference=";
        try {
            JSONArray results = mJsonObject.getJSONArray("results");    //result 가져오기
            for(int i=0; i<results.length(); i++) {
                name.add(i, results.getJSONObject(i).getString("name"));    //result에서 이름가져오기
                address.add(i, results.getJSONObject(i).getString("vicinity")); //result에서 주소 가져오기
                location.add(i, results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location"));  //result에서 위치정보 가져오기

                if(results.getJSONObject(i).isNull("photos"))   //result에서 photo정보를 가져와서 photo 가져오기
                {
                    photoRefer.add(i, null);
                }
                else {
                    photos = results.getJSONObject(i).getJSONArray("photos");
                    photoRefer.add(i, PhotoAPI + photos.getJSONObject(0).getString("photo_reference"));
                }
            }
        }
        catch (JSONException e){
            Log.e("lisa", e.toString());
        }
    }

    void setCurrentLocation(Location location)
    {
        mLastLocation = location;
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrentMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Log.d("lisa", "onMapReady");
        mGoogleMap = map;
        mMoveMapByUser = false;
        mCurrentPosition = DEFAULT_LOCATION;

        if (mGoogleMap == null) {
            return;
        }

        MapsInitializer.initialize(getApplicationContext());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(DEFAULT_LOCATION);
        mGoogleMap.moveCamera(cameraUpdate);

        getDeviceLocation();

        try {
            if (mLocationPermissionGranted) {
                Log.d("lisa", "Location Permission granted");
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            } else {
                Log.d("lisa", "location permission not granted");
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        Log.d("lisa", "getDeviceLocation");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            // Set the map's camera position to the current location of the device.
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d("lisa", "onRequestPermissionsResult");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
    @Override
    public boolean onMyLocationButtonClick(){
        Log.d("lisa", "onMyLocationButtonClick");
        return true;
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d("lisa", "onLocationChanged");
        setCurrentLocation(location);
    }



    @Override
    public  void onResume() {
        Log.d("lisa", "onResume");
        super.onResume();
        if(mGoogleApiClient.isConnected()) {

        }
    }


    @Override
    protected void onStart() {
        Log.d("lisa", "onStart");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("lisa", "onStop");

        if ( mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("lisa", "onConnected");

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("lisa", "onConnectionFailed");
        Toast.makeText(getApplicationContext(), "connection failed",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("lisa", "onConnectionSuspended");
        Toast.makeText(getApplicationContext(), "connection suspend",Toast.LENGTH_SHORT).show();

    }

    private void showPoiInformation()
    {
        Log.d("lisa", "showPoiInformation");
        if(mPlacesMarker !=null)
        {
            mPlacesMarker.remove();
        }

        ListView listview = (ListView)findViewById(R.id.listView);
        ArrayList<ListViewItem> listItem = new ArrayList<ListViewItem>();

        for(int i =0; i<name.size(); i++) {
            try {
                LatLng latLng = new LatLng(location.get(i).getInt("lat"), location.get(i).getInt("lng"));

                String nameOfPlace = name.get(i);
                String addressOfPlace = address.get(i);
                String photoOfPlace = photoRefer.get(i);

                ListViewItem L_item = new ListViewItem(photoOfPlace, nameOfPlace, addressOfPlace);     //리스트에 보일 아이템들을 생성해줍니다.
                listItem.add(L_item);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(nameOfPlace);
                markerOptions.snippet(addressOfPlace);
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mPlacesMarker = mGoogleMap.addMarker(markerOptions);

            } catch (JSONException e) {
                Log.e("lisa", e.toString());
            }
        }
        ListViewAdapter viewAdapter = new ListViewAdapter(mActivity, R.layout.listview_item, listItem);
        listview.setAdapter(viewAdapter);
    }
}
