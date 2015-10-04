package us.wayshine.apollo.myweather;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String PREFS_NAME = "MyData";
    public static final int BEIJING = 0;
    public static final int TOKYO = 1;
    public static final int LONDON = 2;
    public static final int NEWYORK = 3;
    public static final int BRUSSELS = 4;

    private int dataCount = 0;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "MainActivity";

    private JSONreceiver jsonInfo;

    private final Handler runningHandler = new Handler();
    private Runnable showcards = new Runnable(){
        public void run(){
            showCards();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.weather_cards);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(new ArrayList<DataObject>(), this);
        mRecyclerView.setAdapter(mAdapter);

        if(android.os.Build.VERSION.SDK_INT > 20) {
            try {
                Bitmap bmp = decodeSampledBitmapFromResource(getResources(), R.drawable.weather_nobackground, 128, 128);
                this.setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name), bmp, 0xFF2196F3));
                bmp.recycle();
            } catch (Throwable e) {
                Log.e("MainActivity", e.toString());
            }
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.green, R.color.orange);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        jsonInfo = new JSONreceiver(this);

        runningHandler.postDelayed(showcards, 200);
    }

    private void showCards() {

        DataObject dataObject = readData(dataCount);
        if(dataObject != null) {
            ((MyAdapter) mAdapter).addItem(dataObject, dataCount);
            dataCount++;
            if(dataCount < 5) runningHandler.postDelayed(showcards, 300);
        }
        else {
            refreshJSON();
        }
    }

    private boolean refreshJSON(){

        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=beijing", BEIJING);
        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=tokyo", TOKYO);
        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=london", LONDON);
        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=newyork", NEWYORK);
        jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=brussels", BRUSSELS);

        try {
            jsonInfo.startNewRequest();
            return true;
        }
        catch(Exception e){
            Log.e("Fetch Data Error!", e.toString());
            return false;
        }
    }

    private void showDetail(View v, int id) {
        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(DetailActivity.EXTRA_DETAIL, ((MyAdapter)mAdapter).getDataObject(id).getJSONString());

        Pair<View, String> p1 = Pair.create(v.findViewById(R.id.card_cover), "cover");
        Pair<View, String> p2 = Pair.create(v.findViewById(R.id.card_image), "image");
        Pair<View, String> p3 = Pair.create(v.findViewById(R.id.card_city), "city");
        Pair<View, String> p4 = Pair.create(v.findViewById(R.id.card_temper), "temp");
        Pair<View, String> p5 = Pair.create(v.findViewById(R.id.card_weather), "weather");

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5);

        startActivity(intent, options.toBundle());
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private boolean writeData(int id, String data) {

        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = data_file.edit();
        editor.putString("data_" + id, data);

        editor.apply();

        return true;
    }

    protected DataObject readData(int id) {

        String str = "";
        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        str = data_file.getString("data_" + id, "");
        if(str != "") {
            return new DataObject(str);
        }
        else {
            return null;
        }
    }

    @Override
    public void onRefresh(){
        mSwipeRefreshLayout.setRefreshing(true);
        refreshJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyAdapter) mAdapter).setOnItemClickListener(new MyAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                showDetail(v, position);

                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });

        jsonInfo.setJSONreceiveListener(new JSONreceiver.JSONreceiveListener() {
            @Override
            public void onJSONreceive(int id, String data, boolean succeed) {
                if(succeed) {
                    DataObject dat = new DataObject(data);
                    Log.i(LOG_TAG, dat.getWeather());
                    if(id > mAdapter.getItemCount())
                        ((MyAdapter) mAdapter).addItem(new DataObject(data), mAdapter.getItemCount());
                    else
                        ((MyAdapter) mAdapter).updateItem(new DataObject(data), id);
                    writeData(id, data);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
