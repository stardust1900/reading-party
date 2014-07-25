package com.shenshan.readingparty;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

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

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // ��ʱʱ��
	private static final String CHARSET = "utf-8"; // ���ñ���

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
		Button btnJump = (Button) findViewById(R.id.btnJump);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnJump.setOnClickListener(new JumpListener());

		myListView1 = (ListView) findViewById(R.id.ListView01);

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
				System.out.println("upload :"+audioFile);
				if (audioFile != null) {
					System.out.println("111");
					upload(audioFile,"http://127.0.0.1:8000/myapp/list/");
				}
				break;
			}
			setTitle(msg);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

	}

	public void upload(File f, String RequestURL) {
		System.out.println("222");
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������
		String result = null;
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // ����������
			conn.setDoOutput(true); // ���������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST"); // ����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * �����ص�ע�⣺
             * name�����ֵΪ����������Ҫkey   ֻ�����key �ſ��Եõ���Ӧ���ļ�
             * filename���ļ������֣�������׺����   ����:abc.png  
             */
            
            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+f.getName()+"\""+LINE_END); 
            sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
            sb.append("aa=aa&bb=bb").append(LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            InputStream is = new FileInputStream(f);
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len=is.read(bytes))!=-1)
            {
                dos.write(bytes, 0, len);
            }
            is.close();
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
           /**
             * ��ȡ��Ӧ��  200=�ɹ�
             * ����Ӧ�ɹ�����ȡ��Ӧ����  
             */
            int res = conn.getResponseCode();  
            Log.e(TAG, "response code:"+res);
           if(res==200)
           {
                Log.e(TAG, "request success");
                InputStream input =  conn.getInputStream();
                StringBuffer sb1= new StringBuffer();
                int ss ;
                while((ss=input.read())!=-1)
                {
                    sb1.append((char)ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : "+ result);
          }
//            else{
//                Log.e(TAG, "request error");
//            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	class JumpListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, PlayActivity.class);
			
			startActivity(intent);
		}
		
	}
}

