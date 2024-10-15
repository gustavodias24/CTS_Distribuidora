package benicio.solucoes.ctsdistribuidora.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseModelProduto {
    List<ProdutoModel> msg = new ArrayList<>();

    public ResponseModelProduto() {
    }

    public List<ProdutoModel> getMsg() {
        return msg;
    }

    public void setMsg(List<ProdutoModel> msg) {
        this.msg = msg;
    }
}
