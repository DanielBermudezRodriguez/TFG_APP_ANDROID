package org.udg.pds.todoandroid.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;


@SuppressLint("ValidFragment")
public class TabEventoForo extends Fragment {

    private Evento evento;
    private String username;
    private String nombreSala;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    @SuppressLint("ValidFragment")
    public TabEventoForo(Evento e) {
        this.evento = e;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_foro_evento, container, false);

        // Obtener nombre de usuario
        username = "yo";
        // Nombre de la sala
        nombreSala = "observable-salaevento" + evento.getId().toString();

        // crear room chat si no existe ya en database firebase SE DEBERÁ CREAR AL CREAR EL EVENTO
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(nombreSala, "");
        root.updateChildren(map);

        // Accedemos a la sala
        DatabaseReference sala = FirebaseDatabase.getInstance().getReference().child(nombreSala);

        // Al hacer click botón enviar mensaje
        Map<String,Object> mapa = new HashMap<String,Object>();
        String tempKey = sala.push().getKey();
        sala.updateChildren(mapa);

        DatabaseReference mensajeSala = sala.child(tempKey);
        Map<String,Object> mapa2 = new HashMap<String,Object>();
        mapa2.put("name",username);
        mapa2.put("msg","Hola como estas?");
        mensajeSala.updateChildren(mapa2);

        sala.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterator i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    String nombreUsuario = ((DataSnapshot)i.next()).getValue().toString();
                    String mensajeUsuario = ((DataSnapshot)i.next()).getValue().toString();
                    System.out.println("NOMBRE USUARIO: " + nombreUsuario);
                    System.out.println("MENSAJE USUARIO: " + mensajeUsuario);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterator i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    String nombreUsuario = ((DataSnapshot)i.next()).getValue().toString();
                    String mensajeUsuario = ((DataSnapshot)i.next()).getValue().toString();
                    System.out.println("NOMBRE USUARIO: " + nombreUsuario);
                    System.out.println("MENSAJE USUARIO: " + mensajeUsuario);
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


        /*root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        return rootView;
    }


}
