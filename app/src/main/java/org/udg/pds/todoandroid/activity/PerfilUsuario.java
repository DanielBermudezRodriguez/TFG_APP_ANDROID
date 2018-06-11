package org.udg.pds.todoandroid.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUsuario extends AppCompatActivity {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;
    // identificador usuario perfil
    private Long idUsuarioPerfil;
    // imagen perfil
    private ImageView imagenPerfilUsuario;
    // datos perfil
    private TextView nombrePerfilUsuario;
    private TextView apellidosPerfilUsuario;
    private TextView usernamePerfilUsuario;
    private TextView emailPerfilUsuario;
    private TextView ubicacionPerfilUsuario;
    private TextView deportesperfilUsuario;

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
        nombrePerfilUsuario = findViewById(R.id.perfil_usuario_nombre);
        apellidosPerfilUsuario = findViewById(R.id.perfil_usuario_apellidos);
        usernamePerfilUsuario = findViewById(R.id.perfil_usuario_username);
        emailPerfilUsuario = findViewById(R.id.perfil_usuario_email);
        ubicacionPerfilUsuario = findViewById(R.id.perfil_usuario_ubicacion);
        deportesperfilUsuario = findViewById(R.id.perfil_usuario_deportes_favoritos);

        // ObtenerPerfilUsuario
        obtenerPerfilUsuario();

    }

    private void obtenerPerfilUsuario() {
        Call<Usuario> call = apiRest.perfilUsuario(idUsuarioPerfil);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Usuario usuario = response.body();
                    if (usuario != null) {
                        RequestOptions options = new RequestOptions();
                        options.centerCrop();
                        Glide.with(getApplicationContext()).load(Global.BASE_URL + Global.IMAGE_USER + usuario.getId()).apply(options).into(imagenPerfilUsuario);
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

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editar_usuario, menu);
        return true;
    }

}
