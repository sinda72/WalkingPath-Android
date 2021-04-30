package dayoung.walkingpath;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class PathMapActivity<Double> extends AppCompatActivity
        implements OnMapReadyCallback {
    ListView listView;
    public ArrayList line_lat;
    public ArrayList line_long;
    public ArrayList point_lat;
    public ArrayList point_long;
    public ArrayList subway_lat;
    public ArrayList subway_long;
    public ArrayList bus_lat;
    public ArrayList bus_long;
    public ArrayList walk_point;

    String p_info2="";
    String path_info="";
    String end_info="";


    int totalTime_p=0;
    double totalCal_p=0;
    String totalWalk_p="";

    TextView r_time;
    TextView r_road;
    TextView r_cal;
    TextView r_walk;
    TextView r_path;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();

        p_info2=intent.getExtras().getString("p_info");//도보시간
        totalCal_p=intent.getExtras().getDouble("totalCal");
        totalTime_p=intent.getExtras().getInt("totalTime");
        totalWalk_p=intent.getExtras().getString("totalWalk");//걸음수
        path_info=intent.getExtras().getString("path_info");
        end_info=intent.getExtras().getString("end_info");

        r_time=(TextView) findViewById(R.id.r_time);
        r_cal=(TextView) findViewById(R.id.r_cal);
        r_road=(TextView) findViewById(R.id.r_road);
        r_walk=(TextView) findViewById(R.id.r_walk);
        r_path=(TextView) findViewById(R.id.r_path);


        int totalTime3_1 = totalTime_p/60;
        int totalTime3_2 = totalTime_p%60;

        if(totalTime3_1==0){
            r_time.setText(totalTime3_2+"분");
        }else {
            r_time.setText(totalTime3_1 + "시간 " + totalTime3_2 + "분");
        }
        r_cal.setText("-"+totalCal_p+"Kcal");
        r_walk.setText(totalWalk_p);
        r_road.setText(p_info2);
        r_path.setText(path_info+end_info+"하차");

        line_lat = new ArrayList<Double>();
        line_long = new ArrayList<Double>();
        point_lat = new ArrayList<Double>();
        point_long = new ArrayList<Double>();
        subway_lat = new ArrayList<Double>();
        subway_long = new ArrayList<Double>();
        bus_lat = new ArrayList<Double>();
        bus_long = new ArrayList<Double>();
        walk_point = new ArrayList<Double>();

        point_lat = (ArrayList<Double>) intent.getSerializableExtra("point_lat");//방향바뀌는부분
        point_long = (ArrayList<Double>) intent.getSerializableExtra("point_long");
        line_lat = (ArrayList<Double>) intent.getSerializableExtra("line_lat");//도보경로 시작
        line_long = (ArrayList<Double>) intent.getSerializableExtra("line_long");

        subway_lat = (ArrayList<Double>) intent.getSerializableExtra("subway_lat");
        subway_long = (ArrayList<Double>) intent.getSerializableExtra("subway_long");
        bus_lat = (ArrayList<Double>) intent.getSerializableExtra("bus_lat");
        bus_long = (ArrayList<Double>) intent.getSerializableExtra("bus_long");

        walk_point = (ArrayList<Double>) intent.getSerializableExtra("walk_point");
        System.out.println("ddddddd "+ walk_point);
        System.out.println("qqqqqqq "+ walk_point.size());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //onMapReady(mMap, point_lat,point_long,line_lat, line_long);
        //onMapReady(mMap);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        LatLng SEOUL = new LatLng((java.lang.Double) line_lat.get(0),(java.lang.Double) line_long.get(0));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        //markerOptions.position(new LatLng((Double)point_lat.get(point_lat.size()-1),(Double)point_long.get(point_long.size()-1)));
        markerOptions.title("START");
        markerOptions.snippet("도보 걷기 시작");

        MarkerOptions markerOptions1 = new MarkerOptions();
        //markerOptions.position(SEOUL);
        markerOptions1.position(new LatLng((java.lang.Double)point_lat.get(point_lat.size()-1),(java.lang.Double)point_long.get(point_long.size()-1)));
        markerOptions1.title("END");
        markerOptions1.snippet("도착!");

        mMap.addMarker(markerOptions);
        mMap.addMarker(markerOptions1);

        //tmap이 운동하는,,,
        PolylineOptions final_walk = new PolylineOptions();
        final_walk.color(Color.RED);
        final_walk.width(15);
        for(int i = 0; i<line_long.size();i++){
            final_walk.add(new LatLng((java.lang.Double)line_lat.get(i),(java.lang.Double)line_long.get(i)));
        }

        System.out.println("test11111111 "+ walk_point);
        /*
        for(int i = 0; i<walk_point.size()-1;i++){
            PolylineOptions walkline = new PolylineOptions();
            walkline.color(Color.BLACK);
            walkline.width(15);
            List walk_i = (List) walk_point.get(i);
            List walk_ii = (List) walk_point.get(i+1);
            walkline.add(new LatLng((java.lang.Double)walk_i.get(3),(java.lang.Double)walk_i.get(2)));
            walkline.add(new LatLng((java.lang.Double)walk_ii.get(1),(java.lang.Double)walk_ii.get(0)));
            mMap.addPolyline(walkline);
        }*/

        for(int i = 0; i<bus_long.size();i++){
            PolylineOptions busline = new PolylineOptions();
            busline.color(Color.rgb(255,198,42));
            busline.width(15);
            List b_long = (List) bus_long.get(i);
            List b_lat = (List) bus_lat.get(i);
            for(int j = 0; j<b_long.size(); j++) {
                busline.add(new LatLng((java.lang.Double) b_lat.get(j), (java.lang.Double) b_long.get(j)));
            }
            mMap.addPolyline(busline);
        }


        for(int i = 0; i<subway_long.size();i++){
            PolylineOptions subline = new PolylineOptions();
            subline.color(Color.rgb(55,189,165));
            subline.width(15);
            List s_long = (List) subway_long.get(i);
            List s_lat = (List) subway_lat.get(i);
            for(int j = 0; j<s_long.size(); j++) {
                subline.add(new LatLng((java.lang.Double) s_lat.get(j), (java.lang.Double) s_long.get(j)));
            }
            mMap.addPolyline(subline);

        }

        mMap.addPolyline(final_walk);
        //mMap.addPolyline(busline);
        //mMap.addPolyline(subline);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 12));


    }



}
