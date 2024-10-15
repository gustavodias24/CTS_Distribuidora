package benicio.solucoes.ctsdistribuidora.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.ctsdistribuidora.R;
import benicio.solucoes.ctsdistribuidora.databinding.LayoutInfoProdutoBinding;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    List<ProdutoModel> lista;
    Activity c;

    public AdapterProduto(List<ProdutoModel> lista, Activity c) {
        this.lista = lista;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_produto, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProdutoModel produtoModel = lista.get(position);

        holder.nome.setText(produtoModel.getNome());
        holder.valor.setText("  " + produtoModel.getValor().replace(".", ","));
        holder.nome.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(c);

            LayoutInfoProdutoBinding produtoBinding = LayoutInfoProdutoBinding.inflate(
                    c.getLayoutInflater()
            );

            produtoBinding.titulo.setText(produtoModel.getNome());
            produtoBinding.descricao.setText(produtoModel.getDescricao() + "\n\n" + "Valor de: R$" + produtoModel.getValor().replace(".", ","));

            Picasso.get().load("http://147.79.83.218:5000/imagem/" + '"' + produtoModel.get_id() + '"').into(produtoBinding.camera, new Callback() {
                @Override
                public void onSuccess() {
                    produtoBinding.carregarImagemProduto.setVisibility(View.GONE);
                    produtoBinding.camera.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    produtoBinding.carregarImagemProduto.setVisibility(View.GONE);
                    produtoBinding.camera.setVisibility(View.VISIBLE);
                    produtoBinding.camera.setImageResource(R.drawable.camera);
                }
            });

            b.setNegativeButton("fechar", null);
            b.setView(produtoBinding.getRoot());
            b.create().show();

        });

        holder.check.setOnClickListener(v -> {
            if (!produtoModel.isChecado()) {
                holder.check.setImageResource(R.drawable.check_marcado);
                produtoModel.setChecado(true);
                CarrinhoUtils.addProduto(c, produtoModel);
            } else {
                holder.check.setImageResource(R.drawable.check_desmarcado);
                produtoModel.setChecado(false);
                CarrinhoUtils.removeProduto(c, produtoModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView check;
        TextView nome, valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.check_carrinho);
            nome = itemView.findViewById(R.id.nome_produto);
            valor = itemView.findViewById(R.id.valor_produto);
        }
    }
}
