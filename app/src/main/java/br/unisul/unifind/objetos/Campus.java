package br.unisul.unifind.objetos;

/**
 * Created by Ronan Cardoso on 28/10/2015.
 */
public class Campus {

    private int id;
    private String descricao;

    public Campus() {
    }

    public Campus(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
