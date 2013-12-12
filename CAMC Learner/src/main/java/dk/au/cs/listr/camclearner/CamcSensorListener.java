package dk.au.cs.listr.camclearner;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * Created by rohdef on 11/30/13.
 */
public class CamcSensorListener implements SensorEventListener {
    private final Logger logger = LoggerFactory.getLogger(CamcSensorListener.class);
    private final WindowFilledEvent windowFilledEvent;
    // To save calculations
    private final Queue<float[]> slidingWindow;
    private float[] lastReading = null;
    private long timeFrame, timeDelay = 1000;

    public CamcSensorListener() {
        slidingWindow = new ArrayBlockingQueue<float[]>(4096);
        windowFilledEvent = WindowFilledEvent.getInstance();
        windowFilledEvent.registerCamcSensorListener(this);
        timeFrame = new Date().getTime() + (2*timeDelay);
    }

    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        // According to Markus' talk we do not own the event data and should copy them.
        float[] data = new float[sensorEvent.values.length];
        for (int i=0; i<sensorEvent.values.length; i++) {
            data[i] = sensorEvent.values[i];
        }

        slidingWindow.offer(data);

        long currentTime = new Date().getTime();
        if (timeFrame < currentTime) {
            timeFrame = currentTime+timeDelay;
            int currentCount = slidingWindow.size();
            int halfCurrentCount = Math.round(currentCount/2);
            float[] dataSum = new float[sensorEvent.values.length];

            // We only remove haft for data overlap
            for (int i = 0; i<halfCurrentCount; i++) {
                float[] f = slidingWindow.remove();

                for (int j = 0; j<f.length; j++) {
                    dataSum[j] += f[j];
                }
            }

            // Loop the remaining queue for the rest of the calculation. (Potential thread problem?)
            for (float[] f : slidingWindow) {
                for (int i=0; i<f.length; i++) {
                    dataSum[i] += f[i];
                }
            }

            lastReading = new float[dataSum.length];
            for (int i=0; i<dataSum.length; i++) {
                lastReading[i] = dataSum[i]/currentCount;
            }

            windowFilledEvent.sensorEventFired();
        }
    }

    public float[] getLastReading() {
        return lastReading;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
