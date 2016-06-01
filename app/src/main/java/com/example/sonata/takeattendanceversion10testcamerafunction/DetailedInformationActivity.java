package com.example.sonata.takeattendanceversion10testcamerafunction;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailedInformationActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private BeaconManager beaconManager;
    private Region region;
    Button mTakeAttendanceBtn;
    boolean remindDiscover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        mTakeAttendanceBtn = (Button) findViewById(R.id.btn_takeAttendance);
        mTakeAttendanceBtn.setBackgroundColor(Color.RED);
        beaconNotInRange();

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);

                    int txPower = beacon.getMeasuredPower();
                    double rssi = beacon.getRssi();
                    double distance = calculateDistance(txPower, rssi);

                    if (distance <= 2.0)
                    {
                        if (!remindDiscover)
                        {
                            remindDiscover = true;
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
                            builder2.setMessage("You are in right location!");
                            builder2.setCancelable(true);

                            builder2.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert12 = builder2.create();
                            alert12.show();
                        }

                        mTakeAttendanceBtn.setBackgroundColor(Color.GREEN);
                        addListenerOnTakeAttendanceButton();
                    }
                    else
                    {
                        remindDiscover = false;
                        mTakeAttendanceBtn.setBackgroundColor(Color.RED);
                        beaconNotInRange();
                    }
                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 58949, 29933);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 58949, 29933));
//                ProgressBar progressBar = new ProgressBar(DetailedInformationActivity.this);
//                ObjectAnimator animation =  ObjectAnimator.ofInt (progressBar, "progress", 0, 5000);
//
//                animation.setDuration(5000);
//                animation.setInterpolator (new DecelerateInterpolator());
//                animation.start ();
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {

            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });

        beaconManager.setForegroundScanPeriod(5000, 2000);
    }

    private void beaconNotInRange()
    {
        mTakeAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailedInformationActivity.this);
                builder1.setMessage("Please go to the location before try to take attendance!");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    private void addListenerOnTakeAttendanceButton()
    {
        Button btnReport = (Button) findViewById(R.id.btn_takeAttendance);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //=========================

        //TODO: send image to face++ for verifying

            //TODO: send request to local server to get personID
                //TODO: if not existed yet, create a person and get personID, send to local server

            // verify if this image (face) belongs to that personID
            HttpRequests httpRequests = FaceppService.httpRequests;
            String personID = "NULL"; //
            String faceID = get1FaceID(httpRequests, mCurrentPhotoPath);
            VerdictResult verdictResult = getVerification(httpRequests, personID, faceID);

            if(verdictResult.getVerdict() == true && verdictResult.getConfidence() >= FaceppService.verificationThreshold){
                //TODO: OK, attendance checked
            }
            else{
                //TODO: Notify failure, recheck attendance
            }

            //-------------------------
        }
    }

    //=============================

    private String get1FaceID(HttpRequests httpRequests, String imgURL){
        String faceID = null;
        try {

            File imgFile = new File(imgURL);
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");

        }
        catch(Exception e){
            System.out.print("Exception: cannot get faceID from image!");
        }
        return faceID;
    }

    private VerdictResult getVerification(HttpRequests httpRequests, String personID, String faceID) {
        VerdictResult verdictResult = new VerdictResult(false);

        PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(faceID);
        try{
            JSONObject result = httpRequests.recognitionVerify(postParameters);
            verdictResult = new VerdictResult(result);
        }
        catch(Exception e){
            System.out.print("Process interrupted!");
        }

        return(verdictResult);
    }

    //-----------------------------

}
