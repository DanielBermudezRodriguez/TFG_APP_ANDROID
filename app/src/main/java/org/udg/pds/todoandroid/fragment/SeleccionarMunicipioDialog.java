package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Provincia;

import java.util.List;


public class SeleccionarMunicipioDialog extends DialogFragment {

    private CharSequence[] municipios;
    private int municipioActual;
    SeleccionarMunicipioDialogListener mListener;

    public interface SeleccionarMunicipioDialogListener {
         void municipioSeleccionado(int municipioSeleccionado);
    }

    public SeleccionarMunicipioDialog(){

    }

    @SuppressLint("ValidFragment")
    public SeleccionarMunicipioDialog(List<Municipio> municipios, int municipioActual){
        int i = 0;
        this.municipios = new CharSequence[municipios.size()];
        for (Municipio m : municipios){
            this.municipios[i] = m.getMunicipio();
            i++;
        }
        this.municipioActual = municipioActual;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escojer Municipio:");
        builder.setSingleChoiceItems(this.municipios,municipioActual,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int municipioSeleccionado) {
                municipioActual = municipioSeleccionado;
            }

        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.municipioSeleccionado(municipioActual);
            }
        });

        return builder.create();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
