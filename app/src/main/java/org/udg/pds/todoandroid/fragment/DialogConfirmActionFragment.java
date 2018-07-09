package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import org.udg.pds.todoandroid.R;

public class DialogConfirmActionFragment extends DialogFragment {

    private String titulo;

    private String contenido;

    DialogConfirmActionFragmentListener mdialogConfirmActionFragmentListener;

    public interface DialogConfirmActionFragmentListener {
        void accionSeleccionada(boolean accion);
    }

    public DialogConfirmActionFragment() {

    }

    @SuppressLint("ValidFragment")
    public DialogConfirmActionFragment(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo);
        builder.setMessage(contenido)
                .setPositiveButton(getString(R.string.dialogo_aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mdialogConfirmActionFragmentListener.accionSeleccionada(true);
                    }
                })
                .setNegativeButton(getString(R.string.dialogo_cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mdialogConfirmActionFragmentListener.accionSeleccionada(false);
                    }
                });

        return builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogConfirmActionFragmentListener) {
            mdialogConfirmActionFragmentListener = (DialogConfirmActionFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DialogConfirmActionFragmentListener");
        }
    }
}

