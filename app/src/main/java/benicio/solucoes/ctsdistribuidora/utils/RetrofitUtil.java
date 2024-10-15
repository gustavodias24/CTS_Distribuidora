package benicio.solucoes.ctsdistribuidora.utils;

import benicio.solucoes.ctsdistribuidora.services.ApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    public static Retrofit createRetrofit() {
        return new Retrofit.Builder().baseUrl("http://147.79.83.218:5000/").addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static ApiService createService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
