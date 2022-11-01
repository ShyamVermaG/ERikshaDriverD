package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class BlockActivity extends AppCompatActivity {

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

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        text=findViewById(R.id.text);

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(text);
                return false;
            }
        });
        SessionManager sessionManager=new SessionManager(this);
        if(sessionManager.getKeyAppLng()==KeyCon.ERikshaDriver.Value.LNG_HIND){
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
        }else{
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
        }
    }


    private void changeLanguage(String langCode) {
        Resources resources=getResources();
        Configuration configuration=resources.getConfiguration();
//        Locale locale=new Locale(langCode);
//        Locale.setDefault(locale);
        configuration.locale=new Locale(langCode);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        onConfigurationChanged(configuration);
//        Toast.makeText(this, "lang change Init", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        text.setText(R.string.update);
//        Toast.makeText(this, "lang change", Toast.LENGTH_SHORT).show();

    }
}