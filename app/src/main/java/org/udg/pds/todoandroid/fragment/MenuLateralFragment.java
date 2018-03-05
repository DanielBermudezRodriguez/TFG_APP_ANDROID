package org.udg.pds.todoandroid.fragment;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.adapter.NavigationDrawerAdapter;
import org.udg.pds.todoandroid.entity.UsuarioActual;


// Fragment que controla los comportamientos, interacciones y vista del menú lateral
public class MenuLateralFragment extends Fragment {

    // Posición ítem del menú
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    // preferencias menú lateral
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    // Componente auxiliar que ata la barra de acción al panel de navegación.
    private ActionBarDrawerToggle iconoMenuLateral;
    private DrawerLayout menuLateral;
    private ListView listView;
    private View vistaMenuLateral;
    private int posicionActual = 0;
    private boolean mFromSavedInstanceState;
    private boolean usuarioAbrioMenu;
    // Interficie con las llamdas que tiene que utilizar las actividades contenedoras
    private NavigationDrawerCallbacks mCallbacks;
    // Adapter menú lateral
    private NavigationDrawerAdapter mNavigationDrawerAdapter;

    // Interficie con las llamdas que tiene que utilizar las actividades contenedoras
    public static interface NavigationDrawerCallbacks {
        // Se llama cuando un elemento del menú es seleccionado
        void onNavigationDrawerItemSelected(int position);
    }

    public MenuLateralFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        usuarioAbrioMenu = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            posicionActual = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        // Selecciona el item último o el por defecto
        selectItem(posicionActual);
    }

     // Usuarios de este Fragment tienen que llamar a este método para preparar el menú lateral
     // @param fragmentId   The android:id de este fragment en el layout de la actividad.
     // @param drawerLayout The DrawerLayout contenedor del fragment UI.
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        vistaMenuLateral = getActivity().findViewById(fragmentId);
        menuLateral = drawerLayout;

        // Establecer una sombra personalizada que se superpone al contenido principal cuando se abre el cajón
        menuLateral.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Preparar ActionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle vincula las interacciones adecuadas entre el cajón de navegación y el icono de la aplicación de la barra de acciones.
        iconoMenuLateral = new ActionBarDrawerToggle(
                getActivity(),
                menuLateral,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!usuarioAbrioMenu) {
                    // El usuario abrió manualmente el cajón; almacene esta bandera para evitar que se muestre automáticamente automáticamente el cajón de navegación en el futuro.
                    usuarioAbrioMenu = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        // Si el usuario no ha abierto el menú, ábralo para introducirlo en el cajón, según las pautas de diseño del cajón de navegación.
        if (!usuarioAbrioMenu && !mFromSavedInstanceState) {
            menuLateral.openDrawer(vistaMenuLateral);
        }

        // El código diferido depende de la restauración del estado de instancia anterior.
        menuLateral.post(new Runnable() {
            @Override
            public void run() {
                iconoMenuLateral.syncState();
            }
        });

        menuLateral.setDrawerListener(iconoMenuLateral);
    }

    private void selectItem(int position) {
        posicionActual = position;
        if (listView != null) {
            listView.setItemChecked(position, true);
        }
        if (menuLateral != null) {
            menuLateral.closeDrawer(vistaMenuLateral);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicar si este fragmento influye en el conjunto de acciones en la barra de acciones.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(
                R.layout.drawer_principal, container, false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mNavigationDrawerAdapter = mNavigationDrawerAdapter == null ? new NavigationDrawerAdapter(getActivity()) : mNavigationDrawerAdapter;

        View myHeader = inflater.inflate(R.layout.cabecera_menu_lateral, container, false);
        // Obtenemos información del usuario actual logeado
        if (UsuarioActual.getInstance().getId() != -1L){
            TextView username = myHeader.findViewById(R.id.username_menu_lateral);
            TextView email = myHeader.findViewById(R.id.email_menu_lateral);
            username.setText(UsuarioActual.getInstance().getUsername());
            email.setText(UsuarioActual.getInstance().getMail());
        }
        listView.addHeaderView(myHeader);
        listView.setAdapter(mNavigationDrawerAdapter);
        listView.setItemChecked(posicionActual, true);
        return listView;
    }

    public boolean isDrawerOpen() {
        return menuLateral != null && menuLateral.isDrawerOpen(vistaMenuLateral);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, posicionActual);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Adelante la nueva configuración del componente de alternar del cajón.
        iconoMenuLateral.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (menuLateral != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (iconoMenuLateral.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

}