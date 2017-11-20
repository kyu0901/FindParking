package org.androidtown.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, BeaconConsumer {

    String TAG = "MapsActivity";

    private GoogleMap mMap;
    private Button Update;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteFragment autocompleteFragment2;
    private AutocompleteFilter typeFilter;
    private LatLng destination;
    private LatLng origin;
    private String ori, des;
    private Intent intent;

    private BackPressClose backPressClose;

    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();

    private boolean MapsEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Update = (Button) findViewById(R.id.Update);

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivityForResult(intent, 100);

            }
        });


        Log.i("Here is ", "MapsActivity");
        intent = new Intent(this, NavigationActivity.class);


        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(Place.TYPE_COUNTRY).setCountry("KR")
                .build();

        autocompleteFragment2 = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment2);

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                Log.i("", "Place: " + place.getName());
                destination = place.getLatLng();
                des = (String) place.getName();
                startIntent();
            }

            @Override
            public void onError(Status status) {
                Log.i("", "An error occurred: " + status);
            }
        });
        autocompleteFragment2.setFilter(typeFilter);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25"));


        startLocationService();
        beaconManager.bind(this);

        backPressClose = new BackPressClose(this);

    }

    private void startIntent() {
        Bundle args = new Bundle();
        args.putParcelable("origin", origin);
        args.putParcelable("destination", destination);
        intent.putExtra("bundle", args);
        intent.putExtra("des", des);
        startActivity(intent);
        finish();
    }

    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 리스너 객체 생성
        if (MapsEnd == false) {
            GPSListener gpsListener = new GPSListener();

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
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            if (MapsEnd == false) {
                String msg = "Latitude : " + latitude + "\nLongitude:" + longitude;
                Log.i("MAct GPSLocationService", msg);

                //비콘 탐지
                //BeaconSearch bs = new BeaconSearch();

                // 현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
                showCurrentLocation(latitude, longitude);
            }
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
        origin = curPoint;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN, 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng Korea = new LatLng(36.732461, 127.988181);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Korea, 10));
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                        Log.i("MActBeacon", "\n------------------------------------------------");
                        Log.d(TAG, "Mac address is: " + beacon.getBluetoothAddress());
                        beaconMsg = bc.BeaconCheck(beacon.getBluetoothAddress());
                        Log.i("beaconMsg", beaconMsg);
                        if (beaconMsg.equalsIgnoreCase("Success")) {
                            Log.i("Msg is ", beaconMsg);
                            Intent intent1 = new Intent(getApplicationContext(), ParkingMap.class);

                            if (MapsEnd == false) {

                                startActivity(intent1);
                            }
                            MapsEnd = true;
                            finish();
                            Log.d(TAG, "is Return");

                        }
                        Log.i("Beacon", "\n------------------------------------------------");
                        beaconList.add(beacon);

                    }
                }
            }
        });


        // handler.sendEmptyMessage(0);

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
    /*Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //자기 자신을 1초마다 호출
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };*/

    @Override
    public void onBackPressed() {
        backPressClose.onBackPressed();
    }
}