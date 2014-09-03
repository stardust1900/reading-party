package com.shenshan.readingparty;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TimelineActivity extends Activity {
	private PullAndLoadListView mListView = null;

	private StatusItemAdapter statusAdapter;

	private QueryParams param = new QueryParams();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		mListView = (PullAndLoadListView) findViewById(R.id.msg_list_item);
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new PullToRefreshDataTask().execute();
			}
		});
		mListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				new LoadMoreDataTask().execute();
			}
		});

		// adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, mListItems);

		// mListView.setAdapter(adapter);

		statusAdapter = new StatusItemAdapter(this);
		// statusAdapter.setData();
		mListView.setAdapter(statusAdapter);
		new PullToRefreshDataTask().execute();
		// 录音按钮
		Button btnRecord = (Button) findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
				String accessToken = sp.getString("access_token", "");
				//long expireTime = sp.getLong("expire_time", System.currentTimeMillis());
				if (accessToken.isEmpty()) {
					Intent intent = new Intent();
					intent.setClass(TimelineActivity.this, LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(TimelineActivity.this, PostActivity.class);
					startActivity(intent);
				}
			}

		});
	}

	private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			if (isCancelled()) {
				return null;
			}

			// Simulates a background task
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			param.setMaxId("");
			statusAdapter.loadMore(param);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// We need notify the adapter that the data have been changed
			statusAdapter.notifyDataSetChanged();

			// Call onLoadMoreComplete when the LoadMore task, has finished
			mListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mListView.onLoadMoreComplete();
		}
	}

	private class PullToRefreshDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			if (isCancelled()) {
				return null;
			}

			// Simulates a background task
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			if (param.getMaxId().isEmpty() && param.getSinceId().isEmpty()) {
				statusAdapter.setData(param);
			} else {
				param.setSinceId("");
				statusAdapter.refresh(param);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// mListItems.addFirst("Added after pull to refresh");

			// We need notify the adapter that the data have been changed
			statusAdapter.notifyDataSetChanged();

			// Call onLoadMoreComplete when the LoadMore task, has finished
			mListView.onRefreshComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// Notify the loading more operation has finished
			mListView.onLoadMoreComplete();
		}
	}

}

class QueryParams {
	private String sinceId = "";
	private String maxId = "";

	public String getSinceId() {
		return sinceId;
	}

	public void setSinceId(String sinceId) {
		this.sinceId = sinceId;
	}

	public String getMaxId() {
		return maxId;
	}

	public void setMaxId(String maxId) {
		this.maxId = maxId;
	}

	public String toString() {
		return "maxId:" + maxId + " sinceId:" + sinceId;
	}
}
