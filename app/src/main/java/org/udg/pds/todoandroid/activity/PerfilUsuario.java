package org.udg.pds.todoandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.adapter.EventosCreadosAdapter;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.DialogConfirmActionFragment;
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

public class PerfilUsuario extends AppCompatActivity implements View.OnClickListener, DialogConfirmActionFragment.DialogConfirmActionFragmentListener {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;
    // identificador usuario perfil
    private Long idUsuarioPerfil;
    // identificador del evento a cancelar
    private Long eventoCancelar;
    // imagen perfil
    private ImageView imagenPerfilUsuario;
    // datos perfil
    private TextView nombrePerfilUsuario;
    private TextView apellidosPerfilUsuario;
    private TextView usernamePerfilUsuario;
    private TextView emailPerfilUsuario;
    private TextView ubicacionPerfilUsuario;
    private TextView deportesperfilUsuario;
    // Eventos creados
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Evento> eventosCreados = new ArrayList<Evento>();
    private EventosCreadosAdapter eventosCreadosAdapter;
    // Usuario actual
    private Usuario usuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.perfil_usuario_layout);

        // Id del usuario a obtener información del perfil
        idUsuarioPerfil = Objects.requireNonNull(getIntent().getExtras()).getLong(Global.ID_USUARIO_PERFIL);

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Inicializar elementos layout
        imagenPerfilUsuario = findViewById(R.id.perfil_usuario_imagen);
        imagenPerfilUsuario.setOnClickListener(this);
        nombrePerfilUsuario = findViewById(R.id.perfil_usuario_nombre);
        apellidosPerfilUsuario = findViewById(R.id.perfil_usuario_apellidos);
        usernamePerfilUsuario = findViewById(R.id.perfil_usuario_username);
        emailPerfilUsuario = findViewById(R.id.perfil_usuario_email);
        ubicacionPerfilUsuario = findViewById(R.id.perfil_usuario_ubicacion);
        deportesperfilUsuario = findViewById(R.id.perfil_usuario_deportes_favoritos);

        // Eventos creados recyclerview
        recyclerView = findViewById(R.id.recyclerview_perfil_usuario_eventos_creados);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventosCreadosAdapter = new EventosCreadosAdapter(getApplicationContext(), eventosCreados, new EventosCreadosAdapter.OnItemClickListener() {

            @Override
            public void visualizardetalleEvento(Evento e) {
                Intent i = new Intent(getApplicationContext(), EventoDetalle.class);
                i.putExtra(Global.KEY_SELECTED_EVENT, e);
                i.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, e.getAdministrador().getId().equals(UsuarioActual.getInstance().getId()));
                startActivity(i);
            }

            @Override
            public void cancelarEvento(Evento e, int position) {
                DialogConfirmActionFragment accion = new DialogConfirmActionFragment(getString(R.string.registro_dialog_cancelar_evento_titulo), getString(R.string.registro_dialog_cancelar_evento_contenido));
                accion.show(PerfilUsuario.this.getFragmentManager(), "");
                eventoCancelar = e.getId();
            }

        });
        recyclerView.setAdapter(eventosCreadosAdapter);

        // ObtenerPerfilUsuario
        obtenerPerfilUsuario();

        // Obtener eventos creados
        obtenerEventosCreados();

    }

    @Override
    public void accionSeleccionada(boolean accion) {
        if (accion) suspenderEvento(eventoCancelar);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // Mostrar imagen perfil usuario
            case R.id.perfil_usuario_imagen:
                if (getApplicationContext() != null && idUsuarioPerfil != null) {
                    // Obtener nombre imagen usuario actual para completar la URL
                    final Call<String> nombreImagen = apiRest.nombreImagenUsuario(usuario.getId());
                    nombreImagen.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                String imagenNombre = response.body();
                                Intent imagenIntent = new Intent(getApplicationContext(), Imagen.class);
                                imagenIntent.putExtra(Global.URL_IMAGEN, Global.BASE_URL + Global.IMAGE_USER + idUsuarioPerfil + "/" + imagenNombre);
                                startActivity(imagenIntent);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(getString(R.string.log_error), t.getMessage());
                        }
                    });
                }
                break;
        }

    }

    private void obtenerPerfilUsuario() {
        Call<Usuario> call = apiRest.perfilUsuario(idUsuarioPerfil);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    usuario = response.body();
                    if (usuario != null) {
                        // Obtener nombre imagen usuario actual para completar la URL
                        final Call<String> nombreImagen = apiRest.nombreImagenUsuario(usuario.getId());
                        nombreImagen.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                    String imagenNombre = response.body();
                                    RequestOptions options = new RequestOptions().centerCrop();
                                    Glide.with(getApplicationContext()).load(Global.BASE_URL + Global.IMAGE_USER + usuario.getId() + "/" + imagenNombre).apply(options).into(imagenPerfilUsuario);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e(getString(R.string.log_error), t.getMessage());
                            }
                        });

                        nombrePerfilUsuario.setText(usuario.getNombre());
                        apellidosPerfilUsuario.setText(usuario.getApellidos());
                        usernamePerfilUsuario.setText(usuario.getUsername());
                        emailPerfilUsuario.setText(usuario.getEmail());
                        ubicacionPerfilUsuario.setText(usuario.getMunicipio().getMunicipio());

                        StringBuilder deportesFavoritos = new StringBuilder();
                        if (usuario.getDeportesFavoritos() != null && !usuario.getDeportesFavoritos().isEmpty()) {
                            for (Deporte d : usuario.getDeportesFavoritos()) {
                                deportesFavoritos.append(d.getDeporte()).append(", ");
                            }
                            deportesFavoritos = new StringBuilder(deportesFavoritos.substring(0, deportesFavoritos.length() - 2));
                            deportesperfilUsuario.setText(deportesFavoritos.toString());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    // codigoTipoEventos = 0 recupera los eventos creados por el usuario actual
    // codigoTipoEventos = 1 recupera los eventos en que el usuario actual está apuntado
    private void obtenerEventosCreados() {
        final Call<List<Evento>> eventos = apiRest.eventosUsuario(idUsuarioPerfil, Global.CODE_EVENTOS_CREADOS);
        eventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    List<Evento> eventos = response.body();
                    eventosCreados.clear();
                    if (eventos != null)
                        eventosCreados.addAll(eventos);
                    eventosCreadosAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage(), t);
            }
        });
    }

    private void suspenderEvento(Long idEvento) {
        final Call<GenericId> suspenderEvento = apiRest.suspenderEvento(idEvento);
        suspenderEvento.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    SnackbarUtil.showSnackBar(findViewById(android.R.id.content), "El evento ha sido suspendido", Snackbar.LENGTH_LONG, false);
                    obtenerEventosCreados();

                } else
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(findViewById(android.R.id.content), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage(), t);
                SnackbarUtil.showSnackBar(findViewById(android.R.id.content), "Error al cancelar el evento", Snackbar.LENGTH_LONG, true);
            }
        });
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_modificar_perfil) {
            Intent modificarPerfil = new Intent(getApplicationContext(), ModificarPerfil.class);
            modificarPerfil.putExtra(Global.KEY_ACTUAL_USER, usuario);
            startActivity(modificarPerfil);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Si es el perfil del usuario actual activamos icono para modificar el perfil
        if (UsuarioActual.getInstance().getId().equals(idUsuarioPerfil))
            getMenuInflater().inflate(R.menu.editar_usuario, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // ObtenerPerfilUsuario
        obtenerPerfilUsuario();
        // Obtener eventos creados
        obtenerEventosCreados();
    }

}
