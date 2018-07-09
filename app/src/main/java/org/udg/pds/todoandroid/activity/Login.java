package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.CustomTextWatcher;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.SnackbarUtil;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    // Interfície de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    private TextInputLayout tilCorreo;
    private TextInputLayout tilPassword;

    private EditText etCorreo;
    private EditText etPassword;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario actual está logeadoo
        if (UsuarioActual.getInstance().getId() != -1L) {
            Intent principal = new Intent(getApplicationContext(), Principal.class);
            // Eliminamos de la pila todas las actividades
            principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(principal);
            finish();
        }

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        // Cargamos layout del formulario de inicio de sesión.
        setContentView(R.layout.login_layout);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);


        tilCorreo = findViewById(R.id.login_til_correo);
        tilPassword = findViewById(R.id.login_til_password);
        etCorreo = findViewById(R.id.login_et_correo);
        etPassword = findViewById(R.id.login_et_password);

        addTextListeners();


        Button iniciarSesion = findViewById(R.id.login_boton_iniciar_sesion);
        Button crearCuenta = findViewById(R.id.login_boton_crear_cuenta);
        iniciarSesion.setOnClickListener(this);
        crearCuenta.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_boton_crear_cuenta:
                Intent intRegistro = new Intent(this, Registro.class);
                startActivity(intRegistro);
                break;
            case R.id.login_boton_iniciar_sesion:
                iniciarSesion();
                break;
        }


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
            iniciarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Validamos que los elementos del formulario no esten vacios
    private boolean validarFormularioLogin() {

        String correo = Objects.requireNonNull(tilCorreo.getEditText()).getText().toString();
        String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString();

        if (correo.isEmpty()) tilCorreo.setError(getString(R.string.login_correo_obligatorio));
        else tilCorreo.setError(null);

        if (password.isEmpty())
            tilPassword.setError(getString(R.string.login_contrasenya_obligatoria));
        else tilPassword.setError(null);

        return !correo.isEmpty() && !password.isEmpty();
    }


    private void iniciarSesion() {
        if (validarFormularioLogin()) {
            String tokenFireBase = FirebaseInstanceId.getInstance().getToken();
            UsuarioLoginPeticion datosLogin = new UsuarioLoginPeticion(Objects.requireNonNull(tilCorreo.getEditText()).getText().toString(), Objects.requireNonNull(tilPassword.getEditText()).getText().toString(), tokenFireBase);
            Call<UsuarioLoginRespuesta> peticionRest = apiRest.iniciarSesion(datosLogin);
            peticionRest.enqueue(new Callback<UsuarioLoginRespuesta>() {
                @Override
                public void onResponse(Call<UsuarioLoginRespuesta> call, Response<UsuarioLoginRespuesta> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {

                        UsuarioLoginRespuesta dadesResposta = response.body();

                        // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                        UsuarioActual.getInstance().setId(Objects.requireNonNull(dadesResposta).getId());
                        UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                        UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());

                        Intent principal = new Intent(getApplicationContext(), Principal.class);
                        // Eliminamos de la pila todas las actividades
                        principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(principal);
                        finish();

                    } else {
                        try {
                            limpiarFormulario();
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                        } catch (Exception e) {
                            Log.e(getString(R.string.log_error), e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UsuarioLoginRespuesta> call, Throwable t) {
                    limpiarFormulario();
                    Log.e(getString(R.string.log_error), t.getMessage());
                }
            });
        }
    }

    private void limpiarFormulario() {
        Objects.requireNonNull(tilCorreo.getEditText()).getText().clear();
        Objects.requireNonNull(tilPassword.getEditText()).getText().clear();
    }

    // Eliminamos el mensaje de error al introducir texto en un campo vacio
    private void addTextListeners() {

        etCorreo.addTextChangedListener(new CustomTextWatcher(tilCorreo));
        etPassword.addTextChangedListener(new CustomTextWatcher(tilPassword));

    }
}
