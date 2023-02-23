package com.SShop.ERikshaDriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class help_list extends AppCompatActivity {

    LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list);

        container=findViewById(R.id.container);

        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverHelp).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        final View view = View.inflate(getApplicationContext(), R.layout.single_help, null);

                        TextView txt=view.findViewById(R.id.helpText);
                        txt.setText(data.getValue(String.class));
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri uri= Uri.parse(data.getKey());
                                startActivity(new Intent(Intent.ACTION_VIEW,uri));
                            }
                        });


                        container.addView(view);

                    }
            }
        });
    }
}