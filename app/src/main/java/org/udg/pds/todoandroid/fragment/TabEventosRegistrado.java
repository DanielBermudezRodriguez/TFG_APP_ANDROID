package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import org.udg.pds.todoandroid.adapter.EventosRegistradoAdapter;
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


public class TabEventosRegistrado extends Fragment {

    private ApiRest apiRest;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<Evento> eventosRegistrado = new ArrayList<Evento>();
    private EventosRegistradoAdapter eventosRegistradoAdapter;

    private TabLayout.Tab tabActual;
    private MisEventos.SectionsPagerAdapter pagerAdapter;

    public TabEventosRegistrado() {
    }

    @SuppressLint("ValidFragment")
    public TabEventosRegistrado(TabLayout.Tab tabAt, MisEventos.SectionsPagerAdapter mSectionsPagerAdapter) {
        tabActual = tabAt;
        pagerAdapter = mSectionsPagerAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_eventos_registrado, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        recyclerView = rootView.findViewById(R.id.recyclerview_eventos_registrado);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        return rootView;
    }

    private void obtenerEventosRegistrado(final boolean recargarVista) {
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(Global.CODE_EVENTOS_REGISTRADO);
        eventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    eventosRegistrado = response.body();

                    updateTabTitle(eventosRegistrado.size());

                    eventosRegistradoAdapter = new EventosRegistradoAdapter(getActivity().getApplicationContext(), eventosRegistrado, new EventosRegistradoAdapter.OnItemClickListener() {

                        @Override
                        public void visualizardetalleEvento(Evento e) {
                            Intent i = new Intent(getContext(), EventoDetalle.class);
                            i.putExtra(Global.KEY_SELECTED_EVENT, (Serializable) e);
                            i.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, e.getAdministrador().getId().equals(UsuarioActual.getInstance().getId()));
                            startActivity(i);
                        }

                        @Override
                        public void desapuntarDelEvento(Evento e, int position) {
                            desapuntarUsuarioEvento(e.getId(),position);
                        }

                    });
                    recyclerView.setAdapter(eventosRegistradoAdapter);
                    if (recargarVista) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(TabEventosRegistrado.this).attach(TabEventosRegistrado.this).commit();
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
                Toast.makeText(getActivity().getApplicationContext(), "Error al obtener los eventos registrados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void desapuntarUsuarioEvento(Long idEvento, final int position) {
        final Call<GenericId> suspenderEvento = apiRest.eliminarParticipanteEvento(idEvento, UsuarioActual.getInstance().getId());
        suspenderEvento.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    GenericId idEvento = response.body();
                    Toast.makeText(getActivity().getApplicationContext(), "Has sido desapuntado del evento", Toast.LENGTH_SHORT).show();
                    //obtenerEventosRegistrado(true);
                    eventosRegistrado.remove(position);
                    recyclerView.removeViewAt(position);
                    eventosRegistradoAdapter.notifyItemRemoved(position);
                    eventosRegistradoAdapter.notifyItemRangeChanged(position, eventosRegistrado.size());
                    updateTabTitle(eventosRegistrado.size());

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

    private void updateTabTitle(int totalEventosRegistrado) {
        if (tabActual == null || tabActual.getCustomView() == null) {
            tabActual.setCustomView(pagerAdapter.getTabView(totalEventosRegistrado, getActivity().getString(R.string.eventos_registrado)));
        } else {
            TextView totalEventos = tabActual.getCustomView().findViewById(R.id.tab_eventos_creados_total);
            totalEventos.setText(String.valueOf(totalEventosRegistrado));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        obtenerEventosRegistrado(false);
    }

}
