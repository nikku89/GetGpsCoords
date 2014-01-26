package ua.ponick.getgpscoords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class FileOperations {

	private static String FILENAME = "";
	private static final String LOG_TAG = "GPSCOORDS";
	private Context fileContext;

	public FileOperations(Context cont) {
		FILENAME = "file";
		this.fileContext = cont;
	}

	public FileOperations(Context cont, String filename) {
		FILENAME = filename;
		this.fileContext = cont;
	}

	// ---------------------------------------------------------------

	/**
	 * Writes index of the next route and his date
	 */
	public void write_stamp() {
		// stamp structure: route<id>date<date>name<date>
		// by default the name of the route is date
		// but it can be changed by calling change_route_name(String newName)
		// method
		// at first - get last route index in the routes history file
		// and write an incremented one
		/* if file is empty - write first route with index "0" */
		long date = System.currentTimeMillis();

		if (read_lastRouteId() == -1)
			write_file("route0" + "date" + date + "name" + "route" + date);
		/* else - write next route with (index) = (last index of route) + 1 */
		else
			write_file("route" + (Integer.toString(read_lastRouteId() + 1))
					+ "date" + date + "name" + "route" + date);
	}

	// ---------------------------------------------------------------

	/**
	 * Writes coordinates point and bearing in the routes history file
	 * 
	 * @param loc
	 *            - location to write
	 */
	public void write_file(Location loc) {

		try {

			// Log.d(LOG_TAG, "Writing point to file...");

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					fileContext.openFileOutput(FILENAME, Context.MODE_APPEND)));

			// writing latitude, longitude and bearing in the routes history
			// file. numbers are separated by space
			bw.write(Double.toString(loc.getLatitude()) + " "
					+ Double.toString(loc.getLongitude()) + " "
					+ Double.toString(loc.getBearing()) + " " + "\n");

			bw.close();

			// Log.d(LOG_TAG, "Writing done ...");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// ---------------------------------------------------------------
	/**
	 * Writes specified string into routes history file
	 * 
	 * @param str_to_write
	 *            - string to be written into the file
	 */
	public void write_file(String str_to_write) {
		try {

			// Log.d(LOG_TAG, "Starting write string to file...");

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					fileContext.openFileOutput(FILENAME, Context.MODE_APPEND)));

			bw.write(str_to_write + "\n");

			bw.close();

			// Log.d(LOG_TAG, "Writing string to file succesfully done ...");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads routes history file contents into HashMap structure, where KEY is
	 * the route stamp, string consists of route, name and date; and VALUE is an
	 * ArrayList of coordinates of the key points of route.
	 * 
	 * @return Map with routes stamps and coordinates of the key points of the
	 *         routes.
	 */
	public Map<String, ArrayList<String>> read_file_in_struct() {
		Map<String, ArrayList<String>> routes = new HashMap<String, ArrayList<String>>();
		String prevRouteStamp = "";
		String nextRouteStamp = "";
		String str = "";
		int routeID = 0;
		ArrayList<String> points = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileContext.openFileInput(FILENAME)));

			// посмотреть тут (что-то неверно заполняется)
			while ((str = br.readLine()) != null) {

				if (str.startsWith("route")) {
					nextRouteStamp = str;

					if (routeID == 0) { // if this is first route stamp
						prevRouteStamp = nextRouteStamp;
					}
					routeID++;

				} else if (nextRouteStamp.compareTo(prevRouteStamp) != 0) { // if
																			// readed
																			// next
																			// route
																			// stamp
					routes.put(prevRouteStamp, points);
					points.clear();
					prevRouteStamp = nextRouteStamp;
				} else {
					points.add(str);
				}

			} // end of the while loop

			routes.put(nextRouteStamp, points); // for the last route readed

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return routes;
	}

	/**
	 * reads last route index in the routes history file
	 * 
	 * @return
	 */
	private int read_lastRouteId() {

		String str = "", route = "";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileContext.openFileInput(FILENAME)));

			while ((str = br.readLine()) != null) {

				str.toLowerCase(Locale.getDefault());

				if (str.startsWith("route")) {
					route = str.substring(0, str.indexOf("date"));
				}

			}
			// Log.d(LOG_TAG, route);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (route.compareToIgnoreCase("") == 0)
			return -1;
		return Integer.parseInt(route.substring(5, route.length()));
	}

	public void clear_routesHistory() {
		fileContext.deleteFile(FILENAME);
		Log.d(LOG_TAG, "Routes history file successfully cleared");
	}
}
