package com.example.portalrapexpo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.portalrapexpo.Clases.BottomSheet;
import com.example.portalrapexpo.Clases.Palabra;
import com.example.portalrapexpo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntrenarActivity extends AppCompatActivity {

    public static Button btnLevantarBottom;
    ImageButton  btnPlay, btnVolver, btnGrabar;
    TextView txtArtista, txtBase, txtDuracion, txtConfirmar;
    ImageView fondoDifuminado;
    SeekBar barradebeat;
    EditText input;
    LottieAnimationView p1, p2;

    public static MediaRecorder grabacion = null;
    MediaPlayer mediaPlayer = new MediaPlayer();
    MediaObserver observer = null;

    String path_file = "", name, carpeta = "/Portal Rap(expo)/", archivo = "default_name";
    public static File localfile;
    String palabrarandom;
    CountDownTimer timer = null, timerinicial;
    long tiemporestanteDuracion = -1, tiemporestanteInicial = 3500;

    int  beatSeleccionado = -1, frecuencia = -1;
    public static int contadorDeGrabaciones = 0;
    FirebaseFirestore db;
    Random generador = new Random();

    ArrayList<Palabra> arrPalabras = new ArrayList<>();
    public static ArrayList<String> arrGrabaciones = new ArrayList<>();
    File file;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenar);

        db = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Cargando Palabras");
        dialog.show();
        LlenarArrayPalabras();

        SetearVariables();
        SetearEntrenamiento();
    }

    public void SetearVariables() {

        btnGrabar = findViewById(R.id.btnGrabardeentrenar);
        btnVolver = findViewById(R.id.btnVOlverdeentrenar);
        btnPlay = findViewById(R.id.Playdeentrenar);
        btnLevantarBottom = findViewById(R.id.btnLevantarBottom);

        txtArtista = findViewById(R.id.nombreArtistaSeleccionadodeentrenar);
        txtBase = findViewById(R.id.nombreBaseSeleccionadadeentrenar);
        txtDuracion = findViewById(R.id.TiempoFreedeentrenar);
        txtConfirmar = findViewById(R.id.txtConfirmar);
        fondoDifuminado = findViewById(R.id.recdifuminado);
        barradebeat = findViewById(R.id.Barradeentrenar);
        p1 = findViewById(R.id.parlante1);
        p2 = findViewById(R.id.parlante2);

        btnVolver.setEnabled(false);
        btnGrabar.setEnabled(false);
        btnPlay.setEnabled(false);
        barradebeat.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        observer = new MediaObserver();
    }
    public void SetearEntrenamiento() {

        beatSeleccionado = getIntent().getIntExtra("beatSeleccionado", -1);
        Log.d("Entrenamiento", "beat Seleccionado : " + beatSeleccionado);

        if(beatSeleccionado == 0){
            mediaPlayer = MediaPlayer.create(this, R.raw.pumpkin_spice);
            txtBase.setText("Pumpkin Spice");
            txtArtista.setText("yungkartz");
        }
        else {
            mediaPlayer = MediaPlayer.create(this, R.raw.hallucinations);
            txtBase.setText("Hallucinations");
            txtArtista.setText("audiobinger");
        }

        tiemporestanteDuracion = mediaPlayer.getDuration();
        frecuencia = 10000; //10s

    }
    public void Salir(View v){
        AlertDialog.Builder mensaje;
        mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle("Salir del Entrenamiento");
        mensaje.setMessage("Si sales, la grabación se va a borrar");
        mensaje.setPositiveButton("Aceptar", escuchadordesalir);
        mensaje.setNegativeButton("Cancelar", escuchadordesalir);
        mensaje.create();
        mensaje.show();
    }

    public void EmpezarEntrenamiento(View v){
        empezarTimerInicial();
    }

    public void empezarTimerInicial() {
        timerinicial = new CountDownTimer(tiemporestanteInicial, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiemporestanteInicial = millisUntilFinished;
                actualizarTimerInicial();
            }

            @Override
            public void onFinish() {
                actualizarTimerDuracion();
                empezarTimerDuracion();
                actualizarTimerFrecuencia();
                empezarTimerFrecuencia();
                runMedia();

                btnVolver.setEnabled(true);
                btnGrabar.setEnabled(true);
                btnPlay.setEnabled(true);
                barradebeat.setEnabled(true);
                fondoDifuminado.setVisibility(View.GONE);
                txtConfirmar.setEnabled(false);

            }
        }.start();

    }
    public void actualizarTimerInicial() {
        int segundos = (int) (tiemporestanteInicial / 1000) % 60;
        String txttiempores = "";

        txttiempores += "" + segundos;
        if (segundos < 1) {
            txttiempores = "TIEMPO!!";
        }
        txtConfirmar.setText(txttiempores);
    }

    public void empezarTimerDuracion() {
        timer = new CountDownTimer(tiemporestanteDuracion, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tiemporestanteDuracion = millisUntilFinished;
                actualizarTimerDuracion();
            }

            @Override
            public void onFinish() {
                timer.cancel();
                mediaPlayer.stop();
                observer.stop();

                if(grabacion != null){


                    AlertDialog.Builder mensaje;
                    mensaje = new AlertDialog.Builder(getApplicationContext());
                    mensaje.setTitle("Nombrar Grabacion");
                    mensaje.setMessage(" \n No la nombres igual a otra grabacion porque se va a sobreescribir \n \n");
                    input = new EditText(getApplicationContext());
                    input.setTag("timer");
                    mensaje.setView(input);
                    mensaje.setPositiveButton("Guardar", escuchadordeguardargrabacion);
                    mensaje.setNegativeButton("Volver", escuchadordeguardargrabacion);
                    mensaje.create();
                    mensaje.show();
                }
                else {
                    //intent al inicio

                }

            }

        }.start();
    }
    public void actualizarTimerDuracion() {
        int minutos = (int) (tiemporestanteDuracion / 1000) / 60;
        int segundos = (int) (tiemporestanteDuracion / 1000) % 60;
        String txttiempores;

        txttiempores = "" + minutos;
        txttiempores += ":";
        if (segundos < 10) {
            txttiempores += "0";
        }
        txttiempores += "" + segundos;

        txtDuracion.setText(txttiempores);
    }

    public void empezarTimerFrecuencia() {
        timerinicial = new CountDownTimer(tiemporestanteDuracion, frecuencia) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiemporestanteDuracion = millisUntilFinished;
                actualizarTimerFrecuencia();
            }

            @Override
            public void onFinish() {
            }
        }.start();

    }
    public void actualizarTimerFrecuencia() {
        obtenerPalabraRandom();
        txtConfirmar.setTextSize(50);
        if (palabrarandom != null)
            txtConfirmar.setText(palabrarandom.toUpperCase());
    }

    public void runMedia() {
        barradebeat.setMax(mediaPlayer.getDuration());
        observer = new MediaObserver();

        while (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        new Thread(observer).start();

        btnPlay.setImageResource(R.drawable.ic_icono_pausa);
        mediaPlayer.setWakeMode(this, PowerManager.FULL_WAKE_LOCK);

        p1.loop(true);
        p2.loop(true);
        p1.playAnimation();
        p2.playAnimation();


    }

    public void Grabar(View v) {
        if (grabacion == null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            timer.cancel();

            AlertDialog.Builder mensaje;
            mensaje = new AlertDialog.Builder(this);
            mensaje.setTitle("Empezar Grabacion");
            mensaje.setPositiveButton("Empezar", escuchadordeempezargrabacion);
            mensaje.setNegativeButton("Cancelar", escuchadordeempezargrabacion);
            mensaje.create();
            mensaje.show();
        } else {
            btnGrabar.setBackgroundResource(R.drawable.stroke_negro_redondeado);
            btnGrabar.setImageResource(R.drawable.ic_grabar);



            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            timer.cancel();

            try {
                grabacion.stop();
            } catch (RuntimeException e) {
                Log.d("Grabacion", "grabacion: " + grabacion);
            }
            grabacion.release();

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_icono_play_blanco);
            }

            AlertDialog.Builder mensaje;
            mensaje = new AlertDialog.Builder(this);
            mensaje.setTitle("Nombrar Grabacion");
            mensaje.setMessage(" \n No la nombres igual a otra grabacion porque se va a sobreescribir \n \n");
            input = new EditText(this);
            input.setTag(null);
            mensaje.setView(input);
            mensaje.setPositiveButton("Guardar", escuchadordeguardargrabacion);
            mensaje.setNegativeButton("Volver", escuchadordeguardargrabacion);
            mensaje.create();
            mensaje.show();

        }

    }
    public void pausayReproducir(View v) {

        if (!mediaPlayer.isPlaying()) {
            btnPlay.setImageResource(R.drawable.ic_icono_pausa);
            mediaPlayer.start();
            p1.playAnimation();
            p2.playAnimation();

        } else {
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.ic_icono_play);
            p1.cancelAnimation();
            p2.cancelAnimation();
        }

    }

    public void guardarGrabacion() {
        //cambiar nombre del archivo
        File currentFile = new File(localfile, name);
        File newFile = new File(localfile, input.getText().toString().trim() + ".mp3");
        NombrarFile(currentFile, newFile);

        arrGrabaciones.add(input.getText().toString().trim() + ".mp3");
        btnLevantarBottom.setText(String.valueOf(arrGrabaciones.size()));
        if(BottomSheet.subtitulo != null)BottomSheet.subtitulo.setText("Compartir " + arrGrabaciones.size() + " grabacion(es) a: ");
    }
    private boolean NombrarFile(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    public void obtenerPalabraRandom() {
        palabrarandom = arrPalabras.get(generador.nextInt(arrPalabras.size())).getPalabra();
    }
    public void LlenarArrayPalabras(){
        arrPalabras = new ArrayList<>();
        db.collection("Palabras")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Palabra pal = document.toObject(Palabra.class);
                                pal.setId(document.getId());
                                arrPalabras.add(pal);
                            }
                            dialog.cancel();
                        }
                    }
                });
    }

    public class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {

                barradebeat.setProgress(mediaPlayer.getCurrentPosition());

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public void LevantarBottomSheet(View v){

        if (grabacion == null){
            if (arrGrabaciones.size() > 0){
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_icono_play);
                BottomSheet bottomSheet = new BottomSheet();
                //bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                Toast.makeText(this,"No hay ninguna grabacion registrada",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"Hay una grabacion en curso",Toast.LENGTH_SHORT).show();
        }


    }


    DialogInterface.OnClickListener escuchadordeempezargrabacion = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                mediaPlayer.start();
                if(timer  != null){
                    timer.cancel();
                    empezarTimerDuracion();
                }
                btnGrabar.setBackgroundResource(R.drawable.redondeado_rojo);
                btnGrabar.setImageResource(R.drawable.ic_grabar_blanco);

                grabacion = new MediaRecorder();
                grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
                grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                path_file = Environment.getExternalStorageDirectory() + carpeta;
                localfile = new File(path_file);

                if (!localfile.exists()) {
                    localfile.mkdir();
                }

                name = archivo + ".mp3";
                file = new File(localfile, name);

                grabacion.setOutputFile(file.getAbsolutePath());

                try {
                    grabacion.prepare();
                    grabacion.start();
                } catch (IOException e) { }
            }
            else if (which == -2) {
                mediaPlayer.start();
                if(timer  != null){
                    timer.cancel();
                    empezarTimerDuracion();
                }
                dialog.cancel();
            }
        }
    };

    DialogInterface.OnClickListener escuchadordeguardargrabacion = new DialogInterface.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                if (input.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Nombre de grabacion invalido", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.start();
                    if(timer != null){
                        timer.cancel();
                        empezarTimerDuracion();
                    }
                    guardarGrabacion();
                    if(input.getTag() != null){
                        //mostrar bottom sheet
                    }

                    grabacion = null;
                }
            }
            else {
                mediaPlayer.start();
                if(timer  != null){
                    timer.cancel();
                    empezarTimerDuracion();
                }

                dialog.cancel();
            }
        }
    };

    DialogInterface.OnClickListener escuchadordesalir = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                mediaPlayer.stop();
                grabacion = null;
                Intent llamada = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(llamada);
            }
            else if (which == -2) {
                dialog.cancel();
            }
        }
    };



}