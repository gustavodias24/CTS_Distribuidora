package benicio.solucoes.ctsdistribuidora.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import benicio.solucoes.ctsdistribuidora.CadatroClienteActivity;
import benicio.solucoes.ctsdistribuidora.R;
import benicio.solucoes.ctsdistribuidora.model.ClienteModel;

public class AdapterCliente extends RecyclerView.Adapter<AdapterCliente.MyViewHolder> {

    List<ClienteModel> lista;
    Context c;

    public AdapterCliente(List<ClienteModel> lista, Context c) {
        this.lista = lista;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cliente, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClienteModel cliente = lista.get(position);

        StringBuilder info = new StringBuilder();
        info.append("<b>").append("Cliente: ").append("</b>").append(cliente.getCliente()).append("<br>");
        info.append("<b>").append("Representante: ").append("</b>").append(cliente.getRepresentante()).append("<br>");
        info.append("<b>").append("Documento: ").append("</b>").append(cliente.getDocumento()).append("<br>");
        info.append("<b>").append("Endereço: ").append("</b>").append(cliente.getEndereco()).append("<br>");
        info.append("<b>").append("Contato: ").append("</b>").append(cliente.getContato()).append("<br>");
        info.append("<b>").append("Data Inicio: ").append("</b>").append(cliente.getDatainicio()).append("<br>");

        holder.info_cliente.setText(Html.fromHtml(info.toString()));
        holder.editar_cliente_btn.setOnClickListener(v -> {
            Intent i = new Intent(c, CadatroClienteActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("cliente", new Gson().toJson(cliente));
            c.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView info_cliente;
        Button editar_cliente_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            info_cliente = itemView.findViewById(R.id.info_cliente);
            editar_cliente_btn = itemView.findViewById(R.id.editar_cliente_btn);
        }
    }
}
