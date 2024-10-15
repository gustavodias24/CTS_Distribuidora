package benicio.solucoes.ctsdistribuidora;

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

import benicio.solucoes.ctsdistribuidora.adapters.AdapterProduto;
import benicio.solucoes.ctsdistribuidora.adapters.AdapterProdutoCarrinho;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityMainBinding;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;

public class CarrinhoActivity extends AppCompatActivity {

    private ActivityCarrinhoBinding mainBinding;
    private AdapterProdutoCarrinho adapterProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCarrinhoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.voltar.setOnClickListener(v -> finish());

        mainBinding.recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerProdutos.setHasFixedSize(true);
        mainBinding.recyclerProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterProduto = new AdapterProdutoCarrinho(CarrinhoUtils.returnProdutos(this), this);
        mainBinding.recyclerProdutos.setAdapter(adapterProduto);


    }
}