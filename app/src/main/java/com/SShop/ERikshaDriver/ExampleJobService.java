package com.SShop.ERikshaDriver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Key;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private Boolean jobCancelled = false;

    Context context;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        Log.d(TAG, "Job Started");
//        doBackgr(jobParameters);
//        return true;


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            scheduleRefresh();

        jobFinished(jobParameters,false);


//        sendNotification("Hello");

        doBackgr(jobParameters);
        return true;
    }

    private void scheduleRefresh() {
        JobScheduler mjobsc= (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mjobBa=new JobInfo.Builder(123,new ComponentName(getPackageName(), String.valueOf(ExampleJobService.class)));

        
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            mjobBa.setMinimumLatency(60*1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }

//       int res=mjobsc.schedule(mjobBa);
//        if (res==)


    }

    private void doBackgr(JobParameters jobParameters) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                for (int i = 0; i < 10; i++) {
//                    Log.d(TAG, "run:" + i);
//                    if (jobCancelled) {
//                        return;
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }


                if (validateInternet()) {
                    SessionManager sessionManager = new SessionManager(getApplicationContext());

                    Log.d(TAG, "Fetching dt");


                    FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).orderByChild(KeyCon.TransCon.Status).equalTo(KeyCon.TransCon.Value.ValueTransport.TRAV_Initiated).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "listing orderss");

                            String lstOrd = "";
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                lstOrd = d.getKey();
                            }

                            Log.d(TAG, lstOrd + "::" + sessionManager.getLstOrd());

                            if (sessionManager.getLstOrd().equals(lstOrd)) {
                            } else {

                                Log.d(TAG, "New Order found");

                                String usrId=dataSnapshot.child(lstOrd).child(KeyCon.TransCon.OnlineUsrId).getValue(String.class);
                                String loc=dataSnapshot.child(lstOrd).child(KeyCon.TransCon.StartLoc).getValue(String.class);
                                sendNotification(usrId+" id:loc "+sessionManager.getNearLoctionName(KeyCon.strToLocation(loc)));
                                sessionManager.setKEY_LstOrd(lstOrd);
                            }
                        }
                    });

                }
//            }
//        }).start();
    }

    private void sendNotification(String data) {

        NotificationManager notifi = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notify = new Notification.Builder(getApplicationContext()).setContentTitle("New Order").setContentText(data)
                .setSmallIcon(R.drawable.e_riksha).build();

//        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notifi.notify(0, notify);


        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);


        Log.d(TAG, "Notifi");
        //to ringtone
        Ringtone ringtone = null;

        if (ringtone == null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        }

        ringtone.play();

        try {
            Thread.sleep(3000);
            ringtone.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
//        Log.d(TAG, "Job Cancelled before complition");
//        jobCancelled = true;
//        return true;

        return false;


    }


    private boolean validateInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
//            Toast.makeText(this,"internet is going on",Toast.LENGTH_SHORT).show();

            return true;
        } else {

            return false;
        }
    }

}

//this job is started from startActivity page and
//stop on HomeActivity when user will close the service
