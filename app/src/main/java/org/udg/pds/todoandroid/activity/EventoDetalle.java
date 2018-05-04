package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.adapter.ParticipanteEventoAdapter;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.TabEventoForo;
import org.udg.pds.todoandroid.fragment.TabEventoInformacion;
import org.udg.pds.todoandroid.fragment.TabEventoParticipantes;
import org.udg.pds.todoandroid.fragment.TabEventoUbicacion;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class EventoDetalle extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Evento eventoActual;
    private ApiRest apiRest;
    private FloatingTextButton apuntarParticipanteEvento;
    private FloatingTextButton desapuntarParticipanteEvento;
    private Boolean esParticipante;
    private TabLayout tabLayout;
    private List<ParticipanteEvento> participanteEventos = new ArrayList<ParticipanteEvento>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        eventoActual = (Evento) getIntent().getExtras().getSerializable(Global.KEY_SELECTED_EVENT);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        obtenerParticipantesEvento();

        desapuntarParticipanteEvento = findViewById(R.id.eliminar_participante_evento);
        desapuntarParticipanteEvento.setVisibility(View.GONE);

        apuntarParticipanteEvento = findViewById(R.id.apuntar_participante_evento);
        apuntarParticipanteEvento.setVisibility(View.GONE);


        apuntarParticipanteEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSectionsPagerAdapter.notifyDataSetChanged();
                esParticipante = true;
                desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                apuntarParticipanteEvento.setVisibility(View.GONE);
                registrarPartipanteEvento();
            }
        });


        desapuntarParticipanteEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSectionsPagerAdapter.notifyDataSetChanged();
                esParticipante = false;
                apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                desapuntarParticipanteEvento.setVisibility(View.GONE);
                eliminarParticipanteEvento();
            }
        });

    }

    public void obtenerParticipantesEvento() {

        Call<List<ParticipanteEvento>> peticionRest = apiRest.obtenerParticipantesEvento(eventoActual.getId());
        peticionRest.enqueue(new Callback<List<ParticipanteEvento>>() {
            @Override
            public void onResponse(Call<List<ParticipanteEvento>> call, Response<List<ParticipanteEvento>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {

                    participanteEventos = response.body();
                    esParticipante = false;
                    apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    desapuntarParticipanteEvento.setVisibility(View.GONE);

                    if (participanteEventos == null || participanteEventos.isEmpty()) {
                        esParticipante = false;
                        apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                        desapuntarParticipanteEvento.setVisibility(View.GONE);
                    } else {
                        for (ParticipanteEvento participante : participanteEventos) {
                            if (participante.getId().equals(UsuarioActual.getInstance().getId())) {
                                esParticipante = true;
                                apuntarParticipanteEvento.setVisibility(View.GONE);
                                desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ParticipanteEvento>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });

    }

    private void registrarPartipanteEvento() {

        Call<Long> peticionRest = apiRest.addParticipanteEvento(eventoActual.getId());
        peticionRest.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    Long id = response.body();
                    esParticipante = true;
                    apuntarParticipanteEvento.setVisibility(View.GONE);
                    desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Registrado correctamente", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                        desapuntarParticipanteEvento.setVisibility(View.GONE);
                        esParticipante = false;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }

    private void eliminarParticipanteEvento() {

        Call<Long> peticionRest = apiRest.eliminarParticipanteEvento(eventoActual.getId(), UsuarioActual.getInstance().getId());
        peticionRest.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    Long id = response.body();
                    esParticipante = false;
                    apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    desapuntarParticipanteEvento.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Desapuntado correctamente", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        apuntarParticipanteEvento.setVisibility(View.GONE);
                        desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                        esParticipante = true;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evento_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabEventoInformacion();
                case 1:
                    return new TabEventoParticipantes(eventoActual.getId());
                case 2:
                    return new TabEventoForo(eventoActual);
                case 3:
                    return new TabEventoUbicacion(eventoActual.getId());
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof TabEventoParticipantes) {
                ((TabEventoParticipantes) object).update(1);
            }
            return super.getItemPosition(object);
        }


    }

    public void actualizarVisibilidadBotonRegistroParticipantes() {
        int tabSelected = tabLayout.getSelectedTabPosition();

        if (tabSelected > 1) {
            apuntarParticipanteEvento.setVisibility(View.GONE);
            desapuntarParticipanteEvento.setVisibility(View.GONE);
        } else {

            if ((tabSelected == 0 || tabSelected == 1)) {
                if (esParticipante != null && !esParticipante) {
                    apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    desapuntarParticipanteEvento.setVisibility(View.GONE);
                } else if (esParticipante != null && esParticipante) {
                    apuntarParticipanteEvento.setVisibility(View.GONE);
                    desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                } else {
                    //apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                }
            }

        }


    }

}
