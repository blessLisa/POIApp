package com.example.whkang.poiapp;


import android.app.FragmentManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
//git 생성

public class CategorizedActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener, PlacesListener{

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Marker currentMarker = null;
    private AppCompatActivity mActivity;

    LatLng mCurrentPosition;
    List<Marker> previous_marker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    String mType = null;

    //디폴트 위치, Seoul
    LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

    //Google Place URL API
    String jsonUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    JSONArray mJsonArray = new JSONArray();
    JSONObject mJsonObject = new JSONObject();

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

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCurrentPosition = DEFAULT_LOCATION;
        mActivity = this;
        previous_marker = new ArrayList<Marker>();

        jsonUrl = jsonUrl+mCurrentPosition.latitude+","+mCurrentPosition.longitude+"&radius=5000&type="+mType+"&key=AIzaSyB6F0yi1E2MZWBAlqeM2hRLlDczEAkBmOg";

        makeJsonData(jsonUrl);


    }

    private void makeJsonData(final String url)
    {
//        JSON
        URL AsyUrl=null;
        new AsyncTask<String, String, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {

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
                    Log.e("App", "yourDataTask", ex);
                    return null;
                }
                finally
                {
                    if(reader != null)
                    {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            protected void onPostExecute(JSONObject response)
            {
                if(response != null)
                {
                    try {
                        mJsonObject = response;
                        getPOIPhotos(mJsonObject);
                        Log.e("App", "Success: " + response.getString("yourJsonElement") );
                    } catch (JSONException ex) {
                        Log.e("App", "Failure", ex);
                    }
                }
            }
        }.execute(url);
    }

    private  void getPOIPhotos(JSONObject jsonObject)
    {
        JSONArray photos = null;
        try {
            JSONArray results = jsonObject.getJSONArray("results");    //result 가져오기
            for(int i=0; i<results.length(); i++) {
                photos = results.getJSONObject(i).getJSONArray("photos");
                if (photos == null)
            Log.d("lisa", photos.toString());
            }

        }
        catch (JSONException e){
            Log.e("lisa", e.toString());
        }
//https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=YOUR_API_KEY
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Log.d("lisa", "onMapReady");
        mGoogleMap = map;
        MapsInitializer.initialize(getApplicationContext());
        setDefaultLocation();

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
            @Override
            public boolean onMyLocationButtonClick() {
                mMoveMapByAPI = true;
                return true;
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser == true && mRequestingLocationUpdates){
                    mMoveMapByAPI = false;
                }
                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
            }
        });


    }

    @Override
    public boolean onMyLocationButtonClick(){
        return true;
    }

    @Override
    public void onLocationChanged(Location location){
    }



    @Override
    public  void onResume() {
        Log.d("lisa", "onResume");
        super.onResume();

        if (mGoogleApiClient.isConnected()) {

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
        previous_marker.clear();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d("lisa", "onConnected");

        showPlaceInformation(mCurrentPosition);

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



    public void setDefaultLocation() {
        MapsInitializer.initialize(getApplicationContext());
        Log.d("lisa", "setDefaultLocation");
        mMoveMapByUser = false;

        mCurrentPosition = DEFAULT_LOCATION;
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";



        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    @Override
    public void onPlacesFailure(PlacesException e) {
        Log.d("lisa", "onPlacesFailure"+e);
    }

    @Override
    public void onPlacesStart() {
        Log.d("lisa", "onPlaceStart");

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        Log.d("lisa", "onPlacesSuccess");


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView)findViewById(R.id.listView);

                ArrayList<ListViewItem> listItem = new ArrayList<ListViewItem>();
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(place.getVicinity());
                    Log.d("lisa",place.getIcon());
                    Marker item = mGoogleMap.addMarker(markerOptions);
                    previous_marker.add(item);


                    ListViewItem L_item = new ListViewItem(place.getIcon(), place.getName(), place.getVicinity());     //make item
                    listItem.add(L_item);

                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

                ListViewAdapter viewAdapter = new ListViewAdapter(mActivity, R.layout.listview_item, listItem);
                listview.setAdapter(viewAdapter);

            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlaceInformation(LatLng location)
    {
        mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(CategorizedActivity.this)
                .key("AIzaSyCKbcKKTlGC0tZxHM2AFkmtAd6RUeg7bwo")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(500) //500 미터 내에서 검색
                .type(mType) //음식점
                .build()
                .execute();
    }
}
