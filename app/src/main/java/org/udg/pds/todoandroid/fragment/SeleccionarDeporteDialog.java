package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.udg.pds.todoandroid.entity.Deporte;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarDeporteDialog extends DialogFragment {

    private CharSequence[] deportes;
    private List<Long> deportesSeleccionados = new ArrayList<Long>();
    private boolean[] seleccionados;
    SeleccionarDeporteDialogListener mListener;

    public interface SeleccionarDeporteDialogListener{
        void deportesSeleccionados(List<Long> deportes);
    }

    public SeleccionarDeporteDialog(){

    }

    @SuppressLint("ValidFragment")
    public SeleccionarDeporteDialog(List<Deporte> deportes , List<Long> deportesSeleccionados){
        int i = 0;
        this.deportes = new CharSequence[deportes.size()];
        this.seleccionados = new boolean[deportes.size()];
        for (Deporte d : deportes){
            this.deportes[i] = d.getDeporte();
            this.seleccionados[i] = deportesSeleccionados.contains(Long.valueOf(i));
            i++;
        }
        this.deportesSeleccionados = deportesSeleccionados;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escojer Deportes:");
        builder.setMultiChoiceItems(deportes,seleccionados,new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                if (isChecked) {
                    deportesSeleccionados.add((long) which);
                    seleccionados[which] = true;
                } else if (deportesSeleccionados.contains((long)which)) {
                    deportesSeleccionados.remove(Long.valueOf(which));
                    seleccionados[which] = false;
                }
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.deportesSeleccionados(deportesSeleccionados);
            }
        });

        return builder.create();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SeleccionarDeporteDialog.SeleccionarDeporteDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
