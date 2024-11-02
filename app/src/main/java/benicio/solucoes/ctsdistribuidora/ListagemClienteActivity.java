package benicio.solucoes.ctsdistribuidora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import benicio.solucoes.ctsdistribuidora.adapters.AdapterCliente;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityListagemClienteBinding;
import benicio.solucoes.ctsdistribuidora.model.ClienteModel;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.model.ResonseModelCliente;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.CarrinhoUtils;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListagemClienteActivity extends AppCompatActivity {

    private ActivityListagemClienteBinding mainBinding;
    private AdapterCliente adapterCliente;
    private List<ClienteModel> lista = new ArrayList<>();

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityListagemClienteBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.recyclerClientes.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerClientes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mainBinding.recyclerClientes.setHasFixedSize(true);
        adapterCliente = new AdapterCliente(lista, this);
        mainBinding.recyclerClientes.setAdapter(adapterCliente);

        mainBinding.voltar.setOnClickListener(v -> finish());

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        mainBinding.enviarZap.setOnClickListener(v -> compartilharTexto());


    }

    @Override
    protected void onStart() {
        super.onStart();
        apiService.get_clientes().enqueue(new Callback<ResonseModelCliente>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResonseModelCliente> call, Response<ResonseModelCliente> response) {
                if (response.isSuccessful()) {
                    lista.clear();
                    assert response.body() != null;
                    lista.addAll(response.body().getMsg());
                    adapterCliente.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResonseModelCliente> call, Throwable throwable) {

            }
        });
    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    private void compartilharTexto() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, imprimirClientesPorRepresentante(lista));

        // Criando o chooser (menu de seleção de apps)
        Intent chooser = Intent.createChooser(intent, "Compartilhar usando");

        // Iniciando o intent de compartilhamento
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public String imprimirClientesPorRepresentante(List<ClienteModel> clientes) {
        Map<String, List<String>> representantesMap = new HashMap<>();

        // Agrupa os clientes por representante
        for (ClienteModel cliente : clientes) {
            representantesMap
                    .computeIfAbsent(cliente.getRepresentante(), k -> new ArrayList<>())
                    .add(cliente.getCliente());
        }

        StringBuilder info = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : representantesMap.entrySet()) {
            info.append("*Representante:* " + entry.getKey()).append("\n");
            for (String clienteNome : entry.getValue()) {
                info.append(clienteNome).append("\n");
            }
        }

        return info.toString();
    }
}


