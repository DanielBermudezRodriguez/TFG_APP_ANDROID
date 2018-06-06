package org.udg.pds.todoandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.TabEventoForo;
import org.udg.pds.todoandroid.fragment.TabEventoInformacion;
import org.udg.pds.todoandroid.fragment.TabEventoParticipantes;
import org.udg.pds.todoandroid.fragment.TabEventoUbicacion;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class EventoDetalle extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Evento eventoActual;
    private ApiRest apiRest;
    private FloatingTextButton apuntarParticipanteEvento;
    private FloatingTextButton desapuntarParticipanteEvento;
    private Boolean esParticipante;
    private TabLayout tabLayout;
    private List<ParticipanteEvento> participanteEventos = new ArrayList<>();
    private Boolean esAdministradorEvento;
    private int posicionEventoAdapter = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evento_detalle_layout);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Obtenemos parametros pasados a la activity evento detalle
        if (getIntent().getExtras() != null) {
            eventoActual = (Evento) getIntent().getExtras().getSerializable(Global.KEY_SELECTED_EVENT);
            esAdministradorEvento = getIntent().getExtras().getBoolean(Global.KEY_SELECTED_EVENT_IS_ADMIN);
            posicionEventoAdapter = getIntent().getExtras().getInt(Global.KEY_SELECTED_EVENT_POSITION);
        }

        // Gestión visibilidad apuntar/desapuntar participante evento
        desapuntarParticipanteEvento = findViewById(R.id.eliminar_participante_evento);
        apuntarParticipanteEvento = findViewById(R.id.apuntar_participante_evento);
        apuntarParticipanteEvento.setVisibility(View.GONE);
        desapuntarParticipanteEvento.setVisibility(View.GONE);
        apuntarParticipanteEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPartipanteEvento();

            }
        });

        desapuntarParticipanteEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarParticipanteEvento();
            }
        });

        // Eventos cancelados, completos o finalizados
        obtenerParticipantesEvento();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Mostrar botón "atras" en action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    public void obtenerParticipantesEvento() {
        Call<List<ParticipanteEvento>> peticionRest = apiRest.obtenerParticipantesEvento(eventoActual.getId());
        peticionRest.enqueue(new Callback<List<ParticipanteEvento>>() {
            @Override
            public void onResponse(Call<List<ParticipanteEvento>> call, Response<List<ParticipanteEvento>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    participanteEventos = response.body();
                    esParticipante = false;
                    if (participanteEventos != null) {
                        for (ParticipanteEvento participante : participanteEventos) {
                            if (participante.getId().equals(UsuarioActual.getInstance().getId())) {
                                esParticipante = true;
                            }
                        }
                    }
                    if (!esAdministradorEvento) {
                        if (esParticipante) {
                            apuntarParticipanteEvento.setVisibility(View.GONE);
                            desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                        } else {
                            apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                            desapuntarParticipanteEvento.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ParticipanteEvento>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });

    }

    private void registrarPartipanteEvento() {

        Call<GenericId> peticionRest = apiRest.addParticipanteEvento(eventoActual.getId());
        peticionRest.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    esParticipante = true;
                    if (!esAdministradorEvento) {
                        apuntarParticipanteEvento.setVisibility(View.GONE);
                        desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    }
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    SnackbarUtil.showSnackBar(apuntarParticipanteEvento, getString(R.string.registrar_participante_evento), Snackbar.LENGTH_LONG, false);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(apuntarParticipanteEvento, jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    private void eliminarParticipanteEvento() {

        Call<GenericId> peticionRest = apiRest.eliminarParticipanteEvento(eventoActual.getId(), UsuarioActual.getInstance().getId());
        peticionRest.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    esParticipante = false;
                    if (!esAdministradorEvento) {
                        apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                        desapuntarParticipanteEvento.setVisibility(View.GONE);
                    }
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    SnackbarUtil.showSnackBar(desapuntarParticipanteEvento, getString(R.string.desapuntar_participante_evento), Snackbar.LENGTH_LONG, false);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(desapuntarParticipanteEvento, jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
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

    @Override
    public void onBackPressed() {
        obtenerInformacionEvento();
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        obtenerInformacionEvento();
        return false;
    }

    private void obtenerInformacionEvento() {

        Call<Evento> peticionEventos = apiRest.obtenerInformacionEvento(eventoActual.getId());
        peticionEventos.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Evento evento = response.body();
                    Intent intent = new Intent();
                    intent.putExtra(Global.PARAMETER_EVENTO_ADAPTER, evento);
                    intent.putExtra(Global.PARAMETER_POSICION_EVENTO_ADAPTER, posicionEventoAdapter);
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }


    // Gestión de los tabs del tablayout mediante viewPager
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabEventoInformacion(eventoActual.getId());
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
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof TabEventoParticipantes) {
                ((TabEventoParticipantes) object).update(1);
            }
            if (object instanceof TabEventoInformacion) {
                ((TabEventoInformacion) object).update(0);
            }
            if (object instanceof TabEventoForo) {
                ((TabEventoForo) object).update(esParticipante);
            }
            return super.getItemPosition(object);
        }


    }

    public Boolean esParticipante() {
        return esParticipante;
    }

    public void actualizarVisibilidadBotonRegistroParticipantes() {
        int tabSelected = tabLayout.getSelectedTabPosition();

        if (tabSelected > 1) {
            apuntarParticipanteEvento.setVisibility(View.GONE);
            desapuntarParticipanteEvento.setVisibility(View.GONE);
        } else if (!esAdministradorEvento) {
            if ((tabSelected == 0 || tabSelected == 1)) {
                if (esParticipante != null && !esParticipante) {
                    apuntarParticipanteEvento.setVisibility(View.VISIBLE);
                    desapuntarParticipanteEvento.setVisibility(View.GONE);
                } else if (esParticipante != null) {
                    apuntarParticipanteEvento.setVisibility(View.GONE);
                    desapuntarParticipanteEvento.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public Boolean getEsAdministradorEvento() {
        return esAdministradorEvento;
    }
}
