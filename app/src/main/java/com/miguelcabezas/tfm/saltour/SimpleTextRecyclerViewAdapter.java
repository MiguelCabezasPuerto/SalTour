package com.miguelcabezas.tfm.saltour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Gestiona mostrar el listado de elementos simples en un recycler view
 * @author Miguel Cabezas Puerto
 *
 * */
public class SimpleTextRecyclerViewAdapter extends RecyclerView.Adapter<TextViewHolder>{
    private final List<String> items;
    private final LayoutInflater inflater;

    SimpleTextRecyclerViewAdapter(Context context, List<String> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_recycler_view, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        holder.bindText(items.get(position));
    }
}
