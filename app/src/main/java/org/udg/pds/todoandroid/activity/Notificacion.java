package org.udg.pds.todoandroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Notificaciones;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notificacion extends AppCompatActivity {

    private ApiRest apiRest;
    // Switchs notificaciones
    private Switch altaUsuario;
    private Switch bajaUsuario;
    private Switch eventoCancelado;
    private Switch datosModificados;
    private Switch usuarioEliminado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notificacion_layout);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Mostrar botón "atras" en action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        altaUsuario = findViewById(R.id.notificacion_switch_nuevo_usuario);
        bajaUsuario = findViewById(R.id.notificacion_switch_baja_usuario);
        eventoCancelado = findViewById(R.id.notificacion_switch_evento_cancelado);
        datosModificados = findViewById(R.id.notificacion_switch_evento_modificado);
        usuarioEliminado = findViewById(R.id.notificacion_switch_evento_desapuntado);

        // obtener configuracion inicial notificaciones
        obtenerConfiguracionNotificaciones();

    }

    private void guardarConfiguracionNotificaciones(){
        Notificaciones configuracionActual = new Notificaciones(altaUsuario.isChecked(),bajaUsuario.isChecked(),eventoCancelado.isChecked(),datosModificados.isChecked(),usuarioEliminado.isChecked());
        Call<Notificaciones> configuracionNotificaciones = apiRest.guardarConfiguracionNotificaciones(configuracionActual);
        configuracionNotificaciones.enqueue(new Callback<Notificaciones>() {
            @Override
            public void onResponse(Call<Notificaciones> call, Response<Notificaciones> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Notificaciones configuracion = response.body();
                }
            }

            @Override
            public void onFailure(Call<Notificaciones> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    private void obtenerConfiguracionNotificaciones() {
        Call<Notificaciones> configuracionNotificaciones = apiRest.obtenerConfiguracionNotificaciones();
        configuracionNotificaciones.enqueue(new Callback<Notificaciones>() {
            @Override
            public void onResponse(Call<Notificaciones> call, Response<Notificaciones> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Notificaciones configuracion = response.body();
                    if (configuracion != null) {
                        altaUsuario.setChecked(configuracion.getAltaUsuario());
                        bajaUsuario.setChecked(configuracion.getBajaUsuario());
                        eventoCancelado.setChecked(configuracion.getEventoCancelado());
                        datosModificados.setChecked(configuracion.getDatosModificados());
                        usuarioEliminado.setChecked(configuracion.getUsuarioEliminado());

                        altaUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                guardarConfiguracionNotificaciones();
                            }
                        });
                        bajaUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                guardarConfiguracionNotificaciones();
                            }
                        });
                        eventoCancelado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                guardarConfiguracionNotificaciones();
                            }
                        });
                        datosModificados.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                guardarConfiguracionNotificaciones();
                            }
                        });
                        usuarioEliminado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                guardarConfiguracionNotificaciones();
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<Notificaciones> call, Throwable t) {
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

}
