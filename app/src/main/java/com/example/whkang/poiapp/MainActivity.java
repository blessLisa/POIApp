package com.example.whkang.poiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;

    private AppCompatActivity mActivity;

    boolean askPermissionOnceAgain = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button gas_button = (Button)findViewById(R.id.gas_button);
        Button res_button = (Button)findViewById(R.id.res_button);
        Button hos_button= (Button)findViewById(R.id.hos_button);
        Button par_button = (Button)findViewById(R.id.par_button);

        gas_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "gas_station";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });

        res_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "restaurant";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });
        hos_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "hospital";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });
        par_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "parking";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });

        mActivity = this;

//        checkPermissions();
    }



//    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
//    @TargetApi(Build.VERSION_CODES.M)
//    private void checkPermissions() {
//        Log.d("lisa", "CheckPermissions");
//        boolean fineLocationRationale = ActivityCompat
//                .shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (hasFineLocationPermission == PackageManager
//                .PERMISSION_DENIED && fineLocationRationale)
//            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
//
//        else if (hasFineLocationPermission
//                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
//            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
//                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.d("lisa", "onRequestPermissionResult");
//
//        if (permsRequestCode
//                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
//
//            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//            if (permissionAccepted) {
//
//            } else {
//
//                checkPermissions();
//            }
//        }
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private void showDialogForPermission(String msg) {
//        Log.d("lisa", "showDialogForPermission");
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("알림");
//        builder.setMessage(msg);
//        builder.setCancelable(false);
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                ActivityCompat.requestPermissions(mActivity,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            }
//        });
//
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                finish();
//            }
//        });
//        builder.create().show();
//    }
//
//    private void showDialogForPermissionSetting(String msg) {
//        Log.d("lisa", "showDialogForPermissionSetting");
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("알림");
//        builder.setMessage(msg);
//        builder.setCancelable(true);
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//
//                askPermissionOnceAgain = true;
//
//                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.parse("package:" + mActivity.getPackageName()));
//                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
//                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mActivity.startActivity(myAppSettings);
//            }
//        });
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                finish();
//            }
//        });
//        builder.create().show();
//    }
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("lisa", "onActivityResult");
//        switch (requestCode) {
//
//            case GPS_ENABLE_REQUEST_CODE:
//
//                //사용자가 GPS 활성 시켰는지 검사
//                if (checkLocationServicesStatus()) {
//                    return;
//                }
//
//
//                break;
//        }
//    }
//
//    public boolean checkLocationServicesStatus() {
//        Log.d("lisa", "checkLocationServiceStatus");
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//
//    //여기부터는 GPS 활성화를 위한 메소드들
//    private void showDialogForLocationServiceSetting() {
//        Log.d("lisa", "showDialogForLocationServiceSetting");
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("위치 서비스 비활성화");
//        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
//                + "위치 설정을 수정하실래요?");
//        builder.setCancelable(true);
//        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Intent callGPSSettingIntent
//                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
//            }
//        });
//        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        builder.create().show();
//    }
}
