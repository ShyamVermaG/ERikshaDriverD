package com.SShop.ERikshaDriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    public void gotoApp(View view) {
        Uri uri= Uri.parse("https://play.google.com/store/apps/details?id=com.SShop.ERikshaDriver");
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
//        closeDrawer();
    }

}