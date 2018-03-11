package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity  {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargamos layout del formulario de inicio de sesión.
        setContentView(R.layout.login);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Inicializamos el servicio de APIRest de retrofit
        InitRetrofit retrofit = new InitRetrofit();
        retrofit.init();
        apiRest = retrofit.getApiRest();
        //Button botonLogin = (Button) findViewById(R.id.boton_iniciar_sesion);
        // Listener cuando el usuario pulse el botón de Login
        //botonLogin.setOnClickListener(this);
        // Mostrar botón "atras" en action bar
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /*@Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boton_iniciar_sesion:
                EditText mail = (EditText) findViewById(R.id.texto_login_mail);
                EditText password = (EditText) findViewById(R.id.texto_login_password);
                if (validarFormularioLogin(mail, password)){
                    UsuarioLoginPeticion datosLogin = new UsuarioLoginPeticion(mail.getText().toString(), password.getText().toString());
                    Call<UsuarioLoginRespuesta> peticionRest = apiRest.iniciarSesion(datosLogin);
                    peticionRest.enqueue(new Callback<UsuarioLoginRespuesta>() {
                        @Override
                        public void onResponse(Call<UsuarioLoginRespuesta> call, Response<UsuarioLoginRespuesta> response) {
                            if (response.raw().code()!=500 && response.isSuccessful()) {

                                UsuarioLoginRespuesta dadesResposta = response.body();

                                // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                                UsuarioActual.getInstance().setId(dadesResposta.getId());
                                UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                                UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());

                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                // Eliminamos de la pila todas las actividades
                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(principal);

                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getApplicationContext(),jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.i("ERROR:", e.getMessage());
                                }
                            }
                        }
                        @Override
                        public void onFailure (Call <UsuarioLoginRespuesta> call, Throwable t){
                            Log.i("ERROR:", t.getMessage());
                        }
                    });
                }
                break;
                }
        }*/

    private boolean validarFormularioLogin(EditText mail, EditText password) {
        boolean esCorrecto = true;
        if (mail == null || TextUtils.isEmpty(mail.getText().toString())) {
            mail.setError(getString(R.string.mail_vacio_validacion));
            mail.requestFocus();
            esCorrecto = false;
        }
        if (password == null || TextUtils.isEmpty(password.getText().toString())) {
            password.setError(getString(R.string.password_vacio_validacion));
            password.requestFocus();
            esCorrecto = false;
        }
        return esCorrecto;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iniciar_sesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_iniciar_sesion) {
            EditText mail = (EditText) findViewById(R.id.texto_login_mail);
            EditText password = (EditText) findViewById(R.id.texto_login_password);
            if (validarFormularioLogin(mail, password)){
                UsuarioLoginPeticion datosLogin = new UsuarioLoginPeticion(mail.getText().toString(), password.getText().toString());
                Call<UsuarioLoginRespuesta> peticionRest = apiRest.iniciarSesion(datosLogin);
                peticionRest.enqueue(new Callback<UsuarioLoginRespuesta>() {
                    @Override
                    public void onResponse(Call<UsuarioLoginRespuesta> call, Response<UsuarioLoginRespuesta> response) {
                        if (response.raw().code()!=500 && response.isSuccessful()) {

                            UsuarioLoginRespuesta dadesResposta = response.body();

                            // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                            UsuarioActual.getInstance().setId(dadesResposta.getId());
                            UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                            UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());

                            Intent principal = new Intent(getApplicationContext(), Principal.class);
                            // Eliminamos de la pila todas las actividades
                            principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(principal);

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(getApplicationContext(),jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.i("ERROR:", e.getMessage());
                            }
                        }
                    }
                    @Override
                    public void onFailure (Call <UsuarioLoginRespuesta> call, Throwable t){
                        Log.i("ERROR:", t.getMessage());
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
