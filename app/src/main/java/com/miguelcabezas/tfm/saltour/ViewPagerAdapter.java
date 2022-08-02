package com.miguelcabezas.tfm.saltour;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la creación de vistas en tabs
 * @author Miguel Cabezas Puerto
 *
 * */
public class ViewPagerAdapter extends FragmentStateAdapter {
    enum Tab {

        INDIVIDUAL(0, R.string.estadistica_individual, R.drawable.profile_icon),
        GENERAL(1, R.string.estadistica_general,R.drawable.ranking_icon);
        final int title;
        final int icon;
        final int position;

        Tab(int position, @StringRes int title, @DrawableRes int icon) {
            this.position = position;
            this.title = title;
            this.icon = icon;
        }

        private static final Map<Integer,Tab> map;
        static {
            map = new HashMap<>();
            for (Tab t : Tab.values()) {
                map.put(t.position, t);
            }
        }

        static Tab byPosition(int position) {
            return map.get(position);
        }
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == Tab.INDIVIDUAL.position)
            return TabNameFragment.newInstance(Tab.INDIVIDUAL.title);
        else if (position == Tab.GENERAL.position)
            return TabNameFragment.newInstance(Tab.GENERAL.title);
        else
            throw new IllegalArgumentException("unknown position " + position);
    }

    @Override
    public int getItemCount() {
        return Tab.values().length;
    }
}
