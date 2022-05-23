package kr.ac.cnu.computer.buslocation;

import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
{

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference busLocation = mDatabase.child("location");
    DatabaseReference busA = mDatabase.child("busA");

    private GoogleMap map;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000; // 1초
    private static final int FATEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    Location mCurrentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private boolean doesSetCurrentLocation = false;

    private boolean[] visited = new boolean[15];
    private boolean running = false;
    private boolean lastStop = false;

    private View mLayout; // Snackbar 사용하기 위해서는 View가 필요합니다.

    private LatLng currentBusLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.d(TAG, "onMapReady");

        map = googleMap;

        // A노선 정류장 좌표들(위도, 경도)
        LatLng numberOne = new LatLng(36.36393623045683, 127.34512288367394);
        LatLng numberTwo = new LatLng(36.36739570866662, 127.34560855449526);
        LatLng numberThree = new LatLng(36.36948527558873, 127.34637197830905);
        LatLng numberFour = new LatLng(36.37234435419047, 127.34648149906977);
        LatLng numberFive = new LatLng(36.36905242935213, 127.35195036061559);
        LatLng numberSix = new LatLng(36.36718432295817, 127.3520528560284);
        LatLng numberSeven = new LatLng(36.369165630679575, 127.35198915694087);
        LatLng numberEight = new LatLng(36.36942310420768, 127.34612949247371);
        LatLng numberNine = new LatLng(36.370497971570686, 127.34378814811502);
        LatLng numberTen = new LatLng(36.37432282798491, 127.34388914314896);
        LatLng numberEleven = new LatLng(36.37642789094089, 127.34416989166898);
        LatLng numberTwelve = new LatLng(36.37191266851502, 127.34303753992411);
        LatLng numberThirteen = new LatLng(36.36928319643853, 127.3412887661673);
        LatLng numberFourteen = new LatLng(36.36714486295813, 127.34252209519627);
        LatLng numberFifteen = new LatLng(36.365987855484114, 127.3453100226298);


        MarkerOptions stationOne = new MarkerOptions();
        stationOne.position(numberOne)
                .title("①정심화국제문화회관")
                .snippet("null");

        MarkerOptions stationTwo = new MarkerOptions();
        stationTwo.position(numberTwo)
                .title("②경상대학 앞")
                .snippet("null");

        MarkerOptions stationThree = new MarkerOptions();
        stationThree.position(numberThree)
                .title("③도서관 앞(농대방향)")
                .snippet("null");

        MarkerOptions stationFour = new MarkerOptions();
        stationFour.position(numberFour)
                .title("④학생생활관3거리")
                .snippet("null");

        MarkerOptions stationFive = new MarkerOptions();
        stationFive.position(numberFive)
                .title("⑤농업생명과학대학 앞")
                .snippet("null");

        MarkerOptions stationSix = new MarkerOptions();
        stationSix.position(numberSix)
                .title("⑥동문주차장")
                .snippet("null");

        MarkerOptions stationSeven = new MarkerOptions();
        stationSeven.position(numberSeven)
                .title("⑦농업생명과학대학 앞")
                .snippet("null");

        MarkerOptions stationEight = new MarkerOptions();
        stationEight.position(numberEight)
                .title("⑧도서관 앞(도서관 삼거리 방향")
                .snippet("null");

        MarkerOptions stationNine = new MarkerOptions();
        stationNine.position(numberNine)
                .title("⑨예술대학 앞")
                .snippet("null");

        MarkerOptions stationTen = new MarkerOptions();
        stationTen.position(numberTen)
                .title("⑩음악2호관 앞")
                .snippet("null");

        MarkerOptions stationEleven = new MarkerOptions();
        stationEleven.position(numberEleven)
                .title("⑪공동동물실험센터 입구(회차)")
                .snippet("null");

        MarkerOptions stationTwelve = new MarkerOptions();
        stationTwelve.position(numberTwelve)
                .title("⑫체육관 입구")
                .snippet("null");

        MarkerOptions stationThirteen = new MarkerOptions();
        stationThirteen.position(numberThirteen)
                .title("⑬서문(공동실험실습관앞)")
                .snippet("null");

        MarkerOptions stationFourteen = new MarkerOptions();
        stationFourteen.position(numberFourteen)
                .title("⑭사회과학대학 입구(한누리회관 뒤)")
                .snippet("null");

        MarkerOptions stationFifteen = new MarkerOptions();
        stationFifteen.position(numberFifteen)
                .title("⑮산학연교육연구관 앞")
                .snippet("null");


        // 지도에 마커 추가(이 마커 디자인은 변경할 수 있습니다.)
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

        //충남대학교로 지도 이동
        setDefaultLocation();

        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면 (안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(true); // 현제 나의 위치를 가져오는 버튼 UI
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick");
            }
        });

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Date currentTime = new Date();
                int hour = currentTime.getHours();
                int minute = currentTime.getMinutes();
                int seconds = currentTime.getSeconds();

                if (hour <= 7 || hour >= 18) { running = false; }
                else {
                    switch (hour) {
                        case 9:
                        case 11:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                            switch (minute) {
                                case 0:
                                case 30:
                                    running = true;
                            }
                    }
                }

                if (running) {
                    if (calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberOne.latitude, numberOne.longitude) <= 0.1) {
                        if (visited[14]) {
                            busA.child("station15").child("visit").setValue(false);
                            running = false;
                        } else {
                            busA.child("station1").child("visit").setValue(true);
                        }
                    } else if (visited[0] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberTwo.latitude, numberTwo.longitude) <= 0.05) {
                        busA.child("station1").child("visit").setValue(false);
                        busA.child("station2").child("visit").setValue(true);
                    } else if (visited[1] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberThree.latitude, numberThree.longitude) <= 0.05) {
                        busA.child("station2").child("visit").setValue(false);
                        busA.child("station3").child("visit").setValue(true);
                    } else if (visited[2] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberFour.latitude, numberFour.longitude) <= 0.05) {
                        busA.child("station3").child("visit").setValue(false);
                        busA.child("station4").child("visit").setValue(true);
                    } else if (visited[3] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberFive.latitude, numberFive.longitude) <= 0.05) {
                        busA.child("station4").child("visit").setValue(false);
                        busA.child("station5").child("visit").setValue(true);
                    } else if (visited[4] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberSix.latitude, numberSeven.longitude) <= 0.05) {
                        busA.child("station5").child("visit").setValue(false);
                        busA.child("station6").child("visit").setValue(true);
                    } else if (visited[5] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberSeven.latitude, numberSeven.longitude) <= 0.05) {
                        busA.child("station6").child("visit").setValue(false);
                        busA.child("station7").child("visit").setValue(true);
                    } else if (visited[6] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberEight.latitude, numberEight.longitude) <= 0.05) {
                        busA.child("station7").child("visit").setValue(false);
                        busA.child("station8").child("visit").setValue(true);
                    } else if (visited[7] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberNine.latitude, numberNine.longitude) <= 0.05) {
                        busA.child("station8").child("visit").setValue(false);
                        busA.child("station9").child("visit").setValue(true);
                    } else if (visited[8] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberTen.latitude, numberTen.longitude) <= 0.05) {
                        busA.child("station9").child("visit").setValue(false);
                        busA.child("station10").child("visit").setValue(true);
                    } else if (visited[9] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberEleven.latitude, numberEleven.longitude) <= 0.05) {
                        busA.child("station10").child("visit").setValue(false);
                        busA.child("station11").child("visit").setValue(true);
                    } else if (visited[10] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberTwelve.latitude, numberTwelve.longitude) <= 0.05) {
                        busA.child("station11").child("visit").setValue(false);
                        busA.child("station12").child("visit").setValue(true);
                    } else if (visited[11] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberThirteen.latitude, numberThirteen.longitude) <= 0.05) {
                        busA.child("station12").child("visit").setValue(false);
                        busA.child("station13").child("visit").setValue(true);
                    } else if (visited[12] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberFourteen.latitude, numberFourteen.longitude) <= 0.05) {
                        busA.child("station13").child("visit").setValue(false);
                        busA.child("station14").child("visit").setValue(true);
                    } else if (visited[13] && calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), numberFifteen.latitude, numberFifteen.longitude) <= 0.05) {
                        busA.child("station14").child("visit").setValue(false);
                        busA.child("station15").child("visit").setValue(true);
                    }
                }
            }
        };

        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try { Thread.sleep(1000); }
                    catch (InterruptedException e) {}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();


    }


    LocationCallback locationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLatitude());
                if (!doesSetCurrentLocation) {
                    // 앱을 새로 시작할 때 (혹은 껐다가 다시 켤 때)만 현재 위치를 자동으로 보여주게 했습니다.
                    // 이렇게 하지 않으면 내 위치가 움직일 때마다 내 위치가 지도 중심으로 옮겨집니다.
                    // 그렇게 되면 버스 정류장 정보를 보기가 힘들어져서 이렇게 했습니다.
                    // 어차피 현재 위치로 이동하는 버튼은 따로 있으니까요.
                    setCurrentLocation(location);
                    doesSetCurrentLocation = true;
                }
                mCurrentLocation = location;
                busLocation.child("latitude").setValue(location.getLatitude());
                busLocation.child("longitude").setValue(location.getLongitude());
            }
        }
    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
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



    public double calculateDistanceInMeter(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c * 1000)/1000.0;
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        doesSetCurrentLocation = false;
        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (map!=null)
                map.setMyLocationEnabled(true);

        }

        busA.child("connected").setValue(true);

        busA.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    visited[0] = (boolean) snapshot.child("station1").child("visit").getValue();
                    visited[1] = (boolean) snapshot.child("station2").child("visit").getValue();
                    visited[2] = (boolean) snapshot.child("station3").child("visit").getValue();
                    visited[3] = (boolean) snapshot.child("station4").child("visit").getValue();
                    visited[4] = (boolean) snapshot.child("station5").child("visit").getValue();
                    visited[5] = (boolean) snapshot.child("station6").child("visit").getValue();
                    visited[6] = (boolean) snapshot.child("station7").child("visit").getValue();
                    visited[7] = (boolean) snapshot.child("station8").child("visit").getValue();
                    visited[8] = (boolean) snapshot.child("station9").child("visit").getValue();
                    visited[9] = (boolean) snapshot.child("station10").child("visit").getValue();
                    visited[10] = (boolean) snapshot.child("station11").child("visit").getValue();
                    visited[11] = (boolean) snapshot.child("station12").child("visit").getValue();
                    visited[12] = (boolean) snapshot.child("station13").child("visit").getValue();
                    visited[13] = (boolean) snapshot.child("station14").child("visit").getValue();
                    visited[14] = (boolean) snapshot.child("station15").child("visit").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }

        busA.child("connected").setValue(false);
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
