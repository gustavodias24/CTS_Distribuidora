package benicio.solucoes.ctsdistribuidora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.ctsdistribuidora.adapters.AdapterProduto;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityCadatroClienteBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityTabelaPrecoBinding;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.model.ResponseModelProduto;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabelaPrecoActivity extends AppCompatActivity {

    private ActivityTabelaPrecoBinding mainBinding;

    private List<ProdutoModel> lista = new ArrayList<>();
    private AdapterProduto adapterProduto;

    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityTabelaPrecoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.carrinho.setOnClickListener(v -> startActivity(new Intent(this, CarrinhoActivity.class)));

        mainBinding.voltar.setOnClickListener(v -> finish());

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        mainBinding.recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerProdutos.setHasFixedSize(true);
        mainBinding.recyclerProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterProduto = new AdapterProduto(lista, this);
        mainBinding.recyclerProdutos.setAdapter(adapterProduto);

        apiService.get_produto().enqueue(new Callback<ResponseModelProduto>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseModelProduto> call, Response<ResponseModelProduto> response) {
                if (response.isSuccessful()) {
                    lista.clear();
                    lista.addAll(response.body().getMsg());
                    adapterProduto.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseModelProduto> call, Throwable throwable) {

            }
        });
    }
}