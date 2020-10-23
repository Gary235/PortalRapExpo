package com.example.portalrapexpo.Adaptadores;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.portalrapexpo.Clases.SliderItem;
import com.example.portalrapexpo.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItemList;
    private Context miContexto;
    MediaPlayer mp = new MediaPlayer();

    public SliderAdapter(List<SliderItem> sliderItemList, Context context) {
        this.sliderItemList = sliderItemList;
        this.miContexto = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setInfo(sliderItemList.get(position));


    }

    @Override
    public int getItemCount() {
        return sliderItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    class SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private ImageView play;
        private TextView duracion, base, artista;
        private LottieAnimationView audio;

         SliderViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            duracion = itemView.findViewById(R.id.duracion);
            base = itemView.findViewById(R.id.nombreBase);
            artista = itemView.findViewById(R.id.nombreArtista);
            play = itemView.findViewById(R.id.Play);
            audio = itemView.findViewById(R.id.audioAnimacion);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("ViewPager", "Posicion: " + pos);

                    if(sliderItemList.get(pos).isPlaying()){
                        sliderItemList.get(pos).setPlaying(false);
                        audio.cancelAnimation();
                        audio.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);

                        mp.pause();
                    }
                    else {
                        sliderItemList.get(pos).setPlaying(true);
                        play.setVisibility(View.GONE);
                        audio.setVisibility(View.VISIBLE);
                        audio.loop(true);
                        audio.playAnimation();

                        switch(pos){
                            case 0:
                                mp = MediaPlayer.create(miContexto, R.raw.pumpkin_spice);
                                mp.start();
                                break;
                            case 1:
                                mp = MediaPlayer.create(miContexto, R.raw.hallucinations);
                                mp.start();
                                break;
                        }
                    }
                }
            });
         }
        void setInfo(SliderItem sliderItem){
            imageView.setImageResource(sliderItem.getImagen());
            duracion.setText(sliderItem.getDuracion());
            base.setText(sliderItem.getBase());
            artista.setText(sliderItem.getArtista());
        }








    }



}
