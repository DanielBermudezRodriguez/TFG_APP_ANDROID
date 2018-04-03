package org.udg.pds.todoandroid.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import org.udg.pds.todoandroid.entity.Provincia;
import java.util.ArrayList;
import java.util.List;


public class SeleccionarProvinciasDialog extends DialogFragment {

    private static List<Provincia> provincias = new ArrayList<Provincia>();
    private static int provinciaActual;
    SeleccionarProvinciasDialogListener mListener;

    public interface SeleccionarProvinciasDialogListener {
        public void onDialogPositiveClick(int provinciaActual);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> provincias = new ArrayList<>();
        for (Provincia p : getProvincias())provincias.add(p.getProvincia());
        CharSequence[] cs = provincias.toArray(new CharSequence[provincias.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escojer provincia:");
        builder.setSingleChoiceItems(cs,-1,new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

              System.out.println("Posicion seleccionada: " + which);

            }

        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(provinciaActual);
                    }
                });

        return builder.create();
    }

    public static SeleccionarProvinciasDialog newInstance(List<Provincia> provincias,int provinciaActual) {
        SeleccionarProvinciasDialog f = new SeleccionarProvinciasDialog();
        setProvincias(provincias);
        setProvinciaActual(provinciaActual);
        return f;
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

    public static List<Provincia> getProvincias() {
        return provincias;
    }

    public static void setProvincias(List<Provincia> provincias) {
        SeleccionarProvinciasDialog.provincias = provincias;
    }

    public static int getProvinciaActual() {
        return provinciaActual;
    }

    public static void setProvinciaActual(int provinciaActual) {
        SeleccionarProvinciasDialog.provinciaActual = provinciaActual;
    }
}
