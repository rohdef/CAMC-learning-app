package dk.au.cs.listr.camclearner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohdef on 12/8/13.
 */
public class WindowFilledEvent {
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
        stringBuilder.append("[");

        for (CamcSensorListener camcSensorListener : camcSensorListenerList) {
            float[] lastReading = camcSensorListener.getLastReading();
            logger.debug("lastReading == null: " + (lastReading==null));
            for (float f : lastReading) {
                stringBuilder.append(f + ", ");
            }
        }

        stringBuilder.append("]");

        logger.debug(currentLogginType + " data: " + stringBuilder.toString());
    }

    private WindowFilledEvent() {}
}
