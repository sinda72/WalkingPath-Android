package dayoung.walkingpath;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


public class PathActivity extends AppCompatActivity{
    ArrayList<PathData> pathDataList;
    ListView listView;
    ImageView path_btn;
    TextView fromEdit;
    TextView toEdit;
    ImageView changeBtn;
    public static Context context;
    int[][] path_info;
    double[][] station_info1;//좌표
    int[] station_info2;//인덱스
    //출발지 도착지 좌표
    String from_latitude=null;
    String from_longitude=null;
    String to_latitude=null;
    String to_longitude=null;
    String parse_type3="";
    String parse_type="";//도보 총시간
    String s_name2;
    int door=0;
    int length=0;//경로 길이
    int length2=0;//이용하는 교통수단 종류 길이
    int all=0;
    int totalTime=0;
    String p_info="";
    String p_info2="";
    String p_info3="";
    int totalTime_p=0;
    double totalCal_p=0;
    String totalWalk_p="";
    String endinfo="";
    int radiocheck_i=2;

    private RadioGroup radioGroup;

    ArrayList line_lat;
    ArrayList line_long;
    ArrayList point_lat;
    ArrayList point_long;

    List bus_ypoint;
    List<List> bus_lat;

    List bus_xpoint;
    List<List> bus_long;

    List sub_ypoint;
    List<List> subway_lat;

    List sub_xpoint;
    List<List> subway_long;

    List walk_point;
    List<List> WALK_POINT ;

    //버스번호, 지하철 호선
    String[][] path_info2;
    //역이름
    String[] station_info3;
    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 11;
    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 22;

    private static final String TAG = PathActivity.class.getSimpleName();
    ContentValues values = new ContentValues();


    private ODsayService odsayService;
    private JSONObject jsonObject;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        ContentValues values = new ContentValues();
       // context=this;

        line_lat = new ArrayList<Double>();
        line_long = new ArrayList<Double>();
        point_lat = new ArrayList<Double>();
        point_long = new ArrayList<Double>();
        bus_ypoint = new ArrayList();
        bus_lat = new ArrayList();

        bus_xpoint = new ArrayList();
        bus_long = new ArrayList();

        sub_ypoint = new ArrayList();
        subway_lat = new ArrayList();

        sub_xpoint = new ArrayList();
        subway_long = new ArrayList();

        walk_point = new ArrayList();
        WALK_POINT = new ArrayList();

        fromEdit = findViewById(R.id.fromEdit);
        toEdit = findViewById(R.id.toEdit);

