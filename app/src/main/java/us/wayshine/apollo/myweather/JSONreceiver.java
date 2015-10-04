package us.wayshine.apollo.myweather;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.Serializable;
import java.util.ArrayList;


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

    public void setNewRequest(String url, int i) {

        final int id = i;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onJSONreceive(id, response, true);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("response", "error!");
                listener.onJSONreceive(id, error.toString(), false);
            }
        });

        queue.add(stringRequest);
    }


    public void startNewRequest() {
        queue.start();
    }

    public void setJSONreceiveListener(JSONreceiveListener listener) {
        JSONreceiver.listener = listener;
    }

    public interface JSONreceiveListener
    {
        void onJSONreceive(int id, String data, boolean succeed);
    }

}
