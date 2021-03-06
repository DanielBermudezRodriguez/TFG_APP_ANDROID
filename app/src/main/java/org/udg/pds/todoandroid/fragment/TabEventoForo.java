package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.adapter.MensajeForoAdapter;
import org.udg.pds.todoandroid.entity.DatosUsuarioForo;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.MensajeForo;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.util.Global;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


@SuppressLint("ValidFragment")
public class TabEventoForo extends Fragment {

    private Evento evento;
    private String username;
    private String nombreSala;
    private DatabaseReference salaForo;
    private TextInputLayout tilMensaje;
    private EditText editText;
    private MensajeForoAdapter mensajeAdapter;
    private ListView listaMensajes;
    private FloatingActionButton botonEnviar;
    private ChildEventListener childEventListener = null;
    private ConstraintLayout layoutForo;
    private ConstraintLayout foroPrivado;

    public void update(Boolean esParticipante) {

        if (!evento.getForo().getEsPublico() && !esParticipante) {
            layoutForo.setVisibility(View.GONE);
            foroPrivado.setVisibility(View.VISIBLE);
        } else {
            layoutForo.setVisibility(View.VISIBLE);
            foroPrivado.setVisibility(View.GONE);
        }

    }


    @SuppressLint("ValidFragment")
    public TabEventoForo(Evento e) {
        this.evento = e;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_foro_evento, container, false);

        layoutForo = rootView.findViewById(R.id.layout_foro);
        foroPrivado = rootView.findViewById(R.id.tab_foro_evento_privado);

        if (!evento.getForo().getEsPublico() && getActivity() != null && !((EventoDetalle) getActivity()).esParticipante()) {
            layoutForo.setVisibility(View.GONE);
            foroPrivado.setVisibility(View.VISIBLE);
        } else {
            layoutForo.setVisibility(View.VISIBLE);
            foroPrivado.setVisibility(View.GONE);
        }

        ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();

        mensajeAdapter = new MensajeForoAdapter(getActivity().getApplicationContext());
        listaMensajes = rootView.findViewById(R.id.messages_view);
        listaMensajes.setAdapter(mensajeAdapter);

        // Obtener nombre de usuario
        username = UsuarioActual.getInstance().getUsername();
        // Nombre de la sala
        nombreSala = Global.PREFIJO_SALA_FORO_EVENTO + evento.getId().toString();

        tilMensaje = rootView.findViewById(R.id.tab_foro_evento_til_mensaje);
        editText = rootView.findViewById(R.id.editText);
        botonEnviar = rootView.findViewById(R.id.boton_enviar_mensaje_foro);
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                Objects.requireNonNull(tilMensaje.getEditText()).setText("");
                sendMessage(v, message);
            }
        });

        // Accedemos a la sala
        salaForo = FirebaseDatabase.getInstance().getReference().child(nombreSala);

        if (childEventListener != null) {
            salaForo.removeEventListener(childEventListener);
        }
        childEventListener = salaForo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    String fecha = ((DataSnapshot) i.next()).getValue().toString();
                    Long idUsuario = Long.parseLong(((DataSnapshot) i.next()).getValue().toString());
                    String mensajeUsuario = ((DataSnapshot) i.next()).getValue().toString();
                    String nombreUsuario = ((DataSnapshot) i.next()).getValue().toString();


                    DatosUsuarioForo datosUsuarioForo = new DatosUsuarioForo(nombreUsuario, idUsuario,fecha);
                    boolean esUsuarioActual = nombreUsuario.equals(username);
                    final MensajeForo mensajeForo = new MensajeForo(mensajeUsuario, datosUsuarioForo, esUsuarioActual);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensajeAdapter.add(mensajeForo);
                            listaMensajes.setSelection(listaMensajes.getCount() - 1);
                        }
                    });

                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    String fecha = ((DataSnapshot) i.next()).getValue().toString();
                    Long idUsuario = Long.parseLong(((DataSnapshot) i.next()).getValue().toString());
                    String mensajeUsuario = ((DataSnapshot) i.next()).getValue().toString();
                    String nombreUsuario = ((DataSnapshot) i.next()).getValue().toString();


                    DatosUsuarioForo datosUsuarioForo = new DatosUsuarioForo(nombreUsuario, idUsuario,fecha);
                    boolean esUsuarioActual = nombreUsuario.equals(username);
                    final MensajeForo mensajeForo = new MensajeForo(mensajeUsuario, datosUsuarioForo, esUsuarioActual);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensajeAdapter.add(mensajeForo);
                            listaMensajes.setSelection(listaMensajes.getCount() - 1);
                        }
                    });

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        salaForo.removeEventListener(childEventListener);
    }

    public void sendMessage(View view, String message) {
        if (message.length() > 0) {
            // Al hacer click botón enviar mensaje
            Map<String, Object> mapa = new HashMap<>();
            String tempKey = salaForo.push().getKey();
            salaForo.updateChildren(mapa);

            DatabaseReference mensajeSala = salaForo.child(tempKey);
            Calendar fecha = Calendar.getInstance();
            Map<String, Object> mapa2 = new HashMap<>();
            mapa2.put("name", username);
            mapa2.put("msg", message);
            mapa2.put("id", UsuarioActual.getInstance().getId().toString());
            mapa2.put("date",fecha.get(Calendar.HOUR_OF_DAY) + ":" + fecha.get(Calendar.MINUTE));
            mensajeSala.updateChildren(mapa2);

            Objects.requireNonNull(tilMensaje.getEditText()).setText("");
        }
        Objects.requireNonNull(tilMensaje.getEditText()).setText("");
    }


}
