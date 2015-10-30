package br.unisul.unifind.objetos;

/**
 * Created by ronan.cardoso on 30/10/2015.
 */
public class Tabela {

    String nome, nomeNoBD;

    public Tabela() {
    }

    public Tabela(String nome, String nomeNoBD) {
        this.nome = nome;
        this.nomeNoBD = nomeNoBD;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeNoBD() {
        return nomeNoBD;
    }

    public void setNomeNoBD(String nomeNoBD) {
        this.nomeNoBD = nomeNoBD;
    }

    @Override
    public String toString() {
        return nome;
    }
}
