package ua.ponick.getgpscoords;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {

	final int GPS_ERRORDIALOG_REQUEST = 1;

	private TextView latValue, longValue, bearingValue;

	private static final String FILENAME = "file";
	// private static final String LOG_TAG = "GPSCOORDS";

	private LocationManager mLocManager;
	private LocationListener mLocListener;

	private FileOperations fo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// find all views on main activity
		latValue = (TextView) findViewById(R.id.latValue);
		longValue = (TextView) findViewById(R.id.longValue);
		bearingValue = (TextView) findViewById(R.id.bearingValue);

		mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		fo = new FileOperations(this, FILENAME);

	}

	@Override
	protected void onResume() {
		super.onResume();
		servicesOk();
	}

	/**
	 * onClick handler for buttons
	 * 
	 * @param v
	 *            - view that being clicked
	 */
	public void onclick(View v) {
		switch (v.getId()) { // get id of clicked button
		// ---------------------------------------------------------------
		case R.id.btn_startTracking:
			fo.write_stamp();

			mLocListener = new MyLocationListener();

			mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 5, mLocListener);
			break;
		// ---------------------------------------------------------------
		case R.id.btn_stopTracking:
			mLocManager.removeUpdates(mLocListener);
			break;
		// ---------------------------------------------------------------
		case R.id.btn_clearRoutesHistory:
			fo.clear_routesHistory();
			break;
		// ---------------------------------------------------------------
		case R.id.btn_viewRoutesHistory:
			Intent intent = new Intent(this, RoutesHistoryActivity.class);
			intent.putExtra("filename", FILENAME);
			startActivity(intent);
			break;
		}
	}

	// ---------------------------------------------------------------

	/* Class My Location Listener */

	public class MyLocationListener implements LocationListener

	{

		@Override
		public void onLocationChanged(Location loc)

		{

			latValue.setText(Double.toString(loc.getLatitude()));

			longValue.setText(Double.toString(loc.getLongitude()));

			bearingValue.setText(Double.toString(loc.getBearing()));

			fo.write_file(loc);

		}

		@Override
		public void onProviderDisabled(String provider)

		{

			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onProviderEnabled(String provider)

		{

			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}/* End of Class MyLocationListener */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean servicesOk() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Connect Connect to Maps", Toast.LENGTH_SHORT)
					.show();

		}
		return false;
	}

}
