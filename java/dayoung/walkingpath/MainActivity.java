package dayoung.walkingpath;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView My_Chart;
    private ImageView map1;

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private TextView step_conut = null;
    private TextView hour = null;
    private TextView cal = null;
    private TextView distance = null;
    private TextView set_goal;
    private TextView usergoal;
    private TextView ment;
    private FrameLayout goal;
    private FrameLayout gochart;

    public CircleView circleC;
    public int userInputSteps = 0;
    public AppDatabase db;
    private int step;
    String set_goal2="";
    float todayG=0;
    float totalG=0;

    public void mentt(){
        if(todayG/totalG>1){
            ment.setText("목표를 달성했습니다!");
        }else if(todayG/totalG>=0.5){
            ment.setText("조금 더 힘을 냅시다!");
        }else{
            ment.setText("활동이 많이 부족해요!");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleC = findViewById(R.id.circlechart);

        step_conut = findViewById(R.id.step_count);
        hour = findViewById(R.id.hour);
        cal = findViewById(R.id.kcal);
        distance = findViewById(R.id.distance);
        set_goal = findViewById(R.id.set_goal);
        usergoal = findViewById(R.id.usergoal);
        goal = findViewById(R.id.goal);
        gochart = findViewById(R.id.gochart);
        ment = findViewById(R.id.ment);


        db = Room.databaseBuilder(this, AppDatabase.class, "step-db")
                .allowMainThreadQueries()
                .build();
        db.stepDao().insert(new Step("5000"));

        String getin = db.stepDao().getdata();
        usergoal.setText(getin);



        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA)
                        .addDataType(DataType.TYPE_MOVE_MINUTES)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribe();
        }


        //My_Chart = (ImageView)findViewById(R.id.mychart);
        map1 = (ImageView)findViewById(R.id.map1);

        map1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PathActivity.class);
                startActivity(intent);
            }
        });

        gochart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra("goal", db.stepDao().getdata());
                startActivity(intent);
            }
        });

        goal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.stepDao().deleteall();
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("목표 걸음수를 입력하세요");

                final EditText input = new EditText(MainActivity.this);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.stepDao().deleteall();
                        db.stepDao().insert(new Step(input.getText().toString()));
                        //circleC.setgoal = Integer.parseInt(db.stepDao().getdata());
                        //circleC.setgoal = Integer.parseInt(db.stepDao().getdata());
                        set_goal2 = db.stepDao().getdata();
                        totalG=Float.parseFloat(set_goal2);
                        //Toast.makeText(getApplicationContext(),db.stepDao().getAll().toString(),Toast.LENGTH_LONG).show();
                        usergoal.setText(set_goal2);
                        circleC.setgoal = Integer.parseInt(set_goal2);
                        circleC.invalidate();
                        mentt();
                        //init(db.stepDao().getdata(), Integer.parseInt(String.valueOf(userInputSteps)));
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
        mentt();
    }

    public void init(String set_goal, int point){
        System.out.println("dddd " +set_goal);
        circleC.setgoal = Integer.parseInt(set_goal);
        circleC.Point = point;
        //circleC.invalidate();

        circleC.invalidate();
        System.out.println("init 실행");
        System.out.println("ddddddd "+ userInputSteps);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();

            }
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        //걸음수
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
        //활동시간
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_MOVE_MINUTES)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
        //소모칼로리
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_CALORIES_EXPENDED)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
        //활동 거리
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_DISTANCE_DELTA)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
        readData();
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        CircleView circleC = findViewById(R.id.circlechart);
        Log.i(TAG, "Successfully 진입");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                int userInputSteps = 0;
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for(Field field : dp.getDataType().getFields()) {
                                        Log.d("Test Name : ", dp.getOriginalDataSource().getStreamName());
                                        if(!"user_input".equals(dp.getOriginalDataSource().getStreamName())){
                                            int steps = dp.getValue(field).asInt();
                                            userInputSteps += steps;
                                        }
                                    }
                                }
                                step_conut.setText(String.valueOf(userInputSteps));
                                todayG=userInputSteps;
                                //kcal.setText(String.valueOf());
                                init(db.stepDao().getdata(), userInputSteps);
                                mentt();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_MOVE_MINUTES)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                int userInputhours = 0;
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for(Field field : dp.getDataType().getFields()) {
                                        Log.d("Test Name : ", dp.getOriginalDataSource().getStreamName());
                                        if(!"user_input".equals(dp.getOriginalDataSource().getStreamName())){
                                            int steps = dp.getValue(field).asInt();
                                            userInputhours += steps;
                                        }
                                    }
                                }
                                hour.setText(String.valueOf(userInputhours)+"분");
                                //kcal.setText(String.valueOf());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                float userInputcal = 0;
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for(Field field : dp.getDataType().getFields()) {
                                        Log.d("Test Name : ", dp.getOriginalDataSource().getStreamName());
                                        if(!"user_input".equals(dp.getOriginalDataSource().getStreamName())){
                                            float steps = dp.getValue(field).asFloat();
                                            userInputcal += steps;
                                        }
                                    }
                                }
                                cal.setText(String.valueOf((int) userInputcal));

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                float userInputdis = 0;
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for(Field field : dp.getDataType().getFields()) {
                                        Log.d("Test Name : ", dp.getOriginalDataSource().getStreamName());
                                        if(!"user_input".equals(dp.getOriginalDataSource().getStreamName())){
                                            float steps = dp.getValue(field).asFloat();
                                            userInputdis += steps;
                                        }
                                    }
                                }
                                userInputdis = (float)round(userInputdis)/1000;
                                //int digits = pow(10, 4);
                                //userInputdis = round(userInputdis*100)/100;

                                distance.setText(String.valueOf(userInputdis));

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
        mentt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        readData();
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
class CircleView extends View{
    public CircleView(Context context) { super(context);}

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);}

    float Point=0.0f;
    float setgoal=0.0f;
    public void onDraw(Canvas canvas) {

        paint = new Paint();
        int x = canvas.getWidth()/6;
        int y = canvas.getWidth()*5/6;

        final float START_POINT = -90f;
        final float ANGLE_PER_SCORE = 360.f/setgoal;
        float angle = Point*ANGLE_PER_SCORE;

        RectF rectF = new RectF(x,x-50,y,y-50);

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(y/9);
        paint.setAlpha(0x00);
        paint.setColor(Color.rgb(189,189,189));

        //회색 부분 설정
        canvas.drawArc(rectF, START_POINT , -360 + angle, false, paint);

        //강조되는 빨간 부분
        paint.setColor(Color.rgb(44,114,178));
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, START_POINT, angle, false, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);

        Paint t = new Paint();
        t.setColor(Color.BLACK);
        t.setTextSize(y / 7);

    }
    private Path path;
    private Paint paint;
}