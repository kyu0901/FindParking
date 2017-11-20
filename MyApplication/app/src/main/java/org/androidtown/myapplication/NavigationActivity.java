package org.androidtown.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleApiClient.OnConnectionFailedListener, BeaconConsumer {

    String TAG = "NavigationActivity";
    private GoogleMap mMap;
    private Button btnFindPath;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private LatLng destination;
    private LatLng origin;
    private String ori,des;

    private BackPressClose backPressClose;

    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.i("Here is", "NavigationActivity");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        ori = i.getStringExtra("ori");
        des = i.getStringExtra("des");
        origin = bundle.getParcelable("origin");
        destination = bundle.getParcelable("destination");

        btnFindPath = (Button) findViewById(R.id.btnFindPath);



        btnFindPath.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
              startLocationService();
             }
        });
        mGoogleApiClient = new GoogleApiClient
        .Builder(this)
        .addApi(Places.GEO_DATA_API)
        .addApi(Places.PLACE_DETECTION_API)
        .enableAutoManage(this, this)
        .build();
        sendRequest();

        //실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25"));

        beaconManager.bind(this);

        backPressClose = new BackPressClose(this);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
    private void startLocationService(){
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 리스너 객체 생성
        NavigationActivity.GPSListener gpsListener = new NavigationActivity.GPSListener();
        long minTime = 1000; //1초마다 없데이트
        float minDistance = 0;

        try {
        // GPS 기반 위치 요청
        manager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        minTime,
        minDistance,
        gpsListener);

        // 네트워크 기반 위치 요청
        manager.requestLocationUpdates(
        LocationManager.NETWORK_PROVIDER,
        minTime,
        minDistance,
        gpsListener);
        } catch(SecurityException ex) {
        ex.printStackTrace();
        }
        }
    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            Log.i(TAG, " is here");
            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("Navi GPSLocationService", msg);

        //비콘 탐지
        //BeaconSearch bs = new BeaconSearch();

        // 현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
        showCurrentLocation(latitude, longitude);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
    private void showCurrentLocation(Double latitude, Double longitude) {
        // 현재 위치를 이용해 LatLon 객체 생성
        LatLng curPoint = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 19));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN, 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng Korea = new LatLng(36.732461, 127.988181);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Korea, 20));
        //mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    private void sendRequest() {
        Log.i("sendRequest?? ","yes");
        //String destination = etDestination.getText().toString();
        if (destination == null) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            new DirectionFinder(this, origin, des).execute();
            Log.i("directionfinder?? : ","yes");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Log.i("directionfinder?? : ","no");
        }
       /*if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }*/
        //mMap.addMarker(new MarkerOptions().position(origin).title("origin"));
        //mMap.addMarker(new MarkerOptions().position(destination).title("destination"));

    }
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.bounds,200));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            //비콘이 감지되면 해당 함수가 호출. Collection<Beacon> beacons에는 감지된 비콘의 리스트가
            //region에는 비콘들에 대응하는 region객체가 들어옴
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                String beaconMsg;

                BeaconCheck bc = new BeaconCheck();
                System.out.println("------- didRangeBeacons!!!!");
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        Log.i("Beacon", "\n------------------------------------------------");
                        Log.d(TAG, "Mac address is: "+beacon.getBluetoothAddress());
                        beaconMsg = bc.BeaconCheck(beacon.getBluetoothAddress());
                        Log.i("beaconMsg", beaconMsg);
                        if(beaconMsg.equalsIgnoreCase("Success")){
                            Log.i("Msg is ", beaconMsg);
                            Intent intent1 = new Intent(getApplicationContext(), ParkingMap.class);

                            startActivity(intent1);
                            finish();
                            Log.d(TAG, "is Return");

                        }
                        Log.i("Beacon", "\n------------------------------------------------");
                        beaconList.add(beacon);

                    }
                }
            }
        });


        handler.sendEmptyMessage(0);

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //자기 자신을 1초마다 호출
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    public void onBackPressed(){
        backPressClose.onBackPressed();
    }
}
