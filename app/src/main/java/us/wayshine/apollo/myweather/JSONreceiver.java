package us.wayshine.apollo.myweather;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Apollo on 9/26/15.
 */

public class JSONreceiver {

    private Context mContext;
    private RequestQueue queue;
    private static JSONreceiveListener listener;

    public JSONreceiver(Context context) {

        mContext = context;
        queue = Volley.newRequestQueue(mContext);
    }

    public void setNewRequest(String url, String p, int i, int t) {

        final int id = i;
        final int type = t;
        final String option = p;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Not found city")) {
                            listener.onJSONreceive(id, type, response, option, false);
                        }
                        else
                            listener.onJSONreceive(id, type, "404", option, false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response error", error.toString());
                        listener.onJSONreceive(id, type, error.toString(), option, false);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                if(type == MainActivity.TYPE_IMAGE) {
                    map.put("Api-Key", mContext.getString(R.string.gettyimages_api_key));
                }
                return map;
            }
        };

        queue.add(request);
    }

    public void startNewRequest() {
        queue.start();
    }

    public void setJSONreceiveListener(JSONreceiveListener listener) {
        JSONreceiver.listener = listener;
    }

    public interface JSONreceiveListener
    {
        void onJSONreceive(int id, int type, String data, String option, boolean succeed);
    }

}
