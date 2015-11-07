package us.wayshine.apollo.myweather;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {

    static public String EXTRA_DETAIL = "us.wayshine.apollo.myweather.detail_data";
    private static String LOG_TAG = "DetailActivity";
    private DataObject detailData;

    private TextView label_maxtemp;
    private TextView label_mintemp;
    private TextView label_clouds;
    private TextView label_pressure;
    private TextView label_humidity;
    private TextView label_visibility;

    private JSONreceiver jsonInfo;
    private ForecastDataObject forecastData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailData = new DataObject(getIntent().getExtras().getString(EXTRA_DETAIL, ""));

        jsonInfo = new JSONreceiver(this.getBaseContext());

        assert getActionBar() != null;
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        View v = View.inflate(this, R.layout.actionbar_custom, null);
        ((TextView)v.findViewById(R.id.title)).setText(R.string.title_activity_detail);
        actionBar.setCustomView(v);

        findViewById(R.id.detail).setBackgroundColor(0xff1976D2);

        if(DownloadImageTask.imageExists(detailData.getCity()))
            new DownloadImageTask(((ImageView)findViewById(R.id.card_cover)), detailData.getCity(), findViewById(R.id.detail))
                    .loadFromLocal();
        else if(!detailData.getCoverImageUri().equals(""))
            new DownloadImageTask((ImageView)findViewById(R.id.card_cover), detailData.getCity(), findViewById(R.id.detail))
                    .execute(detailData.getCoverImageUri());
        else
            requestCoverImage(detailData.getCity(), 0);

        ((TextView)findViewById(R.id.card_city)).setText(detailData.getCity());
        ((TextView)findViewById(R.id.card_temper)).setText(detailData.getTemp());
        ((TextView)findViewById(R.id.card_weather)).setText(detailData.getWeather());
        ((TextView)findViewById(R.id.card_image)).setText(detailData.getAlterImage());

        ((TextView)findViewById(R.id.lon)).setText(detailData.getLon());
        ((TextView)findViewById(R.id.lat)).setText(detailData.getLat());
        ((TextView)findViewById(R.id.sunrise)).setText(detailData.getSunrise());
        ((TextView)findViewById(R.id.sunset)).setText(detailData.getSunset());

        label_maxtemp = (TextView)findViewById(R.id.temp_max);
        label_mintemp = (TextView)findViewById(R.id.temp_min);
        label_clouds = (TextView)findViewById(R.id.clouds);
        label_pressure = (TextView)findViewById(R.id.pressure);
        label_humidity = (TextView)findViewById(R.id.humidity);
        label_visibility = (TextView)findViewById(R.id.visibility);

        findViewById(R.id.card_forecast).setScaleY(0);
        findViewById(R.id.card_forecasttext).setScaleY(0);
        findViewById(R.id.card_forecast).setAlpha(0);
        findViewById(R.id.card_forecasttext).setAlpha(0);

        MyAnimator.fadeIn(findViewById(R.id.card_detail01), 900);
        MyAnimator.fadeIn(findViewById(R.id.card_detail02), 1100);

        ImageView windImage = (ImageView) findViewById(R.id.wind_image);

        Animation rotate = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000 / (5 + detailData.getWindSpeed()));
        rotate.setRepeatCount(-1);
        rotate.setRepeatMode(Animation.ABSOLUTE);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);

        windImage.setAnimation(rotate);

        startTextAnimation();

        View arrowImage = findViewById(R.id.arrow);
        arrowImage.setRotation(detailData.getWindDeg());

        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/forecast/daily?id=" + detailData.getCityID() + "&APPID=" + getString(R.string.owm_api_key), "", 0, MainActivity.TYPE_FORECAST);

        try {
            jsonInfo.startNewRequest();
        }
        catch(Exception e){
            Log.e("Fetch Data Error!", e.toString());
        }


        jsonInfo.setJSONreceiveListener(new JSONreceiver.JSONreceiveListener() {
            @Override
            public void onJSONreceive(int id, int type, String data, String option, boolean succeed) {
                if(succeed) {
                    if (type == MainActivity.TYPE_IMAGE) {

                        PhotoObject dat = new PhotoObject(data);
                        String url = dat.getURL();
                        try {
                            new DownloadImageTask(((ImageView) findViewById(R.id.card_cover)), option, findViewById(R.id.detail))
                                    .execute(url);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                    else if(type == MainActivity.TYPE_FORECAST) {
                        forecastData = new ForecastDataObject(data);
                        ((TextView) findViewById(R.id.fc01)).setText(forecastData.getTempBoth(0));
                        ((TextView) findViewById(R.id.fc02)).setText(forecastData.getTempBoth(1));
                        ((TextView) findViewById(R.id.fc03)).setText(forecastData.getTempBoth(2));
                        ((TextView) findViewById(R.id.fc04)).setText(forecastData.getTempBoth(3));
                        ((TextView) findViewById(R.id.fc05)).setText(forecastData.getTempBoth(4));

                        ((TextView) findViewById(R.id.fci01)).setText(DataObject.getImage(forecastData.getWeatherID(0)));
                        ((TextView) findViewById(R.id.fci02)).setText(DataObject.getImage(forecastData.getWeatherID(1)));
                        ((TextView) findViewById(R.id.fci03)).setText(DataObject.getImage(forecastData.getWeatherID(2)));
                        ((TextView) findViewById(R.id.fci04)).setText(DataObject.getImage(forecastData.getWeatherID(3)));
                        ((TextView) findViewById(R.id.fci05)).setText(DataObject.getImage(forecastData.getWeatherID(4)));

                        MyAnimator.expandFadeIn(findViewById(R.id.card_forecast), 0);
                        MyAnimator.expandFadeIn(findViewById(R.id.card_forecasttext), 300);
                    }
                }
            }
        });
    }

    private void startTextAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setStartDelay(2000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float v = (float) valueAnimator.getAnimatedValue();
                label_maxtemp.setText(detailData.getTempMax(v));
                label_mintemp.setText(detailData.getTempMin(v));
                label_clouds.setText(detailData.getClouds(v));
                label_pressure.setText(detailData.getPressure(v));
                label_humidity.setText(detailData.getHumidity(v));
                label_visibility.setText(detailData.getVisibility(v));

            }
        });
        valueAnimator.start();
    }

    public void requestCoverImage(String city, int id) {
        jsonInfo.setNewRequest("https://api.gettyimages.com/v3/search/images?fields=thumb&page=1&page_size=1&phrase="
                + Uri.encode(city), city, id, MainActivity.TYPE_IMAGE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

}
