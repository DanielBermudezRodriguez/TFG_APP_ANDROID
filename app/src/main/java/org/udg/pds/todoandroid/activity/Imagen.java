package org.udg.pds.todoandroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.util.Global;

import java.util.Objects;

public class Imagen extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagen_layout);
        String urlImagen = Objects.requireNonNull(getIntent().getExtras()).getString(Global.URL_IMAGEN);
        ImageView imagen = findViewById(R.id.imagen_full_screen);
        Glide.with(getApplicationContext()).load(urlImagen).into(imagen);
    }
}
