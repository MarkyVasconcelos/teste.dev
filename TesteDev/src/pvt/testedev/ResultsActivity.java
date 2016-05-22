package pvt.testedev;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import pvt.testedev.model.ScanResult;

public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		
		ListView results = (ListView) findViewById(R.id.results_list);
		
		List<ScanResult> list = new ResultsDAO(this).list();
		Collections.reverse(list);
		final ResultsAdapter adapter = new ResultsAdapter(list);
		
		results.setAdapter(adapter);
		
		results.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemId, long arg3) {
				ScanResult item = adapter.getItem(itemId);
				openItem(item);
			}
		});
		//https://play.google.com/store/apps/details?id=jp.co.uraraworks.drancia
		//jp.co.uraraworks.drancia
		//market://details?id=jp.co.uraraworks.drancia
	}
	
	protected void openItem(ScanResult item) {
		String content = item.getQrcode();
		
		if(content.startsWith("http")){
			String lCContent = content.toLowerCase();
			
			//Url imagem
			if(lCContent.endsWith("png") || lCContent.endsWith("jpg") || lCContent.endsWith("jpeg")){
				downloadImageContent(content);
				return;
			}
			
			//Url market
			if(lCContent.contains("play.google.com/store/apps/details?id=")){
				openApplicationOrMarket(content);
				return;
			}
			
			//Url
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content)));
			return;
		} 

		//Url market
		if(content.startsWith("market://")){
			openApplicationOrMarket(content);
			return;
		}
		
		//Else = PackageName direto
		tryOpenAppOrMarket(content);
	}

	private void openApplicationOrMarket(String content) {
		String packageName = content.substring(content.lastIndexOf("id=") + 3 /* 3 = 'id='.lenght */, content.length());
		tryOpenAppOrMarket(packageName);
	}

	private void tryOpenAppOrMarket(String packageName) {
		Intent appIntent = getPackageManager().getLaunchIntentForPackage(packageName);
		if(appIntent != null){
			startActivity(appIntent);
			return;
		}
		
		try {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
		}
	}

	private void downloadImageContent(final String content) {
		findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
		new DownloadImageTask(content, new DownloadListener(){
			@Override
			public void onDownloadConcluded(boolean success) {
				Toast.makeText(ResultsActivity.this, "Download do item concluido com " + (success ? "sucesso" : "erro"), Toast.LENGTH_LONG).show();
				findViewById(R.id.loading_view).setVisibility(View.INVISIBLE);		
			}
		}).executeOnExecutor(executor);
	}
	
	private interface DownloadListener {
		void onDownloadConcluded(boolean success);
	}
	
	private static final Executor executor = Executors.newSingleThreadExecutor();
	private static class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {
		private String url;
		private DownloadListener downloadListener;

		public DownloadImageTask(String url, DownloadListener downloadListener) {
			this.url = url;
			this.downloadListener = downloadListener;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try{
				File publicPicsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				File resultFile = new File(publicPicsDir, url.substring(url.lastIndexOf("/") + 1));
				resultFile.createNewFile();
				
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				InputStream input = connection.getInputStream();
				
				OutputStream output = new FileOutputStream(resultFile);
				
				byte[] buffer = new byte[2 * 1024];
				try{
				    int bytesRead;
				    while ((bytesRead = input.read(buffer)) != -1) {
				      output.write(buffer, 0, bytesRead);
				    }
				} finally {
				    output.close();
				    input.close();
				    connection.disconnect();
				}
			}catch(Throwable e){
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			downloadListener.onDownloadConcluded(result);
		}
	}

	private class ResultsAdapter extends BaseAdapter {
		private final List<ScanResult> data;
		
		public ResultsAdapter(List<ScanResult> data) {
			this.data = data;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public ScanResult getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View recycleView, ViewGroup parent) {
			ViewGroup view = recycleView == null ? 					
					(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_result_item_view, parent, false)
					: (ViewGroup) recycleView;
			
			ScanResult item = getItem(arg0);
			
			((TextView)view.findViewById(R.id.qrcode)).setText(item.getQrcode());
			((TextView)view.findViewById(R.id.lat)).setText(item.getLat());
			((TextView)view.findViewById(R.id.lon)).setText(item.getLon());
			
			return view;
		}
	}
}
