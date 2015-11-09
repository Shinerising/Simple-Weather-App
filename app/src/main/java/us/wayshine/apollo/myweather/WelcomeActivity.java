package us.wayshine.apollo.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WelcomeActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;
    public static String PACKAGE_NAME;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PACKAGE_NAME = this.getPackageName();

        if (!getSharedPreferences(MainActivity.PREFS_NAME, 0).getBoolean("first_open", true)) {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }

        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        MyAnimator.inflate(findViewById(R.id.dot0), 0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (currentPage) {
                    case 0:
                        MyAnimator.deflate(findViewById(R.id.dot0), 0);
                        break;
                    case 1:
                        MyAnimator.deflate(findViewById(R.id.dot1), 0);
                        break;
                    case 2:
                        MyAnimator.deflate(findViewById(R.id.dot2), 0);
                        break;
                    case 3:
                        MyAnimator.deflate(findViewById(R.id.dot3), 0);
                        break;
                }
                switch (position) {
                    case 0:
                        MyAnimator.inflate(findViewById(R.id.dot0), 0);
                        break;
                    case 1:
                        MyAnimator.inflate(findViewById(R.id.dot1), 0);
                        break;
                    case 2:
                        MyAnimator.inflate(findViewById(R.id.dot2), 0);
                        break;
                    case 3:
                        MyAnimator.inflate(findViewById(R.id.dot3), 0);
                        break;
                }
                if (currentPage == 0) MyAnimator.fadeIn(findViewById(R.id.back), 0);
                else if (position == 0) MyAnimator.fadeOut(findViewById(R.id.back), 0);

                if (position == 3) {
                    MyAnimator.rotate(findViewById(R.id.forward), 90, 0);
                    MyAnimator.fadeIn(findViewById(R.id.enter), 300);
                } else if (currentPage == 3) {
                    MyAnimator.rotate(findViewById(R.id.forward), 0, 300);
                    MyAnimator.fadeOut(findViewById(R.id.enter), 0);
                }
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) mPager.setCurrentItem(currentPage - 1);
            }
        });

        findViewById(R.id.forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < 3) mPager.setCurrentItem(currentPage + 1);
                else {
                    SharedPreferences data_file = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = data_file.edit();
                    editor.putBoolean("first_open", false);
                    editor.apply();

                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    static public class ScreenSlidePageFragment extends Fragment {

        private int id = 0;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.screen_slide, container, false);
            GIFView gifView = new GIFView(this.getContext());
            TextView info = (TextView) rootView.findViewById(R.id.info);

            switch (id) {
                case 0:
                    info.setText(R.string.info_0);
                    gifView.setAnimatedGif(R.raw.anim1, GIFView.TYPE.FIT_CENTER);
                    break;
                case 1:
                    info.setText(R.string.info_1);
                    gifView.setAnimatedGif(R.raw.anim2, GIFView.TYPE.FIT_CENTER);
                    break;
                case 2:
                    info.setText(R.string.info_2);
                    gifView.setAnimatedGif(R.raw.anim3, GIFView.TYPE.FIT_CENTER);
                    break;
                case 3:
                    info.setText(R.string.info_3);
                    gifView.setAnimatedGif(R.raw.anim4, GIFView.TYPE.FIT_CENTER);
                    break;
                default:
                    info.setText(R.string.info_0);
                    gifView.setAnimatedGif(R.raw.anim1, GIFView.TYPE.FIT_CENTER);
                    break;
            }

            rootView.addView(gifView);

            return rootView;
        }

        public Fragment setPosition(int position) {
            id = position;
            return this;
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment().setPosition(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
