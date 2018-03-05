package org.udg.pds.todoandroid.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

public class PerfilUsuario extends Activity {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.perfil_usuario);
        // Inicializamos el servicio de APIRest de retrofit
        InitRetrofit retrofit = new InitRetrofit();
        retrofit.init();
        apiRest = retrofit.getApiRest();

        TextView email = (TextView) findViewById(R.id.perfil_usuario_email);
        TextView nick = (TextView) findViewById(R.id.perfil_usuario_nick);
        email.setText("hola");
        nick.setText("adios");

        // Mostrar botón "atras" en action bar
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }
}
