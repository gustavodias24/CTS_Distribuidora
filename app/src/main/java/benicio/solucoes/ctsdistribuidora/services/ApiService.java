package benicio.solucoes.ctsdistribuidora.services;

import benicio.solucoes.ctsdistribuidora.model.ClienteModel;
import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;
import benicio.solucoes.ctsdistribuidora.model.ResonseModelCliente;
import benicio.solucoes.ctsdistribuidora.model.ResponseModelProduto;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {


    @POST("cliente")
    Call<Void> create_cliente(@Body ClienteModel clienteModel);

    @GET("cliente")
    Call<ResonseModelCliente> get_clientes();

    @GET("produto")
    Call<ResponseModelProduto> get_produto();

    @GET("produto")
    Call<ResponseModelProduto> get_filter_produto(@Query("query") String query, @Query("value") String value);

    @POST("produto")
    Call<Void> create_produto(@Body ProdutoModel produtoModel);


    @Multipart
    @POST("/upload")
    Call<Void> uploadImagem(
            @Part MultipartBody.Part imagem,
            @Part("idDoProduto") String idDoProduto  // Aqui, String e não RequestBody
    );

}
