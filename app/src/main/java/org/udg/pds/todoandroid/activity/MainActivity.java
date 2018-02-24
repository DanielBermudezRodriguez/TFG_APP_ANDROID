package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;


public class MainActivity extends Activity implements View.OnClickListener{


    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario está logeado
        if (UsuarioActual.getInstance().getId() != -1L){
            Intent intentMain = new Intent(this, Login.class);
            startActivity(intentMain);
        }

        // Cargamos layout del menú principal
        setContentView(R.layout.menu_principal);

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

                Intent intLogin = new Intent(this, org.udg.pds.todoandroid.activity.Login.class);
                startActivity(intLogin);
                break;

            case R.id.boton_crear_cuenta:

                /*email = String.valueOf(entradaMail.getText());
                password = String.valueOf(entradaContrasenya.getText());

                if (!email.equals("") && !password.equals("")) {
                    peticioIniciSessio();
                }
                else {
                    Toast.makeText(this, "Omple els camps Nickname i Password", Toast.LENGTH_SHORT).show();
                    entradaMail.setText("");
                    entradaContrasenya.setText("");
                }*/
            case R.id.boton_salir:
                finish();
                break;
        }
    }


}