package com.example.myapplication;

class Book {

	private String mTitle;
	private String mAuthors;
	private float mRating;
	private float mPrice;
	private String mCurrency;
	private String mLink;


	Book(String title, String authors, float rating, float price, String currency, String link) {
		this.mTitle = title;
		this.mAuthors = authors;
		this.mRating = rating;
		this.mPrice = price;
		this.mCurrency = currency;
		this.mLink = link;
	}

	String getTitle() {
		return mTitle;
	}

	String getAuthor() {
		return mAuthors;
	}

	float getRating() {
		return mRating;
	}

	float getPrice() {
		return mPrice;
	}

	String getCurrency() {
		return mCurrency;
	}

	String getLink() {
		return mLink;
	}



}
