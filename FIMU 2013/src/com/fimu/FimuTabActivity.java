package com.fimu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.fimu.database.MusicGroup;
import com.fimu.fragments.AllConcertsFrag;
import com.fimu.fragments.ConcertList;
import com.fimu.fragments.PhotoGalleryFimu;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class FimuTabActivity extends SlidingFragmentActivity implements OnClickListener, OnItemClickListener {
	
	private static final int REAUTH_ACTIVITY_CODE = 100;

	private ActionBar actionBarFimu = null;
	
    private UiLifecycleHelper uiHelper;
    private GraphUser user;
    private Session userSession;
    
    private ProfilePictureView profilePicture_SMenu;
    private TextView userName;
    private ListView menuList;
    
    String[] items = new String[] {"Facebook share", "Localisation", "Friends", "Settings", "Terms"};
    
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_view_layout);
		setBehindContentView(R.layout.sliding_menu_fimu_layout);
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
		//Setup the action bar sherlock
		actionBarFimu = getSupportActionBar();
		initActionBarFimu();
		
		//Init sliding menu at last ! WE GONNA RULE THE GALAXY !!!!!!!!!!!!!
		SlidingMenu sMenu = getSlidingMenu();
		sMenu.setMode(SlidingMenu.LEFT);
		sMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        sMenu.setFadeDegree(0.35f);
        sMenu.setBehindOffset(120);
        sMenu.setMenu(R.layout.sliding_menu_fimu_layout);
		sMenu.setOnClickListener(this);
		
		profilePicture_SMenu = (ProfilePictureView) sMenu.findViewById(R.id.profilePicture_slidingMenu);
		userName = (TextView) sMenu.findViewById(R.id.text_username);
		menuList = (ListView) sMenu.findViewById(R.id.slidingmenu_items_list);
		ArrayList<HashMap<String, String>> slidingItems = new ArrayList<HashMap<String, String>>();
		for (int i=0; i<items.length; i++) {
			HashMap<String, String> mapItem = new HashMap<String, String>();
	        mapItem.put("item", items[i]);
	        if(i == 0) {
	        	mapItem.put("imgItem", String.valueOf(R.drawable.ic_smenu_fb));
	        } else if (i == 1) {
	        	mapItem.put("imgItem", String.valueOf(R.drawable.ic_smenu_loc));
	        } else if (i == 2) {
	        	mapItem.put("imgItem", String.valueOf(R.drawable.ic_smenu_friends));
	        } else if (i == 3) {
	        	mapItem.put("imgItem", String.valueOf(R.drawable.ic_smenu_settings));
	        } else if (i == 4) {
	        	mapItem.put("imgItem", String.valueOf(R.drawable.ic_smenu_terms));
	        }
	        slidingItems.add(mapItem);
		}
		SimpleAdapter menuAdapter = new SimpleAdapter(this, slidingItems, R.layout.item_sliding_menu
				, new String[] {"item", "imgItem"}, new int[] {R.id.text_sliding_item, R.id.img_sliding_item});
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(this);
		
	}
	
	@Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

	@Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiHelper.onPause();
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
    	
    }

	protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
		userSession = session;
		if (session != null && session.isOpened()) {
	        getFaceBookUserInfo(session);
	    }
	}
	
	private void getFaceBookUserInfo(final Session session) {
		Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser _user, Response response) {
				FimuTabActivity.this.user = _user;
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    profilePicture_SMenu.setProfileId(user.getId());
	                    userName.setText(user.getFirstName()+" "+user.getLastName());
	                }
	            }
	            if (response.getError() != null) {
	                Toast.makeText(FimuTabActivity.this, "Error Facebook call", Toast.LENGTH_SHORT).show();
	            }
			}
	    });
	    request.executeAsync();
	}

	protected void updateUI() {
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()) {
	        getFaceBookUserInfo(session);
	    }
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}

	/**
	 * Init the Fimu action bar with its components.
	 */
	private void initActionBarFimu() {
		actionBarFimu.setTitle(getString(R.string.title_action_bar_concert_list));
		actionBarFimu.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBarFimu.setDisplayHomeAsUpEnabled(true);
		
		//Tab in order to navigate through the custom list, the entire schedule and the photo gallery.
		Tab listPersoTab = actionBarFimu.newTab();
		listPersoTab.setText("Concerts List");
		listPersoTab.setTabListener(new TabListener<ConcertList>(this, "concert_list_perso", ConcertList.class));
		actionBarFimu.addTab(listPersoTab);
		
		Tab entireProgTab = actionBarFimu.newTab();
		entireProgTab.setText("All concerts");
		entireProgTab.setTabListener(new TabListener<AllConcertsFrag>(this, "all_concerts", AllConcertsFrag.class));
		actionBarFimu.addTab(entireProgTab);
		
		Tab galleryTab = actionBarFimu.newTab();
		galleryTab.setText("Photos");
		galleryTab.setTabListener(new TabListener<PhotoGalleryFimu>(this, "photo_gallery", PhotoGalleryFimu.class));
		actionBarFimu.addTab(galleryTab);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu_concerts_global, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_take_picture:
			//TODO: Implement the camera to take pictures.
			return true;
		case android.R.id.home:
			//Display the sliding menu
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		}
	}
	
	/**
	 * Tab listener in order to switch from a tab to another.
	 * @author Julien Salvi
	 * @param <T> The Fragment to show in the tab
	 */
	public class TabListener<T extends SherlockFragment> implements ActionBar.TabListener {
	    private SherlockFragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ignoredFt) {
		  FragmentManager fragMgr = ((FragmentActivity) mActivity)
		        .getSupportFragmentManager();
		  FragmentTransaction ft = fragMgr.beginTransaction();

		  //Check if the fragment is already initialized
		  if (mFragment == null) {
		      //If not, instantiate and add it to the activity
		      mFragment = (SherlockFragment) SherlockFragment.instantiate(mActivity, mClass.getName());
		      ft.replace(android.R.id.content, mFragment, mTag);
		  } else {
		      ft.attach(mFragment);	  
		  }
		  ft.commit();
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ignoredFt) {
		  FragmentManager fragMgr = ((FragmentActivity) mActivity)
		          .getSupportFragmentManager();
		  FragmentTransaction ft = fragMgr.beginTransaction();
		  ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		  //Check if the fragment is already initialized
		  /*if (mFragment == null) {
		      //If not, instantiate and add it to the activity
		      mFragment = (SherlockFragment) SherlockFragment.instantiate(mActivity, mClass.getName());
		      ft.replace(android.R.id.content, mFragment, mTag);
		      //ft.hide(mFragment);
		  } else {
		      if (fragMgr.getBackStackEntryCount() > 0)  {
		    	  fragMgr.popBackStack(fragMgr.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		      }
		      //
		      ft.detach(mFragment);
		  }*/
		  if (mFragment != null)
			  ft.detach(mFragment);
		  ft.commit();
		}

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		// TODO Implement the item listener for the sliding menu
		if (position == 0) {
			//Facebook share
			
		} else if (position == 1) {
			//Localisation
			if (isOnline()) {
				/*ArrayList<Integer> listConcertMap = new ArrayList<Integer>();
				List<MusicGroup> list = null;
				if (list != null) {
					for(MusicGroup att : list) {
						if (att.isChecked()) {
							listConcertMap.add(att.getId());
						}
					}
				}
				if (!listConcertMap.isEmpty()) {
					Intent mapIntent = new Intent(this, MapsFIMU.class);
					mapIntent.putIntegerArrayListExtra("concertIds", listConcertMap);
					mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(mapIntent);
				} else {
					Toast.makeText(this, "No item selected !", Toast.LENGTH_SHORT).show();
				}*/
			} else {
				Toast.makeText(this, "Internet not available, check your connection", Toast.LENGTH_LONG).show();
			}
		} else if (position == 2) {
			//Friends
			
		} else if (position == 3) {
			//Settings
			
		} else if (position == 4) {
			//Terms
			Intent terms_intent = new Intent(this, AboutFIMU.class);
			startActivity(terms_intent);
		}
	}

	/**
	 * Check if the there is a wi-fi or a data plan connection.
	 * @return True if connected, false otherwise.
	 */
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

}
