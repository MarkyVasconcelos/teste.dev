package pvt.testedev;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pvt.testedev.model.ScanResult;

public class ResultsDAO extends SQLiteOpenHelper {
	private static final String DB_NAME = "pvt_db";
	private static final int DB_VS = 1;
	
	public ResultsDAO(Context ctx) {
		super(ctx, DB_NAME, null, DB_VS);
	}
	
	public void insert(ScanResult result) {
		ContentValues values = new ContentValues();
		values.put(CONTENT_FIELD, result.getQrcode());
		values.put(GEO_LAT_FIELD, result.getLat());
		values.put(GEO_LON_FIELD, result.getLon());
		
		SQLiteDatabase writableDatabase = getWritableDatabase();
		long id = writableDatabase.insert(RESULTS_TABLE_NAME, null, values);
		result.setId(id);
		writableDatabase.close();
	}

	public List<ScanResult> list(){
		List<ScanResult> result = new ArrayList<ScanResult>();
		
		SQLiteDatabase readableDatabase = getReadableDatabase();
		Cursor query = readableDatabase.query(RESULTS_TABLE_NAME, null, null, null, null, null, null);
		
		if(query.moveToFirst()){
			int idIdx = query.getColumnIndex(ID_FIELD);
			int contentIdx = query.getColumnIndex(CONTENT_FIELD);
			int latIdx = query.getColumnIndex(GEO_LAT_FIELD);
			int lonIdx = query.getColumnIndex(GEO_LON_FIELD);
			
			do{
				long id = query.getLong(idIdx);
				String qrCode = query.getString(contentIdx);
				String lat = query.getString(latIdx);
				String lon = query.getString(lonIdx);
				
				result.add(new ScanResult(id, qrCode, lat, lon));
			} while(query.moveToNext());
		}
		
		query.close();
		readableDatabase.close();
		
		return result;
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(CREATE_RESULTS_TABLE_SQL);
	}

	@Override public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) { }
	
	private static final String RESULTS_TABLE_NAME = "scan_results";
	private static final String ID_FIELD = "_id";
	private static final String CONTENT_FIELD = "content";
	private static final String GEO_LAT_FIELD = "lat";
	private static final String GEO_LON_FIELD = "lon";
	
	
	private static final String CREATE_RESULTS_TABLE_SQL;
	
	static {
		StringBuilder createResultsTableSqlBuilder = new StringBuilder();
		createResultsTableSqlBuilder.append("CREATE TABLE ").append(RESULTS_TABLE_NAME);
		createResultsTableSqlBuilder.append("(");
		createResultsTableSqlBuilder.append(ID_FIELD).append(" integer primary key autoincrement, ");
		createResultsTableSqlBuilder.append(CONTENT_FIELD).append(" text, ");
		createResultsTableSqlBuilder.append(GEO_LAT_FIELD).append(" text, ");
		createResultsTableSqlBuilder.append(GEO_LON_FIELD).append(" text ");
		createResultsTableSqlBuilder.append(");");
		
		CREATE_RESULTS_TABLE_SQL = createResultsTableSqlBuilder.toString();
	}
}
