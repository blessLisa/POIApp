package com.example.whkang.poiapp;


import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
//implement-->상속은 아니지만 API를 가져다 쓸수 있게 해주는애
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient = null;//구글맵 사용하게 해주는 애
    private Marker mCurrentMarker = null;//주변장소 마카
    private ArrayList<Marker> mPlacesMarker = null;//마카 list
    private AppCompatActivity mActivity;

    Location mCurrentPosition;//위치값
    

    ArrayList<String> mPhotoRefer = new ArrayList<String>();//json에서 정보 빼낸 값 담을 애들
   
    ArrayList<Bitmap> mPhotos = new ArrayList<Bitmap>();//실제 사진이 들어있는 배열
    ArrayList<String> mName = new ArrayList<String>();//장소 이름
    ArrayList<String> mAddress = new ArrayList<String>();//장소 주소
    ArrayList<JSONObject> mLocation = new ArrayList<JSONObject>();//각 장소들의 위치
    private String API_KEY = "AIzaSyCydQq4bw68-gd2yqr2AeTL5sQlmxqUma8";//API키
    JSONObject mJsonObject = new JSONObject();//jsonobject객체

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;//위치 허가 번호
    private static final int UPDATE_INTERVAL_MS = 60000;  // 60초//몇초 주기로 update할 건가
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초//얼마나 빠르게 위치 업데이트 할 것인가

    boolean mLocationPermissionGranted = false;
    boolean mMoveMapByUser = true;
    boolean mMakeJson;
    String mType = null;
    Bitmap mBitmap;
    //Google Place URL API
    String DEFAULT_JSON_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key="+API_KEY+"&radius=5000&location=";//restful API
    String mJsonURL;

    LocationRequest mLocationRequest = new LocationRequest()//현재 위치 요청하게 하는 애
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    Location mLastLocation;//마지막 location 저장할 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AppLog", "onCreate");
        super.onCreate(savedInstanceState);//부모 oncreate호춯

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mType = null;
            } else {
                mType = extras.getString("TYPE");
            }
        }

        setContentView(R.layout.activity_categorized);//layout이랑 activity연결

        mGoogleApiClient = new GoogleApiClient.Builder(this)//googleapiclient사용하게 해주는 애
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();//구글맵이랑 서비스 연결
        mLocationRequest.setNumUpdates(1);//내 위치 물어보기 한번하기
        mActivity = this;
        mPlacesMarker = new ArrayList<Marker>();//장소 담을 배열 생성

    }


    private void makeJsonData(String url)//내가 만든함수! restfulapi에서 json data를 가지고 오는 함수
    {
        Log.d("AppLog", "makeJsonData using url : " + url.toString());

        URL AsyUrl=null;
        new AsyncTask<String, String, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                mMakeJson = true;
                Log.d("AppLog", "makeJsonData : doInBackground");
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
                    Log.e("AppLog", "Error when makeJsonData e: ", ex);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject response)
            {
                Log.d("AppLog", "makeJsonData : onPostExecute");
                if(response != null)
                {
                    mJsonObject = response;
                    getPOIInformation();
                    showPoiInformation();

                    Log.e("AppLog", "makeJsonData Success: " + response.toString() );
                }
                mMakeJson = false;
            }
        }.execute(url);
    }

    private  void getPOIInformation()//가지고 온 json data에서 우리가 필요한 정보를 가지고 오는 애
    {
        Log.d("AppLog", "getPOIInformation");
        JSONArray photos = null;

        String PhotoAPI = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&key="+API_KEY+"&photoreference=";
        try {
            JSONArray results = mJsonObject.getJSONArray("results");    //result 가져오기

            for(int i=0; i<results.length(); i++) {
                mName.add(i, results.getJSONObject(i).getString("name"));    //result에서 이름가져오기
                mAddress.add(i, results.getJSONObject(i).getString("vicinity")); //result에서 주소 가져오기
                mLocation.add(i, results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location"));  //result에서 위치정보 가져오기

                if (results.getJSONObject(i).isNull("photos"))   //result에서 photo정보를 가져와서 photo 가져오기
                {
                    mPhotoRefer.add(i, null);
                } else {
                    photos = results.getJSONObject(i).getJSONArray("photos");
                    mPhotoRefer.add(i, PhotoAPI + photos.getJSONObject(0).getString("photo_reference"));
                }
            }
        }
        catch (JSONException e){
            Log.e("AppLog", "getPOIInformation error: "+e.toString());
        }
    }

    void showCurrentLocation(Location location)//현재 내 위치 보여주는 함수
    {
        Log.d("AppLog", "setCurrentLocation currentLocation: "+ location.toString());

        mLastLocation = location;
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        mCurrentMarker = mGoogleMap.addMarker(markerOptions);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));


    }

    @Override
    public void onMapReady(final GoogleMap map) {//맵이 준비되면 호출이 되는 콜백함수
        Log.d("AppLog", "onMapReady");
        mGoogleMap = map;
        mMoveMapByUser = false;

        if (mGoogleMap == null) {
            return;
        }

        MapsInitializer.initialize(getApplicationContext());
        getDeviceLocation();

        try {
            if (mLocationPermissionGranted) {
                Log.d("AppLog", "onMapReady: Location Permission granted");
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            } else {
                Log.d("AppLog", "onMapReady: location permission not granted");
            }
        } catch (SecurityException e)  {
            Log.e("AppLog", "onMapReady error : "+e.getMessage());
        }
    }

    private void getDeviceLocation() {//내 위치 가져오는 애
        Log.d("AppLog", "getDeviceLocation");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {//위치 정보 사용하시겠습니까?에서 예를 눌렀을때 호출되는 함수
        Log.d("AppLog", "onRequestPermissionsResult");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
    @Override
    public boolean onMyLocationButtonClick(){//구글 맵에서 내위치 버튼 클릭했을떄 불리는 함수
        Log.d("AppLog", "onMyLocationButtonClick");
        return true;
    }

    @Override
    public void onLocationChanged(Location location){//내 위치 변했을떄 불리는 함수
        Log.d("AppLog", "onLocationChanged");
        mCurrentPosition= location;
        mJsonURL = DEFAULT_JSON_URL+mCurrentPosition.getLatitude()+","+mCurrentPosition.getLongitude()+"&type="+mType;//전체restfulapi주소

        showCurrentLocation(location);
        makeJsonData(mJsonURL);
    }



    @Override
    public  void onResume() {//현재 activity에서 전 activity로 넘어갈떄 불리는 함수
        Log.d("AppLog", "onResume");
        super.onResume();
        if(mGoogleApiClient.isConnected()) {

        }
    }


    @Override
    protected void onStart() {//앱이 뒤로갔다가 다시 앞으로 갔을떄 실행되는함수
        Log.d("AppLog", "onStart");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {//앱이 끝나면 불리는 함수
        Log.d("AppLog", "onStop");

        if ( mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {//googlemap connect성공시 불리는 callback함수
        Log.d("AppLog", "onConnected");

        FragmentManager fragmentManager = getFragmentManager();//구글맵 위치 잡아주고 onmapready 부르기
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {//위의 꺼랑 반대의 경우 불리는 애
        Log.d("AppLog", "onConnectionFailed");
        Toast.makeText(getApplicationContext(), "connection failed",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {//구글 맵 연결이 멈춰졌을 때 불리는 함수
        Log.d("AppLog", "onConnectionSuspended");
        Toast.makeText(getApplicationContext(), "connection suspend",Toast.LENGTH_SHORT).show();

    }

    private void showPoiInformation()//activity에 모든걸 표현해주는 함수
    {
        Log.d("AppLog", "showPoiInformation");
        if(mPlacesMarker !=null)
        {
            mPlacesMarker.clear();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView)findViewById(R.id.listView);
                ArrayList<ListViewItem> listItem = new ArrayList<ListViewItem>();

                for(int i =0; i<mName.size(); i++) {
                    try {
                        LatLng latLng = new LatLng(mLocation.get(i).getDouble("lat"), mLocation.get(i).getDouble("lng"));

                        String nameOfPlace = mName.get(i);
                        String addressOfPlace = mAddress.get(i);
                        String photoOfPlace = mPhotoRefer.get(i);
//                        Bitmap photoOfSpot = mPhotos.get(i);

                        ListViewItem L_item = new ListViewItem(photoOfPlace, nameOfPlace, addressOfPlace);     //리스트에 보일 아이템들을 생성해줍니다.
                        listItem.add(L_item);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(nameOfPlace);
                        markerOptions.snippet(addressOfPlace);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker item = mGoogleMap.addMarker(markerOptions);
                        mPlacesMarker.add(item);

                    } catch (JSONException e) {
                        Log.e("AppLog", e.toString());
                    }
                }
                ListViewAdapter viewAdapter = new ListViewAdapter(mActivity, R.layout.listview_item, listItem);
                listview.setAdapter(viewAdapter);
            }
        });

    }
}
