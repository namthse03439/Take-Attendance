package com.example.sonata.takeattendanceversion10testcamerafunction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sonata.takeattendanceversion10testcamerafunction.AttendanceInformation.AttendanceInfo;
import com.example.sonata.takeattendanceversion10testcamerafunction.ScheduleManager.ScheduleManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private static Date start_date;
    private static Date end_date;

    private static boolean initDate = false;

    TextView mStartDateView = null;
    TextView mEndDateView = null;

    static final int START_DATE_DIALOG_ID = 999;
    static final int END_DATE_DIALOG_ID = 1000;

    private TableLayout[] tls = new TableLayout[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initDatePicker();
        initDisplayTable();

        addListenerOnFromButton();
        addListenerOnToButton();
        addListenerOnShowHistoryButton();
    }

    private void initDisplayTable()
    {
        tls[0] = (TableLayout) findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) findViewById(R.id.tableLayout2);
        tls[2] = (TableLayout) findViewById(R.id.tableLayout3);
        tls[3] = (TableLayout) findViewById(R.id.tableLayout4);
    }

    private void setCurrentDateOnView(Date date, int viewCode)
    {
        String dateView = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date);

        if (viewCode == R.id.text_startDate)
        {
            mStartDateView.setText(dateView);
        }
        else if (viewCode == R.id.text_endDate)
        {
            mEndDateView.setText(dateView);
        }
    }

    private void initDatePicker()
    {
        if (initDate == false)
        {
            final Calendar calendar = Calendar.getInstance();
            start_date = calendar.getTime();
            end_date = calendar.getTime();
        }

        mStartDateView = (TextView) findViewById(R.id.text_startDate);
        setCurrentDateOnView(start_date,R.id.text_startDate);

        mEndDateView = (TextView) findViewById(R.id.text_endDate);
        setCurrentDateOnView(end_date, R.id.text_endDate);
    }

    private void initAttributeValue()
    {

    }

    @Override
    protected Dialog onCreateDialog(int datePickerId) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        switch (datePickerId) {
            case START_DATE_DIALOG_ID:
                // set start date picker as current date
                return new DatePickerDialog(this, startDatePickerListener, year, month, day);
            case END_DATE_DIALOG_ID:
                // set end date picker as current date
                return new DatePickerDialog(this, endDatePickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {

            if (selectedYear > end_date.getYear() + 1900)
                return;

            if (selectedYear == end_date.getYear() + 1900 && selectedMonth > end_date.getMonth())
                return;

            if (selectedYear == end_date.getYear() + 1900 && selectedMonth == end_date.getMonth() && selectedDay > end_date.getDate())
                return;

            String dateView = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            end_date.setYear(selectedYear-1900);
            end_date.setMonth(selectedMonth);
            end_date.setDate(selectedDay);

            // set selected date into textview
            mStartDateView.setText(dateView);
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            if (selectedYear < start_date.getYear() + 1900)
                return;

            if (selectedYear == start_date.getYear() + 1900 && selectedMonth < start_date.getMonth())
                return;

            if (selectedYear == start_date.getYear() + 1900 && selectedMonth == start_date.getMonth() && selectedDay < start_date.getDate())
                return;

            String dateView = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            start_date.setYear(selectedYear-1900);
            start_date.setMonth(selectedMonth);
            start_date.setDate(selectedDay);

            // set selected date into textview
            mEndDateView.setText(dateView);
        }
    };

    private void addListenerOnFromButton()
    {
        Button btnFrom = (Button) findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DATE_DIALOG_ID);
            }
        });
    }

    private void addListenerOnToButton()
    {
        Button btnTo = (Button) findViewById(R.id.btn_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(END_DATE_DIALOG_ID);
            }
        });
    }

    private void addListenerOnShowHistoryButton()
    {
        Button btnReport = (Button) findViewById(R.id.btn_report);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayReport();
            }
        });
    }

    private int getPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private  void displayReport() {
        int testNum = 13;
//        AttendanceInfo[] attendanceInfo = new AttendanceInfo[testNum];

        final Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();


        TableRow[] trs = new TableRow[4];
        TextView[] tvs = new TextView[4];
        int statusIndex = 3;

        String dateView;
        String subjectCode;
        String duration;
        String studyStatus = null;

        for(int i = 0; i < testNum; i++)
        {
            //            AttendanceInfo info = attendanceInfo[i];

            List<String> values = new ArrayList<>();
            dateView = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(date);
            subjectCode = new String("MAC101");
            duration = new String("08:30 - 09:45");
            if (i % 3 == 0)
            {
                studyStatus = new String("ABSENT");
            }
            else if (i % 3 == 1)
            {
                studyStatus = new String("PRESENT");
            }
            else if (i % 3 == 2)
            {
                studyStatus = new String("NOT YET");
            }
            else
            {
                studyStatus = new String("NOT YET");
            }

            values.add(dateView);
            values.add(subjectCode);
            values.add(duration);
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

                if (k == 3)
                {
                    tvs[k].setPadding(getPx(2), getPx(0), getPx(0), getPx(0));
                    tvs[k].setGravity(Gravity.LEFT);
                }
                else
                {
                    tvs[k].setGravity(Gravity.CENTER);
                }

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
                        Intent intent = new Intent(ScheduleActivity.this, DetailedSubjectActivity.class);
                        startActivity(intent);
                    }
                });

            }
        }
    }
}
