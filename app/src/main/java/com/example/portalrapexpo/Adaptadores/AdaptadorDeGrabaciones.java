package com.example.portalrapexpo.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.portalrapexpo.Activities.EntrenarActivity;
import com.example.portalrapexpo.Clases.BottomSheet;
import com.example.portalrapexpo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;

public class AdaptadorDeGrabaciones extends BaseAdapter {
    private ArrayList<String> arrGrabaciones;
    private Context miContexto;
    int posicioneliminar;
    public AdaptadorDeGrabaciones(ArrayList<String> arrGrabaciones, Context miContexto) {
        this.arrGrabaciones = arrGrabaciones;
        this.miContexto = miContexto;
    }

    @Override
    public int getCount() {
        return arrGrabaciones.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        protected ImageButton btnEliminar;
        protected TextView grabacion;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) miContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_grabaciones, parent, false);

            holder.grabacion = convertView.findViewById(R.id.itemGrab);
            holder.btnEliminar = convertView.findViewById(R.id.itemBtnEliminar);
            holder.btnEliminar.setFocusable(false);
            holder.grabacion.setFocusable(false);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.grabacion.setText(arrGrabaciones.get(position));

        holder.btnEliminar.setTag(R.integer.btnplusview, convertView);
        holder.btnEliminar.setTag(position);

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posicioneliminar = (Integer)  holder.btnEliminar.getTag();

                AlertDialog.Builder mensaje;
                mensaje = new AlertDialog.Builder(miContexto);
                mensaje.setTitle("Eliminar Grabacion");
                mensaje.setMessage("\n No se puede recuperar una grabacion eliminada \n \n");
                mensaje.setPositiveButton("Eliminar", escuchador);
                mensaje.setNegativeButton("Cancelar", escuchador);
                mensaje.create();
                mensaje.show();


            }
        });

        return convertView;
    }

    void EliminarGrabacion(){
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/Portal Rap(expo)/" );

        File file = new File(carpeta , EntrenarActivity.arrGrabaciones.get(posicioneliminar));
        if (file.delete()){
            Log.d("Eliminar","El fichero  ha sido borrado satisfactoriamente");
        }
        else {
            Log.d("Eliminar", "El fichero no ha sido borrado ");
        }
    }


    DialogInterface.OnClickListener escuchador = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(which == -1) {
                EliminarGrabacion();
                arrGrabaciones.remove(posicioneliminar);
                notifyDataSetChanged();

                EntrenarActivity.btnLevantarBottom.setText(String.valueOf(getCount()));
                BottomSheet.subtitulo.setText("Compartir " + getCount() + " grabacion(es) a: ");
            }
            else if(which == -2) {
                dialog.cancel();
            }

        }
    };

}
