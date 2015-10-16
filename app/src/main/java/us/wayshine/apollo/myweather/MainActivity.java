package us.wayshine.apollo.myweather;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String PREFS_NAME = "SimpleWeatherData";
    public static final int TYPE_INFO = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int ID_SEARCH = -1;

    private int dataCount = 0;
    private DataObject searchData;
    private String searchWord = "";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private MenuItem mSearch;
    private static String LOG_TAG = "MainActivity";

    private JSONreceiver jsonInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.weather_cards);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(24));
        mAdapter = new MyAdapter(new ArrayList<DataObject>(), this);
        MyItemTouchHelperCallback mItemTouchHelperCallback = new MyItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

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

        //ActionBar actionBar = getActionBar();
        //actionBar.setLogo(R.drawable.weather_nobackground);
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        jsonInfo.setJSONreceiveListener(new JSONreceiver.JSONreceiveListener() {
            @Override
            public void onJSONreceive(int id, int type, String data, String option, boolean succeed) {
            if (succeed) {
                if (id == ID_SEARCH && type == TYPE_INFO && !data.equals("{\"cod\":\"404\",\"message\":\"Error: Not found city\"}")) {
                    updateSearchCard(data);
                } else if (id == ID_SEARCH && type == TYPE_IMAGE) {

                    PhotoObject dat = new PhotoObject(data);
                    String url = dat.getURL();
                    try {
                        new DownloadImageTask(((ImageView) findViewById(R.id.search_cover)), option)
                                .execute(url);
                        MyAnimator.fadeIn(findViewById(R.id.search_cover), 0);
                    } catch (Exception e) {
                        Log.e("", e.toString());
                    }
                } else if (type == TYPE_IMAGE) {
                    PhotoObject dat = new PhotoObject(data);
                    String url = dat.getURL();
                    mAdapter.refreshItemCover(id, url);
                } else {
                    DataObject dat = new DataObject(data);
                    Log.i(LOG_TAG, dat.getWeather());
                    if (id > mAdapter.getItemCount())
                        mAdapter.addItem(new DataObject(data), mAdapter.getItemCount());
                    else
                        mAdapter.updateItem(new DataObject(data), id);
                    writeData(id, data);
                }
            }

            mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        findViewById(R.id.card_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchData != null) {
                    mAdapter.addItem(searchData, 0);
                    dataCount++;
                    searchView.setIconified(true);
                    mSearch.collapseActionView();
                    mLayoutManager.scrollToPosition(0);
                }
            }
        });

        showCards();

    }

    private void showCards() {

        if(readSettings("city_count").equals("")) dataCount = 0;
        else dataCount = Integer.parseInt(readSettings("city_count"));
        DataObject dat;
        for(int i = 0; i < dataCount; i++) {
            dat = readData(i);
            if(dat != null) {
                mAdapter.addItem(dat, i);
            }
        }
    }

    private boolean refreshJSON(){

        try {

            for(int i = 0; i < mAdapter.getItemCount(); i++) {
                jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q=" + mAdapter.getDataObject(i).getCity() + "&APPID=" + getString(R.string.owm_api_key), "", i, TYPE_INFO);
            }

            return true;
        }
        catch(Exception e){
            Log.e("Fetch Data Error!", e.toString());
            return false;
        }
    }

    private void showDetail(View v, int id) {
        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(DetailActivity.EXTRA_DETAIL, mAdapter.getDataObject(id).getJSONString());

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
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

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

    private boolean writeAllData() {

        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = data_file.edit();
        dataCount = mAdapter.getItemCount();
        editor.putString("city_count", Integer.toString(dataCount));

        for(int i = 0; i < dataCount; i++) {
            editor.putString("data_" + i, mAdapter.getDataObject(i).getJSONString());
        }

        editor.apply();
        return true;
    }

    protected DataObject readData(int id) {

        String str;
        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        str = data_file.getString("data_" + id, "");
        if(!str.equals("")) {
            return new DataObject(str);
        }
        else {
            return null;
        }
    }

    protected String readSettings(String option) {
        String str;
        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        str = data_file.getString(option, "");
        return str;
    }

    public void requestCoverImage(String city, int id) {
        jsonInfo.setNewRequest("https://api.gettyimages.com/v3/search/images?fields=thumb&page=1&page_size=1&phrase="
                + Uri.encode(city), city, id, TYPE_IMAGE);
    }

    @Override
    public void onRefresh(){
        mSwipeRefreshLayout.setRefreshing(true);
        refreshJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        mSearch = menu.findItem(R.id.search);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint("Enter your city");
        searchView.setOnQueryTextListener(this.onQueryTextListener);

        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                findViewById(R.id.searchBox).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                findViewById(R.id.searchBox).setVisibility(View.VISIBLE);
                return true;
            }
        });


        return true;
    }


    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
            if(!searchWord.equals("")) {
                jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q="
                        + Uri.encode(searchWord) + "&APPID=" + getString(R.string.owm_api_key), searchWord, ID_SEARCH, TYPE_INFO);
                clearSearchCard();
            }

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("") && !searchWord.equals(""))
                MyAnimator.fadeOut(findViewById(R.id.searchBox), 0);
            else if(!newText.equals("") && searchWord.equals(""))
                MyAnimator.fadeIn(findViewById(R.id.searchBox), 0);
            searchWord = newText;
            return true;
        }

    };

    private void updateSearchCard(String data) {
        searchData = new DataObject(data);
        View card = findViewById(R.id.card_search);
        ((TextView)card.findViewById(R.id.card_city)).setText(searchData.getCity());
        ((TextView)card.findViewById(R.id.card_temper)).setText(searchData.getTemp());
        ((TextView)card.findViewById(R.id.card_weather)).setText(searchData.getWeather());
        ((TextView)card.findViewById(R.id.card_image)).setText(searchData.getAlterImage());
        if(!DownloadImageTask.imageExists(searchData.getCity())) requestCoverImage(searchData.getCity(), ID_SEARCH);
        else {
            new DownloadImageTask(((ImageView) findViewById(R.id.search_cover)), searchData.getCity())
                    .loadFromLocal();
            MyAnimator.fadeIn(findViewById(R.id.search_cover), 0);
        }
    }

    private void clearSearchCard() {
        View card = findViewById(R.id.card_search);
        ((TextView)card.findViewById(R.id.card_city)).setText("---");
        ((TextView)card.findViewById(R.id.card_temper)).setText("---");
        ((TextView)card.findViewById(R.id.card_weather)).setText("-----");
        ((TextView)card.findViewById(R.id.card_image)).setText(getString(R.string.wi_cloud_refresh));
        MyAnimator.fadeOut(findViewById(R.id.search_cover), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeAllData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setOnItemClickListener(new MyAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                showDetail(v, position);

                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });

    }
}
