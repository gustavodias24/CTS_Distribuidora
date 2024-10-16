package benicio.solucoes.ctsdistribuidora.model;

import java.util.ArrayList;
import java.util.List;

public class ResonseModelCliente {
    List<ClienteModel> msg = new ArrayList<>();

    public ResonseModelCliente() {
    }

    public List<ClienteModel> getMsg() {
        return msg;
    }

    public void setMsg(List<ClienteModel> msg) {
        this.msg = msg;
    }
}
