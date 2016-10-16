package com.example.ilhamsuaib.vertical_stepper_form;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class NewAlarmFormActivity extends AppCompatActivity implements VerticalStepperForm {

    public static final String NEW_ALRM_ADDED = "new_alarm_added";

    private static final int DAYS_STEP_NUM = 3;

    //title step
    private EditText edtTitle;
    private static final int MIN_CHAR_TITLE = 3;
    public static final String STATE_TITLE = "title";

    //description step
    private EditText edtDescription;
    public static final String STATE_DESCRIPTION = "description";

    //time step
    private TextView txtTimeView;
    private Pair<Integer, Integer> time;
    private TimePickerDialog timePickerDialog;
    public static final String STATE_TIME_HOUR = "state_time_hour";
    public static final String STATE_TIME_MINUTE = "state_time_minute";

    //Week days step
    private boolean[] weekDays;
    private LinearLayout daysStepContainerLayout;
    public static final String STATE_WEEK_DAYS = "week_days";

    private VerticalStepperFormLayout verticalStepperForm;
    private ProgressDialog progressDialog;
    private boolean confirmBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm_form);

        initializeActivity();
    }

    private void initializeActivity(){
        //time step vars
        time = new Pair<>(8, 30);
        setTimePicker(8, 30);

        //Week days step vars
        weekDays = new boolean[7];

        //vertical stepper form vars
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        String[] stepsTitles = getResources().getStringArray(R.array.steps_titles);

        //find and initialize the form
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsTitles, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .init();
    }

    private void setTimePicker(int hour, int minute){
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay, minute);
            }
        }, hour, minute, true);
    }

    private void setTime(int hour, int minute){
        time = new Pair<>(hour, minute);
        String hourSetting = ((time.first > 9) ? String.valueOf(time.first) : ("0" + time.first));
        String minuteString = ((time.second > 9) ? String.valueOf(time.first) : ("0" + time.second));
        String time = hourSetting+":"+minuteString;
        txtTimeView.setText(time);
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber){
            case 0:
                view = createAlarmTitleStep();
                break;
            case 1:
                view = createAlarmDescriptionStep();
                break;
            case 2:
                view = createAlarmTimeStep();
                break;
            case 3:
                view = createAlarmDayStep();
                break;
        }
        return view;
    }

    //create view for vertical stepper form
    private View createAlarmTitleStep(){
        edtTitle = new EditText(this);
        edtTitle.setHint(R.string.form_hint_title);
        edtTitle.setSingleLine(true);
        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkTitleStep(v.toString())){
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return edtTitle;
    }
    private View createAlarmDescriptionStep(){
        edtDescription = new EditText(this);
        edtDescription.setHint(R.string.form_hint_description);
        edtDescription.setSingleLine(true);
        edtDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return edtDescription;
    }
    private View createAlarmTimeStep(){
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout timeStepContent = (LinearLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        txtTimeView = (TextView) timeStepContent.findViewById(R.id.txtTime);
        txtTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        return timeStepContent;
    }
    private View createAlarmDayStep(){
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        daysStepContainerLayout = (LinearLayout) inflater.inflate(R.layout.step_days_of_week_layout, null, true);
        String[] weekDays = getResources().getStringArray(R.array.week_days);
        for (int i = 0;i<weekDays.length;i++){
            final int index = i;
            final LinearLayout dayLayout = getDayLayout(index);
            if (index < 5) activeDay(index, dayLayout, false);
            else deactiveDay(index, dayLayout, false);

            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((boolean)v.getTag()) deactiveDay(index, dayLayout, true);
                    else activeDay(index, dayLayout, true);
                }
            });
            final TextView dayText = (TextView) dayLayout.findViewById(R.id.txtDay);
            dayText.setText(weekDays[index]);
        }
        return daysStepContainerLayout;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber){
            case 0 :
                checkTitleStep(edtTitle.getText().toString());
                break;
            case 1 :
            case 2 :
                verticalStepperForm.setStepAsCompleted(stepNumber);
                break;
            case 3 :
                checkDays();
                break;
        }
    }

    @Override
    public void sendData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));
        executeDataSending();
    }

    private void executeDataSending(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(NEW_ALRM_ADDED, true);
                    intent.putExtra(STATE_TITLE, edtTitle.getText().toString());
                    intent.putExtra(STATE_DESCRIPTION, edtDescription.getText().toString());
                    intent.putExtra(STATE_TIME_HOUR, time.first);
                    intent.putExtra(STATE_TIME_MINUTE, time.second);
                    intent.putExtra(STATE_WEEK_DAYS, weekDays);
                    confirmBack = false;
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean checkTitleStep(String title){
        boolean titleIsCorrect = false;
        if (title.length() >= MIN_CHAR_TITLE){
            titleIsCorrect = true;
            verticalStepperForm.setActiveStepAsCompleted();
        }else{
            String titleErrorString = getResources().getString(R.string.error_title_min_characters);
            String titleError = String.format(titleErrorString, MIN_CHAR_TITLE);
            verticalStepperForm.setActiveStepAsUncompleted(titleError);
        }
        return  titleIsCorrect;
    }

    private LinearLayout getDayLayout(int i){
        int id = daysStepContainerLayout.getResources().getIdentifier("day_"+i, "id", getPackageName());
        return (LinearLayout) daysStepContainerLayout.findViewById(id);
    }

    private void activeDay(int index, LinearLayout dayLayout, boolean check){
        weekDays[index] = true;
        dayLayout.setTag(true);

        Drawable bg = ContextCompat.getDrawable(getBaseContext(), com.example.ilhamsuaib.vertical_stepper_form.R.drawable.circle_step_done);
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.txtDay);
        dayText.setTextColor(Color.rgb(255, 255, 255));
        if (check) checkDays();
    }

    private void deactiveDay(int index, LinearLayout dayLayout, boolean check){
        weekDays[index] = false;
        dayLayout.setTag(false);
        dayLayout.setBackgroundResource(0);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.txtDay);
        int colour = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        dayText.setTextColor(colour);

        if (check) checkDays();
    }

    private boolean checkDays(){
        boolean thereIsAtLeastOneDaySelected = false;
        for (int i = 0;i<weekDays.length && !thereIsAtLeastOneDaySelected;i++){
            if (weekDays[i]){
                verticalStepperForm.setStepAsCompleted(DAYS_STEP_NUM);
                thereIsAtLeastOneDaySelected = true;
            }
            if (!thereIsAtLeastOneDaySelected){
                verticalStepperForm.setStepAsUncompleted(DAYS_STEP_NUM, null);
            }
        }
        return thereIsAtLeastOneDaySelected;
    }

    private void confirmBack(){
        if (confirmBack && verticalStepperForm.isAnyStepCompleted()){
            BackConfirmationFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = true;
                }
            });
            backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = false;
                    finish();
                }
            });
            backConfirmation.show(getSupportFragmentManager(), null);
        }else{
            confirmBack = false;
            finish();
        }
    }

    private void dismissDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && confirmBack){
            confirmBack();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        confirmBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (edtTitle != null) outState.putString(STATE_TITLE, edtTitle.getText().toString());
        if (edtDescription != null) outState.putString(STATE_DESCRIPTION, edtDescription.getText().toString());
        if (time != null){
            outState.putInt(STATE_TIME_HOUR, time.first);
            outState.putInt(STATE_TIME_MINUTE, time.second);
        }
        if (weekDays != null) outState.putBooleanArray(STATE_WEEK_DAYS, weekDays);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_TITLE)) edtTitle.setText(savedInstanceState.getString(STATE_TITLE));
        if (savedInstanceState.containsKey(STATE_DESCRIPTION)) edtDescription.setText(savedInstanceState.getString(STATE_DESCRIPTION));
        if(savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTE)) {
            int hour = savedInstanceState.getInt(STATE_TIME_HOUR);
            int minutes = savedInstanceState.getInt(STATE_TIME_MINUTE);
            time = new Pair<>(hour, minutes);
            setTime(hour, minutes);
            if(timePickerDialog == null) {
                setTimePicker(hour, minutes);
            } else {
                timePickerDialog.updateTime(hour, minutes);
            }
        }

        // Restoration of week days field
        if(savedInstanceState.containsKey(STATE_WEEK_DAYS)) {
            weekDays = savedInstanceState.getBooleanArray(STATE_WEEK_DAYS);
            if (weekDays != null) {
                for (int i = 0; i < weekDays.length; i++) {
                    LinearLayout dayLayout = getDayLayout(i);
                    if (weekDays[i]) {
                        activeDay(i, dayLayout, false);
                    } else {
                        deactiveDay(i, dayLayout, false);
                    }
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
