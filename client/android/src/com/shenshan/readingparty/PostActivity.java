package com.shenshan.readingparty;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
	private static final SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyyMMdd_HHmmss", Locale.CHINA);
	private MediaRecorder mediaRecorder = null;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private File audioFile;

	private ListView myListView1;
	private ArrayAdapter<String> adapter;// 用于ListView的适配器
	private ArrayList<String> recordFiles = new ArrayList<String>();
	private File soundPath;

	private List<String> checkedItems = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		/* 判断SD Card是否插入 */
		/* 取得SD Card路径作为录音的文件位置 */
		if (android.os.Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdCardPath = Environment.getExternalStorageDirectory();
			soundPath = new File(sdCardPath, "readingParty" + File.separator
					+ "audios");
			if (!soundPath.exists()) {
				soundPath.mkdirs();
			}
		} else {
			Toast.makeText(this, "在您的设备上没有发现sd卡！", Toast.LENGTH_LONG).show();
			return;
		}

		Button btnStart = (Button) findViewById(R.id.btnStart);
		Button btnRemove = (Button) findViewById(R.id.btnRemove);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		Button btnUpload = (Button) findViewById(R.id.btnUpload);
		btnStart.setOnClickListener(this);
		btnRemove.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnUpload.setOnClickListener(this);

		myListView1 = (ListView) findViewById(R.id.ListView01);
		
		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item,
				R.id.checktv_title, recordFiles);
		/* 将ArrayAdapter添加ListView对象中 */
		myListView1.setAdapter(adapter);
		// 加载已有录音
		loadAudios();
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
					checkedItems.remove(checktv.getText().toString());
				} else {
					checktv.setChecked(true);
					checkedItems.add(checktv.getText().toString());
				}
			}
		});
		myListView1.setOnItemLongClickListener(new OnItemLongClickListener() {
			// TODO 长按删除
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

	private void loadAudios() {
		adapter.clear();
		String[] audios = soundPath.list(new FilenameFilter() {
			@Override
			public boolean accept(File f, String path) {
				return path.endsWith(".amr");
			}
		});
		// 添加已存在文件
		if (audios != null) {
			for (String s : audios) {
				adapter.add(s);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		try {
			String msg = "";
			Button button = (Button) view;
			// TODO 增加爱删除按钮 将录音和停止按钮合并
			switch (view.getId()) {
			case R.id.btnStart:
				if (getResources().getString(R.string.record).equals(
						button.getText().toString())) {
					button.setText(getResources().getString(R.string.stop));
					mediaRecorder = new MediaRecorder();
					// 设置音频来源(一般为麦克风)
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					// 设置音频输出格式（默认的输出格式）
					mediaRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					// 设置音频编码方式（默认的编码方式）
					mediaRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					// 创建一个临时的音频输出文件
					audioFile = new File(soundPath, SDF.format(new Date())
							+ ".amr");
					audioFile.createNewFile();
					// new File(sdCardPath)
					mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
					mediaRecorder.prepare();
					mediaRecorder.start();
					msg = "正在录音...";
					setTitle(msg);
				} else {
					button.setText(getResources().getString(R.string.record));
					if (audioFile != null && mediaRecorder != null) {
						mediaRecorder.stop();
						mediaRecorder.release();
						mediaRecorder = null;
						// 添加到adapter
						adapter.add(audioFile.getName());
						adapter.notifyDataSetChanged();
						audioFile = null;
					}
					msg = "已经停止录音.";
					setTitle("ReadingParty");
				}
				break;
			case R.id.btnRemove:
				myListView1.getCheckedItemCount();
				myListView1.getCheckedItemIds();
				
				if (checkedItems.isEmpty()) {
					msg = "请选择录音.";
				} else {
					dialog();
					// msg = "正在删除录音";
				}
				break;
			case R.id.btnPlay:
				if (getResources().getString(R.string.play).equals(
						button.getText().toString())) {
					if (checkedItems.isEmpty()) {
						msg = "请选择录音";
					} else {
						button.setText(getResources().getString(R.string.stop));
						ArrayDeque<String> playList = new ArrayDeque<String>(
								checkedItems);
						playList.addAll(checkedItems);
						playAudios(playList);
						msg = "正在播放录音...";

					}
				} else {
					button.setText(getResources().getString(R.string.play));
					mediaPlayer.stop();
					mediaPlayer.reset();
				}
				break;

			case R.id.btnUpload:
				System.out.println("upload :" + audioFile);
				if (audioFile != null) {
					System.out.println("111");
				}
				break;
			}
			// setTitle(msg);
			if (!"".equals(msg.trim())) {
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

	}

	private void playAudios(final ArrayDeque<String> playList)
			throws IOException {
		String audio = playList.poll();
		if (audio == null) {
			setTitle("录音播放完毕.");
			return;
		}
		mediaPlayer.setDataSource(soundPath + File.separator + audio);
		mediaPlayer.prepare();

		mediaPlayer.start();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaPlayer.reset();
				try {
					playAudios(playList);
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			}
		});
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(PostActivity.this);
		builder.setMessage("确认删除所选录音吗？");
		builder.setTitle("删除");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				for (String s : checkedItems) {
					File f = new File(soundPath, s);
					if (f.exists()) {
						f.delete();
					}
					
					adapter.remove(s);
					adapter.notifyDataSetChanged();
				}
				checkedItems.clear();
				//loadAudios();
				// PostActivity.this.finish();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
