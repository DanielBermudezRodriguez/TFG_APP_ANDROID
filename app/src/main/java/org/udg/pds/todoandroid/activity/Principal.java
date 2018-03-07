package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.MenuLateralFragment;

public class Principal extends AppCompatActivity implements MenuLateralFragment.NavigationDrawerCallbacks {

    // Fragment que controla los comportamientos, interacciones y vista del menú lateral
    private MenuLateralFragment mNavigationDrawerFragment;
    // guarda el último título de pantalla
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario está logeado
        if (UsuarioActual.getInstance().getId() == -1L){
            Intent main = new Intent(this, MainActivity.class);
            // Si no está logeado eliminamos de la pila la actividad Principal
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
        }
        setContentView(R.layout.activity_principal);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        mNavigationDrawerFragment = (MenuLateralFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Preparar el menú lateral
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Actualizar el contenido principal mediante la substitución de fragmentos o actividades segun la posición del menú lateral escojida
        System.out.println("Posición actual: "+ position);
        if (position == 0){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
        if (position == 1){
            Intent perfilUsuario = new Intent(getApplicationContext(), PerfilUsuario.class);
            startActivity(perfilUsuario);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    // Fragmento de marcador de posición
    public static class PlaceholderFragment extends Fragment {
        // Número de la posición de la sección del menú lateral
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        // Devuelve una nueva instancia del Fragment para el número de sección del menú
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Principal) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
