package us.wayshine.apollo.myweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Apollo on 9/26/15.
 */
public class DataObject {

    JSONObject jObject;
    private boolean existed = false;

    private String weatherData;
    private String city = "";
    private Integer cityID = 0;
    private String lon, lat;
    private Integer weatherID = 0;
    private String weather_main = "Clear";
    private Float temp = 273.15f, temp_min = 273.15f, temp_max = 273.15f;
    private Integer pressure = 1000, humidity = 0, clouds = 0, visibility = 10;
    private Integer wind_speed = 0, wind_deg = 0;
    private Integer sunrise, sunset, cTime;
    private Integer dayornight = 0;     //1:day -1:night 0 :default
    private Integer cover = R.drawable.beijing;
    private Locale locale;

    public DataObject(String weatherData) {
        this.weatherData = weatherData;
        if(!weatherData.equals("")) {
            try {
                jObject = new JSONObject(weatherData);
                parseJSON();
                existed = true;
            } catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }
    }

    private void parseJSON() {
        try {
            city = jObject.optString("name");
            cityID = jObject.optInt("id");
            JSONObject coord = jObject.optJSONObject("coord");
            JSONArray weather = jObject.optJSONArray("weather");
            JSONObject main = jObject.optJSONObject("main");
            JSONObject clouds = jObject.optJSONObject("clouds");
            JSONObject wind = jObject.optJSONObject("wind");
            JSONObject sys = jObject.optJSONObject("sys");
            weather_main = weather.optJSONObject(0).optString("main");
            weatherID = weather.optJSONObject(0).optInt("id");
            temp = (float)main.optDouble("temp");
            temp_min = (float)main.optDouble("temp_min");
            temp_max = (float)main.optDouble("temp_max");
            pressure = main.optInt("pressure");
            humidity = main.optInt("humidity");
            this.clouds = clouds.optInt("all");
            if(!jObject.optString("visibility").equals(""))visibility = jObject.optInt("visibility") / 1000;
            wind_speed = wind.optInt("wind_speed");
            wind_deg = wind.optInt("deg");
            cTime = jObject.optInt("dt");
            sunrise = sys.optInt("sunrise");
            sunset = sys.optInt("sunset");
            locale = new Locale(sys.optString("country"));

            lon = coord.optString("lon");
            lat = coord.optString("lat");

            if(cTime >= sunrise && cTime < sunset) {
                dayornight = 1;
            }
            else dayornight = -1;

            if(Float.parseFloat(lon) < 0) {
                lon = Math.abs(Math.round(Float.parseFloat(lon))) + "W";
            }
            else
                lon = Math.abs(Math.round(Float.parseFloat(lon))) + "E";

            if(Float.parseFloat(lat) < 0) {
                lat = Math.abs(Math.round(Float.parseFloat(lat))) + "S";
            }
            else
                lat = Math.abs(Math.round(Float.parseFloat(lat))) + "N";

            switch(city) {
                case "Beijing": cover = R.drawable.beijing; break;
                case "Tokyo": cover = R.drawable.tokyo; break;
                case "London": cover = R.drawable.london; break;
                case "New York": cover = R.drawable.newyork; break;
                case "Brussels": cover = R.drawable.brussels; break;
            }
        }
        catch(Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
    }

    public String getJSONString() {
        return weatherData;
    }

    public Boolean getExist() {return existed;}

    public String getCity() {return city;}

    public Integer getCityID() {return cityID;}

    public int getCover() {return cover;}

    public String getTemp() {return Integer.toString(Math.round(temp - 273.15f)) + "°";}

    public String getWeather() {return weather_main;}

    public String getLon() {return lon;}

    public String getLat() {return lat;}

    public Integer getTempMax() {return Math.round(temp_max - 273.15f);}

    public Integer getTempMin() {return Math.round(temp_min - 273.15f);}

    public Integer getPressure() {return pressure;}

    public Integer getClouds() {return clouds;}

    public Integer getHumidity() {return humidity;}

    public Integer getVisibility() {return visibility;}

    public String getTempMax(Float f) {
        return Integer.toString(Math.round((temp_max - 273.15f) * f)) + "°";
    }

    public String getTempMin(Float f) {
        return Integer.toString(Math.round((temp_min - 273.15f) * f)) + "°";
    }

    public String getPressure(Float f) {
        return Integer.toString(Math.round(pressure * f)) + "KPa";
    }

    public String getClouds(Float f) {
        return Integer.toString(Math.round(clouds * f)) + "%";
    }

    public String getHumidity(Float f) {
        return Integer.toString(Math.round(humidity * f)) + "%";
    }

    public String getVisibility(Float f) {
        return Integer.toString(Math.round(visibility * f)) + "KM";
    }

    public Integer getWindSpeed() {return wind_speed;}

    public Integer getWindDeg() {return wind_deg;}

    public String getSunrise() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(1000l * sunrise);
    }

