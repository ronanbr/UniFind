package br.unisul.unifind.objetos;

/**
 * Created by ronan.cardoso on 18/03/2016.
 */
public class ItemMenu {

    private int id;
    private String descricao;

    public ItemMenu(int id, String descricao) {
        this.descricao = descricao;
        this.id = id;
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
