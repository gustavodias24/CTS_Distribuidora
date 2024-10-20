package benicio.solucoes.ctsdistribuidora.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.ctsdistribuidora.CadastroProdutoActivity;
import benicio.solucoes.ctsdistribuidora.R;
import benicio.solucoes.ctsdistribuidora.TabelaPrecoActivity;
import benicio.solucoes.ctsdistribuidora.databinding.LayoutInfoProdutoBinding;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.model.ResponseModelProduto;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Response;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    ApiService apiService = RetrofitUtil.createService(RetrofitUtil.createRetrofit());

    List<ProdutoModel> lista;
    Activity c;

    boolean isAdmin;

    public AdapterProduto(List<ProdutoModel> lista, Activity c, boolean isAdmin) {
        this.lista = lista;
        this.c = c;
        this.isAdmin = isAdmin;
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

        if (isAdmin) {
            holder.layout_admin.setVisibility(View.VISIBLE);
            holder.excluir_produto.setOnClickListener(v -> {
                apiService.delete_produto(
                        produtoModel.get_id()
                ).enqueue(new retrofit2.Callback<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(c, "Excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            lista.remove(position);
                            TabelaPrecoActivity.adapterProduto.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Toast.makeText(c, "Tente novamente!", Toast.LENGTH_SHORT).show();
                        Log.d("mayara", "onFailure: " + throwable.getMessage());
                    }
                });
            });
            holder.editar_produto.setOnClickListener(v -> {
                Intent i = new Intent(c, CadastroProdutoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("produto", new Gson().toJson(produtoModel));
                c.startActivity(i);
            });
        }


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
        LinearLayout layout_admin;
        Button excluir_produto, editar_produto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.check_carrinho);
            nome = itemView.findViewById(R.id.nome_produto);
            valor = itemView.findViewById(R.id.valor_produto);
            layout_admin = itemView.findViewById(R.id.layout_admin);
            excluir_produto = itemView.findViewById(R.id.excluir_produto);
            editar_produto = itemView.findViewById(R.id.editar_produto);
        }
    }
}
