package kr.ac.cnu.computer.googlemaptest;

/*
 *
 *
 * 퍼미션 관련 부분은 혹시 몰라서 '코드 원작자의 주석' 을 달긴 했습니다.
 * 사실 저도 무슨 말인지 모르겠어요.
 * 딱히 퍼미션 관련 부분은 건드리지 않아도 될거 같습니다.
 * 그리고 제가 아무래도 남의 코드를 가져온거다 보니 삭제해도 될거 같은 부분은 삭제해서,
 * 미처 지우지 못한 더미 데이터가 있을 수도 있습니다. 참고 바랍니다.
 * A노선 액티비티와 B노선 액티비티는 거의 유사하므로 A노선 액티비티에만 주석을 달겠습니다.
 *   -최시은
 *
 * 참고 코드 블로그 : https://webnautes.tistory.com/1249
 *
 *
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityForBusA extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener
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

    Location mCurrentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private boolean doesSetCurrentLocation = false;

    private View mLayout; // Snackbar 사용하기 위해서는 View가 필요합니다.

    private LatLng currentBusLocation;
    private Marker currentBusMarker = null;

    private boolean connected = false;
    private boolean running = false;
    private boolean[] visited = new boolean[15];
    private boolean[] visited2 = new boolean[15];


    TextView textView; // 이게 스니펫으로 불러올 텍스트인가?? 잠만 텍스트 뷰가 이거 하난테 설마 이걸로 전부 다 쓴거야??

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView11;
    TextView textView12;
    TextView textView13;
    TextView textView14;
    TextView textView15;


    private String[] startTimeArr;
    private int[] stationTimeArr;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_bus_a);


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

        // 지도의 초기 위치를 충남대학교로 이동하는 버튼입니다.
        ImageButton moveToCNU = findViewById(R.id.moveToCNU);
        moveToCNU.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               setDefaultLocation();
           }
        });

        // B노선 액티비티로 화면 전환하는 버튼입니다.
        ImageButton switchToBButton = findViewById(R.id.switchToB);
        switchToBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityForBusA.this, ActivityForBusB.class);
                startActivity(intent);
                finish();
            }
        });

        textView = findViewById(R.id.textView);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        textView12 = findViewById(R.id.textView12);
        textView13 = findViewById(R.id.textView13);
        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);


        startTimeArr = new String[15];

        startTimeArr[0] = "14:30";
        startTimeArr[1] = "15:00";
        startTimeArr[2] = "15:30";
        startTimeArr[3] = "16:00";
        startTimeArr[4] = "16:30";
        startTimeArr[5] = "17:00";
        startTimeArr[6] = "17:30";
        startTimeArr[7] = "18:00";
        startTimeArr[8] = "19:00";
        startTimeArr[9] = "19:30";
        startTimeArr[10] = "21:00";
        startTimeArr[11] = "21:30";
        startTimeArr[12] = "22:30";
        startTimeArr[13] = "23:00";
        startTimeArr[14] = "23:30";

        stationTimeArr = new int[15];

        for(int i=0; i<stationTimeArr.length; i++) {
            stationTimeArr[i] = i;
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

        //첫번째 작업 : 맵을 잇기 위한 새로운 좌표 추가
        LatLng librery = new LatLng(36.369316, 127.345914);

        LatLng east_of_lib_1_intersection1= new LatLng(36.36978364, 127.34713638);

        //기숙사 방향 좌표들
        LatLng east_of_lib_2_northofInt1= new LatLng(36.37124332, 127.34732369);
        LatLng east_of_lib_3_northofInt2= new LatLng(36.37142844,127.34732399);
        LatLng east_of_lib_4_northofInt3= new LatLng(36.37157123,127.34723114);
        LatLng north_of_lib= new LatLng(36.37275383,127.34604556);


        //5 6 7 노선쪽 좌표
        LatLng east_of_lib_6= new LatLng(36.36995399,127.34767012);
        LatLng east_of_lib_7= new LatLng(36.37019933,127.34800274);
        LatLng east_of_lib_8= new LatLng(36.37056608,127.34825481);
        LatLng east_of_lib_9= new LatLng(36.3706499,127.34842395);
        LatLng east_of_lib_10= new LatLng(36.37062608,127.34868098);
        LatLng east_of_lib_11= new LatLng(36.37053807,127.34882131);
        LatLng east_of_lib_12= new LatLng(36.36915108,127.34961868);
        LatLng east_of_lib_13= new LatLng(36.36902125,127.34968559);
        LatLng east_of_lib_14= new LatLng(36.36893412,127.34983338);
        LatLng east_of_lib_15= new LatLng(36.36891032,127.35002309);
        LatLng east_of_lib_16= new LatLng(36.36898677,127.3501816);
        LatLng east_of_lib_17= new LatLng(36.36911151,127.35035572);
        LatLng east_of_lib_18= new LatLng(36.36927499,127.35063115);
        LatLng east_of_lib_19= new LatLng(36.36940493,127.35085204);
        LatLng east_of_lib_20= new LatLng(36.36944893,127.35094559);
        LatLng east_of_lib_21= new LatLng(36.3694859,127.35098716);
        LatLng east_of_lib_22= new LatLng(36.36946077,127.35117682);

        LatLng east_of_lib_intersection_to_6and7= new LatLng(36.36883054,127.35235855);

        LatLng east_of_lib_24= new LatLng(36.36872541,127.35229934);
        LatLng east_of_lib_25= new LatLng(36.36894547,127.35245851);
        LatLng east_of_lib_26= new LatLng(36.36921879,127.35189782);
        LatLng east_of_lib_27= new LatLng(36.36911765,127.35183482);

        //도서관 서쪽으로 가기 시작하는 코스
        LatLng west_of_lib_1= new LatLng(36.37038209,127.34423234);
        LatLng west_of_lib_2= new LatLng(36.37047777,127.3431969);

        LatLng west_of_lib_intersection= new LatLng(36.37043426,127.34291101);

        //서문 방향 노선 코스
        LatLng west_of_lib_3= new LatLng(36.36984855,127.34101124);
        LatLng west_of_lib_4= new LatLng(36.36888636,127.34152086);
        LatLng west_of_lib_5= new LatLng(36.36783805,127.34147325);
        LatLng west_of_lib_6= new LatLng(36.36616327,127.34396786);

        //서북쪽 노선
        LatLng west_of_lib_7 = new LatLng(36.37137671,127.34289062);
        LatLng west_of_lib_8= new LatLng(36.37594242,127.34441761);
        LatLng west_of_lib_9= new LatLng(36.37612673,127.34443543);
        LatLng west_of_lib_10= new LatLng(36.37629159,127.34443011);
        LatLng west_of_lib_11= new LatLng(36.3763821,127.34433875);

        MarkerOptions stationOne = new MarkerOptions();
        stationOne.position(numberOne)
                .title("①정심화국제문화회관");

        MarkerOptions stationTwo = new MarkerOptions();
        stationTwo.position(numberTwo)
                .title("②경상대학 앞");

        MarkerOptions stationThree = new MarkerOptions();
        stationThree.position(numberThree)
                .title("③도서관 앞(농대방향)");

        MarkerOptions stationFour = new MarkerOptions();
        stationFour.position(numberFour)
                .title("④학생생활관3거리");

        MarkerOptions stationFive = new MarkerOptions();
        stationFive.position(numberFive)
                .title("⑤농업생명과학대학 앞");

        MarkerOptions stationSix = new MarkerOptions();
        stationSix.position(numberSix)
                .title("⑥동문주차장");

        MarkerOptions stationSeven = new MarkerOptions();
        stationSeven.position(numberSeven)
                .title("⑦농업생명과학대학 앞");

        MarkerOptions stationEight = new MarkerOptions();
        stationEight.position(numberEight)
                .title("⑧도서관 앞(도서관 삼거리 방향)");

        MarkerOptions stationNine = new MarkerOptions();
        stationNine.position(numberNine)
                .title("⑨예술대학 앞");

        MarkerOptions stationTen = new MarkerOptions();
        stationTen.position(numberTen)
                .title("⑩음악2호관 앞");

        MarkerOptions stationEleven = new MarkerOptions();
        stationEleven.position(numberEleven)
                .title("⑪공동동물실험센터 입구(회차)");

        MarkerOptions stationTwelve = new MarkerOptions();
        stationTwelve.position(numberTwelve)
                .title("⑫체육관 입구");

        MarkerOptions stationThirteen = new MarkerOptions();
        stationThirteen.position(numberThirteen)
                .title("⑬서문(공동실험실습관앞)");

        MarkerOptions stationFourteen = new MarkerOptions();
        stationFourteen.position(numberFourteen)
                .title("⑭사회과학대학 입구(한누리회관 뒤)");

        MarkerOptions stationFifteen = new MarkerOptions();
        stationFifteen.position(numberFifteen)
                .title("⑮산학연교육연구관 앞");

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

        // 두번째 작업 polyline 부분 구현 ( 그냥 화면이 켜졌을때 선분이 나타나면 되는 것이라서 상관 없음 )
        PolylineOptions polylineOptions1 = new PolylineOptions()
                .add(new LatLng(36.36393623045683, 127.34512288367394),new LatLng(36.36739570866662, 127.34560855449526)
                        ,new LatLng(36.369316, 127.345914) ,new LatLng(36.36948527558873, 127.34637197830905),new LatLng(36.36978364, 127.34713638)
                        ,new LatLng(36.37121358, 127.34734004),new LatLng(36.37142844,127.34732399),new LatLng(36.37157123,127.34723114)
                        ,new LatLng(36.37234435419047, 127.34648149906977),new LatLng(36.37275383,127.34604556))
                .color(Color.RED)
                .geodesic(true);

        PolylineOptions polylineOptions2 = new PolylineOptions()
                .add(new LatLng(36.369316, 127.345914), new LatLng(36.37038209,127.34423234),new LatLng(36.370497971570686, 127.34378814811502)
                        ,new LatLng(36.37047777,127.3431969),new LatLng(36.37040177,127.34292932),new LatLng(36.36984855,127.34101124)
                        ,new LatLng(36.36888636,127.34152086),new LatLng(36.36783805,127.34147325),new LatLng(36.36714486295813, 127.34252209519627)
                        ,new LatLng(36.36616327,127.34396786),new LatLng(36.365987855484114, 127.3453100226298))
                .color(Color.RED)
                .geodesic(true);
        //서북쪽 노선(인터섹션 좌표를 새로 씀 주의!)
        PolylineOptions polylineOptions3 = new PolylineOptions()
                .add(new LatLng(36.37039270,127.34291101),new LatLng(36.37137671,127.34289062),new LatLng(36.37191266851502, 127.34303753992411)
                        ,new LatLng(36.37432282798491, 127.34388914314896),new LatLng(36.37594242,127.34439761),new LatLng(36.37612673,127.34443543)
                        ,new LatLng(36.37629159,127.34443011),new LatLng(36.3763821,127.34433875),new LatLng(36.37642789094089, 127.34416989166898))
                .color(Color.RED)
                .geodesic(true);
        // 5 6 7 번 노선
        PolylineOptions polylineOptions4 = new PolylineOptions()
                .add(new LatLng(36.36978364, 127.34713638),new LatLng(36.36995399,127.34767012),new LatLng(36.37019933,127.34800274)
                        ,new LatLng(36.37056608,127.34825481),new LatLng(36.3706499,127.34842395),new LatLng(36.37062608,127.34868098)
                        ,new LatLng(36.37053807,127.34882131),new LatLng(36.36915108,127.34961868),new LatLng(36.36902125,127.34968559)
                        ,new LatLng(36.36893412,127.34983338),new LatLng(36.36891032,127.35002309),new LatLng(36.36898677,127.3501816)
                        ,new LatLng(36.36911151,127.35035572),new LatLng(36.36927499,127.35063115),new LatLng(36.36940493,127.35085204)
                        ,new LatLng(36.36944893,127.35094559),new LatLng(36.3694799,127.35098716),new LatLng(36.36948077,127.35117682)
                        ,new LatLng(36.36905242935213, 127.35195036061559),new LatLng(36.36883054,127.35235855),new LatLng(36.36872541,127.35229934)
                        ,new LatLng(36.36718432295817, 127.3520528560284))
                .color(Color.RED)
                .geodesic(true);
        PolylineOptions polylineOptions5 = new PolylineOptions()
                .add(new LatLng(36.36883054,127.35235855),new LatLng(36.36894547,127.35245851),new LatLng(36.369165630679575, 127.35198915694087)
                        ,new LatLng(36.36921879,127.35189782),new LatLng(36.36911765,127.35183482))
                .color(Color.RED)
                .geodesic(true);

        Polyline polyline1 = map.addPolyline(polylineOptions1);
        polyline1.setJointType(JointType.ROUND);

        Polyline polyline2 = map.addPolyline(polylineOptions2);
        polyline2.setJointType(JointType.ROUND);

        Polyline polyline3 = map.addPolyline(polylineOptions3);
        polyline3.setJointType(JointType.ROUND);

        Polyline polyline4 = map.addPolyline(polylineOptions4);
        polyline4.setJointType(JointType.ROUND);

        Polyline polyline5 = map.addPolyline(polylineOptions5);
        polyline5.setJointType(JointType.ROUND);



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

        map.setOnMarkerClickListener(this);

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
                        ActivityCompat.requestPermissions(ActivityForBusA.this, REQUIRED_PERMISSIONS,
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

//                if (hour <= 7 || hour >= 18) { running = false; }
//                else { running = true; }
                running = true;

                if (!running) {
                    textView.setText("버스 운행이 종료되었습니다.");
                } else {
                    if (!connected) {
                        textView.setText("버스의 위치 정보를 불러올 수 없습니다.");
                    } else {
                        if (visited[0]) {
                            textView.setText("정심화국제문화회관 -> 경상대학");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min + stationTimeArr[0];
                            textView1.setText(setTime+"");
                            //첫 번째 정류장은 다음 출발시간까지 남은 시간으로 세팅
                            textView2.setText(stationTimeArr[1]+"");
                            textView3.setText(stationTimeArr[2]+"");
                            textView4.setText(stationTimeArr[3]+"");
                            textView5.setText(stationTimeArr[4]+"");
                            textView6.setText(stationTimeArr[5]+"");
                            textView7.setText(stationTimeArr[6]+"");
                            textView8.setText(stationTimeArr[7]+"");
                            textView9.setText(stationTimeArr[8]+"");
                            textView10.setText(stationTimeArr[9]+"");
                            textView11.setText(stationTimeArr[10]+"");
                            textView12.setText(stationTimeArr[11]+"");
                            textView13.setText(stationTimeArr[12]+"");
                            textView14.setText(stationTimeArr[13]+"");
                            textView15.setText(stationTimeArr[14]+"");
                            //나머지 정류장은 stationTime으로 세팅
                        }
                        else if (visited[1]) {
                            textView.setText("경상대학 -> 도서관");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[1]) {
                                textView2.setText((setTime+stationTimeArr[1])+"");

                                textView1.setText(setTime+"");
                                textView3.setText((stationTimeArr[2]-stationTimeArr[1])+"");
                                textView4.setText((stationTimeArr[3]-stationTimeArr[1])+"");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[1])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[1])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[1])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[1])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[1])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[1])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[1])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[1])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[1])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[1])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[1])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText("곧 도착");
                                textView3.setText((stationTimeArr[2]-stationTimeArr[1])+"");
                                textView4.setText((stationTimeArr[3]-stationTimeArr[1])+"");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[1])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[1])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[1])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[1])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[1])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[1])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[1])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[1])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[1])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[1])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[1])+"");
                            }
                        }
                        else if (visited[2]) {
                            textView.setText("도서관 -> 학생생활관");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[2]) {
                                textView3.setText((setTime+stationTimeArr[2])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView4.setText((stationTimeArr[3]-stationTimeArr[2])+"");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[2])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[2])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[2])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[2])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[2])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[2])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[2])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[2])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[2])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[2])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[2])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText("곧 도착");
                                textView4.setText((stationTimeArr[3]-stationTimeArr[2])+"");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[2])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[2])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[2])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[2])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[2])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[2])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[2])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[2])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[2])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[2])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[2])+"");
                            }
                        }
                        else if (visited[3]) {
                            textView.setText("학생생활관 -> 농대");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[3]) {
                                textView4.setText((setTime+stationTimeArr[3])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[3])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[3])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[3])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[3])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[3])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[3])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[3])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[3])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[3])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[3])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[3])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText("곧 도착");
                                textView5.setText((stationTimeArr[4]-stationTimeArr[3])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[3])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[3])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[3])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[3])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[3])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[3])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[3])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[3])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[3])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[3])+"");
                            }
                        }
                        else if (visited[4]) {
                            textView.setText("농대 -> 동문");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[4]) {
                                textView5.setText((setTime+stationTimeArr[4])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[4])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[4])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[4])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[4])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[4])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[4])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[4])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[4])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[4])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[4])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText("곧 도착");
                                textView6.setText((stationTimeArr[5]-stationTimeArr[4])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[4])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[4])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[4])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[4])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[4])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[4])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[4])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[4])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[4])+"");
                            }
                        }
                        else if (visited[5]) {
                            textView.setText("동문 -> 농대");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[5]) {
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[5])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[5])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[5])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[5])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[5])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[5])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[5])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[5])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[5])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText("곧 도착");
                                textView7.setText((stationTimeArr[6]-stationTimeArr[5])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[5])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[5])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[5])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[5])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[5])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[5])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[5])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[5])+"");
                            }
                        }
                        else if (visited[6]) {
                            textView.setText("농대 -> 도서관");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[6]) {
                                textView7.setText((setTime+stationTimeArr[6])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[6])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[6])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[6])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[6])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[6])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[6])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[6])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[6])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText("곧 도착");
                                textView8.setText((stationTimeArr[7]-stationTimeArr[6])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[6])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[6])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[6])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[6])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[6])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[6])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[6])+"");
                            }
                        }
                        else if (visited[7]) {
                            textView.setText("도서관 -> 예술대");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[7]) {
                                textView8.setText((setTime+stationTimeArr[7])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[7])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[7])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[7])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[7])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[7])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[7])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[7])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText("곧 도착");
                                textView9.setText((stationTimeArr[8]-stationTimeArr[7])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[7])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[7])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[7])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[7])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[7])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[7])+"");
                            }
                        }
                        else if (visited[8]) {
                            textView.setText("예술대 -> 음악2");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[8]) {
                                textView9.setText((setTime+stationTimeArr[8])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[8])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[8])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[8])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[8])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[8])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[8])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText("곧 도착");
                                textView10.setText((stationTimeArr[9]-stationTimeArr[8])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[8])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[8])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[8])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[8])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[8])+"");
                            }
                        }
                        else if (visited[9]) {
                            textView.setText("음악2 -> 공동동물실습");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[9]) {
                                textView10.setText((setTime+stationTimeArr[9])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[9])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[9])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[9])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[9])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[9])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText("곧 도착");
                                textView11.setText((stationTimeArr[10]-stationTimeArr[9])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[9])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[9])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[9])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[9])+"");
                            }
                        }
                        else if (visited[10]) {
                            textView.setText("공동동물실습 -> 체육관");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[10]) {
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[10])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[10])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[10])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[10])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText("곧 도착");
                                textView12.setText((stationTimeArr[11]-stationTimeArr[10])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[10])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[10])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[10])+"");
                            }
                        }
                        else if (visited[11]) {
                            textView.setText("체육관 -> 서문");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[11]) {
                                textView12.setText((setTime+stationTimeArr[11])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[11])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[11])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[11])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText("곧 도착");
                                textView13.setText((stationTimeArr[12]-stationTimeArr[11])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[11])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[11])+"");
                            }
                        }
                        else if (visited[12]) {
                            textView.setText("서문 -> 사과대");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[12]) {
                                textView13.setText((setTime+stationTimeArr[12])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[12])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[12])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView13.setText("곧 도착");
                                textView14.setText((stationTimeArr[13]-stationTimeArr[12])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[12])+"");
                            }
                        }
                        else if (visited[13]) {
                            textView.setText("사과대 -> 산학연");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[13]) {
                                textView14.setText((setTime+stationTimeArr[13])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView13.setText((setTime+stationTimeArr[12])+"");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[13])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView13.setText((setTime+stationTimeArr[12])+"");
                                textView14.setText("곧 도착");
                                textView15.setText((stationTimeArr[14]-stationTimeArr[13])+"");
                            }
                        }
                        else if (visited[14]) {
                            textView.setText("산학연 -> 정심화국제문화회관");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            if(visited2[14]) {
                                textView15.setText((setTime+stationTimeArr[14])+"");

                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView13.setText((setTime+stationTimeArr[12])+"");
                                textView14.setText((setTime+stationTimeArr[13])+"");
                            } else {
                                textView1.setText(setTime+"");
                                textView2.setText((setTime+stationTimeArr[1])+"");
                                textView3.setText((setTime+stationTimeArr[2])+"");
                                textView4.setText((setTime+stationTimeArr[3])+"");
                                textView5.setText((setTime+stationTimeArr[4])+"");
                                textView6.setText((setTime+stationTimeArr[5])+"");
                                textView7.setText((setTime+stationTimeArr[6])+"");
                                textView8.setText((setTime+stationTimeArr[7])+"");
                                textView9.setText((setTime+stationTimeArr[8])+"");
                                textView10.setText((setTime+stationTimeArr[9])+"");
                                textView11.setText((setTime+stationTimeArr[10])+"");
                                textView12.setText((setTime+stationTimeArr[11])+"");
                                textView13.setText((setTime+stationTimeArr[12])+"");
                                textView14.setText((setTime+stationTimeArr[13])+"");
                                textView15.setText("곧 도착");
                            }
                        }
                        else {
                            textView.setText("다음 버스 대기 중");
                            long min = 1000;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            for (int i=0; i<startTimeArr.length; i++) {
                                String reqDateStr = startTimeArr[i];
                                try{
                                    Date reqDate = dateFormat.parse(reqDateStr);
                                    long reqDateTime = reqDate.getTime();
                                    //현재시간을 요청시간의 형태로 format 후 time 가져오기
                                    currentTime = dateFormat.parse(dateFormat.format(currentTime));
                                    long curDateTime = currentTime.getTime();
                                    //분으로 표현
                                    long gap = (reqDateTime - curDateTime) / 60000;

                                    if(min>gap && gap>0) {
                                        min = gap;
                                    }
                                } catch(ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            long setTime = min; //setTime은 다음 출발시간까지 남은 시간
                            textView1.setText(setTime+"");
                            textView2.setText((setTime+stationTimeArr[1])+"");
                            textView3.setText((setTime+stationTimeArr[2])+"");
                            textView4.setText((setTime+stationTimeArr[3])+"");
                            textView5.setText((setTime+stationTimeArr[4])+"");
                            textView6.setText((setTime+stationTimeArr[5])+"");
                            textView7.setText((setTime+stationTimeArr[6])+"");
                            textView8.setText((setTime+stationTimeArr[7])+"");
                            textView9.setText((setTime+stationTimeArr[8])+"");
                            textView10.setText((setTime+stationTimeArr[9])+"");
                            textView11.setText((setTime+stationTimeArr[10])+"");
                            textView12.setText((setTime+stationTimeArr[11])+"");
                            textView13.setText((setTime+stationTimeArr[12])+"");
                            textView14.setText((setTime+stationTimeArr[13])+"");
                            textView15.setText((setTime+stationTimeArr[14])+"");
                        }
                    }
                }
            }
        };
//
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

        busA.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    connected = (boolean) snapshot.child("connected").getValue();
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

                    visited2[0] = (boolean) snapshot.child("station1").child("passed").getValue();
                    visited2[1] = (boolean) snapshot.child("station2").child("passed").getValue();
                    visited2[2] = (boolean) snapshot.child("station3").child("passed").getValue();
                    visited2[3] = (boolean) snapshot.child("station4").child("passed").getValue();
                    visited2[4] = (boolean) snapshot.child("station5").child("passed").getValue();
                    visited2[5] = (boolean) snapshot.child("station6").child("passed").getValue();
                    visited2[6] = (boolean) snapshot.child("station7").child("passed").getValue();
                    visited2[7] = (boolean) snapshot.child("station8").child("passed").getValue();
                    visited2[8] = (boolean) snapshot.child("station9").child("passed").getValue();
                    visited2[9] = (boolean) snapshot.child("station10").child("passed").getValue();
                    visited2[10] = (boolean) snapshot.child("station11").child("passed").getValue();
                    visited2[11] = (boolean) snapshot.child("station12").child("passed").getValue();
                    visited2[12] = (boolean) snapshot.child("station13").child("passed").getValue();
                    visited2[13] = (boolean) snapshot.child("station14").child("passed").getValue();
                    visited2[14] = (boolean) snapshot.child("station15").child("passed").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        busLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (connected) {
                    if (currentBusMarker != null) currentBusMarker.remove();

                    final double latitude = (double) dataSnapshot.child("latitude").getValue();
                    final double longitude = (double) dataSnapshot.child("longitude").getValue();

                    currentBusLocation = new LatLng(latitude, longitude);
                    MarkerOptions busMarkerOptions = new MarkerOptions();
                    busMarkerOptions.position(currentBusLocation);

                    BitmapDrawable bitmapDrawBus = (BitmapDrawable) getResources().getDrawable(R.drawable.bus_icon);
                    Bitmap b = bitmapDrawBus.getBitmap();
                    busMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(b));

                    currentBusMarker = map.addMarker(busMarkerOptions);
                }
                else {
                    if (currentBusMarker != null) currentBusMarker.remove();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityForBusA.this);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(marker.equals(currentBusMarker)) {
            builder.setTitle("A노선 운행 버스");
            builder.setMessage("this is the test");

        } else {
            builder.setTitle(marker.getTitle());
            builder.setMessage("A노선 운행버스 노선"+"\n"+"버스 도착까지 남은 시간 : "+marker.getSnippet());

        }
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return true;
    }
}
