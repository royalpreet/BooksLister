package com.example.myapplication;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.android.books.R;

import java.util.ArrayList;
import java.util.List;

public class QueryResultsActivity
		extends AppCompatActivity
		implements LoaderCallbacks<List<Book>> {

	private static final String TAG = "QueryResultsActivity";

	private String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
	private static final String API_KEY = "AIzaSyAuptZgt2nSkjvzCbbpBGxmoPqjvUJlygc";
	private static final int EARTHQUAKE_LOADER_ID = 1;

	private BookAdapter mAdapter;
	private ProgressBar mProgressSpinner;
	private TextView mEmptyStateView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: ");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_of_books);

		BookRecyclerView recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);

		int orientation = this.getResources().getConfiguration().orientation;

		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		}

		mAdapter = new BookAdapter(new ArrayList<Book>(), QueryResultsActivity.this);

		recyclerView.setAdapter(mAdapter);

		mEmptyStateView = findViewById(R.id.empty_text_view);
		recyclerView.setEmptyView(mEmptyStateView);

		mProgressSpinner = findViewById(R.id.progress_spinner);
		mProgressSpinner.setIndeterminate(true);

		Intent queryIntent = getIntent();
		String searchText = getIntent().getStringExtra("topic");
		String processedQuery = "";
		String title = queryIntent.getStringExtra("title");
		String author = queryIntent.getStringExtra("author");
		String isbn = queryIntent.getStringExtra("isbn");

		if (title != null) {
			processedQuery = searchText + "+" + title + searchText;
		} else if (author != null) {
			processedQuery = searchText + "+" + author + searchText;
		} else if (isbn != null) {
			processedQuery = searchText + "+" + isbn + searchText;
		} else {
			processedQuery = searchText;
		}

		REQUEST_URL += processedQuery + "&maxResults=40" + "&key=" + API_KEY;

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			LoaderManager loaderManager = getLoaderManager();
			loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, QueryResultsActivity.this);
		} else {
			mProgressSpinner.setVisibility(View.GONE);
			mEmptyStateView.setText(R.string.no_internet_connection);
		}
	}

	@Override
	public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
		return new BookLoader(QueryResultsActivity.this, REQUEST_URL);
	}

	@Override
	public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
		mProgressSpinner.setVisibility(View.GONE);
		mEmptyStateView.setText(R.string.no_books);

		mAdapter.clear();

		if (books != null && !books.isEmpty()) {
			mAdapter.addAll(books);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Book>> loader) {
		mAdapter.clear();
	}

}

