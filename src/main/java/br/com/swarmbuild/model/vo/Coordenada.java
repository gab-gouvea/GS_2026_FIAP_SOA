package br.com.swarmbuild.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordenada(Double latitude, Double longitude) {

    public Coordenada {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("Latitude deve estar entre -90 e 90");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Longitude deve estar entre -180 e 180");
        }
    }

    public double distanciaEuclidiana(Coordenada outra) {
        if (outra == null || outra.latitude == null || outra.longitude == null
                || this.latitude == null || this.longitude == null) {
            return Double.MAX_VALUE;
        }
        double dx = this.latitude - outra.latitude;
        double dy = this.longitude - outra.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
