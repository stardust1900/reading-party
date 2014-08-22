package com.shenshan.readingparty;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
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
	private static final String TAG = "PostActivity";
	private static final SimpleDateFormat SDF= new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.CHINA);
	private MediaRecorder mediaRecorder = new MediaRecorder();
	private MediaPlayer mediaPlayer;
	private File audioFile;

	private ListView myListView1;
	private ArrayAdapter<String> adapter;// 用于ListView的适配器
	private ArrayList<String> recordFiles = new ArrayList<String>();
	private File soundPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		/* 判断SD Card是否插入 */
		/* 取得SD Card路径作为录音的文件位置 */
		if (android.os.Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdCardPath = Environment.getExternalStorageDirectory();
			soundPath = new File(sdCardPath, "readingParty"
					+ File.separator + "audios");
			if (!soundPath.exists()) {
				soundPath.mkdirs();
			}
		} else {
			Toast.makeText(this, "在您的设备上没有发现sd卡！", Toast.LENGTH_LONG).show();
			return;
		}

		Button btnStart = (Button) findViewById(R.id.btnStart);
		Button btnStop = (Button) findViewById(R.id.btnStop);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		Button btnUpload = (Button) findViewById(R.id.btnUpload);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnUpload.setOnClickListener(this);

		myListView1 = (ListView) findViewById(R.id.ListView01);

		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item,
				R.id.checktv_title, recordFiles);
		/* 将ArrayAdapter添加ListView对象中 */
		myListView1.setAdapter(adapter);
		String[] audios = soundPath.list(new FilenameFilter() {
			@Override
			public boolean accept(File f, String path) {
				return path.endsWith(".amr");
			}
		});
		//添加已存在文件
		if(audios !=null){
			for(String s : audios) {
				adapter.add(s);
			}
		}
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

	}

	@Override
	public void onClick(View view) {
		try {
			String msg = "";
			//TODO 增加爱删除按钮 将录音和停止按钮合并
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
				audioFile = new File(soundPath, SDF.format(new Date())+".amr");
				audioFile.createNewFile();
				// new File(sdCardPath)
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
				System.out.println("upload :" + audioFile);
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
}
