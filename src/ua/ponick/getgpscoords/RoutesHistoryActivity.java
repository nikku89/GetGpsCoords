package ua.ponick.getgpscoords;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RoutesHistoryActivity extends Activity {

	final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routes_history);

		// getting filename from intent
		FileOperations fo2 = new FileOperations(this, getIntent().getExtras()
				.getString("filename"));

		Map<String, ArrayList<String>> routes = fo2.read_file_in_struct();

		Log.d("GPSCOORDS", "routes_quantity=" + routes.size());

		Iterator<Map.Entry<String, ArrayList<String>>> iterator = routes
				.entrySet().iterator();

		ListView routesListView;

		// Structure that need for SimpleAdapter
		ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>(
				routes.size());
		// Element of the structure above (it's needed to fill this structure in
		// future)
		Map<String, Object> m;

		// walking through the map of routes loop

		while (iterator.hasNext()) {

			Map.Entry<String, ArrayList<String>> entry = iterator.next();

			String routeStamp = entry.getKey();
			// --------------------------------------------------------------
			/*
			 * Get route name from route stamp. If the name is a date (user
			 * didn't changed default name) - its converted to understandable
			 * format (yyyy-MM-dd HH:mm), that is stored in the constant
			 * DATE_FORMAT
			 */
			String routeNameStr = routeStamp.substring(
					(routeStamp.indexOf("name") + 4), routeStamp.length());

			// --------------------------------------------------------------
			// Get route date from route stamp
			String routeDateStr = "";
			try {
				long routeDateInMillis = Long.parseLong(routeStamp.substring(
						(routeStamp.indexOf("date") + 4),
						routeStamp.indexOf("name")));
				routeDateStr = convert_to_date(routeDateInMillis);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			// --------------------------------------------------------------

			// Filling the ArrayList needed for SimpleAdapter with the data
			m = new HashMap<String, Object>();
			m.put("name", routeNameStr);
			m.put("date", routeDateStr);
			items.add(m);

		} // end of walking through the map of routes loop

		// array of the attributes names, that will be read
		String[] from = { "name", "date" };

		// array of View elements ID's, that data will be put
		int[] to = { R.id.routeName, R.id.routeDate };

		SimpleAdapter sAdapter = new SimpleAdapter(this, items, R.layout.item,
				from, to);

		// find list and assign adapter to it
		routesListView = (ListView) findViewById(R.id.routesListView);

		routesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				arg1.setBackgroundColor(Color.CYAN);
				
			}
		});

		routesListView.setAdapter(sAdapter);

	}; // end of onCreate method

	/**
	 * Converts date from milliseconds format to DATE_FORMAT(constant)
	 * 
	 * @param millis
	 *            - date in milliseconds
	 * @return Formatted date string.
	 */
	public String convert_to_date(Long millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,
				Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(millis);
		return sdf.format(calendar.getTime());
	}

}
