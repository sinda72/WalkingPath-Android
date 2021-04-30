package dayoung.walkingpath;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    public int setNumber;
    private String set;
    private Button CheckNumber;
    private EditText Number;
    private String[] steps = {"1000", "2000", "3000", "4000", "5000", "6000","7000", "8000", "9000", "10000",
                                "11000", "12000", "13000", "14000", "150000", "16000", "17000", "18000", "19000", "20000"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_goal);



        NumberPicker np = findViewById(R.id.numberPicker);

        np.setMaxValue(19);
        np.setMinValue(0);

        np.setDisplayedValues(steps);
//        setNumber = np.getValue();



        Number = (EditText) findViewById(R.id.editText);
        CheckNumber = (Button) findViewById(R.id.check);

        np.setOnValueChangedListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });


        /*CheckNumber.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              Number.setText(set);
            }
        });
*/




    }
    OnValueChangeListener onValueChangeListener = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            int valuePicker = numberPicker.getValue();
            String set = steps[valuePicker];
            Toast toast = Toast.makeText(getApplicationContext(), set, Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
        @Override
        public String format(int i) {
            return null;
        }
    };

    @Override
    public void onClick(View v) {

    }
}
