package com.shenshan.readingparty;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

public class PostActivity extends Activity implements OnClickListener {
	private static final String TAG = "uploadFile";

	private MediaRecorder mediaRecorder = new MediaRecorder();
	private MediaPlayer mediaPlayer;
	private File audioFile;

	private ListView myListView1;
	private ArrayAdapter<String> adapter;// 用于ListView的适配器
	private ArrayList<String> recordFiles = new ArrayList<String>();
	private File sdCardPath;

	private boolean sdCardExist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnStart = (Button) findViewById(R.id.btnStart);
		Button btnStop = (Button) findViewById(R.id.btnStop);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		Button btnUpload = (Button) findViewById(R.id.btnUpload);
		Button btnJump = (Button) findViewById(R.id.btnJump);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnJump.setOnClickListener(new JumpListener());

		myListView1 = (ListView) findViewById(R.id.ListView01);

		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item,
				R.id.checktv_title, recordFiles);
		/* 将ArrayAdapter添加ListView对象中 */
		myListView1.setAdapter(adapter);

		myListView1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		myListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// System.out.println("onItemClick");
				// System.out.println(parent + " " +view+ " "+ position + " " +
				// id);
				CheckedTextView checktv = (CheckedTextView) parent.getChildAt(
						position).findViewById(R.id.checktv_title);
				if (checktv.isChecked()) {
					checktv.setChecked(false);
				} else {
					checktv.setChecked(true);
				}
			}
		});
		myListView1.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("onItemLongClick");
				System.out.println(parent + " " + view + " " + position + " "
						+ id);
				return false;
			}
		});
		myListView1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("onItemSelected");
				System.out.println(parent + " " + view + " " + position + " "
						+ id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				System.out.println("onNothingSelected");

			}
		});
		/* 判断SD Card是否插入 */
		sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		System.out.println("sdCardExist:" + sdCardExist);
		/* 取得SD Card路径作为录音的文件位置 */
		// if (sdCardExist) {
		sdCardPath = Environment.getExternalStorageDirectory();
		System.out.println("sdCardPath:" + sdCardPath);
		// }
	}

	@Override
	public void onClick(View view) {
		try {
			String msg = "";
			switch (view.getId()) {
			case R.id.btnStart:
				// 设置音频来源(一般为麦克风)
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// 设置音频输出格式（默认的输出格式）
				mediaRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				// 设置音频编码方式（默认的编码方式）
				mediaRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				// 创建一个临时的音频输出文件
				audioFile = File.createTempFile("record_", ".amr");
				// new File(sdCardPath)
				System.out.println(audioFile.getAbsolutePath());
				mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
				mediaRecorder.prepare();
				mediaRecorder.start();
				msg = "正在录音...";
				break;
			case R.id.btnStop:
				if (audioFile != null) {
					mediaRecorder.stop();
					// 添加到adapter
					adapter.add(audioFile.getName());
				}
				msg = "已经停止录音.";
				break;
			case R.id.btnPlay:
				if (audioFile != null) {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setDataSource(audioFile.getAbsolutePath());
					mediaPlayer.prepare();

					mediaPlayer.start();
					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									setTitle("录音播放完毕.");

								}
							});
					msg = "正在播放录音...";
				}
				break;

			case R.id.btnUpload:
				System.out.println("upload :"+audioFile);
				if (audioFile != null) {
					System.out.println("111");
				}
				break;
			}
			setTitle(msg);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

	}

	class JumpListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent=new Intent();
			intent.setClass(PostActivity.this, TimelineActivity.class);
			//intent.setClass(MainActivity.this, PlayActivity.class);
			startActivity(intent);
		}
		
	}
}

