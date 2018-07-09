package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.activity.MisEventos;
import org.udg.pds.todoandroid.adapter.EventosCreadosAdapter;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.SnackbarUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabEventosCreados extends Fragment {

    private ApiRest apiRest;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<Evento> eventosCreados = new ArrayList<Evento>();
    private EventosCreadosAdapter eventosCreadosAdapter;
    // identificador del evento a cancelar
    private Long eventoCancelar;
    private TabLayout.Tab tabActual;
    private MisEventos.SectionsPagerAdapter pagerAdapter;

    public TabEventosCreados() {
    }

    @SuppressLint("ValidFragment")
    public TabEventosCreados(TabLayout.Tab tabAt, MisEventos.SectionsPagerAdapter mSectionsPagerAdapter) {
        tabActual = tabAt;
        pagerAdapter = mSectionsPagerAdapter;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_eventos_creados, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        recyclerView = rootView.findViewById(R.id.recyclerview_eventos_creados);
        recyclerView.setHasFixedSize(true);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        obtenerEventosCreados(false);

        return rootView;
    }

    // codigoTipoEventos = 0 recupera los eventos creados por el usuario actual
    // codigoTipoEventos = 1 recupera los eventos en que el usuario actual está apuntado
    private void obtenerEventosCreados(final boolean recargarVista) {
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(UsuarioActual.getInstance().getId(), Global.CODE_EVENTOS_CREADOS);
        eventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    eventosCreados = response.body();
                    if (recargarVista) {
                        eventosCreadosAdapter.actualizarEventos(eventosCreados);
                        updateTabTitle(eventosCreados.size());
                    } else {
                        updateTabTitle(eventosCreados.size());

                        eventosCreadosAdapter = new EventosCreadosAdapter(getActivity().getApplicationContext(), eventosCreados, new EventosCreadosAdapter.OnItemClickListener() {

                            @Override
                            public void visualizardetalleEvento(Evento e) {
                                Intent i = new Intent(getContext(), EventoDetalle.class);
                                i.putExtra(Global.KEY_SELECTED_EVENT, (Serializable) e);
                                i.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, e.getAdministrador().getId().equals(UsuarioActual.getInstance().getId()));
                                startActivity(i);
                            }

                            @Override
                            public void cancelarEvento(Evento e, int position) {
                                eventoCancelar = e.getId();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getString(R.string.registro_dialog_cancelar_evento_titulo));
                                builder.setMessage(getString(R.string.registro_dialog_cancelar_evento_contenido))
                                        .setPositiveButton(getString(R.string.dialogo_aceptar), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                suspenderEvento(eventoCancelar);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.dialogo_cancelar), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                builder.show();
                            }

                        });
                        recyclerView.setAdapter(eventosCreadosAdapter);
                    }

                } else
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
            }
        });
    }


    private void updateTabTitle(int totalEventosCreados) {
        if (tabActual == null || tabActual.getCustomView() == null) {
            tabActual.setCustomView(pagerAdapter.getTabView(totalEventosCreados, getActivity().getString(R.string.eventos_creados)));
        } else {
            TextView totalEventos = tabActual.getCustomView().findViewById(R.id.tab_eventos_creados_total);
            totalEventos.setText(String.valueOf(totalEventosCreados));
        }

    }

    private void suspenderEvento(Long idEvento) {
        final Call<GenericId> suspenderEvento = apiRest.suspenderEvento(idEvento);
        suspenderEvento.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    GenericId idEvento = response.body();
                    SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), "El evento ha sido suspendido", Snackbar.LENGTH_LONG, false);
                    obtenerEventosCreados(true);

                } else
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), "Error al cancelar el evento", Snackbar.LENGTH_LONG, true);
            }
        });
    }


}