        this.InitializePathData();
        listView = (ListView)findViewById(R.id.list_view);
        listView.setVisibility(View.INVISIBLE);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pathDataList.clear();
                door=0;
                if(checkedId==R.id.radio_1){
                    radiocheck_i=1;
                }else if(checkedId==R.id.radio_2){
                    radiocheck_i=2;
                }else radiocheck_i=3;
            }
        });

        path_btn = (ImageView)findViewById(R.id.path_btn);
        path_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from_latitude==null||to_latitude==null){
                    Toast.makeText(PathActivity.this, "경로를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else {
                    if (door == 0) {
                        door=1;
                        //경도(127), 위도
                        System.out.println("Test new order : "+3);

                        odsayService.requestSearchPubTransPath(from_longitude, from_latitude, to_longitude, to_latitude, "0", "0", "0", onResultCallbackListener);
                        //odsayService.requestSearchPubTransPath(from_longitude, from_latitude, Double.toString(station_info1[0][length-1]), Double.toString(station_info1[1][length-1]), "0", "0", "0", onResultCallbackListener);
                    }
                }
            }
        });
        //출발지 도착지 변경
        changeBtn = (ImageView)findViewById(R.id.arrow_btn);
        changeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                pathDataList.clear();
                door=0;//재검색으로
                line_lat = new ArrayList<Double>();
                line_long = new ArrayList<Double>();

                point_lat = new ArrayList<Double>();
                point_long = new ArrayList<Double>();

                bus_ypoint = new ArrayList();
                bus_lat = new ArrayList();

                bus_xpoint = new ArrayList();
                bus_long = new ArrayList();

                sub_ypoint = new ArrayList();
                subway_lat = new ArrayList();

                sub_xpoint = new ArrayList();
                subway_long = new ArrayList();

                walk_point = new ArrayList();
                WALK_POINT = new ArrayList();

                if(from_latitude==null||to_latitude==null){
                    Toast.makeText(PathActivity.this, "경로를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else {
                    if (door == 0) {
                        door=1;

                        String t="";
                        t=to_longitude;
                        to_longitude=from_longitude;
                        from_longitude=t;
                        t=to_latitude;
                        to_latitude=from_latitude;
                        from_latitude=t;

                        String box="";
                        box=fromEdit.getText().toString();

                        fromEdit.setText(toEdit.getText().toString());
                        toEdit.setText(box);

                        odsayService.requestSearchPubTransPath(from_longitude, from_latitude, to_longitude, to_latitude, "0", "0", "0", onResultCallbackListener);
                        //odsayService.requestSearchPubTransPath(from_longitude, from_latitude, Double.toString(station_info1[0][length-1]), Double.toString(station_info1[1][length-1]), "0", "0", "0", onResultCallbackListener);
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                //Toast.makeText(PathActivity.this, "ID: "+position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PathActivity.this, PathMapActivity.class);
                System.out.println("확인: ");
                System.out.println("확인: "+totalTime_p+" "+totalCal_p+" "+totalWalk_p+" "+p_info2);
                intent.putExtra("point_lat",point_lat);
                intent.putExtra("point_long",point_long);
                intent.putExtra("line_lat",line_lat);
                intent.putExtra("line_long",line_long);
                intent.putExtra("subway_lat", (Serializable) subway_lat);
                intent.putExtra("subway_long", (Serializable) subway_long);
                intent.putExtra("bus_lat", (Serializable) bus_lat);
                intent.putExtra("bus_long", (Serializable) bus_long);
                intent.putExtra("walk_point", (Serializable) WALK_POINT);
                //totalTime3     totalCal    Math.round(totalDistance/80)   p_info2
                intent.putExtra("totalTime", totalTime_p);
                intent.putExtra("totalCal", totalCal_p);
                intent.putExtra("totalWalk", totalWalk_p);//총걸음수
                intent.putExtra("p_info", p_info2);//총도보시간
                intent.putExtra("path_info", p_info);
                intent.putExtra("end_info", endinfo);

                startActivity(intent);
            }
        });
       //장소검색 자동완성
        if (!Places.isInitialized()) {
            //AIzaSyBfZI4lgQGcylNp4efZ059i4yG7UC6qMIs
            //AIzaSyAVTh38MPsq7DMWoJRCJ0s74Egewb55pBE
            Places.initialize(getApplicationContext(), "AIzaSyBfZI4lgQGcylNp4efZ059i4yG7UC6qMIs");
        }
        fromEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test new order : "+2);
                line_lat = new ArrayList<Double>();
                line_long = new ArrayList<Double>();

                point_lat = new ArrayList<Double>();
                point_long = new ArrayList<Double>();

                bus_ypoint = new ArrayList();
                bus_lat = new ArrayList();

                bus_xpoint = new ArrayList();
                bus_long = new ArrayList();

                sub_ypoint = new ArrayList();
                subway_lat = new ArrayList();

                sub_xpoint = new ArrayList();
                subway_long = new ArrayList();

                walk_point = new ArrayList();
                WALK_POINT = new ArrayList();
                //Do your stuff from place
                    onSearchCalled(AUTOCOMPLETE_FROM_REQUEST_CODE);
            }
        });
        toEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Do your stuff from place
                line_lat = new ArrayList<Double>();
                line_long = new ArrayList<Double>();

                point_lat = new ArrayList<Double>();
                point_long = new ArrayList<Double>();

                bus_ypoint = new ArrayList();
                bus_lat = new ArrayList();

                bus_xpoint = new ArrayList();
                bus_long = new ArrayList();

                sub_ypoint = new ArrayList();
                subway_lat = new ArrayList();

                sub_xpoint = new ArrayList();
                subway_long = new ArrayList();

                walk_point = new ArrayList();
                WALK_POINT = new ArrayList();
                onSearchCalled(AUTOCOMPLETE_TO_REQUEST_CODE);
            }
        });

    }
    public void InitializePathData()
    {
        context = this;

        odsayService = ODsayService.init(PathActivity.this, getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        pathDataList = new ArrayList<PathData>();
    }
    public void inni(int[][] path_info, String[][] path_info2){
        //final MyAdapter myAdapter = new MyAdapter(this,pathDataList);

        System.out.println("Test order : "+5);
        //listView.setAdapter(myAdapter);
        totalTime=0;
        p_info="";
        p_info2="";

      //오늘 수정한 부ㅜㅂㄴ
        if(path_info[2][length2-2]<11&&path_info[0][length2-2]==2){
            for(int i=0; i< path_info[0].length-2; i++){
                switch (path_info[0][i]){
                    case 1://지하철
                        p_info=p_info+path_info2[2][i]+"\n"+"("+path_info2[1][i]+")\n";
                        break;
                    case 2://버스
                        p_info=p_info+path_info2[2][i]+"\n"+"(버스 "+path_info2[0][i]+")\n";
                        break;
                    default:
                        break;
                }
            }
        }else{
            for(int i=0; i< path_info[0].length; i++){
                    switch (path_info[0][i]){
                        case 1://지하철
                            p_info=p_info+path_info2[2][i]+"\n"+"("+path_info2[1][i]+")\n";
                            break;
                        case 2://버스
                            p_info=p_info+path_info2[2][i]+"\n"+"(버스 "+path_info2[0][i]+")\n";
                            break;
                        default:
                            break;

                }
            }
        }

        for (int j = 0; j < path_info[2].length; j++) {
            totalTime += path_info[2][j];
        }
        total();

        System.out.println("Test order : "+4);

    }

    public void total_result(int totalTime3, int totalDistance, double totalCal, String endname) {
        final MyAdapter myAdapter = new MyAdapter(this,pathDataList);
        //float re_cal=3.3 * (3.5 * 60 * total)
        totalTime_p=totalTime3;
        totalCal_p=totalCal;
        totalWalk_p=Math.round(totalDistance/80)+" 걸음";
        listView.setAdapter(myAdapter);
        int totalTime3_1 = totalTime3/60;
        int totalTime3_2 = totalTime3%60;
        //마지막 교통수단 이용하지 않고 도보 이용할 경우 하차하는 곳이 전
        if(path_info[2][length2-2]<11&&path_info[0][length2-2]==2){//버스고, 11분 이내일 경우
            endname=s_name2+"역";
        }
        endinfo=endname;

        if(totalTime3_1==0){
            pathDataList.add(new PathData(totalTime3_2+"분","- "+totalCal+"Kcal","( "+Math.round(totalDistance/80)+" 걸음 )",
                    p_info+endname+" 하차", p_info2));
        }else {
            pathDataList.add(new PathData(totalTime3_1 + "시간 " + totalTime3_2 + "분", "- " + totalCal + "Kcal", "( " + Math.round(totalDistance / 80) + " 걸음 )",
                    p_info + endname + " 하차", p_info2));
        }

        System.out.println("Test order : "+6+"=>"+totalTime3);

        listView.setVisibility(View.VISIBLE);
    }

    //여기부터 다시 ㄱ시작
    public void total() {
        Number x=Double.parseDouble(to_longitude);//도착지
        Number y=Double.parseDouble(to_latitude);
        Number x2; Number y2; Number x3; Number y3;
        int i,j;
        if(path_info[0][length2-2]==1) {//지하철일 경우
            if(radiocheck_i==1) {//강=>
                i = 4;
                j = 3;
            }else if(radiocheck_i==2){
                i = 3;
                j = 2;
            }else{
                i = 2;
                j = 1;
            }
            x2 = station_info1[0][length - i];
            y2 = station_info1[1][length - i];

            x3 = station_info1[0][length - j];
            y3 = station_info1[1][length - j];

        }else{//버스일 경우
            if(path_info[2][length2-2]<11&&path_info[0][length2-2]==2){//대중교통 이용할 경우 12분보다 적게 걸릴 경우
                x3 = station_info1[0][0];
                y3 = station_info1[1][0];

                x2 = station_info1[0][0];
                y2 = station_info1[1][0];
                i = length;
                j = length;
            }else {
                if(radiocheck_i==1) {//강=>
                    i = 6;
                    j = 5;
                }else if(radiocheck_i==2){
                    i = 5;
                    j = 4;
                }else{
                    i = 4;
                    j = 3;
                }
                x2 = station_info1[0][length - i];
                y2 = station_info1[1][length - i];

                x3 = station_info1[0][length - j];
                y3 = station_info1[1][length - j];

            }
        }

        NetworkTask networkTask = new
                NetworkTask(" https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json&appKey=l7xx427832d1a5e9495f8f2bf034113f5816&startName=a&endName=b",
                values, x3.doubleValue(), y3.doubleValue(), x.doubleValue(), y.doubleValue(), station_info3[length-j]);
        //경도 위도
        networkTask.execute();
        if(path_info[2][length2-2]>11){
            NetworkTask networkTask2 = new
                    NetworkTask(" https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json&appKey=l7xx427832d1a5e9495f8f2bf034113f5816&startName=a&endName=b",
                    values, x2.doubleValue(), y2.doubleValue(), x.doubleValue(), y.doubleValue(), station_info3[length-i]);
            //경도 위도
            networkTask2.execute();
        }

        /*
        try {
            networkTaskd.join();
        }catch (InterruptedException e){}
        //networkTask.join();
        //networkTask;*/
        System.out.println("Test order : "+1);
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {

            jsonObject = oDsayData.getJson();

            try {
                JSONObject parse_result=jsonObject.getJSONObject("result");
                JSONArray parse_path=parse_result.getJSONArray("path");
                JSONObject parse_1=parse_path.getJSONObject(0);
                JSONArray parse_subpath=parse_1.getJSONArray("subPath");

                JSONObject index = parse_subpath.getJSONObject(parse_subpath.length()-2);
                if(parse_subpath.length()>3){
                    JSONObject index2 = parse_subpath.getJSONObject(parse_subpath.length()-4);
                    JSONObject passStopList2=index2.getJSONObject("passStopList");
                    JSONArray stations2=passStopList2.getJSONArray("stations");
                    JSONObject result2 = stations2.getJSONObject(stations2.length()-1);
                    s_name2 = result2.getString("stationName");//지하철 마지막
                }
                JSONObject passStopList=index.getJSONObject("passStopList");
                JSONArray stations=passStopList.getJSONArray("stations");
                length=stations.length();

                station_info1 = new double[2][stations.length()];
                station_info2 = new int[stations.length()];
                station_info3 = new String[stations.length()];

                //지연이파트 (경로 그리기 위한 변수설정)
                JSONObject path0 = parse_subpath.getJSONObject(0);
                int trafficType0 = path0.getInt("trafficType");
                JSONObject path1 = parse_subpath.getJSONObject(1);
                int trafficType1 = path1.getInt("trafficType");
                Number fx=Double.parseDouble(from_longitude);//도착지
                Number fy=Double.parseDouble(from_latitude);
                if (trafficType0 == 3){
                    if(trafficType1 == 1){

                        walk_point = new ArrayList();
                        walk_point.add(path1.getDouble("startX"));
                        walk_point.add(path1.getDouble("startY"));
                        walk_point.add(fx);//처음출발점
                        walk_point.add(fy);
                        WALK_POINT.add(walk_point);
                    }

                    if(trafficType1 == 2){
                        walk_point = new ArrayList();
                        walk_point.add(path1.getDouble("startX"));
                        walk_point.add(path1.getDouble("startY"));
                        walk_point.add(fx);//처음출발점
                        walk_point.add(fy);
                        WALK_POINT.add(walk_point);
                    }
                }
                for(int i=1; i<parse_subpath.length();i++){
                    JSONObject path = parse_subpath.getJSONObject(i);
                    int trafficType = path.getInt("trafficType");

                    //처음에 걸어야 하면, 좌표값 넣음

                    if(trafficType == 1) {
                        JSONObject get_sub = path.getJSONObject("passStopList");
                        JSONArray sub_parse = get_sub.getJSONArray("stations");
                        walk_point = new ArrayList();
                        walk_point.add(path.getDouble("startX"));
                        walk_point.add(path.getDouble("startY"));
                        walk_point.add(path.getDouble("endX"));
                        walk_point.add(path.getDouble("endY"));
                        WALK_POINT.add(walk_point);
                        sub_ypoint = new ArrayList();//lat
                        sub_xpoint = new ArrayList();//long
                        for (int j = 0; j < sub_parse.length(); j++) {
                            JSONObject sub_line = sub_parse.getJSONObject(j);
                            sub_xpoint.add(sub_line.getDouble("x"));
                            sub_ypoint.add(sub_line.getDouble("y"));
                        }
                        subway_lat.add(sub_ypoint);
                        subway_long.add(sub_xpoint);
                    }

                    if(trafficType == 2) {
                        JSONObject get_bus = path.getJSONObject("passStopList");
                        JSONArray bus_parse = get_bus.getJSONArray("stations");
                        walk_point = new ArrayList();
                        walk_point.add(path.getDouble("startX"));
                        walk_point.add(path.getDouble("startY"));
                        walk_point.add(path.getDouble("endX"));
                        walk_point.add(path.getDouble("endY"));
                        WALK_POINT.add(walk_point);
                        bus_ypoint = new ArrayList();//lat
                        bus_xpoint = new ArrayList();//long
                        for (int j = 0; j < bus_parse.length(); j++) {
                            JSONObject bus_line = bus_parse.getJSONObject(j);
                            bus_xpoint.add(bus_line.getDouble("x"));
                            bus_ypoint.add(bus_line.getDouble("y"));
                        }
                        bus_lat.add(bus_ypoint);
                        bus_long.add(bus_xpoint);
                    }

                }//지연이 파트(변수 설정)


                for(int i=0; i<stations.length(); i++){
                    //index랑 x,y좌표, stationName (4개 data)
                    JSONObject result = stations.getJSONObject(i);
                    int s_index = result.getInt("index");
                    Double s_x = result.getDouble("x");//경도
                    Double s_y = result.getDouble("y");
                    String s_name = result.getString("stationName");

                    station_info2[i]=s_index;
                    station_info1[0][i]=s_x;
                    station_info1[1][i]=s_y;
                    station_info3[i]=s_name;

                    System.out.println("Example -> "+s_name);
                }
                System.out.println("경도 : "+station_info1[0][stations.length()-2]);


                //TrafficType, 거리, 걸린 시간
                path_info = new int[3][parse_subpath.length()];
                length2=path_info[0].length;

                //버스번호, 지하철 호선
                path_info2 = new String[4][parse_subpath.length()];

                for(int i=0; i<parse_subpath.length(); i++){
                    JSONObject path = parse_subpath.getJSONObject(i);
                    int trafficType = path.getInt("trafficType");
                    int distance = path.getInt("distance");
                    int sectionTime = path.getInt("sectionTime");

                    path_info[0][i]=trafficType;
                    path_info[1][i]=distance;
                    path_info[2][i]=sectionTime;

                    if(trafficType==2){//버스인경우
                        JSONArray lane = path.getJSONArray("lane");
                        JSONObject lane1 = lane.getJSONObject(0);

                        String busNo = lane1.getString("busNo");
                        String startName = path.getString("startName");
                        String endName = path.getString("endName");
                        path_info2[0][i]=busNo;
                        path_info2[2][i]=startName;
                        path_info2[3][i]=endName;
                    }
                    if(trafficType==1){//지하철인경우
                        JSONArray lane = path.getJSONArray("lane");
                        JSONObject lane1 = lane.getJSONObject(0);

                        String subwayName = lane1.getString("name");
                        String startName = path.getString("startName");
                        String endName = path.getString("endName");

                        path_info2[1][i]=subwayName;
                        path_info2[2][i]=startName+"역";
                        path_info2[3][i]=endName;
                        //System.out.println("Test-subway "+i+" =>"+path_info2[1][i]);
                    }

                    MyPath mypath = new MyPath(trafficType);
                    mypath.setTrafficType(trafficType);

                    System.out.println("Test-da "+i+" =>"+trafficType);
                }
                for(int i=0; i<path_info.length; i++) {
                    for (int j = 0; j < path_info[i].length; j++) {
                        System.out.println("결과: "+i+" :"+ path_info[i][j]);
                    }
                }
                for(int i=0; i<path_info2.length; i++) {
                    for (int j = 0; j < path_info2[i].length; j++) {
                        System.out.println("결과: " + path_info2[i][j]);
                    }
                }
                inni(path_info, path_info2);
                System.out.println("Test order : "+10);
                //total_result();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onError(int i, String errorMessage, API api) {
        }
    };
    //장소 검색 자동 완성 호출
    public void onSearchCalled(int Code) {
        pathDataList.clear();
        door=0;//재검색으로
        if(Code==AUTOCOMPLETE_FROM_REQUEST_CODE) {fromEdit.setText("출발지 검색"); from_latitude=null; from_longitude=null;}
        else if(Code==AUTOCOMPLETE_TO_REQUEST_CODE) {toEdit.setText("도착지 검색"); to_longitude=null; to_longitude=null;}
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields).setCountry("KR")
                .build(this);
        startActivityForResult(intent, Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
             //   Toast.makeText(PathActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                from_latitude = Double.toString(place.getLatLng().latitude);
                from_longitude = Double.toString(place.getLatLng().longitude);

                fromEdit.setText(place.getName());
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            //    Toast.makeText(PathActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
            //    Toast.makeText(PathActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                to_latitude = Double.toString(place.getLatLng().latitude);
                to_longitude = Double.toString(place.getLatLng().longitude);

                toEdit.setText(place.getName());
                // do query with address
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
               // Toast.makeText(PathActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String url="";
        String endName="";
        //ContentValues values;

        NetworkTask(String url, ContentValues values, Number sX, Number sY, Number eX, Number eY, String name) {
            //url뒤에 파라미터 추가
            url += ("&startX="+sX);
            url += ("&startY="+sY);
            url += ("&endX="+eX);
            url += ("&endY="+eY);
            endName=name;

            this.url = url;
            System.out.println("check" + url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result="";
            System.out.println("Test order : "+2);

            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서
            String str = result;
            String parse_type="";
            String parse_type2="";
            line_lat=new ArrayList<Double>();
            line_long=new ArrayList<Double>();

            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray("features");
                JSONObject parse_0 = jsonArray.getJSONObject(0);
                //좌표를 찾기 위한 geometry
                JSONObject parse_geo = parse_0.getJSONObject("properties");
                parse_type = parse_geo.getString("totalTime");
                parse_type2 = parse_geo.getString("totalDistance");

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject path = jsonArray.getJSONObject(i);
                    JSONObject parse_geoi = path.getJSONObject("geometry");
                    JSONArray coor = parse_geoi.getJSONArray("coordinates");
                    String type = parse_geoi.getString("type");

                    if(type.equals("Point")) {
                        point_lat.add(coor.getDouble(1));
                        point_long.add(coor.getDouble(0));
                    }

                    if(type.equals("LineString")){
                        for(int j = 0; j<coor.length();j++) {
                            JSONArray line_coor = coor.getJSONArray(j);
                            line_lat.add(line_coor.getDouble(1));
                            line_long.add(line_coor.getDouble(0));
                        }
                    }
                }
                //new Draw_map(point_lat,point_long,line_lat,line_long);

            } catch (JSONException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
            int i =Math.round(Integer.parseInt(parse_type)/60);
            all=i;
            int totalTime2 = totalTime+all;
            int totalDistance = Integer.parseInt(parse_type2)*100;
            double totalcalro=Math.round((3.3 * (3.5 * 60 * i)/1000)*5);

            p_info2="도보 "+i+"분";

            total_result(totalTime2,totalDistance,totalcalro, endName);

        }
    }

}
