package benicio.solucoes.ctsdistribuidora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import benicio.solucoes.ctsdistribuidora.databinding.ActivityMainAdminBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityMainBinding;
import benicio.solucoes.ctsdistribuidora.databinding.LayoutSenhaAdmBinding;

public class MainAdminActivity extends AppCompatActivity {

    private ActivityMainAdminBinding mainBinding;

    private Dialog dialogCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.imageView2.setOnClickListener(v -> finish());

        mainBinding.cadastroCliente.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Escolha uma Opção");

            b.setPositiveButton("Ir para Cadastro", (d, i) -> {
                startActivity(new Intent(this, CadatroClienteActivity.class));
                dialogCliente.dismiss();
            });
            b.setNegativeButton("Listagem de Cliente", (d, i) -> {
                startActivity(new Intent(this, ListagemClienteActivity.class));
                dialogCliente.dismiss();
            });

            dialogCliente = b.create();
            dialogCliente.show();
        });

        mainBinding.cadastroProduto.setOnClickListener(v -> startActivity(new Intent(this, CadastroProdutoActivity.class)));
        mainBinding.tabelaPreco.setOnClickListener(v -> {
            Intent i = new Intent(this, TabelaPrecoActivity.class);
            i.putExtra("isAdmin", true);
            startActivity(i);
        });
    }
}