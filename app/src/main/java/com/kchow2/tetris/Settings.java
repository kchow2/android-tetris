package com.kchow2.tetris;

import android.content.SharedPreferences;

/**
 * Created by Kevin on 2015-10-27.
 */
public class Settings {

	private int dragSensitivity;	//0-100
	private boolean dragLock;
	private int musicSelection;
	private boolean musicOn;
	private boolean soundsOn;

	public Settings(){
		restoreDefaults();
		//musicSelection = 0;
		//musicOn = true;
		//soundsOn = true;
	}

	public boolean isDragLock() {
		return dragLock;
	}

	public void setDragLock(boolean dragLock) {
		this.dragLock = dragLock;
	}

	public int getMusicSelection() {
		return musicSelection;
	}

	public void setMusicSelection(int musicSelection) {
		this.musicSelection = musicSelection;
	}

	public boolean isMusicOn() {
		return musicOn;
	}

	public void setMusicOn(boolean musicOn) {
		this.musicOn = musicOn;
	}

	public void setDragSensitivity(int dragSensitivity){
		this.dragSensitivity = dragSensitivity;
	}
	public int getDragSensitivity(){
		return dragSensitivity;
	}

	public boolean isSoundOn() {
		return soundsOn;
	}

	public void setSoundOn(boolean soundsOn) {
		this.soundsOn = soundsOn;
	}

	public void restoreDefaults(){
		dragSensitivity = 50;
		dragLock = true;
		musicSelection = 0;
		musicOn = true;
		soundsOn = true;
	}


	public void loadFromSharedPreferences(SharedPreferences prefs){
		dragSensitivity = prefs.getInt("dragSensitivity", 50);
		dragLock = prefs.getBoolean("dragLock", true);
		musicSelection = prefs.getInt("musicSelection", 0);
		musicOn = prefs.getBoolean("musicOn", true);
		soundsOn = prefs.getBoolean("soundsOn", true);
	}

	public void saveToSharedPreferences(SharedPreferences prefs){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("dragSensitivity", dragSensitivity);
		editor.putBoolean("dragLock", true);
		editor.putInt("musicSelection", musicSelection);
		editor.putBoolean("musicOn", musicOn);
		editor.putBoolean("soundsOn", soundsOn);
		editor.commit();
	}
}
