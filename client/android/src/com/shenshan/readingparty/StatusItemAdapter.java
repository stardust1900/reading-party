package com.shenshan.readingparty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.shenshan.readingparty.utils.HttpUtils;
import com.shenshan.readingparty.utils.RestConst;

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

	private LinkedList<HashMap<String, Object>> mData = new LinkedList<HashMap<String, Object>>();

	private LayoutInflater mInflater;// 动态布局映射

	private ImageView img;

	private ImageButton imgBtn;

	public StatusItemAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	// 决定ListView有几行可见
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
		convertView = mInflater.inflate(R.layout.friend_list_item, null);// 根据布局文件实例化view
		TextView title = (TextView) convertView.findViewById(R.id.title);// 找某个控件
		title.setText(mData.get(position).get("title").toString());// 给该控件设置数据(数据从集合类中来)
		TextView time = (TextView) convertView.findViewById(R.id.time);// 找某个控件
		time.setText(mData.get(position).get("time").toString());// 给该控件设置数据(数据从集合类中来)

		TextView info = (TextView) convertView.findViewById(R.id.info);
		info.setText(mData.get(position).get("info").toString());
		// img = (ImageView) convertView.findViewById(R.id.img);
		// img.setBackgroundResource((Integer) mData.get(position).get("img"));
		imgBtn = (ImageButton) convertView.findViewById(R.id.imageButton1);
		// imgBtn.setBackgroundResource(R.drawable.play);
		final Drawable palyDrawable = convertView.getResources().getDrawable(
				R.drawable.play);
		final Drawable pauseDrawable = convertView.getResources().getDrawable(
				R.drawable.pause);
		imgBtn.setBackground(palyDrawable);
		final MediaPlayer player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(mData.get(position).get("url").toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (palyDrawable == view.getBackground()) {
					view.setBackground(pauseDrawable);
					try {
						player.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.start();
				} else {
					view.setBackground(palyDrawable);
					player.stop();
				}
			}
		});

		return convertView;
	}

	private List<HashMap<String, Object>> getData(QueryParams params) {
		// 新建一个集合类，用于存放多条数据
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("since_id", params.getSinceId());
		paramsMap.put("max_id", params.getMaxId());
		try {
			String JSON = HttpUtils.get(RestConst.GETURL, paramsMap);
			JSONTokener jsonParser = new JSONTokener(JSON);
			JSONObject sounds = (JSONObject) jsonParser.nextValue();
			JSONArray jsonArray = sounds.getJSONArray("sounds");
			HashMap<String, Object> map = null;
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				map = new HashMap<String, Object>();
				JSONObject sound = (JSONObject) jsonArray.opt(i);
				map.put("title", sound.getString("readerName"));
				map.put("time", sound.getString("pubtime"));
				map.put("info", sound.getString("memo"));
				map.put("img", R.drawable.ic_launcher);
				map.put("url", RestConst.DOMAIN + sound.getString("soundUrl"));
				list.add(map);
				if (i == 0 && params.getSinceId().isEmpty()) {
					params.setMaxId(String.valueOf(sound.getInt("soundId")));
				}
				if (i == len - 1 && params.getMaxId().isEmpty()) {
					params.setSinceId(String.valueOf(sound.getInt("soundId")));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public void setData(QueryParams params) {
		mData.addAll(getData(params));
	}
	
	public void refresh(QueryParams params) {
		List<HashMap<String, Object>> result = getData(params);
		for(int i = result.size()-1;i>=0;i--){
			mData.addFirst(result.get(i));
		}
	}

	public void loadMore(QueryParams params) {
		List<HashMap<String, Object>> result = getData(params);
		for(int i = 0; i<result.size();i++){
			mData.addLast(result.get(i));
		}
	}
}
