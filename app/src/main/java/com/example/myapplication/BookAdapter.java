package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.books.R;

import java.util.ArrayList;
import java.util.List;

class BookAdapter extends RecyclerView.Adapter<BookAdapter.CardViewHolder> {

	private final String LOG_TAG = BookAdapter.class.getSimpleName();

	private List<Book> mListOfBooks;
	private Context mContext;

	BookAdapter(List<Book> listOfBooks, Context context) {
		this.mListOfBooks = listOfBooks;
		this.mContext = context;
	}

	@Override
	public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.book_card, parent, false);

		return new CardViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CardViewHolder holder, int position) {
		Book currentBook = mListOfBooks.get(position);

		holder.bookTitle.setText(currentBook.getTitle());

		try {
			String authors = currentBook.getAuthor();

			if (!authors.isEmpty()) {
				holder.bookAuthor.setText(authors);
			}

		} catch (NullPointerException e) {
			Log.v(LOG_TAG, "No information on authors");
			holder.bookAuthor.setVisibility(View.INVISIBLE);
		}

		if(currentBook.getRating() != -1f){
			holder.bookRating.setVisibility(View.VISIBLE);
			holder.bookRating.setRating((float) currentBook.getRating());
		}
		else{
			holder.bookRating.setVisibility(View.INVISIBLE);
		}
		String price = "";
		if (currentBook.getPrice() > 0) {
			price = currentBook.getCurrency() + " " + currentBook.getPrice();
			holder.bookPrice.setText(price);
		}

		holder.cardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start intent for opening link
				Intent intent = new Intent(mContext, LinkActivity.class);
				intent.putExtra("url", currentBook.getLink());
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mListOfBooks.size();
	}

	void clear() {
		mListOfBooks = new ArrayList<>();
	}

	void addAll(List<Book> data) {
		for (int i = 0; i < data.size(); i++) {
			mListOfBooks.add(data.get(i));

			notifyDataSetChanged();
		}
	}

	static class CardViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;
		TextView bookTitle;
		TextView bookAuthor;
		RatingBar bookRating;
		TextView bookPrice;

		CardViewHolder(View itemView) {
			super(itemView);

			cardView = itemView.findViewById(R.id.card_view);
			bookTitle = itemView.findViewById(R.id.book_title_text_view);
			bookAuthor = itemView.findViewById(R.id.author_text_view);

			bookRating = itemView.findViewById(R.id.rating_bar);
			Drawable progress = bookRating.getProgressDrawable();
			DrawableCompat.setTint(progress, Color.YELLOW);

			bookPrice = itemView.findViewById(R.id.retail_price_text_view);
		}
	}
}
