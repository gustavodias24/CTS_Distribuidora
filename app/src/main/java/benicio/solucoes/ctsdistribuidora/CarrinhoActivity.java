package benicio.solucoes.ctsdistribuidora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import benicio.solucoes.ctsdistribuidora.adapters.AdapterProdutoCarrinho;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityMainBinding;
import benicio.solucoes.ctsdistribuidora.model.ClienteModel;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.model.ResonseModelCliente;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarrinhoActivity extends AppCompatActivity {

    public static ActivityCarrinhoBinding mainBinding;
    private AdapterProdutoCarrinho adapterProduto;

    private ApiService apiService;
    private List<ClienteModel> lista_clientes = new ArrayList<>();

    public static List<ProdutoModel> produtosCarrinho = new ArrayList<>();

    public static String descontoSelecionado = "0";
    public static String clienteSelecionado = "";

    public static SharedPreferences carrinho_prefs;
    public static SharedPreferences.Editor editor;

    @SuppressLint("SetTextI18n")
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

        carrinho_prefs = getSharedPreferences("carrinho_prefs", MODE_PRIVATE);
        editor = carrinho_prefs.edit();

        descontoSelecionado = carrinho_prefs.getString("descontoSelecionado", "0");
        clienteSelecionado = carrinho_prefs.getString("clienteSelecionado", "");

        mainBinding.descontoText.setText("Desconto Aplicado: " + descontoSelecionado + " %");
        mainBinding.autoCompleteClientes.setText(carrinho_prefs.getString("clienteSelecionado", ""));
        mainBinding.editRepresentante.setText(carrinho_prefs.getString("Representante", ""));

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        apiService.get_clientes().enqueue(new Callback<ResonseModelCliente>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResonseModelCliente> call, Response<ResonseModelCliente> response) {
                if (response.isSuccessful()) {
                    lista_clientes.addAll(response.body().getMsg());
                }
                // Criar uma lista de nomes dos clientes para o AutoCompleteTextView
                List<String> nomesClientes = new ArrayList<>();
                for (ClienteModel cliente : lista_clientes) {
                    nomesClientes.add(cliente.getCliente());
                }
                // Adaptador para o AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CarrinhoActivity.this, android.R.layout.simple_dropdown_item_1line, nomesClientes);
                mainBinding.autoCompleteClientes.setAdapter(adapter);

                // Ação quando um cliente é selecionado
                mainBinding.autoCompleteClientes.setOnItemClickListener((parent, view, position, id) -> {
                    clienteSelecionado = parent.getItemAtPosition(position).toString();

                    // Procurar pelo cliente na lista e pegar o desconto
                    for (ClienteModel cliente : lista_clientes) {
                        if (cliente.getCliente().equals(clienteSelecionado)) {
                            descontoSelecionado = cliente.getDesconto();
                            mainBinding.descontoText.setText("Desconto Aplicado: " + descontoSelecionado + " %");
                            calcularValorPagar();
                            Toast.makeText(CarrinhoActivity.this, clienteSelecionado + " selecionado!", Toast.LENGTH_SHORT).show();
                            editor.putString("descontoSelecionado", descontoSelecionado).apply();
                            editor.putString("clienteSelecionado", clienteSelecionado).apply();
                            break;
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResonseModelCliente> call, Throwable throwable) {
            }
        });

        mainBinding.enviarZap.setOnClickListener(v -> compartilharTexto());
        mainBinding.voltar.setOnClickListener(v ->

                finish());

        mainBinding.recyclerProdutos.setLayoutManager(new

                LinearLayoutManager(this));
        mainBinding.recyclerProdutos.setHasFixedSize(true);
        mainBinding.recyclerProdutos.addItemDecoration(new

                DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        produtosCarrinho.clear();
        produtosCarrinho.addAll(CarrinhoUtils.returnProdutos(this));
        adapterProduto = new AdapterProdutoCarrinho(produtosCarrinho, this);
        mainBinding.recyclerProdutos.setAdapter(adapterProduto);

        calcularValorPagar();
    }

    @SuppressLint("DefaultLocale")
    public static void calcularValorPagar() {

        int descontoInteiro = 0;
        try {
            descontoInteiro = Integer.parseInt(descontoSelecionado.replace(",", "."));
        } catch (Exception e) {
        }

        float somaTotal = 0;
        for (ProdutoModel produtoModel : produtosCarrinho) {
            try {
                Float valorProduto = Float.parseFloat(produtoModel.getValorSomado().replace(",", "."));
                somaTotal += valorProduto;
            } catch (Exception e) {
            }
        }

        mainBinding.valorTotalText.setText(String.format("Valor Total: R$ %.2f", somaTotal));


        float removerPrecoFinal = ((float) descontoInteiro / 100) * somaTotal;
        float valorDescontato = somaTotal - removerPrecoFinal;
        mainBinding.valorDesconto.setText(String.format("Valor a pagar: R$ %.2f", valorDescontato));


    }


    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    private void compartilharTexto() {

        String representanteString = mainBinding.editRepresentante.getText().toString();
        editor.putString("Representante", representanteString).apply();

        if (clienteSelecionado.isEmpty()) {
            Toast.makeText(this, "Selecione um cliente primeiro!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            StringBuilder infos = new StringBuilder();
            infos.append(representanteString).append("\n\n");
            infos.append(clienteSelecionado).append("\n\n");

            for (ProdutoModel produtoModel : produtosCarrinho) {
                Float valorUnitario = Float.parseFloat(produtoModel.getValor().replace(",", "."));
                Float valorSomado = Float.parseFloat(produtoModel.getValorSomado().replace(",", "."));
                int quantidadeComprada = (int) (valorSomado / valorUnitario);

                infos.append(String.format("%03d", quantidadeComprada))
                        .append(" ").append(produtoModel.getNome())
                        .append(" ").append(produtoModel.getValorSomado()).append('\n');
            }

            infos.append("\n")
                    .append("_" + mainBinding.valorTotalText.getText().toString().split(":")[1].trim() + "_").append("\n")
                    .append("_" + mainBinding.descontoText.getText().toString().split(":")[1].trim() + "_").append("\n")
                    .append("*" + mainBinding.valorDesconto.getText().toString().split(":")[1].toUpperCase().trim() + "*").append("\n");


            intent.putExtra(Intent.EXTRA_TEXT, infos.toString());

            // Criando o chooser (menu de seleção de apps)
            Intent chooser = Intent.createChooser(intent, "Compartilhar usando");

            // Iniciando o intent de compartilhamento
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);

                produtosCarrinho.clear();
                adapterProduto.notifyDataSetChanged();
                CarrinhoUtils.clearProdutos(this);
            }
        }

    }
}