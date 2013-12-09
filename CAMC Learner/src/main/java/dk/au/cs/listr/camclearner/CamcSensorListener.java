package dk.au.cs.listr.camclearner;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * Created by rohdef on 11/30/13.
 */
public class CamcSensorListener implements SensorEventListener {
    private final Logger logger = LoggerFactory.getLogger(CamcSensorListener.class);
    private final WindowFilledEvent windowFilledEvent;
    private static final int WINDOW_SIZE = 128;
    // To save calculations
    private final int HALF_WINDOW;
    private final Queue<float[]> slidingWindow;
    private float[] lastReading;
    private int windowFilledCount = 0;

    public CamcSensorListener() {
        HALF_WINDOW = Math.round(WINDOW_SIZE/2);
        slidingWindow = new ArrayBlockingQueue<float[]>(WINDOW_SIZE);
        windowFilledEvent = WindowFilledEvent.getInstance();
        windowFilledEvent.registerCamcSensorListener(this);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        // According to Markus' talk we do not own the event data and should copy them.
        float[] data = new float[sensorEvent.values.length];
        for (int i=0; i<sensorEvent.values.length; i++) {
            data[i] = sensorEvent.values[i];
        }

        slidingWindow.offer(data);

        if (slidingWindow.size() == WINDOW_SIZE) {
            float[] dataSum = new float[sensorEvent.values.length];

            // We only remove haft for data overlap
            for (int i = 0; i<HALF_WINDOW; i++) {
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
                lastReading[i] = dataSum[i]/WINDOW_SIZE;
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (float f : lastReading) {
                stringBuilder.append(f + ", ");
            }

            stringBuilder.append("]");
            windowFilledCount++;

            logger.debug("Firering window filled event for the readings: " + stringBuilder.toString());
            windowFilledEvent.sensorEventFired();
        }
    }

    public float[] getLastReading() {
        return lastReading;
    }

    public int getWindowFilledCount() {
        return windowFilledCount;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
