package com.fimu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.fimu.R;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class AboutFIMU extends Activity {
	
	private TextView t_about = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_fimu);
		
		t_about = (TextView) findViewById(R.id.about_content);
		t_about.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
	}

}
