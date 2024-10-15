package benicio.solucoes.ctsdistribuidora;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Objects;
import java.util.UUID;

import benicio.solucoes.ctsdistribuidora.databinding.ActivityCadatroClienteBinding;
import benicio.solucoes.ctsdistribuidora.model.ClienteModel;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadatroClienteActivity extends AppCompatActivity {

    private ActivityCadatroClienteBinding mainBinding;

    private ClienteModel clienteModel;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadatroClienteBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        clienteModel = new ClienteModel();

        mainBinding.voltar.setOnClickListener(v -> finish());

        mainBinding.salvar.setOnClickListener(v -> {

            if (clienteModel.get_id().isEmpty()) {
                clienteModel.set_id(UUID.randomUUID().toString());
            }

            clienteModel.setRepresentante(Objects.requireNonNull(mainBinding.fieldRepresentante.getText()).toString());
            clienteModel.setCliente(Objects.requireNonNull(mainBinding.fieldCliente.getText()).toString());
            clienteModel.setDocumento(Objects.requireNonNull(mainBinding.fieldDoc.getText()).toString());
            clienteModel.setEndereco(Objects.requireNonNull(mainBinding.fieldEndereco.getText()).toString());
            clienteModel.setContato(Objects.requireNonNull(mainBinding.fieldContato.getText()).toString());
            clienteModel.setDatainicio(Objects.requireNonNull(mainBinding.fieldDataInicio.getText()).toString());
            clienteModel.setDatainicio(Objects.requireNonNull(mainBinding.fieldDataInicio.getText()).toString());
            clienteModel.setDesconto(Objects.requireNonNull(mainBinding.fieldDesconto.getText()).toString());


            apiService.create_cliente(clienteModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String msg = "Cliente criado com sucesso!";

                        Toast.makeText(CadatroClienteActivity.this, msg, Toast.LENGTH_SHORT).show();
                        mainBinding.fieldRepresentante.setText("");
                        mainBinding.fieldCliente.setText("");
                        mainBinding.fieldDoc.setText("");
                        mainBinding.fieldEndereco.setText("");
                        mainBinding.fieldContato.setText("");
                        mainBinding.fieldDataInicio.setText("");
                        mainBinding.fieldDesconto.setText("");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {

                }
            });

        });
    }
}