package pvt.testedev;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import pvt.testedev.model.Hero;
import pvt.testedev.model.ScanResult;

public class DetailsActivity extends Activity {
	private static final String OBJECT_KEY = "object_key";
	private TextView name, height, mass, hair, eyes, gender, birth, planet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		ScanResult obj = getIntent().getParcelableExtra(OBJECT_KEY);
		startFetchData(obj);
		
		name = (TextView) findViewById(R.id.tv_name);
		height = (TextView) findViewById(R.id.tv_height);
		mass = (TextView) findViewById(R.id.tv_mass);
		hair = (TextView) findViewById(R.id.tv_hair_color);
		eyes = (TextView) findViewById(R.id.tv_eyes_color);
		gender = (TextView) findViewById(R.id.tv_gender);
		birth = (TextView) findViewById(R.id.tv_birth_year);
		planet = (TextView) findViewById(R.id.tv_homeworld);
	}

	private void startFetchData(ScanResult obj) {
		String qrcode = obj.getQrcode();
		if(!qrcode.endsWith("?format=json"))
			qrcode += "?format=json";
		new GetScanDataTask().execute(qrcode);
	}

	public static Intent createOpenItem(Context from, ScanResult item) {
		Intent open = new Intent(from, DetailsActivity.class);
		open.putExtra(OBJECT_KEY, item);
		return open;
	}
	
	public void displayItem(Hero obj) {
		name.setText(obj.getName());
		height.setText(obj.getHeight());
		mass.setText(obj.getMass());
		hair.setText(obj.getHairColor());
		eyes.setText(obj.getEyeColor());
		gender.setText(obj.getGender());
		birth.setText(obj.getBirthYear());
		planet.setText(obj.getHomeworld());
		
		ListView films = (ListView) findViewById(R.id.lv_films);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		arrayAdapter.addAll(obj.getFilms());
		films.setAdapter(arrayAdapter);
	}
	
	class GetScanDataTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... params) {
			try{
				URL url = new URL(params[0]);
		        URLConnection openConnection = url.openConnection();
		        openConnection.connect();
		        InputStream inputStream = openConnection.getInputStream();
	
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        byte[] buffer = new byte[1024];
		        for (int n = inputStream.read(buffer); n >= 0; n = inputStream.read(buffer))
		            out.write(buffer, 0, n);
	
		        return out.toString();
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			try{
				Hero obj = new Gson().fromJson(result, Hero.class);
				displayItem(obj);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

}
