package dayoung.walkingpath;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;


public class ChartActivity extends AppCompatActivity {
    public AppDatabase daydb;
    public AppDatabase dbgoal;
    public HorizontalBarChart barHor;
    public BarChart barChart;
    public BarChart bar;
    public ArrayList weekstep;
    public ArrayList dayofweek;
    public ArrayList realdayofweek;
    public TextView today;
    public TextView goal;
    public TextView percent;
    public TextView weekT;
    ArrayList<Float> steps = new ArrayList<Float>();
    List<BarEntry>list=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_chart);

        daydb = Room.databaseBuilder(this,AppDatabase.class,"day-db")
                .allowMainThreadQueries()
                .build();
        weekT = (TextView) findViewById(R.id.week);

        ViewWeekStepCountTask viewweek = new ViewWeekStepCountTask();
        viewweek.execute();

        dbgoal = Room.databaseBuilder(this,AppDatabase.class,"db-goal")
                .allowMainThreadQueries()
                .build();
        dbgoal.stepDao().insert(new Step("5000"));


        Intent intent = getIntent();
        String Value = intent.getStringExtra("goal");
        System.out.println("인텐트 "+ Value);

        if(dbgoal.stepDao().getdata() == null){
            if(Value == null) {
                dbgoal.stepDao().deleteall();
                dbgoal.stepDao().insert(new Step("5000"));
                System.out.println("널값, 5000넣기 " + dbgoal.stepDao().getAll());
                //dbgoal.stepDao().deleteall();
            }
        }
        else {
            if(Value == null){
                System.out.println("널값넣지 않고 계속 유지" + dbgoal.stepDao().getAll());
            }
            else {
                dbgoal.stepDao().deleteall();
                System.out.println("널이 아니어서 지우고 " + dbgoal.stepDao().getAll());
                dbgoal.stepDao().insert(new Step(Value));
                System.out.println("널이 아니어서 새로 넣음" + dbgoal.stepDao().getAll());

            }
        }

        weekstep = new ArrayList();
        dayofweek = new ArrayList();
        realdayofweek = new ArrayList();
        goal = (TextView) findViewById(R.id.goal);
        today = (TextView) findViewById(R.id.today);
        percent = (TextView) findViewById(R.id.percent);

        barHor = (HorizontalBarChart) findViewById(R.id.chart1);
        bar = (BarChart) findViewById(R.id.chart2);


    }



    //주간 데이터 불러오는 곳
    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),23,59,59);
            long endTime = cal.getTimeInMillis();

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-6,0,0,1);
            long startTime = cal.getTimeInMillis();

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();


            GoogleApiClient client = new GoogleApiClient.Builder(ChartActivity.this)
                    .addApi(Fitness.HISTORY_API).build();
            client.connect();

            DataReadResult dataReadResult=null;

            dataReadResult = Fitness.HistoryApi.readData(client, readRequest).await(30, TimeUnit.SECONDS);

            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            Log.e("History", "Data point:");
                            Log.e("History", "\tType: " + dp.getDataType().getName());
                            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                            for(Field field : dp.getDataType().getFields()) {
                                Log.e("History", "\tField: " + field.getName() +
                                        " Value: " + dp.getValue(field));
                                String stringstep = dp.getValue(field).toString();
                                Float floatstep = Float.parseFloat(stringstep);
                                daydb.stepDao().insert(new Step(floatstep.toString()));
                                System.out.println("디비디비딥 " + daydb.stepDao().getAll());
                                System.out.println("디비디비딥원데이터 " +dp.getValue(field).toString());
                            }
                        }
                    }
                }
            }
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    DateFormat timeFormat = DateFormat.getTimeInstance();
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        Log.e("History", "Data point:");
                        Log.e("History", "\tType: " + dp.getDataType().getName());
                        Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        for(Field field : dp.getDataType().getFields()) {
                            Log.e("History", "\tField: " + field.getName() +
                                    " Value: " + dp.getValue(field));
                            String stringstep = dp.getValue(field).toString();
                            Float floatstep = Float.parseFloat(stringstep);
                            daydb.stepDao().insert(new Step(floatstep.toString()));
                            System.out.println("디비디비딥 " + daydb.stepDao().getAll());
                            System.out.println("디비디비딥원데이터 " +dp.getValue(field).toString());
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 비동기 작업이 끝난 후 화면으로 알립니다.
            HorizontalBar();
            BarChart();
        }



    }

    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                String stringstep = dp.getValue(field).toString();
                Float floatstep = Float.parseFloat(stringstep);
                daydb.stepDao().insert(new Step(floatstep.toString()));
                System.out.println("디비디비딥 " + daydb.stepDao().getAll());
                System.out.println("디비디비딥원데이터 " +dp.getValue(field).toString());
            }
        }
    }

    public void HorizontalBar(){
        List<BarEntry>listh=new ArrayList<>();


        float todaystep = Float.parseFloat((daydb.stepDao().getAll()).get(6).toString());
        int test = Math.round(todaystep);
        String stepS = Integer.toString(test);
        float todaygoal = Float.parseFloat(String.valueOf(dbgoal.stepDao().getdata()));

        //int todaygoalint = Integer.parseInt(String.valueOf(dbgoal.stepDao().getdata()));//percent 계산
        //int per = (test/todaygoalint*100);
        float sum = 0;
        for (int i = 0; i < 7; i++){
            sum += Float.parseFloat((daydb.stepDao().getAll()).get(i).toString());
        }

        float week = sum/7;
        int weekI = Math.round(week);
        String weekS = Integer.toString(weekI);

        weekT.setText(weekS);

        System.out.println("걸은 수  "+ todaystep);
        System.out.println("목표 "+ todaygoal);
        System.out.println("나누면  "+ todaystep/todaygoal);

        int per = Math.round(todaystep/todaygoal*100);
        String percentS = String.valueOf(per);

        goal.setText(String.valueOf(dbgoal.stepDao().getdata()));
        today.setText(stepS);
        percent.setText(percentS + "%");

        System.out.println("목표 "+ todaygoal);
        barHor.getAxisRight().setAxisMaximum(todaygoal);

        listh.add(new BarEntry(1,todaystep));

        BarDataSet barDataSeth=new BarDataSet(listh,"");
        BarData barDatah=new BarData(barDataSeth);
        barDataSeth.setColors(Color.rgb(178,199,217));



        barHor.setData(barDatah);
        barDatah.setBarWidth(1.5f);
        barDatah.setValueTextSize(20);

        barHor.getDescription().setEnabled(false);
        barHor.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barHor.getAxisLeft().setEnabled(false);//
        barHor.getAxisLeft().setAxisMaximum(todaygoal);
        barHor.getAxisLeft().setAxisMinimum(0);
        barHor.getLegend().setEnabled(false);
        barHor.animateXY(700,700);

        //barHor.getAxisLeft().setAxisMaximum(Float.parseFloat(dbgoal.stepDao().getdata())+1000);

        XAxis xAxis = barHor.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.enableGridDashedLine(10, 3, 0);

        YAxis yLAxis = barHor.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = barHor.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);


    }

    public void BarChart(){

        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
        Date time = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        for(int i = 6; i >= 0; i--){
            cal.add(Calendar.DATE, -i);
            dayofweek.add(format1.format(cal.getTime()));
            cal.add(Calendar.DATE, +i);
        }

        for(int i = 0; i < 7; i++){

        }


        MakeList();


        BarDataSet barDataSet=new BarDataSet(list,"주별 걸음 수");
        BarData barData =new BarData(barDataSet);
        barData.setValueTextSize(20);

        barData.setBarWidth(0.5f);
        barDataSet.setColors(Color.rgb(178,199,217));
        bar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayofweek));
        bar.getLegend().setEnabled(false);

        bar.setData(barData);
        bar.getDescription().setEnabled(false);

        bar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        bar.getAxisRight().setEnabled(false);
        bar.animateY(700);

        bar.getXAxis().setDrawGridLines(false);

        //System.out.println("널현재 db일치,,?" + dbgoal.stepDao().getAll());
        float limit = Float.parseFloat((dbgoal.stepDao().getdata()));

        //if(limit )
        LimitLine limitLine = new LimitLine(limit,"goal");

        //limitLine.enableDashedLine(10f,10f,10f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextSize(20);
        limitLine.setLineColor(Color.rgb(0,49,148));
        bar.getAxisLeft().addLimitLine(limitLine);

        bar.getAxisLeft().setAxisMinimum(0);

        daydb.stepDao().deleteall();
        //dbgoal.stepDao().deleteall();

    }

    public void MakeList(){


        if(daydb.stepDao().getAll().size()>0) {
            for (int i = 0; i < 7; i++) {
                System.out.println("chchchchchc "+daydb.stepDao().getAll());
                weekstep.add(Float.parseFloat((daydb.stepDao().getAll()).get(i).toString()));
                float a = i;
                list.add(new BarEntry(a, Float.parseFloat((daydb.stepDao().getAll()).get(i).toString())));

            }
            System.out.println("wwwwwwwww " + Collections.max(weekstep).getClass().getName());
            float a = (float) Collections.max(weekstep);
            if(a < Float.parseFloat(dbgoal.stepDao().getdata())){
                MakeMaximum();
            }
        }

    }

    public void MakeMaximum(){
        bar.getAxisLeft().setAxisMaximum(Float.parseFloat(dbgoal.stepDao().getdata())+1000);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        return true;
    }

}