package br.unisul.unifind.objetos;

import java.io.Serializable;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class VersaoBD implements Serializable {

    private static final long serialVersionUID = 1L;

    private int versaoBD;


    public VersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    public VersaoBD() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getVersaoBD() {
        return versaoBD;
    }

    public void setVersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    @Override
    public String toString() {
        return ""+versaoBD;
    }
}
