package com.shenshan.readingparty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusItemAdapter extends BaseAdapter {

	private List<HashMap<String, Object>> mData;
	
	private LayoutInflater mInflater;//动态布局映射 
	
	private ImageView img;
	
	private ImageButton imgBtn;
	
	public StatusItemAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	//决定ListView有几行可见 
	@Override
	public int getCount() {
		return mData.size();// ListView的条目数
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.friend_list_item, null);//根据布局文件实例化view  
		TextView title = (TextView) convertView.findViewById(R.id.title);//找某个控件
		title.setText(mData.get(position).get("title").toString());//给该控件设置数据(数据从集合类中来)
		TextView time = (TextView) convertView.findViewById(R.id.time);//找某个控件 
		time.setText(mData.get(position).get("time").toString());//给该控件设置数据(数据从集合类中来) 
		
		TextView info = (TextView) convertView.findViewById(R.id.info);
		info.setText(mData.get(position).get("info").toString());
		//img = (ImageView) convertView.findViewById(R.id.img);		
		//img.setBackgroundResource((Integer) mData.get(position).get("img")); 
		imgBtn = (ImageButton) convertView.findViewById(R.id.imageButton1);
		//imgBtn.setBackgroundResource(R.drawable.play);
		final Drawable palyDrawable = convertView.getResources().getDrawable(R.drawable.play);
		final Drawable pauseDrawable = convertView.getResources().getDrawable(R.drawable.pause);
		imgBtn.setBackground(palyDrawable);
		final MediaPlayer   player  =   new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource("http://yinyueshiting.baidu.com/data2/music/122112390/12012502946800128.mp3?xcode=e8d534c2286a1e82c08beaf0ed928e9d3f8882ee658cd7de");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imgBtn.setOnClickListener(new OnClickListener(){
						
			@Override
			public void onClick(View view) {
				if(palyDrawable == view.getBackground()) {
					view.setBackground(pauseDrawable);
					try {
						player.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.start();
				}else{
					view.setBackground(palyDrawable);
					player.stop();
				}
			} });
		
		return convertView;
	}
	
	private List<HashMap<String, Object>> getData() {  
        // 新建一个集合类，用于存放多条数据  
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();  
        HashMap<String, Object> map = null;  
        for (int i = 1; i <= 40; i++) {  
            map = new HashMap<String, Object>();  
            map.put("title", "人物" + i);  
            map.put("time", "9月20日");
            map.put("info", "一分钟读书会");
            map.put("img", R.drawable.ic_launcher);
            list.add(map);
        }  
  
        return list;  
    }  
    public void setData(){  
    	mData = getData();
    } 

}
