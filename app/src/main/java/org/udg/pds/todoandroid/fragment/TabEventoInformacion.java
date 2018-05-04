package org.udg.pds.todoandroid.fragment;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.service.ApiRest;


public class TabEventoInformacion extends Fragment {

    private ApiRest apiRest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_informacion_evento , container , false);

        ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();

        return rootView;
    }


}
