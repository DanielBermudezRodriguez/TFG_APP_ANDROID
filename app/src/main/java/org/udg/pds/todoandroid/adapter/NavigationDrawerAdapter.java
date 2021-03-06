package org.udg.pds.todoandroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;

import java.util.Arrays;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {

    private List<String> seccionesMenuLateral;
    private boolean locked = false;
    private Context mContext = null;

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
        String[] secciones = {"Perfil", "Modificar perfil", "Notificaciones", "Mis eventos", "Buscar eventos", "Crear Evento", "Cerrar sesión"};
        seccionesMenuLateral = Arrays.asList(secciones);
    }

    @Override
    public int getCount() {
        return seccionesMenuLateral.size();
    }

    @Override
    public Object getItem(int position) {
        return seccionesMenuLateral.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getSectionIcon(int position) {
        switch (position) {
            default:
            case 0:
                return R.mipmap.ic_perfil_usuario;
            case 1:
                return R.mipmap.ic_editar_perfil;
            case 2:
                return R.mipmap.ic_notificaciones;
            case 3:
                return R.mipmap.ic_eventos_creados;
            case 4:
                return R.mipmap.ic_buscar_eventos_menu_lateral;
            case 5:
                return R.mipmap.ic_crear_evento_menu_lateral;
            case 6:
                return R.mipmap.ic_cerrar_sesion;

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerHolder drawerHolder;
        if (convertView == null) {
            drawerHolder = new DrawerHolder();
            convertView = new DrawerView(mContext);
            drawerHolder.titleTextView = convertView.findViewById(R.id.title_text_view);
            drawerHolder.drawer_image = convertView.findViewById(R.id.drawer_image);
            drawerHolder.layout = convertView.findViewById(R.id.drawer_relative);
            ((DrawerView) convertView).setTag(drawerHolder, seccionesMenuLateral.get(position), getSectionIcon(position));
        } else {
            drawerHolder = (DrawerHolder) convertView.getTag();
            ((DrawerView) convertView).setTag(drawerHolder, seccionesMenuLateral.get(position), getSectionIcon(position));
        }
        if (position == 0) {
            ((DrawerView) convertView).setChecked(true);
        }
        return convertView;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyDataSetChanged();
    }

    public class DrawerHolder {
        TextView titleTextView;
        ImageView drawer_image;
        RelativeLayout layout;
    }

    public class DrawerView extends RelativeLayout implements Checkable {
        private TextView titleTextView;
        private RelativeLayout layout;
        private boolean isChecked;

        public DrawerView(Context context) {
            super(context);
            inflate(context, R.layout.items_menu_lateral, this);
        }

        public void setTag(DrawerHolder tag, String title, int imageId) {
            super.setTag(tag);
            this.titleTextView = tag.titleTextView;
            ImageView drawer_image = tag.drawer_image;
            this.layout = tag.layout;
            titleTextView.setText(title);
            Context mContext = getContext();
            if (mContext != null) {
                drawer_image.setImageResource(imageId);
            }
        }


        @Override
        public void setTag(Object tag) {
            super.setTag(tag);
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public void setChecked(boolean checked) {
            isChecked = checked;


        }

        @Override
        public void toggle() {
            this.isChecked = !this.isChecked;
        }
    }
}


