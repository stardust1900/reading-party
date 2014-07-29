package com.shenshan.readingparty;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class PlayActivity extends Activity {
	private Button btnPause, btnPlayUrl, btnStop;
	private SeekBar skbProgress;
	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		this.setTitle("在线音乐播放");
		  
        btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);  
        btnPlayUrl.setOnClickListener(new ClickEvent());  
  
        btnPause = (Button) this.findViewById(R.id.btnPause);  
        btnPause.setOnClickListener(new ClickEvent());  
  
        btnStop = (Button) this.findViewById(R.id.btnStop);  
        btnStop.setOnClickListener(new ClickEvent());  
  
        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);  
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());  
        player = new Player(skbProgress);  
        
        
		/*if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View view) {
			System.out.println("click "+view);
			if (view == btnPause) {
				player.pause();
			} else if (view == btnPlayUrl) {
				//TODO 在百度MP3里随便搜索到的,大家可以试试别的链接
				String url = "http://yinyueshiting.baidu.com/data2/music/47880046/2230211406606461128.mp3?xcode=dbea0069952e953c280517a260f331cb63e0764ef3af3153";
				player.playUrl(url);
			} else if (view == btnStop) {
				player.stop();
			}
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_play, container,
					false);
			return rootView;
		}
	}

}
