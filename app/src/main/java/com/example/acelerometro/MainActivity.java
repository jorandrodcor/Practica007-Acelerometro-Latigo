package com.example.acelerometro;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.Session;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView ValorX, ValorY, ValorZ, LogText;
    private float X, Y, Z;
    private ScrollView scrollview;
    private SensorManager Sensores;
    private Sensor SensorAce;
    private CheckBox Latigo;
    private SoundPool sPool;
    private int sonido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogText = findViewById(R.id.textView1);
        ValorX = findViewById(R.id.textView3);
        ValorY = findViewById(R.id.textView5);
        ValorZ = findViewById(R.id.textView7);
        scrollview = findViewById(R.id.scrollview);
        Latigo = findViewById(R.id.checkBox);
        ValorX.setText("0");
        ValorY.setText("0");
        ValorZ.setText("0");
        X=0;Y=0;Z=0;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(10)
                    .build();
        }
        else{
            sPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        sonido = sPool.load(this, R.raw.latigazo,1);
        Sensores = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> listSensores = Sensores.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor:listSensores){
            log("Sensor: "+ sensor.getName().toString());
        }
        SensorAce = Sensores    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensores.registerListener(this,SensorAce,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void log (String s){
        LogText.append("\n" + s);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor   = sensorEvent.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            try{
                float Xa = sensorEvent.values[0];
                float Ya = sensorEvent.values[0];
                float Za = sensorEvent.values[0];
                // Dependiendo de los valor de cada uno de los ejes se tiene mayor nivel de sensibilidad
                if(Math.abs(Xa-X)>=10 || Math.abs(Ya-Y)>=10 || Math.abs(Za-Z)>=20){
                //if(Math.abs(Xa-X)>=1 || Math.abs(Ya-Y)>=1 || Math.abs(Za-Z)>=1){
                    ValorX.setText(String.valueOf(sensorEvent.values[0]));
                    ValorY.setText(String.valueOf(sensorEvent.values[1]));
                    ValorZ.setText(String.valueOf(sensorEvent.values[2]));
                    if(Latigo.isChecked()){sPool.play(sonido,1,1,1,0,1);}
                }
                X =sensorEvent.values[0];
                Y =sensorEvent.values[0];
                Z =sensorEvent.values[0];
            } catch (Exception ex){}
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}