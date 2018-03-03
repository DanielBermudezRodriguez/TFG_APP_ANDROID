package org.udg.pds.todoandroid.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends Activity  implements View.OnClickListener {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cargamos layout formulario de registro
        setContentView(R.layout.registrar_usuario);
        // Inicializamos el servicio de APIRest de retrofit
        InitRetrofit retrofit = new InitRetrofit();
        retrofit.init();
        apiRest = retrofit.getApiRest();
        Button botonLogin = (Button) findViewById(R.id.boton_crear_cuenta);
        // Listener cuando el usuario pulse el botón de Login
        botonLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boton_crear_cuenta:
                EditText nombre = (EditText) findViewById(R.id.texto_registro_nombre);
                EditText apellidos = (EditText) findViewById(R.id.texto_registro_apellidos);
                EditText telefono = (EditText) findViewById(R.id.texto_registro_telefono);
                EditText username = (EditText) findViewById(R.id.texto_registro_nick);
                EditText email = (EditText) findViewById(R.id.texto_registro_email);
                EditText password1 = (EditText) findViewById(R.id.texto_registro_password_1);
                EditText password2 = (EditText) findViewById(R.id.texto_registro_password_2);
                if (validarFormularioRegistro(nombre,apellidos,telefono,username,email,password1,password2)){
                    UsuarioRegistroPeticion datosRegistro = new UsuarioRegistroPeticion(nombre.getText().toString(),apellidos.getText().toString(),telefono.getText().toString(),username.getText().toString(),email.getText().toString(),password1.getText().toString());
                    Call<UsuarioRegistroRespuesta> peticionRest = apiRest.registrar(datosRegistro);


                    peticionRest.enqueue(new Callback<UsuarioRegistroRespuesta>() {
                        @Override
                        public void onResponse(Call<UsuarioRegistroRespuesta> call, Response<UsuarioRegistroRespuesta> response) {
                            if (response.raw().code()!=500 && response.isSuccessful()) {

                                UsuarioRegistroRespuesta dadesResposta = response.body();

                                // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                                UsuarioActual.getInstance().setId(dadesResposta.getId());
                                UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                                UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());

                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                // Eliminamos de la pila la actividad de Login
                                finish();
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
                        public void onFailure (Call <UsuarioRegistroRespuesta> call, Throwable t){
                            Log.i("ERROR:", t.getMessage());
                        }
                    });

                }
                break;
        }
    }

    private boolean validarFormularioRegistro(EditText nombre, EditText apellidos, EditText telefono, EditText username, EditText email, EditText password1, EditText password2) {
        boolean esCorrecto = true;
        if (nombre == null || TextUtils.isEmpty(nombre.getText().toString())) {
            nombre.setError(getString(R.string.nombre_registro_validacion));
            nombre.requestFocus();
            esCorrecto = false;
        }
        if (apellidos == null || TextUtils.isEmpty(apellidos.getText().toString())) {
            apellidos.setError(getString(R.string.apellidos_registro_validacion));
            apellidos.requestFocus();
            esCorrecto = false;
        }
        if (telefono == null || TextUtils.isEmpty(telefono.getText().toString())) {
            telefono.setError(getString(R.string.telefono_registro_validacion));
            telefono.requestFocus();
            esCorrecto = false;
        }
        if (username == null || TextUtils.isEmpty(username.getText().toString())) {
            username.setError(getString(R.string.nick_registro_validacion));
            username.requestFocus();
            esCorrecto = false;
        }
        if (email == null || TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.email_registro_validacion));
            email.requestFocus();
            esCorrecto = false;
        }
        if (password1 == null || TextUtils.isEmpty(password1.getText().toString())) {
            password1.setError(getString(R.string.password_registro_validacion));
            password1.requestFocus();
            esCorrecto = false;
        }
        if (password2 == null || TextUtils.isEmpty(password2.getText().toString())) {
            password2.setError(getString(R.string.password_registro_validacion));
            password2.requestFocus();
            esCorrecto = false;
        }
        if (password1 != null && password2 != null && !TextUtils.isEmpty(password1.getText().toString()) && !TextUtils.isEmpty(password2.getText().toString())){
            if (!password1.getText().toString().equals(password2.getText().toString())){
                password1.setText("");
                password2.setText("");
                password1.setError(getString(R.string.password_diferentes_registro_validacion));
                password1.requestFocus();
                esCorrecto = false;
            }
        }
        return esCorrecto;

    }
}
