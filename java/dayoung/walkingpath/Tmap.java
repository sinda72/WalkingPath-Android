package dayoung.walkingpath;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.os.AsyncTask;


import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tmap extends AppCompatActivity {
    private TextView tv_outPut;
    Number sX, sY, eX, eY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);

        // 위젯에 대한 참조.
        tv_outPut = (TextView) findViewById(R.id.outPut);

        ContentValues values = new ContentValues();

        //좌표값 입력
        sX = 126.773045;
        sY = 37.666047;
        eX = 126.769526;
        eY = 37.660866;

        //앱키 주소 뒤에 입력
        //시작점과 도착점은 임의로 a와 b로 했음. 어차피 텍스트 값 받을테니..
        NetworkTask networkTask = new
                NetworkTask(" https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json&appKey=l7xx427832d1a5e9495f8f2bf034113f5816&startName=a&endName=b",
                values, sX, sY, eX, eY);
        networkTask.execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String url;
        //ContentValues values;

        NetworkTask(String url, ContentValues values, Number sX, Number sY, Number eX, Number eY) {
            //url뒤에 파라미터 추가
            url += ("&startX="+sX);
            url += ("&startY="+sY);
            url += ("&endX="+eX);
            url += ("&endY="+eY);

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
            String result;

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
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray("features");
                JSONObject parse_0 = jsonArray.getJSONObject(0);
                //좌표를 찾기 위한 geometry
                JSONObject parse_geo = parse_0.getJSONObject("properties");
                //pointindex, 거리 시간 등 properties
                //JSONObject parse_pro = parse_0.getJSONObject("properties");
                parse_type = parse_geo.getString("totalTime");

            } catch (JSONException e) {
                System.out.println("fail");
                e.printStackTrace();

            }
            //int to=Integer.parseInt(parse_type);
            tv_outPut.setText(result);
        }
    }
}
