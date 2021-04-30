package dayoung.walkingpath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<PathData> path;//리스트 뷰를 통해 보여줄 사용자정의 데이터 배열

    public MyAdapter(Context context, ArrayList<PathData> data) {
        mContext = context;
        path = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return path.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public PathData getItem(int position) {
        return path.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        //각 데이터 항목에 대하여 ListView에 표현하기 위한 뷰(View)를 생성하는 함수
        View view = mLayoutInflater.inflate(R.layout.list_path, null);

        TextView r_time = (TextView)view.findViewById(R.id.r_time);
        TextView r_walk = (TextView)view.findViewById(R.id.r_walk);
        TextView r_cal = (TextView)view.findViewById(R.id.r_cal);
        TextView r_path = (TextView)view.findViewById(R.id.r_path);
        TextView r_road = (TextView)view.findViewById(R.id.r_road);

        //ImageView imageView = (ImageView)view.findViewById(R.id.arr);

        //imageView.setImageResource(sample.get(position).getPoster());
        r_walk.setText(path.get(position).getWalk());
        r_time.setText(path.get(position).getTime());
        r_cal.setText(path.get(position).getCal());
        r_path.setText(path.get(position).getPath());
        r_road.setText(path.get(position).getRoad());

        return view;
    }
}