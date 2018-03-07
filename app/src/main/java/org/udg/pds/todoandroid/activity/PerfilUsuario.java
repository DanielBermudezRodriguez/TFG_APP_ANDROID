package org.udg.pds.todoandroid.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

public class PerfilUsuario extends AppCompatActivity {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.perfil_usuario);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Inicializamos el servicio de APIRest de retrofit
        InitRetrofit retrofit = new InitRetrofit();
        retrofit.init();
        apiRest = retrofit.getApiRest();

        TextView email = (TextView) findViewById(R.id.perfil_usuario_email);
        TextView nick = (TextView) findViewById(R.id.perfil_usuario_nick);
        email.setText("hola");
        nick.setText("adios");

        // Mostrar botón "atras" en toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editar_usuario, menu);
        return true;
    }

}
