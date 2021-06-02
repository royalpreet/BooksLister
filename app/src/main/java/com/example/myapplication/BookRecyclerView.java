package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BookRecyclerView extends RecyclerView {

	private View mEmptyView;

	private RecyclerView.AdapterDataObserver mDataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			updateEmptyStatus();
		}
	};

	public BookRecyclerView(Context context) {
		super(context);
	}

	public BookRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public BookRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setEmptyView(View emptyView) {
		this.mEmptyView = emptyView;
	}

	@Override
	public void setAdapter(RecyclerView.Adapter adapter) {
		if (getAdapter() != null) {
			getAdapter().unregisterAdapterDataObserver(mDataObserver);
		}
		if (adapter != null) {
			adapter.registerAdapterDataObserver(mDataObserver);
		}
		super.setAdapter(adapter);
		updateEmptyStatus();
	}

	private void updateEmptyStatus() {
		if (mEmptyView != null && getAdapter() != null) {

			final boolean showEmptyView = getAdapter().getItemCount() == 0;
			mEmptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
			setVisibility(showEmptyView ? GONE : VISIBLE);
		}
	}
}
