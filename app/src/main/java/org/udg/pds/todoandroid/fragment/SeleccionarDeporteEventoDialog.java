package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Municipio;

import java.util.List;

public class SeleccionarDeporteEventoDialog extends DialogFragment {

    private CharSequence[] deportes;
    private int deporteActual;
    SeleccionarDeporteEventoDialog.SeleccionarDeporteEventoDialogListener mListener;

    public interface SeleccionarDeporteEventoDialogListener {
        void deporteSeleccionado(int deporteSeleccionado);
    }

    public SeleccionarDeporteEventoDialog(){

    }

    @SuppressLint("ValidFragment")
    public SeleccionarDeporteEventoDialog(List<Deporte> deportes, int deporteActual){
        int i = 0;
        this.deportes = new CharSequence[deportes.size()];
        for (Deporte d : deportes){
            this.deportes[i] = d.getDeporte();
            i++;
        }
        this.deporteActual = deporteActual;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escojer Deporte:");
        builder.setSingleChoiceItems(this.deportes,deporteActual,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int deporteSeleccionado) {
                deporteActual = deporteSeleccionado;
            }

        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.deporteSeleccionado(deporteActual);
            }
        });

        return builder.create();
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SeleccionarDeporteEventoDialog.SeleccionarDeporteEventoDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
