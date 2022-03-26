package com.miguelcabezas.tfm.saltour.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.miguelcabezas.tfm.saltour.R;

public class CarrouseelAdapter extends StaticPagerAdapter {
    private int[] imagenes = {
            R.drawable.img_rana,
            R.drawable.img_jardin,
            R.drawable.img_penguins,
            R.drawable.img_aldehuela,
            R.drawable.img_astronauta,
            R.drawable.img_medallon,
            R.drawable.img_volcan
    };
    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setImageResource(imagenes[position]);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getCount() {
        return imagenes.length;
    }
}
