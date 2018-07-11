package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import org.udg.pds.todoandroid.adapter.EventosRegistradoAdapter;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.UsuarioActual;
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


public class TabEventosRegistrado extends Fragment {

    private ApiRest apiRest;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<Evento> eventosRegistrado = new ArrayList<>();
    private EventosRegistradoAdapter eventosRegistradoAdapter;
    private TabLayout.Tab tabActual;
    private MisEventos.SectionsPagerAdapter pagerAdapter;
    // Refrescar recyclerview scroll hacia arriba
    private SwipeRefreshLayout swipeRefreshLayout;

    public TabEventosRegistrado() {
    }

    @SuppressLint("ValidFragment")
    public TabEventosRegistrado(TabLayout.Tab tabAt, MisEventos.SectionsPagerAdapter mSectionsPagerAdapter) {
        tabActual = tabAt;
        pagerAdapter = mSectionsPagerAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_eventos_registrado, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        recyclerView = rootView.findViewById(R.id.recyclerview_eventos_registrado);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        if (getActivity() != null)
            eventosRegistradoAdapter = new EventosRegistradoAdapter(getActivity().getApplicationContext(), eventosRegistrado, new EventosRegistradoAdapter.OnItemClickListener() {

                @Override
                public void visualizardetalleEvento(Evento e) {
                    Intent i = new Intent(getContext(), EventoDetalle.class);
                    i.putExtra(Global.KEY_SELECTED_EVENT, e);
                    i.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, e.getAdministrador().getId().equals(UsuarioActual.getInstance().getId()));
                    startActivity(i);
                }

                @Override
                public void desapuntarDelEvento(final Evento e, final int position) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.registro_dialog_desapuntar_evento_titulo));
                    builder.setMessage(getString(R.string.registro_dialog_desapuntar_evento_contenido))
                            .setPositiveButton(getString(R.string.dialogo_aceptar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    desapuntarUsuarioEvento(e.getId(), position);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialogo_cancelar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    builder.show();

                }

            });
        recyclerView.setAdapter(eventosRegistradoAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (eventosRegistrado != null)
                            eventosRegistrado.clear();
                        obtenerEventosRegistrado();
                    }
                });

        return rootView;
    }

    private void obtenerEventosRegistrado() {
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(UsuarioActual.getInstance().getId(), Global.CODE_EVENTOS_REGISTRADO);
        eventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    List<Evento> eventos = response.body();
                    eventosRegistrado.clear();
                    if (eventos != null)
                        eventosRegistrado.addAll(eventos);
                    updateTabTitle(eventosRegistrado.size());
                    eventosRegistradoAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(getString(R.string.log_error), t.getMessage(), t);
                if (getActivity() != null)
                    SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.mis_eventos_error_eventos_registrados), Snackbar.LENGTH_LONG, true);
            }
        });
    }

    private void desapuntarUsuarioEvento(Long idEvento, final int position) {
        final Call<GenericId> suspenderEvento = apiRest.eliminarParticipanteEvento(idEvento, UsuarioActual.getInstance().getId());
        suspenderEvento.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    if (getActivity() != null)
                        SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.mis_eventos_desapuntar_evento), Snackbar.LENGTH_LONG, false);
                    eventosRegistrado.remove(position);
                    recyclerView.removeViewAt(position);
                    eventosRegistradoAdapter.notifyItemRemoved(position);
                    eventosRegistradoAdapter.notifyItemRangeChanged(position, eventosRegistrado.size());
                    updateTabTitle(eventosRegistrado.size());

                } else
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        if (getActivity() != null)
                            SnackbarUtil.showSnackBar(getActivity().findViewById(android.R.id.content), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.i(getString(R.string.log_error), e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage(), t);
            }
        });
    }

    private void updateTabTitle(int totalEventosRegistrado) {
        if (tabActual == null || tabActual.getCustomView() == null) {
            if (getActivity() != null)
                tabActual.setCustomView(pagerAdapter.getTabView(totalEventosRegistrado, getActivity().getString(R.string.eventos_registrado)));
        } else {
            TextView totalEventos = tabActual.getCustomView().findViewById(R.id.tab_eventos_creados_total);
            totalEventos.setText(String.valueOf(totalEventosRegistrado));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        obtenerEventosRegistrado();
    }

}
