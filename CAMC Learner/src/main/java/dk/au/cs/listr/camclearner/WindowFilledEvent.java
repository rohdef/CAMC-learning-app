package dk.au.cs.listr.camclearner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohdef on 12/8/13.
 */
public class WindowFilledEvent {
    private static final WindowFilledEvent instance = new WindowFilledEvent();
    private List<CamcSensorListener> camcSensorListenerList = new ArrayList<CamcSensorListener>();

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

    }

    private WindowFilledEvent() {}
}
