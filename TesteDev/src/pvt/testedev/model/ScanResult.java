package pvt.testedev.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScanResult implements Parcelable {
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

	public ScanResult(Parcel source) {
		this(source.readLong(), source.readString(), source.readString(), source.readString());
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

	public static Parcelable.Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
		
		@Override
		public ScanResult[] newArray(int size) {
			return new ScanResult[size];
		}
		
		@Override
		public ScanResult createFromParcel(Parcel source) {
			return new ScanResult(source);
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeLong(id);
		arg0.writeString(qrcode);
		arg0.writeString(lat);
		arg0.writeString(lon);
	}
}
