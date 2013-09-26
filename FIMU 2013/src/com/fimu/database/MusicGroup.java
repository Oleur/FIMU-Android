package com.fimu.database;

/**
 * MusicGroup element which will be stored in the SQLite database. The user will be able to access to the music group he selected 
 * in an off line mode.
 * @author Julien Salvi
 *
 */
public class MusicGroup {
	
	private int id;
	private String groupName;
	private String musicStyle;
	private String scene;
	private String country;
	private String date;
	private String hour;
	private boolean checked;
	
	public MusicGroup() {
		
	}
	
	public MusicGroup(String _gname, String _mStyle, String _scene, String _country, String _date, String _hour) {
		this.groupName = _gname;
		this.musicStyle = _mStyle;
		this.scene = _scene;
		this.country = _country;
		this.date = _date;
		this.hour = _hour;
		this.checked = false;
	}
	
	public MusicGroup(int _id, String _gname, String _mStyle, String _scene, String _country, String _date, String _hour) {
		this.id = _id;
		this.groupName = _gname;
		this.musicStyle = _mStyle;
		this.scene = _scene;
		this.country = _country;
		this.date = _date;
		this.hour = _hour;
		this.checked = false;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMusicStyle() {
		return musicStyle;
	}

	public void setMusicStyle(String musicStyle) {
		this.musicStyle = musicStyle;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	

}
