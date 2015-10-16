package us.wayshine.apollo.myweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Apollo on 10/12/15.
 */
public class PhotoObject {

    private JSONObject jObject;
    private String url;

    public PhotoObject(String data) {

        if(!data.equals("")) {
            try {
                jObject = new JSONObject(data);
                parseJSON();
            } catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }

    }

    private void parseJSON() {

        try {
            JSONArray images = jObject.optJSONArray("images");
            JSONArray sizes = images.optJSONObject(0).optJSONArray("display_sizes");
            url = sizes.optJSONObject(0).optString("uri");
            Log.i("JSON Parser", getURL());
        }
        catch(Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
    }

    public String getURL() {
        return url;
    }
}
