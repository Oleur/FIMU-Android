package com.fimu;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fimu.R;
import com.fimu.constant.ConstSceneLocation;
import com.fimu.database.MusicGroup;
import com.fimu.database.MusicGroupDBAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Google Map implementation in order to see the concerts on the map.
 * @author Julien Salvi
 */
public class MapsFIMU extends FragmentActivity implements OnMarkerClickListener {
	
	private GoogleMap map = null;
	
	private MusicGroupDBAdapter databaseAdapter = null;
	private ArrayList<Integer> listCMap = null;
	private ArrayList<Marker> listMarker = null;
	
	private static final LatLng BELFORT = new LatLng(47.6388488, 6.8596959);
	private LatLng myLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_fimu);
		
		//Setting up the database adapter.
		databaseAdapter = new MusicGroupDBAdapter(MapsFIMU.this);
		
		 map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map_fimu)).getMap();
		//Check if correctly init.
		setUpMapIfNeeded(); 
		
		//Setting up the new options for the google map.
		GoogleMapOptions options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_NORMAL)
			.rotateGesturesEnabled(true);
		
		SupportMapFragment.newInstance(options);
		
		CameraPosition camera = new CameraPosition.Builder().target(BELFORT).zoom(15).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
		map.setMyLocationEnabled(true);
		map.setOnMarkerClickListener(this);
		
		//Add the concert locations on the map
		addMarkersFromConcertLocation();
		getUserLocation();
		
	}

	/**
	 * Get the user location thanks to the GPS data.
	 */
	private void getUserLocation() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (isGPSEnabled(service)) {
			Criteria criteria = new Criteria();
			String provider = service.getBestProvider(criteria, false);
			Location location = service.getLastKnownLocation(provider);
			System.out.println("Lat: "+location.getLatitude()+", Lng: "+location.getLongitude()+"\n");
			this.myLocation = new LatLng(location.getLatitude(),location.getLongitude());
		} else {
			Toast.makeText(this, "Please, turn on the GPS !", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Check if the GPS is enabled on the device.
	 * @param locManager The location manager.
	 * @return True if the GPS is enabled, false otherwise.
	 */
	private boolean isGPSEnabled(LocationManager locManager) {
		boolean isGPS = false;
		isGPS = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isGPS) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add the markers to the map thanks to the concert scene coordinates.
	 */
	private void addMarkersFromConcertLocation() {
		ArrayList<String> listScene = new ArrayList<String>();
		databaseAdapter.open();
		listCMap = getIntent().getExtras().getIntegerArrayList("concertIds");
		
		for (int i = 0; i < listCMap.size(); i++) {
			MusicGroup concert = databaseAdapter.getMusicGroup(listCMap.get(i));
			listScene.add(concert.getScene());
			//Add the marker to the map with respect to the genre.
			if (concert.getMusicStyle().equals("Jazz")) {
				checkConcertStage(concert, R.drawable.marker_jazz);
			} else if (concert.getMusicStyle().equals("Choeurs")) {
				checkConcertStage(concert, R.drawable.marker_music_choral);
			} else if (concert.getMusicStyle().equals("Musique Classique")) {
				checkConcertStage(concert, R.drawable.marker_music_classical);
			} else {
				checkConcertStage(concert, R.drawable.marker_other_music);
			}
		}
		databaseAdapter.close();
		Set<String> setMarkers = new HashSet<String>(listScene);
		if (setMarkers.size() < listScene.size()) {
			Toast.makeText(this, "Attention ! Plusieurs concerts à la même scène", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Check the stage and add the corresponding marker to the marker list.
	 * @param band Music group 
	 * @param resId Stage resource ID 
	 */
	private void checkConcertStage(MusicGroup band, int resId) {
		//Add a marker to the map with respect to the concert stage.
		listMarker = new ArrayList<Marker>();
		
		if (band.getScene().equals(getString(R.string.stage_arsenal))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_ARSENAL_LATITUDE, ConstSceneLocation.SCENE_ARSENAL_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_atria))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_ATRIA_LATITUDE, ConstSceneLocation.SCENE_ATRIA_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));		
		} else if (band.getScene().equals(getString(R.string.stage_bleu))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_BLEU_LATITUDE, ConstSceneLocation.SCENE_BLEU_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_cathedral))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_CATHEDRAL_LATITUDE, ConstSceneLocation.SCENE_CATHEDRAL_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_chapiteau_jazz))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_CHAPITEAU_JAZZ_LATITUDE, ConstSceneLocation.SCENE_CHATIEAU_JAZZ_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_chore))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_CHORE_LATITUDE, ConstSceneLocation.SCENE_CHORE_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_commerce))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_COMMERCE_LATITUDE, ConstSceneLocation.SCENE_COMMERCE_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_concervatoire))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_CONSERVATOIRE_LATITUDE, ConstSceneLocation.SCENE_CONSERVATOIRE_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_granit))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_GRANIT_LATITUDE, ConstSceneLocation.SCENE_GRANIT_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_hotel_dep))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_HOTEL_DEP_LATITUDE, ConstSceneLocation.SCENE_HOTEL_DEP_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_hotel_ville))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_HOTEL_VILLE_LATITUDE, ConstSceneLocation.SCENE_HOTEL_VILLE_LONGITUDE));
			maOption.title(band.getGroupName());
			maOption.snippet(band.getScene());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_kiosque))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_KIOSQUE_LATITUDE, ConstSceneLocation.SCENE_KIOSQUE_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_lion))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_LION_LATITUDE, ConstSceneLocation.SCENE_LION_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_republic))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_REPUBLIQUE_LONGITUDE, ConstSceneLocation.SCENE_REPUPLIQUE_LATITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else if (band.getScene().equals(getString(R.string.stage_salle_fete))) {
			MarkerOptions maOption = new MarkerOptions();
			maOption.position(new LatLng(ConstSceneLocation.SCENE_SDF_LATITUDE, ConstSceneLocation.SCENE_SDF_LONGITUDE));
			maOption.title(band.getScene());
			maOption.snippet(band.getGroupName()+" : "+band.getHour());
			maOption.draggable(false);
			maOption.icon(BitmapDescriptorFactory.fromResource(resId));
			listMarker.add(map.addMarker(maOption));
		} else {
			Toast.makeText(this, "Scène non répertoriée", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.maps_fimu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_settings_refresh_pos:
			map.clear();
			addMarkersFromConcertLocation();
			getUserLocation();
			return true;
		}
		return false;
	}
	
	/**
	 * Set up the map if needed.
	 */
	private void setUpMapIfNeeded() {
	    if (map == null) {
	        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map_fimu)).getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	        	//Do nothing
	        }
	    }
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		//Let us draw the path between the user location and the concert scene.
		final Marker stageMarker = marker;
		if (myLocation != null) {
			AlertDialog.Builder dialogMap = new AlertDialog.Builder(MapsFIMU.this);
			dialogMap.setTitle(getString(R.string.display_route_title));
			dialogMap.setMessage(getString(R.string.display_route_question));
			dialogMap.setIcon(android.R.drawable.ic_dialog_map);
			dialogMap.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Draw the route between your position and the selected stage.
					drawLines(stageMarker);
				}
			});
			dialogMap.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Display only the stage name and the group name
				}
			});
			AlertDialog alert = dialogMap.create();
			alert.show();
		} else {
			Toast.makeText(this, "User location not found! Check GPS connection.", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	
	/**
	 * Draw the route on the map.
	 * @param m The marker target.
	 */
	public void drawLines(Marker m) {
		ArrayList<LatLng> listLatLng = getCoordsFromGoogleMapRequest(myLocation, m.getPosition());
		PolylineOptions polyOpt = new PolylineOptions();
		polyOpt.color(0x880000ff);
		polyOpt.addAll(listLatLng);
		//Let display the route on the map.
		map.addPolyline(polyOpt);
	}

	/**
	 * Get all the Lat/Lng coordinates from the google map api in order to be able to draw the route between a start location and a target.
	 * @param start The start location coordinates.
	 * @param target The target coordinates.
	 * @return An array list of LatLng.
	 */
	private ArrayList<LatLng> getCoordsFromGoogleMapRequest(LatLng start, LatLng target) {
		ArrayList<LatLng> listCoords = new ArrayList<LatLng>(); 
		String url = "http://maps.googleapis.com/maps/api/directions/xml?" 
                + "origin=" + start.latitude + "," + start.longitude  
                + "&destination=" + target.latitude + "," + target.longitude 
                + "&sensor=false&units=metric&mode=walking";
		try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            
            //Parse the XML document, returned by the Google API, to store the geoPoints extracted.
            if (doc != null) {
            	NodeList nl1, nl2, nl3;
                nl1 = doc.getElementsByTagName("step");
                if (nl1.getLength() > 0) {
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Node node1 = nl1.item(i);
                        nl2 = node1.getChildNodes();

                        Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                        nl3 = locationNode.getChildNodes();
                        Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                        double lat = Double.parseDouble(latNode.getTextContent());
                        Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                        double lng = Double.parseDouble(lngNode.getTextContent());
                        listCoords.add(new LatLng(lat, lng));

                        locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                        nl3 = locationNode.getChildNodes();
                        latNode = nl3.item(getNodeIndex(nl3, "points"));
                        ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                        for(int j = 0 ; j < arr.size() ; j++) {
                        	listCoords.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                        }

                        locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                        nl3 = locationNode.getChildNodes();
                        latNode = nl3.item(getNodeIndex(nl3, "lat"));
                        lat = Double.parseDouble(latNode.getTextContent());
                        lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                        lng = Double.parseDouble(lngNode.getTextContent());
                        listCoords.add(new LatLng(lat, lng));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return listCoords;
	}

	/**
	 * Decode the polyline string from the google map api response in order to get the corresponding coordinates.
	 * @param encoded Encoded string from the google map api response.
	 * @return An arrayList of LatLng.
	 */
	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
	}

	/**
	 * Get the node index of the xml document.
	 * @param nl NodeList.
	 * @param nodename Node name.
	 * @return The node index.
	 */
	private int getNodeIndex(NodeList nl, String nodename) {
		for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
	}
	
	/*private class DrawRouteTask extends AsyncTask<Marker, Void, Void> {

		private MapsFIMU mapActivity;
		
		public DrawRouteTask(MapsFIMU fgmActivity) {
			this.mapActivity = fgmActivity;
		}
		
		@Override
		protected Void doInBackground(Marker... params) {
			mapActivity.drawLines(params[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void unused) {
			Toast.makeText(mapActivity, "Route displayed", Toast.LENGTH_SHORT).show();
		}
		
	}*/


}
