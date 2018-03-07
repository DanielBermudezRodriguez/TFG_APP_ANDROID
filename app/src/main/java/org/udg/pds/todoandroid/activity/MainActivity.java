package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario está logeado
        if (UsuarioActual.getInstance().getId() != -1L){
            Intent principal = new Intent(this, Principal.class);
            // Eliminamos de la pila la actividad Main
            finish();
            startActivity(principal);
        }

        // Cargamos layout del menú principal
        setContentView(R.layout.menu_principal);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        Button botonIniciarSesion = (Button) findViewById(R.id.boton_inicio_sesion);
        Button botonCrearCuenta = (Button) findViewById(R.id.boton_crear_cuenta);
        Button botonSalir = (Button) findViewById(R.id.boton_salir);

        // Listener cuando el usuario pulse el botón de Login
        botonIniciarSesion.setOnClickListener(this);

        // Listener cuando el usuario pulse el botón de crear cuenta
        botonCrearCuenta.setOnClickListener(this);

        // Listener cuando el usuario pulse el botón de salir
        botonSalir.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boton_inicio_sesion:
                Intent intLogin = new Intent(this, Login.class);
                startActivity(intLogin);
                break;
            case R.id.boton_crear_cuenta:
                Intent intRegistro = new Intent(this, Registro.class);
                startActivity(intRegistro);
                break;
            case R.id.boton_salir:
                finish();
                break;
        }
    }


}