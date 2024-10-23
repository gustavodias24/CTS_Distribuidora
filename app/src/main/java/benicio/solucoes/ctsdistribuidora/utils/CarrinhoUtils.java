package benicio.solucoes.ctsdistribuidora.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.ctsdistribuidora.model.ProdutoModel;

public class CarrinhoUtils {

    public static final String NOME_PREFS = "carrinho_prefs";
    public static final String LISTA_NAME = "carrinho_lista";

    public static void clearProdutos(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(NOME_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LISTA_NAME, new Gson().toJson(new ArrayList<>())).apply();
    }

    public static List<ProdutoModel> returnProdutos(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(NOME_PREFS, Context.MODE_PRIVATE);
        List<ProdutoModel> lista = new ArrayList<>();

        List<ProdutoModel> listaExistente = new Gson().fromJson(prefs.getString(LISTA_NAME, ""), new TypeToken<List<ProdutoModel>>() {
        }.getType());

        if (listaExistente != null)
            return listaExistente;

        return lista;
    }

    public static void addProduto(Context c, ProdutoModel produto) {
        SharedPreferences prefs = c.getSharedPreferences(NOME_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = prefs.edit();


        List<ProdutoModel> lista = new ArrayList<>();

        List<ProdutoModel> listaExistente = new Gson().fromJson(prefs.getString(LISTA_NAME, ""), new TypeToken<List<ProdutoModel>>() {
        }.getType());

        if (listaExistente == null) {
            listaExistente = new ArrayList<>();
        }

        boolean cadastrarNovoProduto = true;
        for (int i = 0; i < listaExistente.size(); i++) {
            ProdutoModel produtoExistente = listaExistente.get(i);
            lista.add(produtoExistente);
            if (produtoExistente.get_id().equals(produto.get_id())) {
                cadastrarNovoProduto = false;
            }
        }

        if (cadastrarNovoProduto) {
            lista.add(produto);
        }

        editor.putString(LISTA_NAME, new Gson().toJson(lista)).apply();
    }

    public static void removeProduto(Context c, ProdutoModel produto) {
        SharedPreferences prefs = c.getSharedPreferences(NOME_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = prefs.edit();


        List<ProdutoModel> lista = new ArrayList<>();

        List<ProdutoModel> listaExistente = new Gson().fromJson(prefs.getString(LISTA_NAME, ""), new TypeToken<List<ProdutoModel>>() {
        }.getType());

        if (listaExistente != null) {
            lista.addAll(listaExistente);
        }

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).get_id().equals(produto.get_id())) {
                lista.remove(i);
            }
        }

        editor.putString(LISTA_NAME, new Gson().toJson(lista)).apply();
    }
}
