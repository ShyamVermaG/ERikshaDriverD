package com.SShop.ERikshaDriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class LoginFront extends AppCompatActivity {

    TextToSpeech tts;
    //for textView on longPress it activated
    private void speakTVLP(TextView view){

        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int res=0;
                    //setting up text language
                    SessionManager sessionManager=new SessionManager(getApplicationContext());
                    if(sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)){
                        res=tts.setLanguage(Locale.ENGLISH);
                    }else{
                        res=tts.setLanguage(Locale.forLanguageTag("hin"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if(res==TextToSpeech.LANG_MISSING_DATA|| res==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getApplicationContext(),"Language Not supp",Toast.LENGTH_SHORT).show();
                    }else{
                        tts.setPitch(0.2f);
                        tts.setSpeechRate(0.6f);
                        tts.speak(view.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tts.stop();
//        tts.shutdown();
    }

    LinearLayout loginBtn,signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_front);

        loginBtn=findViewById(R.id.loginBtn);
        signupBtn=findViewById(R.id.signUpBtn);
        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LogIn.class);
                startActivity(intent);
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });

    }


}