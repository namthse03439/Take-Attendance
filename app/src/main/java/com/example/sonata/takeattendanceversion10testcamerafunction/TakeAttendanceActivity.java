package com.example.sonata.takeattendanceversion10testcamerafunction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Calendar calendar;
    private TableLayout[] tls = new TableLayout[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MM_dd_yyyy");
        String stringTime = simpleDateFormat.format(calendar.getTime());

        TextView textView = (TextView) findViewById(R.id.text_Time);
        textView.setText(stringTime);

        initDisplayTable();

        Button btnShowHistory = (Button) findViewById(R.id.btn_showHistory);
        btnShowHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeAttendanceActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        displayReport();
    }

    private void initDisplayTable()
    {
        tls[0] = (TableLayout) findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) findViewById(R.id.tableLayout2);
        tls[2] = (TableLayout) findViewById(R.id.tableLayout3);
        tls[3] = (TableLayout) findViewById(R.id.tableLayout4);
    }

    private int getPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private  void displayReport() {
        int testNum = 6;
//        AttendanceInfo[] attendanceInfo = new AttendanceInfo[testNum];


        TableRow[] trs = new TableRow[4];
        TextView[] tvs = new TextView[4];
        int statusIndex = 3;

        String[] time = new String[6];
        time[0] = new String("07:30");
        time[1] = new String("09:10");
        time[2] = new String("10:50");
        time[3] = new String("12:50");
        time[4] = new String("14:30");
        time[5] = new String("16:10");

        String[] subjectCode = new String[6];
        subjectCode[0] = new String("MAC101");
        subjectCode[1] = new String("MAC101");
        subjectCode[2] = new String("MLN301");
        subjectCode[3] = new String("PHY101");
        subjectCode[4] = new String("CGG201");
        subjectCode[5] = new String("ETL302");

        String location = new String("Room 202, 2nd Floor, Beta Building");
        String studyStatus = null;

        for(int i = 0; i < testNum; i++)
        {
            //            AttendanceInfo info = attendanceInfo[i];

            List<String> values = new ArrayList<>();

            if (i < 2)
            {
                studyStatus = new String("ABSENT");
            }
            else if (i < 4)
            {
                studyStatus = new String("PRESENT");
            }
            else if (i < 6)
            {
                studyStatus = new String("NOT YET");
            }
            else
            {
                studyStatus = new String("NOT YET");
            }

            values.add(time[i]);
            values.add(subjectCode[i]);
            values.add(location);
            values.add(studyStatus);

            for (int k = 0; k < 4; k++) {
                tvs[k] = new TextView(this);
                tvs[k].setText(values.get(k));
                tvs[k].setTextColor(Color.BLACK);
                if (i % 2 == 0)
                {
                    tvs[k].setBackgroundColor(Color.parseColor("#bbdefb"));
                }
                else
                {
                    tvs[k].setBackgroundColor(Color.WHITE);
                }

                if (k == 2 || k == 3)
                {
                    tvs[k].setPadding(getPx(2), getPx(0), getPx(0), getPx(0));
                    tvs[k].setGravity(Gravity.LEFT);
                }
                else
                {
                    tvs[k].setGravity(Gravity.CENTER);
                }

                tvs[k].setLines(2);

                trs[k] = new TableRow(this);

                if (k == statusIndex)
                {
                    if (values.get(statusIndex).equals("ABSENT"))
                    {
                        tvs[k].setTextColor(Color.RED);
                    }
                    else if (values.get(statusIndex).equals("PRESENT"))
                    {
                        tvs[k].setTextColor(Color.parseColor("#339900"));
                    }
                    else if (values.get(statusIndex).equals("NOT YET"))
                    {
                        tvs[k].setTextColor(Color.BLACK);
                    }
                }

                trs[k].setBackgroundColor(Color.WHITE);
                trs[k].addView(tvs[k]);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                tvs[k].setLayoutParams(params);

                tls[k].addView(trs[k]);

                tvs[k].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TakeAttendanceActivity.this, DetailedInformationActivity.class);
                        startActivity(intent);
                    }
                });

            }
        }
    }
    private  void display()
    {

    }

    private void requestDailyData()
    {
        display();
    }


}
