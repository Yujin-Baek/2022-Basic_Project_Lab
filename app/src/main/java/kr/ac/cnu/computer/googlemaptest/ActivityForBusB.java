package kr.ac.cnu.computer.googlemaptest;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ActivityForBusB extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
{
    private GoogleMap map;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FATEST_UPDATE_INTERVAL_MS = 500;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    Location mCurrentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    boolean isSetCurrentLocation = false;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_bus_b);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FATEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button moveToCNU = findViewById(R.id.moveToCNU);
        moveToCNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultLocation();
            }
        });

        Button switchToAButton = findViewById(R.id.switchToA);
        switchToAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityForBusB.this, ActivityForBusA.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.d(TAG, "onMapReady");

        map = googleMap;

        LatLng numberOne = new LatLng(36.36393623045683, 127.34512288367394);
        LatLng numberTwo = new LatLng(36.36714486295813, 127.34252209519627);
        LatLng numberThree = new LatLng(36.36928319643853, 127.3412887661673);
        LatLng numberFour = new LatLng(36.37432282798491, 127.34388914314896);
        LatLng numberFive = new LatLng(36.37642789094089, 127.34416989166898);
        LatLng numberSix = new LatLng(36.37191266851502, 127.34303753992411);
        LatLng numberSeven = new LatLng(36.370497971570686, 127.34378814811502);
        LatLng numberEight = new LatLng(36.36948527558873, 127.34637197830905);
        LatLng numberNine = new LatLng(36.36905242935213, 127.35195036061559);
        LatLng numberTen = new LatLng(36.36718432295817, 127.3520528560284);
        LatLng numberEleven = new LatLng(36.369165630679575, 127.35198915694087);
        LatLng numberTwelve = new LatLng(36.37234435419047, 127.34648149906977);
        LatLng numberThirteen = new LatLng(36.36948527558873, 127.34637197830905);
        LatLng numberFourteen = new LatLng(36.3673418, 127.3455367);
        LatLng numberFifteen = new LatLng(36.365987855484114, 127.3453100226298);


        MarkerOptions stationOne = new MarkerOptions();
        stationOne.position(numberOne)
                .title("①정심화국제문화회관");

        MarkerOptions stationTwo = new MarkerOptions();
        stationTwo.position(numberTwo)
                .title("②사회과학대학입구(한누리회관뒤)");

        MarkerOptions stationThree = new MarkerOptions();
        stationThree.position(numberThree)
                .title("③서문(공동실험실습관앞)");

        MarkerOptions stationFour = new MarkerOptions();
        stationFour.position(numberFour)
                .title("④음악2호관앞");

        MarkerOptions stationFive = new MarkerOptions();
        stationFive.position(numberFive)
                .title("⑤공동동물실험센터입구(회차)");

        MarkerOptions stationSix = new MarkerOptions();
        stationSix.position(numberSix)
                .title("⑥체육관입구");

        MarkerOptions stationSeven = new MarkerOptions();
        stationSeven.position(numberSeven)
                .title("⑦예술대학앞");

        MarkerOptions stationEight = new MarkerOptions();
        stationEight.position(numberEight)
                .title("⑧도서관앞(대학본부옆농대방향)");

        MarkerOptions stationNine = new MarkerOptions();
        stationNine.position(numberNine)
                .title("⑨농업생명과학대학 앞");

        MarkerOptions stationTen = new MarkerOptions();
        stationTen.position(numberTen)
                .title("⑩동문주차장");

        MarkerOptions stationEleven = new MarkerOptions();
        stationEleven.position(numberEleven)
                .title("⑪농업생명과학대학앞");

        MarkerOptions stationTwelve = new MarkerOptions();
        stationTwelve.position(numberTwelve)
                .title("⑫학생생활관3거리");

        MarkerOptions stationThirteen = new MarkerOptions();
        stationThirteen.position(numberThirteen)
                .title("⑬도서관앞(도서관삼거리 방향)");

        MarkerOptions stationFourteen = new MarkerOptions();
        stationFourteen.position(numberFourteen)
                .title("⑭공과대학앞");

        MarkerOptions stationFifteen = new MarkerOptions();
        stationFifteen.position(numberFifteen)
                .title("⑮산학연교육연구관앞");

        BitmapDrawable bitmapDrawStationOne = (BitmapDrawable) getResources().getDrawable(R.drawable.first_station);
        Bitmap b1 = bitmapDrawStationOne.getBitmap();
        stationOne.icon(BitmapDescriptorFactory.fromBitmap(b1));

        BitmapDrawable bitmapDrawStationTwo = (BitmapDrawable) getResources().getDrawable(R.drawable.second_station);
        Bitmap b2 = bitmapDrawStationTwo.getBitmap();
        stationTwo.icon(BitmapDescriptorFactory.fromBitmap(b2));

        BitmapDrawable bitmapDrawStationThree = (BitmapDrawable) getResources().getDrawable(R.drawable.third_station);
        Bitmap b3 = bitmapDrawStationThree.getBitmap();
        stationThree.icon(BitmapDescriptorFactory.fromBitmap(b3));

        BitmapDrawable bitmapDrawStationFour = (BitmapDrawable) getResources().getDrawable(R.drawable.fourth_station);
        Bitmap b4 = bitmapDrawStationFour.getBitmap();
        stationFour.icon(BitmapDescriptorFactory.fromBitmap(b4));

        BitmapDrawable bitmapDrawStationFive = (BitmapDrawable) getResources().getDrawable(R.drawable.fifth_station);
        Bitmap b5 = bitmapDrawStationFive.getBitmap();
        stationFive.icon(BitmapDescriptorFactory.fromBitmap(b5));

        BitmapDrawable bitmapDrawStationSix = (BitmapDrawable) getResources().getDrawable(R.drawable.sixth_station);
        Bitmap b6 = bitmapDrawStationSix.getBitmap();
        stationSix.icon(BitmapDescriptorFactory.fromBitmap(b6));

        BitmapDrawable bitmapDrawStationSeven = (BitmapDrawable) getResources().getDrawable(R.drawable.seventh_station);
        Bitmap b7 = bitmapDrawStationSeven.getBitmap();
        stationSeven.icon(BitmapDescriptorFactory.fromBitmap(b7));

        BitmapDrawable bitmapDrawStationEight = (BitmapDrawable) getResources().getDrawable(R.drawable.eighth_station);
        Bitmap b8 = bitmapDrawStationEight.getBitmap();
        stationEight.icon(BitmapDescriptorFactory.fromBitmap(b8));

        BitmapDrawable bitmapDrawStationNine = (BitmapDrawable) getResources().getDrawable(R.drawable.ninth_station);
        Bitmap b9 = bitmapDrawStationNine.getBitmap();
        stationNine.icon(BitmapDescriptorFactory.fromBitmap(b9));

        BitmapDrawable bitmapDrawStationTen = (BitmapDrawable) getResources().getDrawable(R.drawable.tenth_station);
        Bitmap b10 = bitmapDrawStationTen.getBitmap();
        stationTen.icon(BitmapDescriptorFactory.fromBitmap(b10));

        BitmapDrawable bitmapDrawStationEleven = (BitmapDrawable) getResources().getDrawable(R.drawable.eleventh_station);
        Bitmap b11 = bitmapDrawStationEleven.getBitmap();
        stationEleven.icon(BitmapDescriptorFactory.fromBitmap(b11));

        BitmapDrawable bitmapDrawStationTwelve = (BitmapDrawable) getResources().getDrawable(R.drawable.twelfth_station);
        Bitmap b12 = bitmapDrawStationTwelve.getBitmap();
        stationTwelve.icon(BitmapDescriptorFactory.fromBitmap(b12));

        BitmapDrawable bitmapDrawStationThirteen = (BitmapDrawable) getResources().getDrawable(R.drawable.thirteenth_station);
        Bitmap b13 = bitmapDrawStationThirteen.getBitmap();
        stationThirteen.icon(BitmapDescriptorFactory.fromBitmap(b13));

        BitmapDrawable bitmapDrawStationFourteen = (BitmapDrawable) getResources().getDrawable(R.drawable.fourteenth_station);
        Bitmap b14 = bitmapDrawStationFourteen.getBitmap();
        stationFourteen.icon(BitmapDescriptorFactory.fromBitmap(b14));

        BitmapDrawable bitmapDrawStationFifteen = (BitmapDrawable) getResources().getDrawable(R.drawable.fifteenth_station);
        Bitmap b15 = bitmapDrawStationFifteen.getBitmap();
        stationFifteen.icon(BitmapDescriptorFactory.fromBitmap(b15));

        map.addMarker(stationOne);
        map.addMarker(stationTwo);
        map.addMarker(stationThree);
        map.addMarker(stationFour);
        map.addMarker(stationFive);
        map.addMarker(stationSix);
        map.addMarker(stationSeven);
        map.addMarker(stationEight);
        map.addMarker(stationNine);
        map.addMarker(stationTen);
        map.addMarker(stationEleven);
        map.addMarker(stationTwelve);
        map.addMarker(stationThirteen);
        map.addMarker(stationFourteen);
        map.addMarker(stationFifteen);

        setDefaultLocation();

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(ActivityForBusB.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick");
            }
        });

    }

    LocationCallback locationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLatitude());
                if (!isSetCurrentLocation) {
                    setCurrentLocation(location);
                    isSetCurrentLocation = true;
                }
                mCurrentLocation = location;
            }
        }
    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocaionUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안 가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                map.setMyLocationEnabled(true);
            }


        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        isSetCurrentLocation = false;
        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (map!=null)
                map.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location) { // 앱을 시작할 때 현재 위치를 화면에 띄우도록 함
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        map.moveCamera(cameraUpdate);
    }


    public void setDefaultLocation() {

        LatLng DEFAULT_LOCATION = new LatLng(36.369067172679344, 127.346911313043);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        map.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityForBusB.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
}
