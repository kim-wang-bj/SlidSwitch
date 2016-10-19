package com.wq.android.lib.slidswitch.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wq.android.lib.slidswitch.SlidSwitch;
import com.wq.android.lib.slidswitch.SlidSwitch.OnSwitchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlidSwitch slidSwitch1 = (SlidSwitch) findViewById(R.id.slid_switch_1);
        SlidSwitch slidSwitch2 = (SlidSwitch) findViewById(R.id.slid_switch_2);
        SlidSwitch slidSwitch3 = (SlidSwitch) findViewById(R.id.slid_switch_3);
        SlidSwitch slidSwitch4 = (SlidSwitch) findViewById(R.id.slid_switch_4);

        OnSwitchListener onSwitchListener = new OnSwitchListener() {
            Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

            @Override
            public void onSwitch(int selectedIndex, String text) {
                toast.setText(text);
                toast.show();
            }
        };

        slidSwitch1.setOnSwitchListener(onSwitchListener);
        slidSwitch2.setOnSwitchListener(onSwitchListener);
        slidSwitch3.setOnSwitchListener(onSwitchListener);
        slidSwitch4.setOnSwitchListener(onSwitchListener);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            labels.add("Label" + i);
        }
        slidSwitch4.setLabels(labels);
        slidSwitch4.setSelectedIndex(4);
    }
}
