package pvt.testedev;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
				startActivity(DetailsActivity.createOpenItem(ResultsActivity.this, item));
			}
		});
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
