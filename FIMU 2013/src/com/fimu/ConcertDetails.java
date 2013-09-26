package com.fimu;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class ConcertDetails extends SherlockActivity {
	
	private TextView concert_name, concert_scene, concert_style, concert_hour;
	private Button b_day1, b_day2, b_day3, b_day4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concert_details);
		
		//setup the actionbar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Typefaces for the scene, hour and style textview
		Typeface tfText = Typeface.createFromAsset(getAssets(), "fonts/PWScolarpaper.ttf");
		Typeface tfHour = Typeface.createFromAsset(getAssets(), "fonts/TravelingTypewriter.otf");
		
		concert_name = (TextView) findViewById(R.id.text_name_details);
		concert_name.setText(getIntent().getExtras().getString("c_name").toString());
		concert_scene = (TextView) findViewById(R.id.text_scene_datails);
		concert_scene.setText(getString(R.string.scene)+" "+getIntent().getExtras().getString("c_scene").toString());
		concert_scene.setTypeface(tfText);
		concert_style = (TextView) findViewById(R.id.text_style_details);
		concert_style.setText(getString(R.string.genre)+" "+getIntent().getExtras().getString("c_style").toString());
		concert_style.setTypeface(tfText);
		concert_hour = (TextView) findViewById(R.id.text_time_details);
		concert_hour.setText(getString(R.string.time) + " " + getIntent().getExtras().getString("c_hour").toString());
		concert_hour.setTypeface(tfHour);
		
		//Init the buttons
		b_day1 = (Button) findViewById(R.id.b_day_one);
		b_day2 = (Button) findViewById(R.id.b_day_two);
		b_day3 = (Button) findViewById(R.id.b_day_three);
		b_day4 = (Button) findViewById(R.id.b_day_four);
		
		//Set the drawables with respect to the concert time.
		initButtonWithDate();
		
	}

	private void initButtonWithDate() {
		String date = getIntent().getExtras().getString("c_date").toString();
		if (date.equals(getString(R.string.day1))) {
			b_day1.setBackgroundResource(R.drawable.day_one_icon);
		} else if (date.equals(getString(R.string.day2))) {
			b_day2.setBackgroundResource(R.drawable.day_two_icon);
		} else if (date.equals(getString(R.string.day3))) {
			b_day3.setBackgroundResource(R.drawable.day_three_icon);
		} else if (date.equals(getString(R.string.day4))) {
			b_day4.setBackgroundResource(R.drawable.day_four_icon);
		} else {
			//Do nothing
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent previous = new Intent(this, FimuTabActivity.class);
			startActivity(previous);
		}
		return super.onOptionsItemSelected(item);
	}

}
