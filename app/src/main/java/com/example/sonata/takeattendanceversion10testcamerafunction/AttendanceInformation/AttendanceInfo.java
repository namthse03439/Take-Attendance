package com.example.sonata.takeattendanceversion10testcamerafunction.AttendanceInformation;

import android.media.Image;

/**
 * Created by Sonata on 5/17/2016.
 */
public class AttendanceInfo {

    enum EAttendanceStatus {
        NOT_YET,
        PRESENT,
        ABSENT,
        UNKNOWN
    };

    int Timetable_id;
    Image studentImage;
    EAttendanceStatus status;
}
