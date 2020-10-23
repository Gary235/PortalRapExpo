package com.example.portalrapexpo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.portalrapexpo.Clases.SliderItem;
import com.example.portalrapexpo.R;
import com.example.portalrapexpo.Adaptadores.SliderAdapter;

import java.util.ArrayList;
import java.util.List;

public class BeatActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    int  PageScrolled = 0;
    List<SliderItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat);


        viewPager2 = findViewById(R.id.viewPager);
        setearViewPager();


    }

    private void setearViewPager(){


        list.add(new SliderItem(R.drawable.imagen1, "3:18","Pumpkin Spice","yungkartz",false));
        list.add(new SliderItem(R.drawable.imagen2, "2:21","Hallucinations","audiobinger",false));

        viewPager2.setAdapter(new SliderAdapter(list,this));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer((40)));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                PageScrolled = position;
                Log.d("ViewPager", "PageScrolled: " + PageScrolled);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.d("ViewPager", "PageSelected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                Log.d("ViewPager", "PageScrollStateChanged: " + state);
            }
        });

    }

    public void PasarAEntrenar(View v){

        if (list.get(0).isPlaying() || list.get(1).isPlaying()){
            Toast.makeText(this,"Frene el beat para empezar a entrenar",Toast.LENGTH_SHORT).show();
        } else {
            Intent llamada = new Intent(getApplicationContext(), EntrenarActivity.class);
            llamada.putExtra("beatSeleccionado", PageScrolled);
            startActivity(llamada);
        }



    }

}