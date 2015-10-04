package us.wayshine.apollo.myweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Apollo on 10/4/15.
 */
public class ForecastDataObject {

    JSONObject jObject;
    private String forecastData;
    private int[] weather = new int[5];
    private float[] temp_max = new float[5];
    private float[] temp_min = new float[5];

    public ForecastDataObject(String forecastData) {
        this.forecastData = forecastData;
        if(!forecastData.equals("")) {
            try {
                jObject = new JSONObject(forecastData);
                parseJSON();
            } catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }
    }

    private void parseJSON() {
        try {
            JSONArray list = jObject.optJSONArray("list");
            for(int i = 0; i < 5; i++) {
                weather[i] = list.optJSONObject(i).optJSONArray("weather").optJSONObject(0).optInt("id");
                Log.i("JSON Parser", weather[i] + "");
                temp_max[i] = (float)list.optJSONObject(i).optJSONObject("temp").optDouble("max");
                temp_min[i] = (float)list.optJSONObject(i).optJSONObject("temp").optDouble("min");
            }
        }
        catch(Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
    }

    public String getTempMax(int id) {
        return Integer.toString(Math.round((temp_max[id] - 273.15f))) + "°";
    }

    public String getTempMin(int id) {
        return Integer.toString(Math.round((temp_min[id] - 273.15f))) + "°";
    }

    public String getTempBoth(int id) {
        return Integer.toString(Math.round((temp_min[id] - 273.15f))) + "°~" +
                Integer.toString(Math.round((temp_max[id] - 273.15f))) + "°";
    }

    public Integer getWeatherID(int id) {return weather[id];}
}
