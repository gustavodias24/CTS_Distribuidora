package benicio.solucoes.ctsdistribuidora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import benicio.solucoes.ctsdistribuidora.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityMenuCategoriasBinding;

public class MenuCategoriasActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMenuCategoriasBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuCategoriasBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.chocolates.setOnClickListener(this);
        mainBinding.doces.setOnClickListener(this);
        mainBinding.bebidas.setOnClickListener(this);
        mainBinding.saldgadinhos.setOnClickListener(this);
        mainBinding.embalagens.setOnClickListener(this);
        mainBinding.balas.setOnClickListener(this);
        mainBinding.outros.setOnClickListener(this);

        mainBinding.voltar.setOnClickListener( v -> finish());

    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, TabelaPrecoActivity.class);
        i.putExtra("isAdmin", Objects.requireNonNull(getIntent().getExtras()).getBoolean("isAdmin", false));
        i.putExtra("categoria", ((Button) view).getText().toString());
        startActivity(i);
    }
}