package vladtylkovich.myapplication4;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


/**
 * Created by gerard on 2016-05-22.
 */
public class Route {

    public String distanceText;
    public int distanceValue;
    public String durationText;
    public int durationValue;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public int getDurationValue() {
        return durationValue;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }


    public List<LatLng> points;
}
