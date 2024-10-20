package benicio.solucoes.ctsdistribuidora;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import benicio.solucoes.ctsdistribuidora.databinding.ActivityCadastroProdutoBinding;
import benicio.solucoes.ctsdistribuidora.databinding.ActivityCadatroClienteBinding;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.services.ApiService;
import benicio.solucoes.ctsdistribuidora.utils.RetrofitUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroProdutoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ApiService apiService;

    private ProdutoModel produtoModel;
    private ActivityCadastroProdutoBinding mainBinding;

    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroProdutoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        mainBinding.voltar.setOnClickListener(v -> finish());

        mainBinding.camera.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isUpdate = true;
            produtoModel = new Gson().fromJson(
                    b.getString("produto", ""),
                    new TypeToken<ProdutoModel>() {
                    }.getType()
            );

            imageUri = Uri.parse("http://147.79.83.218:5000/imagem/" + '"' + produtoModel.get_id() + '"');
            Picasso.get().load(imageUri).into(mainBinding.camera, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    mainBinding.camera.setImageResource(R.drawable.camera);
                }
            });

            mainBinding.fieldNome.setText(produtoModel.getNome());
            mainBinding.fieldDescricao.setText(produtoModel.getDescricao());
            mainBinding.fieldValor.setText(produtoModel.getValor());
            mainBinding.checkPromo.setChecked(produtoModel.isPromo());

        } else {
            produtoModel = new ProdutoModel();
        }

        mainBinding.salvar.setOnClickListener(v -> {
            if (produtoModel.get_id().isEmpty()) {
                produtoModel.set_id(UUID.randomUUID().toString());
            }

            uploadImagem(imageUri, produtoModel.get_id());

            apiService.create_produto(new ProdutoModel(
                    produtoModel.get_id(),
                    Objects.requireNonNull(mainBinding.fieldNome.getText()).toString(),
                    Objects.requireNonNull(mainBinding.fieldDescricao.getText()).toString(),
                    Objects.requireNonNull(Objects.requireNonNull(mainBinding.fieldValor.getText()).toString().replace(",", ".")),
                    mainBinding.checkPromo.isChecked()
            )).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String msg = "";
                        if (isUpdate) {
                            msg = "Produto atualizado com sucesso!";
                        } else {
                            msg = "Produto cadastrado com sucesso!";
                            mainBinding.camera.setImageResource(R.drawable.camera);
                            mainBinding.fieldNome.setText("");
                            mainBinding.fieldDescricao.setText("");
                            mainBinding.fieldValor.setText("");
                            mainBinding.checkPromo.setChecked(false);
                        }
                        Toast.makeText(CadastroProdutoActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {

                }
            });

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            mainBinding.camera.setImageURI(imageUri); // Exibe a imagem selecionada na ImageView
        }
    }

    public void uploadImagem(Uri imageUri, String idDoProduto) {
        // Obtenha o caminho absoluto da imagem a partir do URI
        String imagePath = getRealPathFromURI(imageUri);
        if (imagePath == null) return;

        File file = new File(imagePath);

        // Converter o arquivo para RequestBody e MultipartBody.Part
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imagem", file.getName(), requestFile);

        apiService.uploadImagem(body, idDoProduto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Upload foi bem-sucedido
                    Log.d("mayara", "Upload bem-sucedido");
                } else {
                    // Falha na resposta
                    Log.d("mayara", "Falha no upload: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("mayara", "Erro: " + t.getMessage());
            }
        });

    }

    // Função para obter o caminho real da imagem a partir do URI
    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }
}