package com.example.myapplication;

import android.content.AsyncTaskLoader;
import android.content.Context;


import java.util.List;

class BookLoader extends AsyncTaskLoader<List<Book>> {

	private String mSearchUrl;
	private List<Book> mData;

	BookLoader(Context context, String url) {
		super(context);
		mSearchUrl = url;
	}

	@Override
	protected void onStartLoading() {
		if (mData != null) {
			deliverResult(mData); // Use cached data
		} else {
			forceLoad();
		}
	}

	@Override
	public List<Book> loadInBackground() {
		if (mSearchUrl == null) {
			return null;
		}
		return QueryUtils.fetchBooks(mSearchUrl);
	}

	@Override
	public void deliverResult(List<Book> data) {
		mData = data; // Cache data
		super.deliverResult(data);
	}
}
