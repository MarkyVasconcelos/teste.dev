package pvt.testedev.model;

public class ScanResult {
	/** Database id */
	private long id; 
	private String qrcode;
	private String lat, lon;
	
	public ScanResult(){ }
	
	public ScanResult(long id, String qrcode, String lat, String lon){
		this.id = id;
		this.qrcode = qrcode;
		this.lat = lat;
		this.lon = lon;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
