package org.udg.pds.todoandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

public class CrearEvento extends AppCompatActivity {

    private ApiRest apiRest;

    // Campos formulario
    private EditText tituloEvento;
    private EditText descripcionEvento;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_evento);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtener campos formulario
        tituloEvento = findViewById(R.id.crear_evento_titulo);
        descripcionEvento = findViewById(R.id.crear_evento_descripcion);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_crear_evento) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crear_evento, menu);
        return true;
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
