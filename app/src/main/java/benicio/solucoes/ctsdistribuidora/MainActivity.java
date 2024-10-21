package benicio.solucoes.ctsdistribuidora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import benicio.solucoes.ctsdistribuidora.databinding.ActivityMainBinding;
import benicio.solucoes.ctsdistribuidora.databinding.LayoutSenhaAdmBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    private static final String SENHA_ADM = "admin";
    private Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );


        mainBinding.tabelaPreco.setOnClickListener(v -> startActivity(new Intent(this, TabelaPrecoActivity.class)));
        mainBinding.ofertaSemana.setOnClickListener(v -> startActivity(new Intent(this, OfertasSemanaActivity.class)));

        mainBinding.buttonAdminCadastro.setOnClickListener(v -> {
            LayoutSenhaAdmBinding senhaAdmBinding = LayoutSenhaAdmBinding.inflate(getLayoutInflater());
            senhaAdmBinding.prosseguir.setOnClickListener(v2 -> {
                String senha = senhaAdmBinding.senhaInput.getText().toString();

                if (senha.equals(SENHA_ADM)) {
                    startActivity(new Intent(this, MainAdminActivity.class));
                    senhaAdmBinding.senhaInput.setText("");
                    d.dismiss();
                }
            });
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setView(senhaAdmBinding.getRoot());
            d = b.create();
            d.show();
        });


    }
}