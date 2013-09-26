package com.fimu.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fimu.ConcertDetails;
import com.fimu.ConcertListAdapter;
import com.fimu.MapsFIMU;
import com.fimu.R;
import com.fimu.database.MusicGroup;
import com.fimu.database.MusicGroupDBAdapter;
import com.fimu.parser.XMLFimuParser;

/**
 * Fragment where the selected concert will be displayed as a list in the chronological order.
 * @author Julien Salvi
 */
public class ConcertList extends SherlockFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private ListView concerts;
	private ImageButton openGMap;
	private ImageButton shareFB;
	private TextView textNbConcert;
	private View adView = null;
	
	private AutoCompleteTextView groupNameAutoComplete = null;
	private Spinner spinnerCountry = null;
	private Spinner spinnerStyle = null;
	private Spinner spinnerResults = null;
	private Button buttonSearch = null;
	private static Document xmlDoc = null;
	
	public static final String PREFS_NAME = "prefsFile";
	public SharedPreferences prefs = null;
	
	private int nbConcert = 0;
	private Set<String> xmlCountries = null;
	private Set<String> xmlMusicStyle = null;
	
	private MusicGroupDBAdapter musicDatabase = null;
	private ConcertListAdapter concertAdapter = null;
	private ArrayList<MusicGroup> allMusicGroups = null;
	
	//private NotificationService notifService = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.activity_concert_list);
		this.setHasOptionsMenu(true);
		
		//Listview which contains the selected concerts.
		concerts = (ListView) getActivity().findViewById(R.id.listview_concerts);
		musicDatabase = new MusicGroupDBAdapter(getActivity());
		
		//Buttons for facebook and google map opening.
		openGMap = (ImageButton) getActivity().findViewById(R.id.button_gmaps);
		shareFB = (ImageButton) getActivity().findViewById(R.id.button_share_facebook);
		openGMap.setOnClickListener(this);
		shareFB.setOnClickListener(this);
		openGMap.setVisibility(View.INVISIBLE);
		shareFB.setVisibility(View.INVISIBLE);
		
		textNbConcert = (TextView) getActivity().findViewById(R.id.text_nb_concert);
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TravelingTypewriter.otf");
		textNbConcert.setTypeface(tf);
		//Typeface tfTitle = Typeface.createFromAsset(getAssets(), "fonts/PWScolarpaper.ttf");
		
		//Init the sets:
        xmlMusicStyle = new HashSet<String>();
        xmlCountries = new HashSet<String>();
		
		//********************************************
		//****** Setting up the custom list **********
		//********************************************
		List<MusicGroup> groupItems = new ArrayList<MusicGroup>();
		
		getActivity();
		prefs = getActivity().getSharedPreferences(PREFS_NAME, FragmentActivity.MODE_PRIVATE);
		nbConcert = prefs.getInt("NB_CONCERTS", 0);
		if (nbConcert == 0) {
			textNbConcert.setText(R.string.no_concert);
			openGMap.setEnabled(false);
			shareFB.setEnabled(false);
		} else {
			openGMap.setEnabled(true);
			shareFB.setEnabled(true);
			musicDatabase.open();
			allMusicGroups = musicDatabase.getAllMusicGroupOrderByTime();
			int dataSize = allMusicGroups.size();
			textNbConcert.setText(nbConcert+" "+getString(R.string.nb_concert));
			
			//Adding the items into the custom list.
	        for (int i=0; i < dataSize ;i++) {
	        	musicDatabase.open();
	        	MusicGroup group = allMusicGroups.get(i);
	        	musicDatabase.close();
	            groupItems.add(i, new MusicGroup(group.getId(), group.getGroupName(), group.getMusicStyle(), group.getScene(), 
	            		group.getCountry(), group.getDate(), group.getHour()));
	            
	    		/*long dateMillis = System.currentTimeMillis();
	    		try {
	    			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    			Date date = format.parse(group.getDate()+" "+group.getHour()+":00");
	    			dateMillis = (date.getTime()-(5*60*1000));
	    		} catch (ParseException e) {
	    			e.printStackTrace();
	    		}*/
	    		//Service to receive the notifications.
	    		//notifService = new NotificationService(this, dateMillis)
	    		/*Intent serviceIntent = new Intent(this, NotificationService.class);
	    		serviceIntent.putExtra("concertTime", dateMillis);
	    		startService(serviceIntent);*/
	    		
	        }
			musicDatabase.close();
		}
		
		XMLFimuParser parser = new XMLFimuParser(getActivity());
        xmlDoc = parser.getLocalXMLDocument("xml/fimu.xml");
		
		concertAdapter = new ConcertListAdapter(getActivity(), groupItems);
		concerts.setAdapter(concertAdapter);
		concerts.setClickable(true);
		concerts.setOnItemClickListener(this);
		concerts.setOnItemLongClickListener(this);
	}
	
	@Override
    public void onStop() {
        super.onStop();
        concerts.setClickable(false);
        concerts.setOnItemClickListener(null);
        concerts.setOnItemLongClickListener(null);
        openGMap.setEnabled(false);
        shareFB.setEnabled(false);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		concerts.setClickable(true);
        concerts.setOnItemClickListener(this);
        concerts.setOnItemLongClickListener(this);
		refreshView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.concert_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_add_concert:
			openSelectionDialog();
			return true;
		case R.id.menu_refresh_concert:
			refreshView();
			return true;
		}
		return false;
	}
	
	public void refreshView() {
		if (nbConcert == 0) {
			textNbConcert.setText(R.string.no_concert);
		} else {
			notifyChange();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void notifyChange() {
        //Updating the list view with the newest data.
		List<MusicGroup> groupItems = new ArrayList<MusicGroup>();
		musicDatabase.open();
		allMusicGroups = musicDatabase.getAllMusicGroupOrderByTime();
		int dataSize = allMusicGroups.size();
		textNbConcert.setText(dataSize+" "+getString(R.string.nb_concert));
		
		//Adding the items into the custom list.
        for (int i=0; i < dataSize ;i++) {
        	musicDatabase.open();
        	MusicGroup group = allMusicGroups.get(i);
        	musicDatabase.close();
        	try {
    			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    			Date date = format.parse(group.getDate()+" "+group.getHour()+":00");
    			long diff = (date.getTime()+(20*60*1000)) - System.currentTimeMillis();
    			if (diff < 0) {
    				//Remove the concert is done.
    				//Toast.makeText(this, "Time low: "+diff, Toast.LENGTH_LONG).show();
    				musicDatabase.open();
    				musicDatabase.removeConcert(group.getId());
    				nbConcert--;
    				musicDatabase.close();
    			} else {
    				//Toast.makeText(this, "Time high: "+diff, Toast.LENGTH_LONG).show();
    				groupItems.add(new MusicGroup(group.getId(), group.getGroupName(), group.getMusicStyle(), group.getScene(), 
    	            		group.getCountry(), group.getDate(), group.getHour()));
    			}
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
        }
		updatePrefs();
		textNbConcert.setText(nbConcert+" "+getString(R.string.nb_concert));
		//Enabled the buttons
		if (nbConcert == 0) {
			openGMap.setEnabled(false);
			shareFB.setEnabled(false);
		} else {
			openGMap.setEnabled(true);
			shareFB.setEnabled(true);
		}
		
		
		concertAdapter.clear();
        concertAdapter = new ConcertListAdapter(getActivity(), groupItems);
        concerts.setAdapter(concertAdapter);
        concertAdapter.notifyDataSetChanged();
        musicDatabase.close();
	}
	
	/**
	 * 
	 */
	public void openSelectionDialog() {
		new AlertDialog.Builder(getActivity())
    	.setTitle(R.string.string_menu_search)
    	.setItems(R.array.array_selection_menu, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				//Select the group thanks to its name.
				case 0:
					selectByGroupName();
					break;
				//Select a bench of groups thanks to the music style and/or their country.
				case 1:
					selectByStyleAndCountry();
					break;
				}
			}
		}).show();
	}

	protected void selectByGroupName() {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		//Let set the alert dialog view.
        adView = factory.inflate(R.layout.dialog_add_concert_group_name, null);
        groupNameAutoComplete = (AutoCompleteTextView) adView.findViewById(R.id.multiAutoCompleteTextView1);

        //Let us populate the expLists with the countries and music styles.
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = null;
        
        String[] arrayGroupNames = null;
        
		try {
			expr = xpath.compile("//@name");
			NodeList nodeGroupName = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if( nodeGroupName != null && nodeGroupName.getLength() > 0) {
				//Set the string array:
				arrayGroupNames = new String[nodeGroupName.getLength()];
				for (int i = 0; i < nodeGroupName.getLength(); i++) {
					Attr attr = (Attr) nodeGroupName.item(i);
					arrayGroupNames[i] = attr.getValue();
				}
			}
		} catch (XPathExpressionException e) {
			Toast.makeText(getActivity(), "Error while parsing the data", Toast.LENGTH_SHORT).show();
		}
		
		groupNameAutoComplete.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.select_dialog_item, arrayGroupNames));
		groupNameAutoComplete.setTextColor(Color.BLACK);
		groupNameAutoComplete.setThreshold(1);
        
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setView(adView);
		ad.setTitle(R.string.search_a_group);
		ad.setIcon(android.R.drawable.ic_dialog_info);
		ad.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addConcertFromName(groupNameAutoComplete.getText().toString());
			}
		});
		ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
			}
		});
		AlertDialog alert = ad.create();
		alert.show();
	}

	protected void addConcertFromName(String name) {
		XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = null;
        
        //Arrays where dates and hours will be stored in order to get the closest concert from the current time
        String[] arrayDates = null;
        String[] arrayHours = null;
        String[] arrayScene = null;
        
        //List of music group to check if the list contains a duplicated element.
        List<MusicGroup> checkList = new ArrayList<MusicGroup>();
        if (nbConcert != 0) {
        	checkList = concertAdapter.getGroupItems();
        }
        
        try {
			expr = xpath.compile("/fimu/artist[@name='"+name+"']/@country");
			String country = (String) expr.evaluate(ConcertList.xmlDoc, XPathConstants.STRING);
			expr = xpath.compile("/fimu/artist[@name='"+name+"']/@style");
			String style = (String) expr.evaluate(ConcertList.xmlDoc, XPathConstants.STRING);
			expr = xpath.compile("/fimu/artist[@name='"+name+"']/date");
			NodeList nodeDates = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if( nodeDates != null && nodeDates.getLength() > 0) {
				//Set the string array:
				arrayDates = new String[nodeDates.getLength()];
				for (int i = 0; i < nodeDates.getLength(); i++) { 
					String elem = nodeDates.item(i).getChildNodes().item(0).getNodeValue().toString();
					arrayDates[i] = elem;
				}
			}
			expr = xpath.compile("/fimu/artist[@name='"+name+"']/date/@hour");
			NodeList nodeHours = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if( nodeHours != null && nodeHours.getLength() > 0) {
				//Set the string array:
				arrayHours = new String[nodeHours.getLength()];
				for (int i = 0; i < nodeHours.getLength(); i++) {
					Attr attr = (Attr) nodeHours.item(i);
					arrayHours[i] = attr.getValue();
				}
			}
			expr = xpath.compile("/fimu/artist[@name='"+name+"']/date/@scene");
			NodeList nodeScene = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if( nodeScene != null && nodeScene.getLength() > 0) {
				//Set the string array:
				arrayScene = new String[nodeScene.getLength()];
				for (int i = 0; i < nodeScene.getLength(); i++) {
					Attr attr = (Attr) nodeScene.item(i);
					arrayScene[i] = attr.getValue();
				}
			}
			if (arrayDates.length > 1 && arrayDates != null) {
				for (int j = 0; j < arrayDates.length; j++) {
					MusicGroup nElem = new MusicGroup(name, style, arrayScene[j], country, arrayDates[j], arrayHours[j]);
					if (nbConcert == 0) {
						musicDatabase.open();
						musicDatabase.insert(nElem);
						nbConcert++;
						musicDatabase.close();
					} else {
						int flag = 0;
						for (int i = 0; i < checkList.size(); i++) {
							if (checkList.get(i).getGroupName().equals(nElem.getGroupName().toString()) 
									&& checkList.get(i).getDate().equals(nElem.getDate().toString()) 
									&& checkList.get(i).getHour().equals(nElem.getHour().toString())) {
								//Do not add the concert into the list.
								Toast.makeText(getActivity(), getString(R.string.concert_in_list), Toast.LENGTH_SHORT).show();
								flag = 1;
							}
						}
						if (flag == 0) {
							musicDatabase.open();
							musicDatabase.insert(nElem);
							nbConcert++;
							musicDatabase.close();
						}
					}
				}
			} else {
				MusicGroup nElem = new MusicGroup(name, style, arrayScene[0], country, arrayDates[0], arrayHours[0]);
				if (nbConcert == 0) {
					musicDatabase.open();
					musicDatabase.insert(nElem);
					nbConcert++;
					musicDatabase.close();
				} else {
					int flagCount = 0;
					for (int i = 0; i < checkList.size(); i++) {
						if (checkList.get(i).getGroupName().equals(nElem.getGroupName().toString()) 
								&& checkList.get(i).getDate().equals(nElem.getDate().toString()) 
								&& checkList.get(i).getHour().equals(nElem.getHour().toString())) {
							Toast.makeText(getActivity(), getString(R.string.concert_in_list), Toast.LENGTH_SHORT).show();
							flagCount = 1;
						}
					}
					if (flagCount == 0) {
						musicDatabase.open();
						musicDatabase.insert(nElem);
						nbConcert++;
						musicDatabase.close();
					}
				}
			}
			
			//Let us update the preferences and the change in the list view.
			updatePrefs();
			notifyChange();
			
		} catch (XPathExpressionException e) {
			Toast.makeText(getActivity(), "Error while parsing the data", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Error! Group name might be wrong.", Toast.LENGTH_SHORT).show();
		}
	}

	private void updatePrefs() {
		getActivity();
		SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME,FragmentActivity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("NB_CONCERTS", nbConcert);
		editor.commit();
	}

	protected void selectByStyleAndCountry() {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		//Let set the alert dialog view.
        adView = factory.inflate(R.layout.dialog_layout_add_concert, null);
        spinnerCountry = (Spinner) adView.findViewById(R.id.spinner_country);
        spinnerStyle = (Spinner) adView.findViewById(R.id.spinner_style);
        spinnerResults = (Spinner) adView.findViewById(R.id.spinner_results);
        buttonSearch = (Button) adView.findViewById(R.id.b_valid_search);
        
        //Let us populate the expLists with the countries and music styles.
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = null;
        
		try {
			expr = xpath.compile("//@style");
			NodeList nodeMStyle = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if (nodeMStyle != null && nodeMStyle.getLength() > 0) {
				xmlMusicStyle.clear();
				for (int i = 0; i < nodeMStyle.getLength(); i++) {
					Attr attr = (Attr) nodeMStyle.item(i);
					xmlMusicStyle.add(attr.getValue());
				}
			}

			expr = xpath.compile("//@country");
			NodeList nodeCountry = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
			if (nodeCountry != null && nodeCountry.getLength() > 0) {
				xmlCountries.clear();
				for (int i = 0; i < nodeCountry.getLength(); i++) {
					Attr attr = (Attr) nodeCountry.item(i);
					xmlCountries.add(attr.getValue());
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		List<String> _c = new ArrayList<String>(xmlCountries);
		List<String> _s = new ArrayList<String>(xmlMusicStyle);
		spinnerCountry.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.select_dialog_item, _c));
		spinnerStyle.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, _s));
        spinnerResults.setEnabled(false);
        
		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				XPath xpath = XPathFactory.newInstance().newXPath();
		        XPathExpression expr = null;
				
				String[] arrayGroupNames = null;
		        spinnerResults.setEnabled(true);
		        
				try {
					expr = xpath.compile("/fimu/artist[@style='"+spinnerStyle.getSelectedItem().toString()+
							"' and @country='"+spinnerCountry.getSelectedItem().toString()+"']/@name");
					NodeList nodeGroupName = (NodeList) expr.evaluate(ConcertList.xmlDoc, XPathConstants.NODESET);
					if( nodeGroupName != null && nodeGroupName.getLength() > 0) {
						//Set the string array:
						arrayGroupNames = new String[nodeGroupName.getLength()];
						for (int i = 0; i < nodeGroupName.getLength(); i++) {
							Attr attr = (Attr) nodeGroupName.item(i);
							arrayGroupNames[i] = attr.getValue();
						}
					} else {
						arrayGroupNames = new String[1];
						arrayGroupNames[0] = "---------";
						Toast.makeText(getActivity(), "No concert found! Try again !", Toast.LENGTH_LONG).show();
						spinnerResults.setEnabled(false);
					}
				} catch (XPathExpressionException e) {
					Toast.makeText(getActivity(), "Error while parsing the data", Toast.LENGTH_SHORT).show();
				}
				
				spinnerResults.setAdapter(new ArrayAdapter<String>(getActivity(), 
						android.R.layout.select_dialog_item, arrayGroupNames));
			}
		});
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setView(adView);
		ad.setTitle(R.string.style_and_country_selection);
		ad.setIcon(android.R.drawable.ic_dialog_info);
		ad.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Add the selected concert located on the result spinner
				if (spinnerResults.isEnabled() || spinnerResults.getSelectedItem() != null) {
					addConcertFromName(spinnerResults.getSelectedItem().toString());
				}
			}
		});
		ad.setNeutralButton(R.string.add_all, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Adding all concerts that match the selected properties.
				if (spinnerResults.isEnabled()) {
					for (int i = 0; i < spinnerResults.getAdapter().getCount(); i++) {
						addConcertFromName(spinnerResults.getItemAtPosition(i).toString());
					}
				}
			}
		});
		ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
			}
		});
		AlertDialog alert = ad.create();
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_gmaps:
			if (isOnline()) {
				ArrayList<Integer> listConcertMap = new ArrayList<Integer>();
				List<MusicGroup> list = concertAdapter.getGroupItems();
				if (list != null) {
					for(MusicGroup att : list) {
						if (att.isChecked()) {
							listConcertMap.add(att.getId());
						}
					}
				}
				if (!listConcertMap.isEmpty()) {
					Intent mapIntent = new Intent(getActivity(), MapsFIMU.class);
					mapIntent.putIntegerArrayListExtra("concertIds", listConcertMap);
					mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(mapIntent);
				} else {
					Toast.makeText(getActivity(), "No item selected !", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getActivity(), "Internet not available, check your connection", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button_share_facebook:
			if (isOnline()) {
				ArrayList<Integer> listMGFB = new ArrayList<Integer>();
				List<MusicGroup> cList = concertAdapter.getGroupItems();
				if (cList != null) {
					for(MusicGroup att : cList) {
						if (att.isChecked()) {
							listMGFB.add(att.getId());
						}
					}
				}
				if (listMGFB.size() == 0 || listMGFB.size() > 1) {
					Toast.makeText(getActivity(), getString(R.string.share_sentence), Toast.LENGTH_LONG).show();
				} else {
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("text/plain");
					share.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.share_comment1)+cList.get(0).getGroupName()+" "+getString(R.string.share_comment2)
							+" "+cList.get(0).getDate()+" "+getString(R.string.share_comment3));
					startActivity(Intent.createChooser(share, "Share Image"));
				}
			} else {
				Toast.makeText(getActivity(), "Internet not available, check your connection", Toast.LENGTH_LONG).show();
			}
			break;
		}	
	}
	
	/**
	 * Check if the there is a wi-fi or a data plan connection.
	 * @return True if connected, false otherwise.
	 */
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		MusicGroup mgroup = (MusicGroup) concerts.getItemAtPosition(position);
		Intent concertIntent = new Intent(getActivity(), ConcertDetails.class);
		concertIntent.putExtra("c_name", mgroup.getGroupName());
		concertIntent.putExtra("c_style", mgroup.getMusicStyle());
		concertIntent.putExtra("c_scene", mgroup.getScene());
		concertIntent.putExtra("c_hour", mgroup.getHour());
		concertIntent.putExtra("c_date", mgroup.getDate());
		concertIntent.putExtra("c_country", mgroup.getCountry());
		startActivity(concertIntent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		setAlertDeleteConcert(getString(R.string.delete), getString(R.string.delete_text), false, position);
		return true;
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param finishAc
	 * @param position
	 */
	private void setAlertDeleteConcert(String title, String msg, final boolean finishAc, final int position) {
		new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setMessage(msg)
		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Finish the activity when "Ok" is pressed
				if (finishAc) getActivity().finish();
			}
		})
		.setNeutralButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteConcert(position);
	    		Toast.makeText(getActivity(), "Concert deleted", Toast.LENGTH_SHORT).show();
			}
		}).show();
	}

	/**
	 * Delete the selected concert thanks to its position in the list.
	 * @param position Concert position
	 */
	protected void deleteConcert(int position) {
		MusicGroup att = (MusicGroup) concerts.getItemAtPosition(position);
		musicDatabase.open();
		//Remove the selected concert from the list.
		musicDatabase.removeConcert(att.getId());
		musicDatabase.close();
		nbConcert--;
		updatePrefs();
        //Notify the change into the database in order to update the list view.
        refreshView();	
	}

}
