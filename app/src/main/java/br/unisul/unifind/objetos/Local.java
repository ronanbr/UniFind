package br.unisul.unifind.objetos;

/**
 * Created by ronan.cardoso on 30/10/2015.
 */
public class Local {

    String descricao;
    Double latitude, longitude;

    public Local(String descricao, Double latitude, Double longitude) {
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Local() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
