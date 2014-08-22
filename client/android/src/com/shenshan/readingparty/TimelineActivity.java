package com.shenshan.readingparty;

import java.util.Arrays;
import java.util.LinkedList;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class TimelineActivity extends Activity {
	private PullAndLoadListView mListView = null;
	private LinkedList<String> mListItems;
	
	private ArrayAdapter<String> adapter;
	private String[] mNames = { "Fabian", "Carlos", "Alex", "Andrea", "Karla",
			"Freddy", "Lazaro", "Hector", "Carolina", "Edwin", "Jhon",
			"Edelmira", "Andres" };

	private String[] mAnimals = { "Perro", "Gato", "Oveja", "Elefante", "Pez",
			"Nicuro", "Bocachico", "Chucha", "Curie", "Raton", "Aguila",
			"Leon", "Jirafa" };
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
		
		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mNames));

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItems);
		
		//mListView.setAdapter(adapter);
		
		StatusItemAdapter statusAdapter = new StatusItemAdapter(this);
		statusAdapter.setData();
		mListView.setAdapter(statusAdapter);
		
		//录音按钮
		Button btnRecord = (Button) findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent();
				intent.setClass(TimelineActivity.this, PostActivity.class);
				startActivity(intent);
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

			for (int i = 0; i < mNames.length; i++)
				mListItems.add(mNames[i]);

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			mListItems.add("Added after load more");

			// We need notify the adapter that the data have been changed
			adapter.notifyDataSetChanged();

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

			for (int i = 0; i < mAnimals.length; i++)
				mListItems.addFirst(mAnimals[i]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			mListItems.addFirst("Added after pull to refresh");

			// We need notify the adapter that the data have been changed
			adapter.notifyDataSetChanged();

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
