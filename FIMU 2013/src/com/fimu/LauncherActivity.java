package com.fimu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
//import android.view.Menu;
//import android.view.MenuItem;

/**
 * Application launcher where you can view your custom festival schedule.
 * @author Julien Salvi
 *
 */
public class LauncherActivity extends FragmentActivity implements OnClickListener {

	private Button b_customProg = null;
	private Button b_about = null;
    
    private LoginButton facebookButton;
    private UiLifecycleHelper uiHelper;
    private GraphUser user;
    private ProfilePictureView profilePictureView;
	
	public static final String PREFS_NAME = "prefsFile";
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_layout);
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        //test profile picture
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        
        
		b_customProg = (Button) findViewById(R.id.b_id_prog);
		b_about = (Button) findViewById(R.id.b_id_about);
		
		facebookButton = (LoginButton) findViewById(R.id.b_facebook_sign_in);
		facebookButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				// TODO Auto-generated method stub
				LauncherActivity.this.user = user;
                updateUI();
			}
		});
		
		b_customProg.setOnClickListener(this);
		b_about.setOnClickListener(this);

	}
	
	protected void updateUI() {
		Session session = Session.getActiveSession();
        
        if (user != null && (session != null && session.isOpened())) {
            profilePictureView.setProfileId(user.getId());
        } else {
            profilePictureView.setProfileId(null);
        }
	}

	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session.isOpened()) {
			Intent prog_intent = new Intent(this, FimuTabActivity.class);
			startActivity(prog_intent);
		}
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


	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}*/

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.b_id_prog:
			//TODO: Open the custom list view where the user will be able to add concert elems.
			Intent prog_intent = new Intent(this, FimuTabActivity.class);
			startActivity(prog_intent);
			break;
		case R.id.b_id_about:
			Intent about_intent = new Intent(this, AboutFIMU.class);
			startActivity(about_intent);
			break;
		}	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	    Intent prog_intent = new Intent(this, FimuTabActivity.class);
	    String userJSON = user.getInnerJSONObject().toString();
	    Bundle bundle = new Bundle();
	    bundle.putString("userJSON", userJSON);
	    prog_intent.putExtras(bundle);
		startActivity(prog_intent);
	}

}
