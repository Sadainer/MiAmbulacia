package com.example.admin_sena.miambulacia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.actividades.HistorialActivity;

import java.util.ArrayList;


public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

 //   ArrayList<UbicacionPacienteDto> milista;

    ArrayList<UbicacionPacienteDto> milista;
    HistorialViewHolder v;

    public HistorialAdapter(ArrayList<UbicacionPacienteDto> milista, HistorialActivity h) {
        this.milista = milista;

    }

    @Override
    public HistorialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_recyclerview,parent,false);
        v = new HistorialViewHolder(view);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistorialViewHolder holder, int position) {

        UbicacionPacienteDto item = milista.get(position);
        holder.tvTipo.setText("Tipo: "+item.getTipoEmergencia());
        holder.tvNumero.setText("Pacientes: "+ item.getNumeroPacientes());
        holder.tvDieccion.setText("Direccion: "+item.getDireccion());


    }

    @Override
    public int getItemCount() {
        return milista.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView tvDieccion;
        TextView tvTipo;
        TextView tvNumero;

        public HistorialViewHolder(View itemView) {
            super(itemView);
            tvDieccion = (TextView)itemView.findViewById(R.id.tv1);
            tvTipo = (TextView)itemView.findViewById(R.id.tv2);
            tvNumero = (TextView)itemView.findViewById(R.id.tv3);
            imagen = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }
}
