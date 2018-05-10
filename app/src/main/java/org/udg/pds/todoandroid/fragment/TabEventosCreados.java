package org.udg.pds.todoandroid.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.adapter.EventosCreadosAdapter;
import org.udg.pds.todoandroid.adapter.ParticipanteEventoAdapter;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_eventos_creados, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        recyclerView = rootView.findViewById(R.id.recyclerview_eventos_creados);
        recyclerView.setHasFixedSize(true);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        obtenerEventosCreados();

        return rootView;
    }

    // codigoTipoEventos = 0 recupera los eventos creados por el usuario actual
    // codigoTipoEventos = 1 recupera los eventos en que el usuario actual está apuntado
    private void obtenerEventosCreados() {
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(Global.CODE_EVENTOS_CREADOS);
        eventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    eventosCreados = response.body();
                    eventosCreadosAdapter = new EventosCreadosAdapter(getActivity().getApplicationContext(),eventosCreados);
                    recyclerView.setAdapter(eventosCreadosAdapter);

                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Error al obtener los eventos creados", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                Toast.makeText(getActivity().getApplicationContext(), "Error al obtener los eventos creados", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
