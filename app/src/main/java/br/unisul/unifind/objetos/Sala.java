package br.unisul.unifind.objetos;

/**
 * Created by Ronan Cardoso on 28/10/2015.
 */
public class Sala {

    private int id;
    private String descricao;
    private Double latitude, longitude;

    public Sala() {
    }

    public Sala(int id, String descricao, Double latitude, Double longitude) {
        this.id = id;
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
