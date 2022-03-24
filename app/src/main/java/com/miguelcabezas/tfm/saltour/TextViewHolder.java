package com.miguelcabezas.tfm.saltour;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final TextView textView;

    TextViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        itemView.setOnClickListener(this);
    }

    void bindText(String text) {
        textView.setText(text);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
    }
}
