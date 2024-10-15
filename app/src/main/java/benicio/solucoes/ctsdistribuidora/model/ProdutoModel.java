package benicio.solucoes.ctsdistribuidora.model;

public class ProdutoModel {
    String _id = "";
    String nome = "";
    String descricao = "";
    String valor = "";
    String valorSomado = valor;

    boolean promo;

    boolean checado = false;

    public ProdutoModel() {
    }

    public String getValorSomado() {

        if (!valorSomado.isEmpty())
            return valorSomado;

        return valor;
    }

    public void setValorSomado(String valorSomado) {
        this.valorSomado = valorSomado;
    }

    public boolean isChecado() {
        return checado;
    }

    public void setChecado(boolean checado) {
        this.checado = checado;
    }

    public String get_id() {
        return _id;
    }

    public boolean isPromo() {
        return promo;
    }

    public void setPromo(boolean promo) {
        this.promo = promo;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}