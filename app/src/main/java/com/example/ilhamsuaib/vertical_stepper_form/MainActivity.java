package com.example.ilhamsuaib.vertical_stepper_form;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    public static final int NEW_ALARM = 1;

    private static final String DATA_RECEIVED = "data_received";
    private static final String INFORMATION = "information";
    private static final String DISCLAIMER = "disclaimer";

    private FloatingActionButton fab;
    private TextView txtInformation, txtDisclaimer;
    private boolean dataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), NewAlarmFormActivity.class), NEW_ALARM);
            }
        });
        txtInformation = (TextView) findViewById(R.id.txtInformation);
        txtDisclaimer = (TextView) findViewById(R.id.txtDisclaimer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(DATA_RECEIVED, dataReceived);
        if (dataReceived){
            savedInstanceState.putString(INFORMATION, txtInformation.getText().toString());
            savedInstanceState.putString(DISCLAIMER, txtDisclaimer.getText().toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == NEW_ALARM && data != null){
            if (data.hasExtra(NewAlarmFormActivity.NEW_ALRM_ADDED)
                    && data.getExtras().getBoolean(NewAlarmFormActivity.NEW_ALRM_ADDED, false)){
                //handling the data received from stepper form
                dataReceived = true;
                String tile = data.getExtras().getString(NewAlarmFormActivity.STATE_TITLE);
                int hour = data.getExtras().getInt(NewAlarmFormActivity.STATE_TIME_HOUR);
                int minute = data.getExtras().getInt(NewAlarmFormActivity.STATE_TIME_MINUTE);
                String time = ((hour> 9) ? minute : ("0" + hour)) + ":" + ((minute > 9) ? minute : ("0" + minute));
                txtInformation.setText("Alarm \""+tile+"\" set up at "+time);
                txtDisclaimer.setVisibility(View.VISIBLE);
                Snackbar.make(fab, getString(R.string.new_alarm_added), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
