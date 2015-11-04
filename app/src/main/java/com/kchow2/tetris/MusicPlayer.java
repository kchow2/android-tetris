package com.kchow2.tetris;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Kevin on 2015-10-17.
 */
/*
* Note: have been having problems with audio files created by Audacity. Android has some trouble reading wav, ogg files created with Audacity but mp3 seems to be ok.
*/
public class MusicPlayer {

	Context context;
	private MediaPlayer mediaPlayer;
	private final String tracks[] = {"theme_a.mp3","theme_b.mp3","theme_c.mp3","theme_d.mp3"};

	public MusicPlayer(Context ctx){
		this.context = ctx;
	}

	public void play(int track){
		try {
			this.stop();	//stop any currently playing music before trying to play new track

			this.mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			//NOTE: do not call MediaPlayer.setDataSource(FileDescriptor). Call MediaPlayer.setDataSource(FileDescriptor, int, int) instead or it will play random sound files instead of the one you want!!
			AssetFileDescriptor afd = this.context.getAssets().openFd(this.tracks[track]);
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mediaPlayer.prepare(); // might take long! (for buffering, etc)
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public void stop(){
		if(this.mediaPlayer != null){
			if(this.mediaPlayer.isPlaying())
				this.mediaPlayer.stop();
			this.mediaPlayer.reset();
			this.mediaPlayer.release();
			this.mediaPlayer = null;
		}
	}
}
