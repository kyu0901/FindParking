package org.androidtown.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class ParkingMap extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    String TAG = "Parking Map";
    //감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();

    //Map image
    private ImageView imageView;
    private String add;
    private String add2;
    private String cdn;
    private Paint paint;
    private Coordinate[] coor;
    private Coordinate[] temp;
    private Edge[] edges;
    private String Mac = null;
    private Stack<Integer> S;
    private Hashtable<Integer, Coordinate> map = new Hashtable<Integer, Coordinate>();
    private Path path;
    private boolean beaconcheck;
    private boolean goMap = false;
    private String parkroad;

    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);

        //실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25"));

        imageView = (ImageView) findViewById(R.id.imageViewShow);

        //비콘 탐지를 시작(비콘 서비스 시작)
        beaconManager.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        final UserInfo userInfo = (UserInfo) getApplicationContext();

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            //비콘이 감지되면 해당 함수가 호출. Collection<Beacon> beacons에는 감지된 비콘의 리스트가
            //region에는 비콘들에 대응하는 region객체가 들어옴
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                ParkingImage pi = new ParkingImage();
                RequestCoordinate rc = new RequestCoordinate();
                System.out.println("------- didRangeBeacons!!!!");
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconcheck = true;
                        Log.i("Beacon", "\n------------------------------------------------");
                        if (Mac == null) {
                            Mac = beacon.getBluetoothAddress();
                            add = pi.ParkingImage(Mac, userInfo.getHandicap());
                            cdn = rc.RequestCoordinate(Mac);
                            coor = new Coordinate[Integer.parseInt(cdn.split(" ")[0])];
                            if (!add.equalsIgnoreCase("Invalid")) {
                                int i = 0;
                                int j = 0;
                                String check;
                                int id, x, y;
                                do {
                                    id = Integer.parseInt(cdn.split(" ")[i]);
                                    Log.i("id", "is " + id);
                                    i++;
                                    x = Integer.parseInt(cdn.split(" ")[i]);
                                    Log.i("x", "is " + x);
                                    i++;
                                    y = Integer.parseInt(cdn.split(" ")[i]);
                                    Log.i("y", "is " + y);
                                    i++;
                                    check = cdn.split(" ")[i];
                                    Log.i("check", "is " + check);
                                    coor[j] = new Coordinate(x, y);
                                    map.put(id, coor[j]);
                                } while (!Objects.equals(check, "end"));

                                getImage(add);
                            }
                        } else {
                            add2 = pi.ParkingImage(Mac, userInfo.getHandicap());
                            if (!add.equalsIgnoreCase(add2)) {
                                Mac = beacon.getBluetoothAddress();

                                cdn = rc.RequestCoordinate(Mac);

                                if (!cdn.split(" ")[0].equalsIgnoreCase("Invalid")) {
                                    coor = new Coordinate[Integer.parseInt(cdn.split(" ")[0])];
                                }
                                if (!add.equalsIgnoreCase("Invalid")) {
                                    int i = 0;
                                    int j = 0;
                                    String check;
                                    int id, x, y;
                                    do {
                                        id = Integer.parseInt(cdn.split(" ")[i]);
                                        i++;
                                        x = Integer.parseInt(cdn.split(" ")[i]);
                                        i++;
                                        y = Integer.parseInt(cdn.split(" ")[i]);
                                        i++;
                                        check = cdn.split(" ")[i];
                                        coor[j] = new Coordinate(x, y);
                                        map.put(id, coor[j]);
                                    } while (!Objects.equals(check, "end"));


                                    add = add2;
                                    getImage(add);
                                }

                            }
                        }
                        Log.d(TAG, "Mac address is: " + Mac);

                        beaconList.add(beacon);

                    }
                } else if (beacons.size() == 0) {
                    Log.i(TAG, "No beacon");
                    beaconcheck = false;
                    //비콘이 없을 경우 다시 구글맵으로 이동
                    timer.schedule(new TimerTask() {
                                       public void run() {
                                           if(!beaconcheck) {
                                               if(goMap == false) {
                                                   Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                                   Log.d(TAG, " go to Maps");
                                                   startActivity(intent);
                                                   finish();
                                                   goMap = true;
                                               }
                                           }
                                       }
                                   }
                            , 10000);
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

            //비콘의 아이디와 거리를 측정하여 tv에 넣음
            for (Beacon beacon : beaconList) {
                String msg2 = String.valueOf(beacon.getDistance());
                Log.i("dis : ", msg2);
            }
            //자기 자신을 2초마다 호출
            handler.sendEmptyMessageDelayed(0, 2000);
        }
    };

    private void getImage(final String add) {

        class GetImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                Log.i("where?", "onPost");
                super.onPostExecute(b);

                imageView.setImageBitmap(b);

            }

            @Override
            protected Bitmap doInBackground(String... params) {

                String add = params[0];

                String imagesrc;
                int[] des;
                String x, y, use;   //x좌표, y좌표, 빈자리 여부
                int from, to, w, cntEdges;
                URL url;
                Bitmap bm = null;
                Canvas canvas = new Canvas();

                try {
                    String check;
                    int i = 0;


                    Log.i("This", "add" + add);
                    imagesrc = add.split(" ")[i];
                    Log.i("this imagesrc", imagesrc);
                    i++;
                    parkroad = add.split(" ")[i];
                    Log.i("this parkroad", parkroad);
                    i++;
                    url = new URL(imagesrc);
                    URLConnection conn = url.openConnection();

                    BufferedInputStream bis = new BufferedInputStream(
                            conn.getInputStream());
                    bm = BitmapFactory.decodeStream(bis);
                    Bitmap newImage = Bitmap.createBitmap(bm).copy(Bitmap.Config.ARGB_8888, true);

                    do {
                        x = add.split(" ")[i];
                        i++;
                        y = add.split(" ")[i];
                        i++;
                        use = add.split(" ")[i];
                        i++;
                        check = add.split(" ")[i];
                        Log.d("x : ", x);
                        Log.d("y : ", y);
                        Log.d("use : ", use);
                        Log.d("check : ", check);

                        bm = onDraw(canvas, newImage, Float.parseFloat(x), Float.parseFloat(y), Integer.parseInt(use));
                    } while (!Objects.equals(check, "des"));
                    i++;
                    int j = 0;
                    Log.i(TAG, "desnum " + add.split(" ")[i]);
                    des = new int[Integer.parseInt(add.split(" ")[i])];
                    i++;
                    Log.i(TAG, " add is " + des[j]);
                    do {
                        Log.i("desdsesd : ", "");
                        des[j] = Integer.parseInt(add.split(" ")[i]);
                        i++;
                        Log.i("des : ", "" + des[j]);
                        check = add.split(" ")[i];
                        j++;
                    } while (!Objects.equals(check, "road"));
                    i++;
                    j = 0;
                    cntEdges = Integer.parseInt(add.split(" ")[i]);
                    Log.i("cntEdges : ", "" + cntEdges);
                    i++;
                    edges = new Edge[cntEdges];

                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(parkroad);
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity ht = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(ht);
                    InputStream is = buf.getContent();
                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    String line;
                    i = 0;
                    while ((line = r.readLine()) != null) {
                        to = Integer.parseInt(line.split(" ")[i]);
                        Log.d("to : ", "" + to);
                        i++;
                        from = Integer.parseInt(line.split(" ")[i]);
                        Log.d("from : ", "" + from);
                        i++;
                        w = Integer.parseInt(line.split(" ")[i]);
                        Log.d("w : ", "" + w);
                        i = 0;

                        edges[j] = new Edge(to, from, w);
                        j++;
                    }
                    DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(edges, des);
                    dijkstraAlgorithm.calculateShortestDistances();
                    S = dijkstraAlgorithm.printResult();
                    int t = 0;
                    temp = new Coordinate[S.size()];
                    temp[t] = map.get(S.pop());
                    Log.i("temp 0 :::", "" + temp[t]);

                    Paint MyPaint = new Paint();
                    MyPaint.setStrokeWidth(5f);
                    MyPaint.setStyle(Paint.Style.STROKE);
                    MyPaint.setColor(Color.BLUE);
                    path = new Path();
                    path.moveTo(temp[t].getX(), temp[t].getY());
                    t++;
                    while (!S.isEmpty()) {
                        temp[t] = map.get(S.pop());
                        path = onDrawLine(path, temp[t].getX(), temp[t].getY());
                        t++;
                    }
                    canvas.drawPath(path, MyPaint);
                    canvas.drawBitmap(bm, 0, 0, null);
                    Log.i("where?", "url image");
                } catch (MalformedURLException e) {
                    Log.i("where?", "Malformed");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("where?", "IOExcpetion");
                    e.printStackTrace();
                }

                Log.d("bm", "is " + bm);
                return bm;
            }

            protected Bitmap onDraw(Canvas canvas, Bitmap bm, float x, float y, int use) {

                paint = new Paint();
                paint.setColor(getResources().getColor(R.color.FireBrick));
                Paint paint2 = new Paint();
                paint2.setColor(getResources().getColor(R.color.LimeGreen));

                Log.d(TAG, "bm : " + bm);
                Log.d(TAG, "x : " + x);
                Log.d(TAG, "y : " + y);
                Log.d(TAG, "z :" + use);

                canvas.setBitmap(bm);
                Log.d(TAG, "setBitmap is OK");
                if (use == 0) {
                    canvas.drawCircle(x, y, 30, paint2);
                } else if (use == 1) {
                    canvas.drawCircle(x, y, 30, paint);
                }
                canvas.drawBitmap(bm, 0, 0, null);
                Log.d(TAG, "After drawBitmap");

                return bm;
            }

            protected Path onDrawLine(Path path, int x, int y) {


                path.lineTo(x, y);
                return path;
            }
        }

        GetImage gi = new GetImage();
        Log.i(TAG, "GetImage");
        gi.execute(add);
    }
}