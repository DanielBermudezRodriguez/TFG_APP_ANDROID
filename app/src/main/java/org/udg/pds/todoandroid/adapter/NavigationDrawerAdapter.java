package org.udg.pds.todoandroid.adapter;

import android.content.Context;
import android.content.res.Resources;
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

    private List<String> sectionsList;
    private boolean locked = false;
    private Context mContext = null;

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
        String[] sections = {"Perfil", "Modificar perfil", "Cerrar sesión"};
        sectionsList = Arrays.asList(sections);
    }

    @Override
    public int getCount() {
        return sectionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sectionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getSectionIcon(int position) {
        switch (position) {
            default:
            case 0:
                return R.drawable.ic_drawer;
            case 2:
                return R.mipmap.ic_launcher;
            case 1:
                return R.drawable.drawer_shadow;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerHolder drawerHolder;
        if(convertView == null) {
            drawerHolder = new DrawerHolder();
            convertView = new DrawerView(mContext);
            drawerHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            drawerHolder.drawer_image = (ImageView) convertView.findViewById(R.id.drawer_image);
            drawerHolder.layout = (RelativeLayout) convertView.findViewById(R.id.drawer_relative);
            ((DrawerView) convertView).setTag(drawerHolder, sectionsList.get(position), getSectionIcon(position));
        } else {
            drawerHolder = (DrawerHolder) convertView.getTag();
            ((DrawerView) convertView).setTag(drawerHolder, sectionsList.get(position), getSectionIcon(position));
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

        private void highlighCell(boolean checked) {
            Resources resources = getResources();
            if (resources != null) {
                if (checked) {
                    layout.setBackgroundColor(resources.getColor(R.color.color_fondo));
                } else {
                    layout.setBackgroundColor(resources.getColor(android.R.color.white));
                }
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
            highlighCell(checked);

        }

        @Override
        public void toggle() {
            this.isChecked = !this.isChecked;
            highlighCell(isChecked);
        }
    }
}


