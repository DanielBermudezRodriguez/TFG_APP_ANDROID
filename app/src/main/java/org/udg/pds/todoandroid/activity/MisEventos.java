package org.udg.pds.todoandroid.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.TabEventosCreados;
import org.udg.pds.todoandroid.fragment.TabEventosEnCola;
import org.udg.pds.todoandroid.fragment.TabEventosRegistrado;

public class MisEventos extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Mostrar botón "atras" en action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        tabLayout = findViewById(R.id.tabs);

        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabEventosCreados(tabLayout.getTabAt(position), mSectionsPagerAdapter);
                case 1:
                    return new TabEventosRegistrado(tabLayout.getTabAt(position), mSectionsPagerAdapter);
                case 2:
                    return new TabEventosEnCola(tabLayout.getTabAt(position), mSectionsPagerAdapter);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        public View getTabView(int total, String titulo) {
            @SuppressLint("InflateParams") View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_eventos, null);
            TextView tv = v.findViewById(R.id.tab_eventos_creados_total);
            tv.setText(String.valueOf(total));
            TextView m = v.findViewById(R.id.tab_eventos_creados);
            m.setText(titulo);

            return v;
        }
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
