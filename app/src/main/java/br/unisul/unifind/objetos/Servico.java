package br.unisul.unifind.objetos;

/**
 * Created by Ronan Cardoso on 28/10/2015.
 */
public class Servico {
    private int id;
    private String descricao;
    private Double latitude, longitude;
    private Campus campus;

    public Servico() {
    }

    public Servico(int id, String descricao, Double latitude, Double longitude, Campus campus) {
        this.id = id;
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.campus = campus;
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

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Campus getCampus() { return campus; }

    public void setCampus(Campus campus) { this.campus = campus; }

    @Override
    public String toString() {
        return descricao;
    }
}
