package com.shenshan.readingparty;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends Activity implements OnClickListener {

	private MediaRecorder mediaRecorder = new MediaRecorder();
	private MediaPlayer mediaPlayer;
	private File audioFile;

	private ListView myListView1;
	private ArrayAdapter<String> adapter;// ����ListView��������
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
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnUpload.setOnClickListener(this);

		myListView1 = (ListView) findViewById(R.id.ListView01);

		recordFiles.add("aaa");
		recordFiles.add("bbb");
		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item,
				R.id.checktv_title, recordFiles);
		/* ��ArrayAdapter���ListView������ */
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
		/* �ж�SD Card�Ƿ���� */
		sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		System.out.println("sdCardExist:" + sdCardExist);
		/* ȡ��SD Card·����Ϊ¼�����ļ�λ�� */
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
				// ������Ƶ��Դ(һ��Ϊ��˷�)
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// ������Ƶ�����ʽ��Ĭ�ϵ������ʽ��
				mediaRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				// ������Ƶ���뷽ʽ��Ĭ�ϵı��뷽ʽ��
				mediaRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				// ����һ����ʱ����Ƶ����ļ�
				audioFile = File.createTempFile("record_", ".amr");
				// new File(sdCardPath)
				System.out.println(audioFile.getAbsolutePath());
				mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
				mediaRecorder.prepare();
				mediaRecorder.start();
				msg = "����¼��...";
				break;
			case R.id.btnStop:
				if (audioFile != null) {
					mediaRecorder.stop();
					// ��ӵ�adapter
					adapter.add(audioFile.getName());
				}
				msg = "�Ѿ�ֹͣ¼��.";
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
									setTitle("¼���������.");

								}
							});
					msg = "���ڲ���¼��...";
				}
				break;
				
			case R.id.btnUpload:
				System.out.println("upload");
				if (audioFile != null) {
					
				}
				break;
			}
			setTitle(msg);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			setTitle(e.getMessage());
		}

	}
}
