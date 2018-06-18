package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(UsuarioActual.getInstance().getId(),Global.CODE_EVENTOS_CREADOS);
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
                                suspenderEvento(e.getId());
                            }

                        });
                        recyclerView.setAdapter(eventosCreadosAdapter);
                    }

                } else
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity().getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                Toast.makeText(getActivity().getApplicationContext(), "Error al obtener los eventos creados", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity().getApplicationContext(), "El evento ha sido suspendido", Toast.LENGTH_SHORT).show();
                    obtenerEventosCreados(true);

                } else
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity().getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                Toast.makeText(getActivity().getApplicationContext(), "Error al cancelar el evento", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
