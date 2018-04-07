package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import org.udg.pds.todoandroid.entity.Provincia;
import java.util.List;


public class SeleccionarProvinciasDialog extends DialogFragment {

    private CharSequence[] provincias;
    private int provinciaActual;
    SeleccionarProvinciasDialogListener mListener;

    public interface SeleccionarProvinciasDialogListener {
         void provinciaSeleccionada(int provinciaSeleccionada);
    }

    public SeleccionarProvinciasDialog(){

    }

    @SuppressLint("ValidFragment")
    public SeleccionarProvinciasDialog(List<Provincia> provincias, int provinciaActual) {
        int i = 0;
        this.provincias = new CharSequence[provincias.size()];
        for (Provincia p : provincias){
            this.provincias[i] = p.getProvincia();
            i++;
        }
        this.provinciaActual = provinciaActual;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escojer Provincia:");
        builder.setSingleChoiceItems(this.provincias,provinciaActual,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int provinciaSeleccionada) {
             provinciaActual = provinciaSeleccionada;
            }

        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.provinciaSeleccionada(provinciaActual);
                    }
                });

        return builder.create();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SeleccionarProvinciasDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
