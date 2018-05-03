package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

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

public class Login extends AppCompatActivity implements View.OnClickListener  {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    private TextView crearCuenta;

    private EditText mail;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargamos layout del formulario de inicio de sesión.
        setContentView(R.layout.login_constraint_layout);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        crearCuenta = findViewById(R.id.texto_login_crear_cuenta);
        crearCuenta.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.texto_login_crear_cuenta:
                Intent intRegistro = new Intent(this, Registro.class);
                startActivity(intRegistro);
                break;
        }


    }

    private boolean validarFormularioLogin() {
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
            mail = findViewById(R.id.texto_login_mail);
            password = findViewById(R.id.texto_login_password);
            if (validarFormularioLogin()){
                String tokenFireBase = FirebaseInstanceId.getInstance().getToken();
                UsuarioLoginPeticion datosLogin = new UsuarioLoginPeticion(mail.getText().toString(), password.getText().toString(),tokenFireBase);
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
                                limpiarFormulario();
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(getApplicationContext(),jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.i("ERROR:", e.getMessage());
                            }
                        }
                    }
                    @Override
                    public void onFailure (Call <UsuarioLoginRespuesta> call, Throwable t){
                        limpiarFormulario();
                        Log.i("ERROR:", t.getMessage());
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void limpiarFormulario (){
        mail.getText().clear();
        password.getText().clear();
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
