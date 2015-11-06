package us.wayshine.apollo.myweather;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String PREFS_NAME = "SimpleWeatherData";
    public static final int TYPE_INFO = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_FORECAST = 2;
    public static final int ID_SEARCH = -1;
    public static final int TOAST_TIMEOUT = 0;
    public static final int TOAST_SEARCH_TIMEOUT = 1;
    public static final int TOAST_SEARCH_NORESULT = 2;
    private static String LOG_TAG = "MainActivity";
    private int dataCount = 0;
    private DataObject searchData;
    private String searchWord = "";
    private MyDatabaseHelper mDatabaseHelper;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView mSearchView;
    private MenuItem mSearch;
    private JSONreceiver jsonInfo;

    private JSONreceiver.JSONreceiveListener JSONListener = new JSONreceiver.JSONreceiveListener() {
        @Override
        public void onJSONreceive(int id, int type, String data, String option, boolean succeed) {
            if (succeed) {
                if (id == ID_SEARCH && type == TYPE_INFO) {
                    updateSearchCard(data);
                } else if (id == ID_SEARCH && type == TYPE_IMAGE) {
                    try {
                        PhotoObject dat = new PhotoObject(data);
                        String url = dat.getURL();
                        if (!url.equals(""))
                            new DownloadImageTask(((ImageView) findViewById(R.id.search_cover)), option)
                                    .execute(url);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                } else if (type == TYPE_IMAGE) {
                    PhotoObject dat = new PhotoObject(data);
                    String url = dat.getURL();
                    try {
                        mAdapter.refreshItemCover(id, url);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                } else {
                    DataObject dat = new DataObject(data);
                    try {
                        if (id > mAdapter.getItemCount())
                            mAdapter.addItem(dat, mAdapter.getItemCount());
                        else
                            mAdapter.updateItem(dat, id);
                        writeData(id, data);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            } else {
                if (id == ID_SEARCH && type == TYPE_INFO) {
                    findViewById(R.id.card_search).setVisibility(View.GONE);
                    if (data.equals("404"))
                        showToast(TOAST_SEARCH_NORESULT);
                    else
                        showToast(TOAST_SEARCH_TIMEOUT);
                } else {
                    showToast(TOAST_TIMEOUT);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabaseHelper = new MyDatabaseHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.weather_cards);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(24));
        mAdapter = new MyAdapter(new ArrayList<DataObject>(), this);
        MyItemTouchHelperCallback mItemTouchHelperCallback = new MyItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            try {
                Bitmap bmp = decodeSampledBitmapFromResource(getResources(), R.drawable.weather_nobackground, 128, 128);
                this.setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name), bmp, 0xFF2196F3));
                bmp.recycle();
            } catch (Throwable e) {
                Log.e("MainActivity", e.toString());
            }
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.green, R.color.orange);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        jsonInfo = new JSONreceiver(this);

        assert getActionBar() != null;
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        View v = View.inflate(this, R.layout.actionbar_custom, null);
        actionBar.setCustomView(v);

        findViewById(R.id.card_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchData != null) {
                    mAdapter.addItem(searchData, 0);
                    dataCount++;
                    mSearchView.onActionViewCollapsed();
                    mSearch.collapseActionView();
                    mLayoutManager.scrollToPosition(0);
                }
            }
        });

        mAdapter.setOnItemClickListener(new MyAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                showDetail(v, position);
            }
        });

        try {
            mDatabaseHelper.createDataBase();
        } catch (Exception e) {
            Log.e("SQLite Database Error", e.toString());
        }

        showCards();
        //refreshJSON();
    }

    private void showCards() {

        if (readSettings("city_count").equals("")) {
            dataCount = 0;
            jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q="
                    + Uri.encode("New York") + "&APPID=" + getString(R.string.owm_api_key), "New York", 0, TYPE_INFO);
            jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q="
                    + Uri.encode("Beijing") + "&APPID=" + getString(R.string.owm_api_key), "Beijing", 1, TYPE_INFO);
            jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q="
                    + Uri.encode("Tokyo") + "&APPID=" + getString(R.string.owm_api_key), "Tokyo", 2, TYPE_INFO);
        }
        else {
            dataCount = Integer.parseInt(readSettings("city_count"));
            DataObject dat;
            for (int i = 0; i < dataCount; i++) {
                dat = readData(i);
                if (dat != null) {
                    mAdapter.addItem(dat, i);
                }
            }
        }
    }

    private void showToast(int type) {
        switch (type) {
            case TOAST_SEARCH_NORESULT:
                Toast.makeText(this, getString(R.string.toast_noresult), Toast.LENGTH_SHORT).show();
                break;
            case TOAST_SEARCH_TIMEOUT:
                Toast.makeText(this, getString(R.string.toast_timeout), Toast.LENGTH_SHORT).show();
                break;
            case TOAST_TIMEOUT:
                Toast.makeText(this, getString(R.string.toast_timeout), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, getString(R.string.toast_timeout), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean refreshJSON() {

        if (mAdapter.getItemCount() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
            return false;
        }
        try {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?id=" + mAdapter.getDataObject(i).getCityID() + "&APPID=" + getString(R.string.owm_api_key), "", i, TYPE_INFO);
            }
            return true;
        } catch (Exception e) {
            Log.e("Fetch Data Error!", e.toString());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
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

        for (int i = 0; i < dataCount; i++) {
            editor.putString("data_" + i, mAdapter.getDataObject(i).getJSONString());
        }

        editor.apply();
        return true;
    }

    protected DataObject readData(int id) {

        String str;
        SharedPreferences data_file = getSharedPreferences(PREFS_NAME, 0);
        str = data_file.getString("data_" + id, "");
        if (!str.equals("")) {
            return new DataObject(str);
        } else {
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
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        refreshJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        mSearch = menu.findItem(R.id.search);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setQueryHint("Enter your city");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchWord) {
                if (!searchWord.equals("")) {
                    jsonInfo.setNewRequest("http://api.openweathermap.org/data/2.5/weather?q="
                            + Uri.encode(searchWord) + "&APPID=" + getString(R.string.owm_api_key), searchWord, ID_SEARCH, TYPE_INFO);
                    clearSearchCard();
                    MyAnimator.fadeIn(findViewById(R.id.card_search), 0);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("") && !searchWord.equals(""))
                    MyAnimator.fadeOut(findViewById(R.id.searchBox), 0);
                else if (!newText.equals("") && searchWord.equals(""))
                    MyAnimator.fadeIn(findViewById(R.id.searchBox), 0);

                if (!newText.equals("")) {
                    SimpleCursorAdapter suggestions = new SimpleCursorAdapter(getBaseContext(),
                            android.R.layout.simple_list_item_1, mDatabaseHelper.getCityListCursor(newText),
                            new String[]{"city"},
                            new int[]{android.R.id.text1}, 0);
                    mSearchView.setSuggestionsAdapter(suggestions);
                }

                findViewById(R.id.card_search).setVisibility(View.INVISIBLE);
                searchWord = newText;
                return true;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {

                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                int indexColumnSuggestion = cursor.getColumnIndex("city");
                mSearchView.setQuery(cursor.getString(indexColumnSuggestion), true);

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                findViewById(R.id.card_search).setVisibility(View.GONE);
                findViewById(R.id.searchBox).setVisibility(View.GONE);
                return true;
            }
        });

        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                findViewById(R.id.card_search).setVisibility(View.GONE);
                findViewById(R.id.searchBox).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                findViewById(R.id.searchBox).setVisibility(View.VISIBLE);
                return true;
            }
        });
        int searchCloseButtonId = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) mSearchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery("", false);
                mSearchView.onActionViewCollapsed();
            }
        });
        return true;
    }

    private void updateSearchCard(String data) {
        searchData = new DataObject(data);
        View card = findViewById(R.id.card_search);
        ((TextView) card.findViewById(R.id.card_city)).setText(searchData.getCity());
        ((TextView) card.findViewById(R.id.card_temper)).setText(searchData.getTemp());
        ((TextView) card.findViewById(R.id.card_weather)).setText(searchData.getWeather());
        ((TextView) card.findViewById(R.id.card_image)).setText(searchData.getAlterImage());
        card.findViewById(R.id.card_info).setVisibility(View.VISIBLE);
        card.findViewById(R.id.search_progress).setVisibility(View.INVISIBLE);
        if (!DownloadImageTask.imageExists(searchData.getCity()))
            requestCoverImage(searchData.getCity(), ID_SEARCH);
        else {
            new DownloadImageTask(((ImageView) findViewById(R.id.search_cover)), searchData.getCity())
                    .loadFromLocal();
            MyAnimator.fadeIn(findViewById(R.id.search_cover), 0);
        }
    }

    private void clearSearchCard() {
        View card = findViewById(R.id.card_search);
        card.findViewById(R.id.card_info).setVisibility(View.GONE);
        card.findViewById(R.id.search_progress).setVisibility(View.VISIBLE);
        ((ImageView) card.findViewById(R.id.search_cover)).setImageBitmap(null);
        ((TextView) card.findViewById(R.id.card_image)).setText(getString(R.string.wi_cloud_refresh));
        MyAnimator.fadeOut(findViewById(R.id.search_cover), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mSearchView.onActionViewCollapsed();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mDatabaseHelper.close();
        } catch (Exception e) {
            Log.e("SQLite Database Error", e.toString());
        }
        jsonInfo.setJSONreceiveListener(null);
        writeAllData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDatabaseHelper.openDataBase();
        } catch (Exception e) {
            Log.e("SQLite Database Error", e.toString());
        }
        jsonInfo.setJSONreceiveListener(JSONListener);
    }
}
