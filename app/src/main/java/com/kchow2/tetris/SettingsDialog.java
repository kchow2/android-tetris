package com.kchow2.tetris;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.kevin.tetris.R;

/**
 * Created by Kevin on 2015-10-17.
 * Allows user to change game settings like sensitivity, music, etc
 */
public class SettingsDialog extends DialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{

	private MusicPlayer musicPlayer;
	private Settings settings;

	public SettingsDialog(){
	}

	public void setMusicPlayer(MusicPlayer musicPlayer) {
		this.musicPlayer = musicPlayer;
	}

	public void setSettings(Settings settings){
		this.settings = settings;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_dialog, container);

		this.getDialog().setTitle(R.string.settings_title);

		//set the state of the controls based on the current settings
		((CheckBox)view.findViewById(R.id.id_drag_lock)).setChecked(settings.isDragLock());
		((SeekBar)(view.findViewById(R.id.id_drag_sensitivity))).setProgress((int) (settings.getDragSensitivity()));
		((CheckBox)view.findViewById(R.id.play_sounds)).setChecked(settings.isSoundOn());
		if(!settings.isMusicOn()){
			((CheckBox)view.findViewById(R.id.play_music)).setChecked(false);
			view.findViewById(R.id.track_selection_group).setEnabled(false);
		}
		else{
			((CheckBox)view.findViewById(R.id.play_music)).setChecked(true);
		}
		switch(settings.getMusicSelection()){
			case 0:
				((RadioGroup)view.findViewById(R.id.track_selection_group)).check(R.id.a);
				break;
			case 1:
				((RadioGroup)view.findViewById(R.id.track_selection_group)).check(R.id.b);
				break;
			case 2:
				((RadioGroup)view.findViewById(R.id.track_selection_group)).check(R.id.c);
				break;
			case 3:
				((RadioGroup)view.findViewById(R.id.track_selection_group)).check(R.id.d);
				break;
		}

		//setup the listeners for the controls. Note that this should be done after setting the initial states of the controls,
		//since they don't 'exist' yet, so any listener code that tries to change
		//stuff in the dialog will cause a crash.
		view.findViewById(R.id.id_back).setOnClickListener(this);
		view.findViewById(R.id.id_drag_lock).setOnClickListener(this);
		view.findViewById(R.id.id_restore_defaults).setOnClickListener(this);
		view.findViewById(R.id.a).setOnClickListener(this);
		view.findViewById(R.id.b).setOnClickListener(this);
		view.findViewById(R.id.c).setOnClickListener(this);
		view.findViewById(R.id.d).setOnClickListener(this);
		((CheckBox)view.findViewById(R.id.play_music)).setOnCheckedChangeListener(this);
		((CheckBox)view.findViewById(R.id.play_sounds)).setOnCheckedChangeListener(this);
		((SeekBar)(view.findViewById(R.id.id_drag_sensitivity))).setOnSeekBarChangeListener(this);

		return view;
	}

	//resets the control states based on Settings
	private void refreshControls(){
		((CheckBox)this.getDialog().findViewById(R.id.id_drag_lock)).setChecked(settings.isDragLock());
		((SeekBar)(this.getDialog().findViewById(R.id.id_drag_sensitivity))).setProgress((int) (settings.getDragSensitivity()));
		((CheckBox)this.getDialog().findViewById(R.id.play_sounds)).setChecked(settings.isSoundOn());
		if(!settings.isMusicOn()){
			((CheckBox)this.getDialog().findViewById(R.id.play_music)).setChecked(false);
			this.getDialog().findViewById(R.id.track_selection_group).setEnabled(false);
		}
		else{
			((CheckBox)this.getDialog().findViewById(R.id.play_music)).setChecked(true);
		}
		switch(settings.getMusicSelection()){
			case 0:
				((RadioGroup)this.getDialog().findViewById(R.id.track_selection_group)).check(R.id.a);
				break;
			case 1:
				((RadioGroup)this.getDialog().findViewById(R.id.track_selection_group)).check(R.id.b);
				break;
			case 2:
				((RadioGroup)this.getDialog().findViewById(R.id.track_selection_group)).check(R.id.c);
				break;
			case 3:
				((RadioGroup)this.getDialog().findViewById(R.id.track_selection_group)).check(R.id.d);
				break;
		}
	}

	public void onClick(View v){
		boolean refreshMusic = false;

		switch(v.getId()){
			case R.id.id_back:
				this.dismiss();
				break;
			case R.id.id_drag_lock:
				CheckBox dragLock = (CheckBox) v;
				this.settings.setDragLock(dragLock.isChecked());
				break;
			case R.id.id_restore_defaults:
				this.settings.restoreDefaults();
				refreshControls();
				refreshMusic = true;
				break;
			case R.id.a:
				this.settings.setMusicSelection(0);
				this.settings.setMusicOn(true);
				refreshMusic = true;
				break;
			case R.id.b:
				this.settings.setMusicSelection(1);
				this.settings.setMusicOn(true);
				refreshMusic = true;
				break;
			case R.id.c:
				this.settings.setMusicSelection(2);
				this.settings.setMusicOn(true);
				refreshMusic = true;
				break;
			case R.id.d:
				this.settings.setMusicSelection(3);
				this.settings.setMusicOn(true);
				refreshMusic = true;
				break;

		}

		if(refreshMusic){
			if(settings.isMusicOn()){
				this.musicPlayer.play(settings.getMusicSelection());
			}
			else{
				this.musicPlayer.stop();
			}
		}
	}

	public void onCheckedChanged(CompoundButton btn, boolean isChecked){
		switch(btn.getId()){
			case R.id.play_music:
				this.settings.setMusicOn(isChecked);
				//this.getDialog().findViewById(R.id.track_selection_group).setEnabled(isChecked);

				//disable/enable radio buttons for music group
				this.getDialog().findViewById(R.id.a).setEnabled(isChecked);
				this.getDialog().findViewById(R.id.b).setEnabled(isChecked);
				this.getDialog().findViewById(R.id.c).setEnabled(isChecked);
				this.getDialog().findViewById(R.id.d).setEnabled(isChecked);

				//update the music player
				if(settings.isMusicOn()){
					this.musicPlayer.play(settings.getMusicSelection());
				}
				else{
					this.musicPlayer.stop();
				}
				break;
			case R.id.play_sounds:
				this.settings.setSoundOn(isChecked);
				break;
		}
	}

	public void	onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		//seekbar progress is a value from 0-100. Scale this to sensitivity value to use in GameActivity
		this.settings.setDragSensitivity(progress);
	}

	public void	onStartTrackingTouch(SeekBar seekBar){

	}

	public void	onStopTrackingTouch(SeekBar seekBar){

	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		super.onDismiss(dialog);
		final Activity activity = getActivity();
		if (activity instanceof DialogInterface.OnDismissListener) {
			((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
		}
	}
}
