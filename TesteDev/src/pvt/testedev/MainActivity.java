package pvt.testedev;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import pvt.testedev.model.ScanResult;

public class MainActivity extends Activity {

	private static final int REQUEST_SCAN = 111;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.start_scan).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
			}
		});
		
		findViewById(R.id.scan_results).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, ResultsActivity.class));
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_SCAN && data != null){
			String decodedResult = data.getStringExtra(CaptureActivity.DECODED_CODE_KEY);
			startAddQrcodeWithGeoWorkflow(decodedResult);
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startAddQrcodeWithGeoWorkflow(final String decodedResult) {
		findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
		queryLocation(new LocListener(){
			@Override
			public void onLocationReceived(Location location) {
				ScanResult result = new ScanResult();
				result.setQrcode(decodedResult);
				if(location == null){
					result.setLat("0.0");
					result.setLon("0.0");
				}else{
					result.setLat(Double.toString(location.getLatitude()));
					result.setLon(Double.toString(location.getLongitude()));
				}
				
				ResultsDAO dao = new ResultsDAO(MainActivity.this);
				dao.insert(result);

				findViewById(R.id.loading_view).setVisibility(View.INVISIBLE);
				startActivity(new Intent(MainActivity.this, ResultsActivity.class));
			}
		});
	}

	public interface LocListener {
		void onLocationReceived(Location location);
	}
	private void queryLocation(final LocListener listener) {
		LocationManager gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria qry = new Criteria();
		qry.setAccuracy(Criteria.ACCURACY_FINE);
		qry.setAltitudeRequired(false);
		qry.setBearingRequired(false);
		qry.setCostAllowed(true);
		qry.setPowerRequirement(Criteria.POWER_LOW);
		
		gps.requestSingleUpdate(qry, new LocationListener() {
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) { }
			
			@Override
			public void onProviderEnabled(String arg0) { }
			
			@Override
			public void onProviderDisabled(String arg0) { }
			
			@Override
			public void onLocationChanged(Location arg0) {
				listener.onLocationReceived(arg0);
			}
		}, getMainLooper());
	}
}
