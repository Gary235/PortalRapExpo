package com.example.portalrapexpo.Clases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.portalrapexpo.Activities.EntrenarActivity;
import com.example.portalrapexpo.Adaptadores.AdaptadorDeGrabaciones;
import com.example.portalrapexpo.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;

public class BottomSheet extends BottomSheetDialogFragment {

    public static TextView subtitulo;
    Button btnBottom;
    TextInputEditText edtBottom;
    BottomSheetListner bottomSheetListner;
    ListView lista;
    AdaptadorDeGrabaciones adaptadorDeGrabaciones;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { bottomSheetListner = (BottomSheetListner) context; }
        catch (Exception e){ Log.d("Error", e.getMessage()); }
    }

    public interface BottomSheetListner{ void OnClicked(String texto);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.bottomsheetlayout, container, false);

        btnBottom = v.findViewById(R.id.btnBottom);
        edtBottom = v.findViewById(R.id.edtBottom);
        subtitulo = v.findViewById(R.id.subtitulo);
        lista = v.findViewById(R.id.listaDeGrabaciones);

        adaptadorDeGrabaciones = new AdaptadorDeGrabaciones(EntrenarActivity.arrGrabaciones, getActivity());
        lista.setAdapter(adaptadorDeGrabaciones);

        subtitulo.setText("Compartir " + EntrenarActivity.arrGrabaciones.size() + " grabacion(es) a: ");

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "549" + edtBottom.getText().toString().trim();

                if (edtBottom.getText().toString().trim().length() > 0){
                    EnviarGrabacion(numero);

                } else {
                    Toast.makeText(getActivity(), "Numero invalido", Toast.LENGTH_LONG).show();
                }


            }
        });


        return v;
    }

    private void EnviarGrabacion(String numTelefono) {

        String path = Environment.getExternalStorageDirectory() + "/Portal Rap(expo)/" ;
        ArrayList<Uri> UriArray = new ArrayList<>();

        for (int i = 0; i < EntrenarActivity.arrGrabaciones.size(); i++) {
            Uri uriii = Uri.parse(path + EntrenarActivity.arrGrabaciones.get(i));
            UriArray.add(uriii);
        }


        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_STREAM, UriArray);
            intent.putExtra("jid", numTelefono + "@s.whatsapp.net"); //numero telefonico sin prefijo "+"!
            intent.setPackage("com.whatsapp");
            //startActivity(intent);
            startActivityForResult(intent,999);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Whatsapp no se encuentra instalado.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 999) {
            LimpiarGrabaciones();
        }
    }

    public void LimpiarGrabaciones(){
        EliminarGrabDeAlmacenamiento();
        EntrenarActivity.arrGrabaciones.clear();
        EntrenarActivity.btnLevantarBottom.setText("0");
    }
    public void EliminarGrabDeAlmacenamiento() {
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/Portal Rap(expo)/" );

        for (int i = 0; i < EntrenarActivity.arrGrabaciones.size(); i++) {
            File file = new File(carpeta , EntrenarActivity.arrGrabaciones.get(i));
            if (file.delete()){
                Log.d("Eliminar","El fichero " + i +" ha sido borrado satisfactoriamente");
            }
            else {
                Log.d("Eliminar", "El fichero " + i + " no ha sido borrado ");
            }
        }
    }


}
