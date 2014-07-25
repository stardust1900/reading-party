package com.shenshan.readingparty;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		OnPreparedListener {
	public MediaPlayer mediaPlayer;
	private SeekBar skbProgress;
	private Timer mTimer = new Timer();
	TimerTask mTimerTask = new TimerTask() {
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();
			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
			}
		}
	};

	public Player(SeekBar skbProgress) {
		this.skbProgress = skbProgress;
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
		mTimer.schedule(mTimerTask, 0, 1000);
	}

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare之后自动播放
			// mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			Log.e("mediaPlayer", "error", e);
		} catch (SecurityException e) {
			Log.e("mediaPlayer", "error", e);
		} catch (IllegalStateException e) {
			Log.e("mediaPlayer", "error", e);
		} catch (IOException e) {
			Log.e("mediaPlayer", "error", e);
		}
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	
	@Override
	public void onPrepared(MediaPlayer player) {
		player.start();
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.e("mediaPlayer", "onCompletion");  
	}

	@Override
	public void onBufferingUpdate(MediaPlayer player, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);  
		int currentProgress=skbProgress.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
		Log.e(currentProgress+"% play", bufferingProgress + "% buffer");  
	}

}
