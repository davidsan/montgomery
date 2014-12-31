package fr.davidsan.montgomery.app.transfer.in;

public class GeoTransfer {
	private Long id;
	private Double geolat;
	private Double geolon;

	public GeoTransfer(Long id, Double geolat, Double geolon) {
		super();
		this.id = id;
		this.geolat = geolat;
		this.geolon = geolon;
	}

	public GeoTransfer() {
		// dummy ctor
	}

	public Long getId() {
		return id;
	}

	public Double getGeolat() {
		return geolat;
	}

	public Double getGeolon() {
		return geolon;
	}

}
