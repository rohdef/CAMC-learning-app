package dk.au.cs.listr.camclearner;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {
    private final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private static Button lyingDownButton;
    private static Button sitButton;
    private static Button walkButton;
    private static Button jumpButton;
    private static boolean isIdle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lyingDownButton = (Button) findViewById(R.id.lyingDownButton);
        lyingDownButton.setOnClickListener(this);

        sitButton = (Button) findViewById(R.id.sitButton);
        sitButton.setOnClickListener(this);

        walkButton = (Button) findViewById(R.id.walkButton);
        walkButton.setOnClickListener(this);

        jumpButton = (Button) findViewById(R.id.jumpButton);
        jumpButton.setOnClickListener(this);

        setButtonsEnabled(isIdle);
    }

    @Override
    public void onClick(View view) {
        isIdle = false;

        setButtonsEnabled(false);

        final int timeToMeasure;
        if (view == lyingDownButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
            WindowFilledEvent.getInstance().setCurrentLogginType("lying");
        } else if (view == sitButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
            WindowFilledEvent.getInstance().setCurrentLogginType("sitting");
        } else if (view == walkButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
            WindowFilledEvent.getInstance().setCurrentLogginType("walking");
        } else {
            timeToMeasure = 30000; // 30 sec
            WindowFilledEvent.getInstance().setCurrentLogginType("dancing");
        }

        final Handler myHandler = new Handler();

        final Ringtone notificationTone;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        Runnable updateUi = new Runnable() {
            private int count = 9;

            @Override
            public void run() {
                TextView statusText = (TextView) findViewById(R.id.countDownText);
                statusText.setText("" + count--);

                if (count >= 0) {
                    myHandler.postDelayed(this, 1000);
                } else {
                    final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    final CamcSensorListener camcSensorListenerAccel, camcSensorListenerRotation;


                    Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                    camcSensorListenerAccel = new CamcSensorListener();
                    camcSensorListenerAccel.id = "Acc";
                    sensorManager.registerListener(camcSensorListenerAccel, s, SensorManager.SENSOR_DELAY_NORMAL);

                    s = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    camcSensorListenerRotation = new CamcSensorListener();
                    camcSensorListenerRotation.id = "Rot";
                    sensorManager.registerListener(camcSensorListenerRotation, s, SensorManager.SENSOR_DELAY_NORMAL);

                    try {
                        notificationTone.play();
                    } catch (Exception e) {}

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sensorManager.unregisterListener(camcSensorListenerAccel);
                            sensorManager.unregisterListener(camcSensorListenerRotation);
                            setButtonsEnabled(true);
                            isIdle = true;
                            try {
                                notificationTone.play();
                            } catch (Exception e) {}
                        }
                    }, timeToMeasure);
                }
            }
        };

        myHandler.postDelayed(updateUi, 1000);
    }

    private void setButtonsEnabled(boolean enabled) {
        lyingDownButton.setEnabled(enabled);
        sitButton.setEnabled(enabled);
        walkButton.setEnabled(enabled);
        jumpButton.setEnabled(enabled);
    }
}