package vladtylkovich.myapplication4;

import java.util.List;

/**
 * Created by gerard on 2016-05-22.
 */
public interface MapsActivityInterface {

    // Clear MapsActivity interface from old routes
    void clearPrevious();

    // Change MapsActivity interface to add new routes
    void addNewRoutes(List<Route> route);
}
