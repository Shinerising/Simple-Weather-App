package us.wayshine.apollo.myweather;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;


public class ForecastService extends IntentService {
    private static String LOG_TAG = "ForecastService";
    static public String EXTRA_DETAIL = "us.wayshine.apollo.myweather.forecast.city_id";
    private JSONreceiver jsonInfo;
    private int startHour = 8;

    private JSONreceiver.JSONreceiveListener JSONListener = new JSONreceiver.JSONreceiveListener() {
        @Override
        public void onJSONreceive(int id, int type, String data, String option, boolean succeed) {
            if (succeed & type == 3) {
                DataObject dat = new DataObject(data);
                showNotification(dat.getCity(), dat.getTemp() + " " + dat.getWeather());
            }
        }
    };

    public ForecastService() {
        super("ForecastService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String city_id = intent.getStringExtra(ForecastService.EXTRA_DETAIL);
        long endTime = 30l * 24 * 3600 * 1000 + System.currentTimeMillis();
        jsonInfo = new JSONreceiver(this.getBaseContext());
        jsonInfo.setJSONreceiveListener(JSONListener);
        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?id=" + city_id + "&APPID=" + getString(R.string.owm_api_key), "", 0, 3);
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if(hour == startHour) {
                        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?id=" + city_id + "&APPID=" + getString(R.string.owm_api_key), "", 0, 3);
                        wait(24 * 3600 * 1000);
                    }
                    else if(hour < startHour) wait((startHour - hour) * 3600 * 1000);
                    else if(hour > startHour) wait((startHour + 24 - hour) * 3600 * 1000);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }
    }

    private boolean showNotification(String city, String weather) {
        Log.i(LOG_TAG, city + weather);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.weather_nobackground)
                        .setContentTitle(city)
                        .setContentText(weather);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        return true;
    }
}
