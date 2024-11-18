package benicio.solucoes.ctsdistribuidora;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private String categoriaSelecionada = "";
    private Dialog dialogSelectImagem;

    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ApiService apiService;

    private ProdutoModel produtoModel;
    private ActivityCadastroProdutoBinding mainBinding;

    private boolean isUpdate = false;

    @SuppressLint("QueryPermissionsNeeded")
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

        configurarSpinnerCategoria();

        apiService = RetrofitUtil.createService(
                RetrofitUtil.createRetrofit()
        );

        mainBinding.voltar.setOnClickListener(v -> finish());

        mainBinding.camera.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Escolha um opção");
            b.setPositiveButton("Galeria", (d, i) -> {
                dialogSelectImagem.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            });
            b.setNegativeButton("Câmera", (d, i) -> {
                dialogSelectImagem.dismiss();
                openCamera();
            });
            dialogSelectImagem = b.create();
            dialogSelectImagem.show();
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

            switch (produtoModel.getCategoria()) {
                case "Chocolates":
                    mainBinding.spinnerCategorias.setSelection(0);
                    break;
                case "Doces":
                    mainBinding.spinnerCategorias.setSelection(1);
                    break;
                case "Bebidas":
                    mainBinding.spinnerCategorias.setSelection(2);
                    break;
                case "Salgadinhos":
                    mainBinding.spinnerCategorias.setSelection(3);
                    break;
                case "Embalagens":
                    mainBinding.spinnerCategorias.setSelection(4);
                    break;
                case "Balas":
                    mainBinding.spinnerCategorias.setSelection(5);
                    break;
                case "Outros":
                    mainBinding.spinnerCategorias.setSelection(6);
                    break;
            }

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
            try {
                uploadImagem(imageUri, produtoModel.get_id());
            } catch (Exception ignored) {
                Log.d("mayara", "onCreate: " + ignored.getMessage());
            }

            apiService.create_produto(new ProdutoModel(
                    produtoModel.get_id(),
                    Objects.requireNonNull(mainBinding.fieldNome.getText()).toString(),
                    Objects.requireNonNull(mainBinding.fieldDescricao.getText()).toString(),
                    Objects.requireNonNull(Objects.requireNonNull(mainBinding.fieldValor.getText()).toString().replace(",", ".")),
                    mainBinding.checkPromo.isChecked(),
                    categoriaSelecionada
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

    private void configurarSpinnerCategoria() {
        // Referência ao Spinner
        Spinner spinnerCategorias = findViewById(R.id.spinnerCategorias);

        // Lista de opções
        String[] categorias = {"Chocolates", "Doces", "Bebidas", "Salgadinhos", "Embalagens", "Balas", "Outros"};

        // Adaptador para o Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapter);
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Salva a seleção na variável
                categoriaSelecionada = categorias[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Pode deixar vazio ou definir um valor padrão
//                categoriaSelecionada = "Nenhuma";
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            mainBinding.camera.setImageURI(imageUri);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mainBinding.camera.setImageURI(imageUri);
        }

    }

    public void uploadImagem(Uri imageUri, String idDoProduto) {
        try {
            // Obtenha um InputStream do URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Crie um RequestBody a partir do InputStream
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), inputStream.readAllBytes());

            // Use o nome do arquivo desejado para o upload
            MultipartBody.Part body = MultipartBody.Part.createFormData("imagem", "image.jpg", requestFile);

            // Chame a API para fazer o upload
            apiService.uploadImagem(body, idDoProduto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("mayara", "Upload bem-sucedido");
                    } else {
                        Log.d("mayara", "Falha no upload: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("mayara", "Erro: " + t.getMessage());
                }
            });

        } catch (IOException e) {
            Log.e("mayara", "Erro ao abrir o InputStream: " + e.getMessage());
        }
    }

    private void openCamera() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "benicio.solucoes.ctsdistribuidora.fileprovider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}