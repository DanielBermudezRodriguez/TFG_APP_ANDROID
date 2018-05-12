package org.udg.pds.todoandroid.activity;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.TabEventosCreados;
import org.udg.pds.todoandroid.fragment.TabEventosRegistrado;

public class MisEventos extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mis_eventos, menu);
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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabEventosCreados(tabLayout.getTabAt(position), mSectionsPagerAdapter);
                case 1:
                    return new TabEventosRegistrado(tabLayout.getTabAt(position), mSectionsPagerAdapter);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public View getTabView(int total, String titulo) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_eventos, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_eventos_creados_total);
            tv.setText(String.valueOf(total));
            TextView m = (TextView) v.findViewById(R.id.tab_eventos_creados);
            m.setText(titulo);

            return v;
        }
    }
}