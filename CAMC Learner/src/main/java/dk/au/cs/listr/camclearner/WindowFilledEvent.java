package dk.au.cs.listr.camclearner;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohdef on 12/8/13.
 */
public class WindowFilledEvent extends Activity {
    private final Logger logger = LoggerFactory.getLogger(WindowFilledEvent.class);
    private static final WindowFilledEvent instance = new WindowFilledEvent();
    private List<CamcSensorListener> camcSensorListenerList = new ArrayList<CamcSensorListener>();
    private boolean ready = false;
    private String currentLogginType = "none";

    public static WindowFilledEvent getInstance() {
        return  instance;
    }

    public void registerCamcSensorListener(CamcSensorListener camcSensorListener) {
        camcSensorListenerList.add(camcSensorListener);
    }

    public void removeCamcSensorListener(CamcSensorListener camcSensorListener) {
        camcSensorListenerList.remove(camcSensorListener);
    }

    public void reset() {
        ready = false;
        camcSensorListenerList.clear();
    }

    public void sensorEventFired() {
        // To ensure that all windows have been filled.
        if (!ready) {
            for (CamcSensorListener camcSensorListener : camcSensorListenerList) {
                if (camcSensorListener.getLastReading() == null)
                    return;
            }
            ready = true;
        }

        writeData();
    }

    public String getCurrentLogginType() {
        return currentLogginType;
    }

    public void setCurrentLogginType(String currentLogginType) {
        this.currentLogginType = currentLogginType;
    }

    private void writeData() {
        StringBuilder stringBuilder = new StringBuilder();

        for (CamcSensorListener camcSensorListener : camcSensorListenerList) {
            float[] lastReading = camcSensorListener.getLastReading();
            for (float f : lastReading) {
                stringBuilder.append(f + ", ");
            }
        }
        stringBuilder.append(currentLogginType);

        String filename = "camcWeka.data";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            logger.error("Something went wrong saving data", e);
        }

        logger.debug("data: " + stringBuilder.toString());
    }

    private WindowFilledEvent() {}
}
