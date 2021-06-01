package com.example.myapplication;

class Book {

	/**
	 * Title of the book
	 */
	private String mTitle;

	/**
	 * Author of the book
	 */
	private String mAuthors;

	/**
	 * Average rating for the book
	 */
	private float mRating;

	/**
	 * Retail price of the book
	 */
	private float mPrice;

	/**
	 * Currency of price
	 */
	private String mCurrency;


	Book(String title, String authors, float rating, float price, String currency) {
		this.mTitle = title;
		this.mAuthors = authors;
		this.mRating = rating;
		this.mPrice = price;
		this.mCurrency = currency;
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



}
