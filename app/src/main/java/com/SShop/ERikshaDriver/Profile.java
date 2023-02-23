package com.SShop.ERikshaDriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Calendar;

public class Profile extends AppCompatActivity {

    TextView ttlDistTraveled, nameV, mobNoV, getPaidV;

//    ImageView shrImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


//        shrImg = findViewById(R.id.shrImg);
        nameV = findViewById(R.id.name);
        mobNoV = findViewById(R.id.mobNo);
        getPaidV = findViewById(R.id.getPaid);

        SessionManager sessionManager = new SessionManager(this);

        nameV.setText(""+sessionManager.getKeyName());
        mobNoV.setText(""+sessionManager.getKeyMobile());

        ttlDistTraveled = findViewById(R.id.ttlDistTraveled);

        ttlDistTraveled.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                KeyCon.speakStr(getString(R.string.ttlDis) + ttlDistTraveled.getText().toString(), getApplicationContext());
                return false;
            }
        });


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        //this is for if the internet
        if (networkInfo != null && networkInfo.isConnected() == true) {

            //this is for taking total Travelled
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).orderByChild(KeyCon.TransCon.ERikshaID).equalTo(sessionManager.getKeyRikshaid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    int ttlDist = 0, ttlTravel = 0;
                    if (dataSnapshot.exists()) {
//                        Toast.makeText(getApplicationContext(),"ss",Toast.LENGTH_SHORT).show();

                        //getting time
                        Calendar calendar = Calendar.getInstance();
                        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child(KeyCon.TransCon.Status).getValue(String.class).equals(KeyCon.TransCon.Value.ValueTransport.TRAV_FINISHED) && data.child(KeyCon.TransCon.Date).getValue(String.class).equals(currentDate)) {
                                ttlDist += KeyCon.strToLocation(data.child(KeyCon.TransCon.StartLoc).getValue(String.class)).distanceTo(KeyCon.strToLocation(data.child(KeyCon.TransCon.DestLoc).getValue(String.class)));
                                Toast.makeText(getApplicationContext(), String.valueOf(data.child(KeyCon.TransCon.StartLoc)), Toast.LENGTH_SHORT).show();
                                ttlTravel++;
                            }

                        }
                    }
                    if (ttlTravel == 0)
                        ttlDistTraveled.setText(ttlDist + "m");
                    else
                        ttlDistTraveled.setText(ttlDist / ttlTravel + "m");


                    double price = 0.0;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child(KeyCon.TransCon.NotPaid).getValue(String.class).equals(KeyCon.True)) {
                                price += Float.parseFloat(data.child(KeyCon.TransCon.Price).getValue(String.class)) * Integer.parseInt(data.child(KeyCon.TransCon.TtlPerson).getValue(String.class));
                            }
                        }

                    }
                    getPaidV.setText(price + "Rs");
                }
            });
        } else {
            ttlDistTraveled.setText("404 Error!");
        }


    }


    public void speak(View view) {
        KeyCon.speakTVLP((TextView) view, this);
    }
}
