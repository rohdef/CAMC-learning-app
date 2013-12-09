package dk.au.cs.listr.camclearner;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {
    private final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private Button lyingDownButton;
    private Button sitButton;
    private Button walkButton;
    private Button jumpButton;
    private boolean isIdle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
        }

        logger.debug("" + isIdle);

        lyingDownButton = (Button) findViewById(R.id.lyingDownButton);
        lyingDownButton.setOnClickListener(this);

        sitButton = (Button) findViewById(R.id.sitButton);
        sitButton.setOnClickListener(this);

        walkButton = (Button) findViewById(R.id.walkButton);
        walkButton.setOnClickListener(this);

        jumpButton = (Button) findViewById(R.id.jumpButton);
        jumpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        isIdle = false;
        logger.debug(""+isIdle);

        setButtonsEnabled(false);

        int timeToMeasure = 0;
        if (view == lyingDownButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
        } else if (view == sitButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
        } else if (view == walkButton) {
            timeToMeasure = 30000; // 300 secs = 5 min
        } else {
            timeToMeasure = 30000; // 30 sec
        }

        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final CamcSensorListener camcSensorListenerAccel, camcSensorListenerRotation;

        Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        camcSensorListenerAccel = new CamcSensorListener();
        sensorManager.registerListener(camcSensorListenerAccel, s, SensorManager.SENSOR_DELAY_NORMAL);

        s = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        camcSensorListenerRotation = new CamcSensorListener();
        sensorManager.registerListener(camcSensorListenerRotation, s, SensorManager.SENSOR_DELAY_NORMAL);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sensorManager.unregisterListener(camcSensorListenerAccel);
                sensorManager.unregisterListener(camcSensorListenerRotation);
                setButtonsEnabled(true);
                isIdle = true;
            }
        }, timeToMeasure);
    }

    private void setButtonsEnabled(boolean enabled) {
        lyingDownButton.setEnabled(enabled);
        sitButton.setEnabled(enabled);
        walkButton.setEnabled(enabled);
        jumpButton.setEnabled(enabled);
    }
}