

package benicio.solucoes.ctsdistribuidora.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.ctsdistribuidora.CarrinhoActivity;
import benicio.solucoes.ctsdistribuidora.R;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;

public class AdapterProdutoCarrinho extends RecyclerView.Adapter<AdapterProdutoCarrinho.ViewHolderCarrinho> {

    List<ProdutoModel> lista;
    Activity c;

    public AdapterProdutoCarrinho(List<ProdutoModel> lista, Activity c) {
        this.lista = lista;
        this.c = c;
    }

    @NonNull
    @Override
    public ViewHolderCarrinho onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderCarrinho(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_produto_carrinho, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolderCarrinho holder, int position) {
        ProdutoModel produtoModel = lista.get(position);

        holder.nome.setText(produtoModel.getNome());
        holder.valor.setText(produtoModel.getValorSomado().replace(".", ","));

        holder.maisUmCarrinho.setOnClickListener(v -> {
            int quantidadeExistente = Integer.parseInt(holder.quantidadeTexView.getText().toString());

            quantidadeExistente++;

            float valorTotal = Float.parseFloat(produtoModel.getValor().replace(",", ".")) * quantidadeExistente;
            @SuppressLint("DefaultLocale") String novoValor = String.format("%.2f", valorTotal).replace(".", ",");
            produtoModel.setValorSomado(novoValor);
            holder.valor.setText(novoValor);
            holder.quantidadeTexView.setText(quantidadeExistente + "");
            CarrinhoActivity.calcularValorPagar();
        });

        holder.menosUmCarrinho.setOnClickListener(v -> {
            int quantidadeExistente = Integer.parseInt(holder.quantidadeTexView.getText().toString());
            if (quantidadeExistente > 1) {
                quantidadeExistente--;
                float valorTotal = Float.parseFloat(produtoModel.getValor().replace(",", ".")) * quantidadeExistente;
                @SuppressLint("DefaultLocale") String novoValor = String.format("%.2f", valorTotal).replace(".", ",");
                produtoModel.setValorSomado(novoValor);
                holder.valor.setText(novoValor);
                holder.quantidadeTexView.setText(quantidadeExistente + "");
            } else {
                lista.remove(position);
                CarrinhoUtils.removeProduto(c, produtoModel);
                notifyDataSetChanged();
            }
            CarrinhoActivity.calcularValorPagar();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolderCarrinho extends RecyclerView.ViewHolder {

        TextView nome, valor, quantidadeTexView;
        ImageView maisUmCarrinho, menosUmCarrinho;

        public ViewHolderCarrinho(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome_produto_carrinho);
            valor = itemView.findViewById(R.id.valor_produto_carrinho);
            quantidadeTexView = itemView.findViewById(R.id.quantidade_produto_carrinho);
            maisUmCarrinho = itemView.findViewById(R.id.maisUmCarrinho);
            menosUmCarrinho = itemView.findViewById(R.id.menosUmCarrinho);
        }
    }
}