    public String getSunset() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(1000l * sunset);
    }

    public static Integer getImage(int weatherID) {

        int i;
        switch(weatherID) {

            case 200: i = R.string.wi_owm_200;break;
            case 201: i = R.string.wi_owm_201;break;
            case 202: i = R.string.wi_owm_202;break;
            case 210: i = R.string.wi_owm_210;break;
            case 211: i = R.string.wi_owm_211;break;
            case 212: i = R.string.wi_owm_212;break;
            case 221: i = R.string.wi_owm_221;break;
            case 230: i = R.string.wi_owm_230;break;
            case 231: i = R.string.wi_owm_231;break;
            case 232: i = R.string.wi_owm_232;break;
            case 300: i = R.string.wi_owm_300;break;
            case 301: i = R.string.wi_owm_301;break;
            case 302: i = R.string.wi_owm_302;break;
            case 310: i = R.string.wi_owm_310;break;
            case 311: i = R.string.wi_owm_311;break;
            case 312: i = R.string.wi_owm_312;break;
            case 313: i = R.string.wi_owm_313;break;
            case 314: i = R.string.wi_owm_314;break;
            case 321: i = R.string.wi_owm_321;break;
            case 500: i = R.string.wi_owm_500;break;
            case 501: i = R.string.wi_owm_501;break;
            case 502: i = R.string.wi_owm_502;break;
            case 503: i = R.string.wi_owm_503;break;
            case 504: i = R.string.wi_owm_504;break;
            case 511: i = R.string.wi_owm_511;break;
            case 520: i = R.string.wi_owm_520;break;
            case 521: i = R.string.wi_owm_521;break;
            case 522: i = R.string.wi_owm_522;break;
            case 531: i = R.string.wi_owm_531;break;
            case 600: i = R.string.wi_owm_600;break;
            case 601: i = R.string.wi_owm_601;break;
            case 602: i = R.string.wi_owm_602;break;
            case 611: i = R.string.wi_owm_611;break;
            case 612: i = R.string.wi_owm_612;break;
            case 615: i = R.string.wi_owm_615;break;
            case 616: i = R.string.wi_owm_616;break;
            case 620: i = R.string.wi_owm_620;break;
            case 621: i = R.string.wi_owm_621;break;
            case 622: i = R.string.wi_owm_622;break;
            case 701: i = R.string.wi_owm_701;break;
            case 711: i = R.string.wi_owm_711;break;
            case 721: i = R.string.wi_owm_721;break;
            case 731: i = R.string.wi_owm_731;break;
            case 741: i = R.string.wi_owm_741;break;
            case 761: i = R.string.wi_owm_761;break;
            case 762: i = R.string.wi_owm_762;break;
            case 771: i = R.string.wi_owm_771;break;
            case 781: i = R.string.wi_owm_781;break;
            case 800: i = R.string.wi_owm_800;break;
            case 801: i = R.string.wi_owm_801;break;
            case 802: i = R.string.wi_owm_802;break;
            case 803: i = R.string.wi_owm_803;break;
            case 804: i = R.string.wi_owm_804;break;
            case 900: i = R.string.wi_owm_900;break;
            case 901: i = R.string.wi_owm_901;break;
            case 902: i = R.string.wi_owm_902;break;
            case 903: i = R.string.wi_owm_903;break;
            case 904: i = R.string.wi_owm_904;break;
            case 905: i = R.string.wi_owm_905;break;
            case 906: i = R.string.wi_owm_906;break;
            case 957: i = R.string.wi_owm_957;break;

            default: i = R.string.wi_cloud_refresh;break;

        }
        return i;
    }

    public Integer getAlterImage() {

        int i;
        if(dayornight == 1) {
            switch (weatherID) {

                case 200: i = R.string.wi_owm_day_200;break;
                case 201: i = R.string.wi_owm_day_201;break;
                case 202: i = R.string.wi_owm_day_202;break;
                case 210: i = R.string.wi_owm_day_210;break;
                case 211: i = R.string.wi_owm_day_211;break;
                case 212: i = R.string.wi_owm_day_212;break;
                case 221: i = R.string.wi_owm_day_221;break;
                case 230: i = R.string.wi_owm_day_230;break;
                case 231: i = R.string.wi_owm_day_231;break;
                case 232: i = R.string.wi_owm_day_232;break;
                case 300: i = R.string.wi_owm_day_300;break;
                case 301: i = R.string.wi_owm_day_301;break;
                case 302: i = R.string.wi_owm_day_302;break;
                case 310: i = R.string.wi_owm_day_310;break;
                case 311: i = R.string.wi_owm_day_311;break;
                case 312: i = R.string.wi_owm_day_312;break;
                case 313: i = R.string.wi_owm_day_313;break;
                case 314: i = R.string.wi_owm_day_314;break;
                case 321: i = R.string.wi_owm_day_321;break;
                case 500: i = R.string.wi_owm_day_500;break;
                case 501: i = R.string.wi_owm_day_501;break;
                case 502: i = R.string.wi_owm_day_502;break;
                case 503: i = R.string.wi_owm_day_503;break;
                case 504: i = R.string.wi_owm_day_504;break;
                case 511: i = R.string.wi_owm_day_511;break;
                case 520: i = R.string.wi_owm_day_520;break;
                case 521: i = R.string.wi_owm_day_521;break;
                case 522: i = R.string.wi_owm_day_522;break;
                case 531: i = R.string.wi_owm_day_531;break;
                case 600: i = R.string.wi_owm_day_600;break;
                case 601: i = R.string.wi_owm_day_601;break;
                case 602: i = R.string.wi_owm_day_602;break;
                case 611: i = R.string.wi_owm_day_611;break;
                case 612: i = R.string.wi_owm_day_612;break;
                case 615: i = R.string.wi_owm_day_615;break;
                case 616: i = R.string.wi_owm_day_616;break;
                case 620: i = R.string.wi_owm_day_620;break;
                case 621: i = R.string.wi_owm_day_621;break;
                case 622: i = R.string.wi_owm_day_622;break;
                case 701: i = R.string.wi_owm_day_701;break;
                case 711: i = R.string.wi_owm_day_711;break;
                case 721: i = R.string.wi_owm_day_721;break;
                case 731: i = R.string.wi_owm_day_731;break;
                case 741: i = R.string.wi_owm_day_741;break;
                case 761: i = R.string.wi_owm_day_761;break;
                case 762: i = R.string.wi_owm_day_762;break;
                case 771: i = R.string.wi_owm_day_771;break;
                case 781: i = R.string.wi_owm_day_781;break;
                case 800: i = R.string.wi_owm_day_800;break;
                case 801: i = R.string.wi_owm_day_801;break;
                case 802: i = R.string.wi_owm_day_802;break;
                case 803: i = R.string.wi_owm_day_803;break;
                case 804: i = R.string.wi_owm_day_804;break;
                case 900: i = R.string.wi_owm_day_900;break;
                case 901: i = R.string.wi_owm_day_901;break;
                case 902: i = R.string.wi_owm_day_902;break;
                case 903: i = R.string.wi_owm_day_903;break;
                case 904: i = R.string.wi_owm_day_904;break;
                case 905: i = R.string.wi_owm_day_905;break;
                case 906: i = R.string.wi_owm_day_906;break;
                case 957: i = R.string.wi_owm_day_957;break;

                default: i = R.string.wi_cloud_refresh;break;

            }
            return i;
        }
        else if(dayornight == -1) {
            switch (weatherID) {

                case 200: i = R.string.wi_owm_night_200;break;
                case 201: i = R.string.wi_owm_night_201;break;
                case 202: i = R.string.wi_owm_night_202;break;
                case 210: i = R.string.wi_owm_night_210;break;
                case 211: i = R.string.wi_owm_night_211;break;
                case 212: i = R.string.wi_owm_night_212;break;
                case 221: i = R.string.wi_owm_night_221;break;
                case 230: i = R.string.wi_owm_night_230;break;
                case 231: i = R.string.wi_owm_night_231;break;
                case 232: i = R.string.wi_owm_night_232;break;
                case 300: i = R.string.wi_owm_night_300;break;
                case 301: i = R.string.wi_owm_night_301;break;
                case 302: i = R.string.wi_owm_night_302;break;
                case 310: i = R.string.wi_owm_night_310;break;
                case 311: i = R.string.wi_owm_night_311;break;
                case 312: i = R.string.wi_owm_night_312;break;
                case 313: i = R.string.wi_owm_night_313;break;
                case 314: i = R.string.wi_owm_night_314;break;
                case 321: i = R.string.wi_owm_night_321;break;
                case 500: i = R.string.wi_owm_night_500;break;
                case 501: i = R.string.wi_owm_night_501;break;
                case 502: i = R.string.wi_owm_night_502;break;
                case 503: i = R.string.wi_owm_night_503;break;
                case 504: i = R.string.wi_owm_night_504;break;
                case 511: i = R.string.wi_owm_night_511;break;
                case 520: i = R.string.wi_owm_night_520;break;
                case 521: i = R.string.wi_owm_night_521;break;
                case 522: i = R.string.wi_owm_night_522;break;
                case 531: i = R.string.wi_owm_night_531;break;
                case 600: i = R.string.wi_owm_night_600;break;
                case 601: i = R.string.wi_owm_night_601;break;
                case 602: i = R.string.wi_owm_night_602;break;
                case 611: i = R.string.wi_owm_night_611;break;
                case 612: i = R.string.wi_owm_night_612;break;
                case 615: i = R.string.wi_owm_night_615;break;
                case 616: i = R.string.wi_owm_night_616;break;
                case 620: i = R.string.wi_owm_night_620;break;
                case 621: i = R.string.wi_owm_night_621;break;
                case 622: i = R.string.wi_owm_night_622;break;
                case 701: i = R.string.wi_owm_night_701;break;
                case 711: i = R.string.wi_owm_night_711;break;
                case 721: i = R.string.wi_owm_night_721;break;
                case 731: i = R.string.wi_owm_night_731;break;
                case 741: i = R.string.wi_owm_night_741;break;
                case 761: i = R.string.wi_owm_night_761;break;
                case 762: i = R.string.wi_owm_night_762;break;
                case 771: i = R.string.wi_owm_night_771;break;
                case 781: i = R.string.wi_owm_night_781;break;
                case 800: i = R.string.wi_owm_night_800;break;
                case 801: i = R.string.wi_owm_night_801;break;
                case 802: i = R.string.wi_owm_night_802;break;
                case 803: i = R.string.wi_owm_night_803;break;
                case 804: i = R.string.wi_owm_night_804;break;
                case 900: i = R.string.wi_owm_night_900;break;
                case 901: i = R.string.wi_owm_night_901;break;
                case 902: i = R.string.wi_owm_night_902;break;
                case 903: i = R.string.wi_owm_night_903;break;
                case 904: i = R.string.wi_owm_night_904;break;
                case 905: i = R.string.wi_owm_night_905;break;
                case 906: i = R.string.wi_owm_night_906;break;
                case 957: i = R.string.wi_owm_night_957;break;

                default: i = R.string.wi_cloud_refresh;break;

            }
            return i;
        }
        else return getImage(weatherID);
    }
}
