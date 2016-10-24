package vladtylkovich.myapplication4;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerard on 2016-05-22.
 */
public class Directions {
    private static final String GOOGLE_DIRECTION_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyC-Kl7xnVkGriFmx2Tc5TI19XUdWS0_zH8";
    private String origin;
    private String destination;
    private MapsActivityInterface activityHandler;


    public Directions(MapsActivityInterface mai, String origin, String destination) {
        this.activityHandler = mai;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        // Clear MapsActivity interface from old routes
        activityHandler.clearPrevious();
        new DownloadDataTask().execute(createJsonUrl());
    }

    private String createJsonUrl() throws UnsupportedEncodingException {

        String orignParameter = URLEncoder.encode(origin, "utf-8");
        String destinationParameter = URLEncoder.encode(destination, "utf-8");
        return GOOGLE_DIRECTION_API_URL + "origin=" + orignParameter + "&destination=" + destinationParameter + "&key=" + GOOGLE_API_KEY;
    }

    // Independent thread from the main thread to download data; Takes more time and would bottleneck operations
    private class DownloadDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Catches any JSON error responses
        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        // create json object
        JSONObject jasonObject = new JSONObject(data);
        // retrieve routes array (it is a json array because it starts with a "["
        JSONArray jsonRouteArray = jasonObject.getJSONArray("routes");
        // parse each route
        // generally only 1 route but could be multiple if you pass in "alternatives=true"
        for (int i = 0; i < jsonRouteArray.length(); i++) {
            JSONObject jsonCurrentRoute = jsonRouteArray.getJSONObject(i);
            Route currentRoute = new Route();

            // line display we have to parse
            JSONObject overview_polylineJson = jsonCurrentRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonCurrentRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            currentRoute.setDistanceText(jsonDistance.getString("text"));
            currentRoute.setDistanceValue(jsonDistance.getInt("value"));
            currentRoute.setDurationText(jsonDuration.getString("text"));
            currentRoute.setDistanceValue(jsonDuration.getInt("value"));

            currentRoute.setEndAddress(jsonLeg.getString("end_address"));
            currentRoute.setStartAddress(jsonLeg.getString("start_address"));
            currentRoute.setStartLocation(new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng")));
            currentRoute.setEndLocation(new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")));
            currentRoute.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(currentRoute);
        }

        // Change MapsActivity interface to add new routes
        activityHandler.addNewRoutes(routes);

    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
