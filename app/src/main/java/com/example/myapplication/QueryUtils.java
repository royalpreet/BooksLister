package com.example.myapplication;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class QueryUtils {

	private static final String LOG_TAG = QueryUtils.class.getSimpleName();

	private QueryUtils() {
	}

	static List<Book> fetchBooks(String requestUrl) {
		URL url = createUrl(requestUrl);
		String jsonResponse = "";

		// Perform HTTP request to the above created valid URL
		try {
			jsonResponse = makeHttpRequest(url);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem making the HTTP request for the search criteria");
		}

		// Extract information from the JSON response for each book
		return QueryUtils.extractFeatures(jsonResponse);
	}

	private static URL createUrl(String stringUrl) {
		URL url = null;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Problem building the url!");
		}

		return url;
	}

	private static List<Book> extractFeatures(String booksJSON) {
		if (TextUtils.isEmpty(booksJSON)) {
			return null;
		}

		// Initialize list of strings to hold the extracted books
		List<Book> allBooks = new ArrayList<>();

		// Traverse the raw JSON response parameter and mine for relevant information
		try {
			// Create JSON object from response
			JSONObject rawJSONResponse = new JSONObject(booksJSON);

			// Extract the array that holds the books
			JSONArray books = rawJSONResponse.getJSONArray("items");
			for (int i = 0; i < books.length(); i++) {
				// Get the current book
				JSONObject book = books.getJSONObject(i);
				// Get the current book's volume information
				JSONObject volume = book.getJSONObject("volumeInfo");
				// Get the book's title from the volume information
				String bookTitle = volume.getString("title");

				// Extract information on authors of the book
				// Initialize empty string to hold authors of the book
				StringBuilder authors = new StringBuilder();
				// Check whether the JSON results contain information on authors of the book
				if (volume.has("authors")) {
					// JSON does have author information
					// Extract the array that holds the data
					JSONArray jsonAuthors = volume.getJSONArray("authors");
					// Find and store the number of authors present in the authors array
					int numberOfAuthors = jsonAuthors.length();
					// Set max number of authors that can be displayed effectively without
					// over-populating the view
					int maxAuthors = 3;

					/* Sometimes author information within the author JSON array
					 is a single string item with concatenated authors separated by
					 a semicolon or a comma and this does not display itself properly on the
					 screen because there are too many authors along with the separators */

					// Initialize variables
					String cAuthors = "";
					String[] allAuthors =  null;

					// Length of the first item from the array is used to deterministically
					// come to the conclusion that the authors are concatenated together
					// as a single string
					int numberOfLetters = jsonAuthors.get(0).toString().length();
					// Conservatively set 40 as the max length for an author's name
					if (numberOfLetters > 40) {
						// Authors are concatenated
						// Extract concatenated authors and remove beginning and trailing characters
						// as a result of toString() artifact
						cAuthors = jsonAuthors.toString().substring(2, numberOfLetters - 1);
						// Split on semi-colons or commas
						allAuthors = cAuthors.split("[;,]");
						// Traverse the array and get up to max authors
						for (int j = 0; j < allAuthors.length && j < maxAuthors; j++) {
							authors.append(allAuthors[j].trim()).append("\n");
						}

					} else {
						// Authors are not concatenated within the array as a single string item
						// Traverse the json array and add authors to the newly initialized array
						for (int j = 0; j < numberOfAuthors && j < maxAuthors; j++) {
							authors.append(jsonAuthors.getString(j)).append("\n");
						}
					}
				}

				// Initialize float variable to hold current book's ratings
				float bookRating = -1f;
				// Check whether the JSON results contain information on book rating
				if (volume.has("averageRating")) {
					// Get the average rating of the book from the JSON response
					bookRating = (float) volume.getDouble("averageRating");
				}

				// Get the current book's sale information
				JSONObject saleInfo = book.getJSONObject("saleInfo");
				// Get the value to determine whether the current book is saleable or not
				String saleability = saleInfo.getString("saleability");
				// Initialize a boolean variable to get the sale status of the book
				boolean isSold = saleability.equals("FOR_SALE");
				// Initialize variable to store book price
				float bookPrice = 0f;
				String currency = "";
				// Extract sale price only when book is available for sale
				if (isSold) {
					JSONObject priceInfo = saleInfo.getJSONObject("retailPrice");
					bookPrice = (float) priceInfo.getDouble("amount");
					currency = priceInfo.getString("currencyCode");
				}

				String link = volume.getString("previewLink");

				allBooks.add(new Book(bookTitle, authors.toString(), bookRating, bookPrice, currency, link));
			}

		} catch (JSONException e) {
			Log.e(LOG_TAG, "Problem parsing the google books JSON results", e);
		}

		return allBooks;
	}

	private static String makeHttpRequest(URL url) throws IOException {
		// Initialize variable to hold the parsed json response
		String jsonResponse = "";

		// Return early if url is null
		if (url == null) {
			return jsonResponse;
		}

		// Initialize HTTP connection object
		HttpURLConnection urlConnection = null;

		// Initialize {@link InputStream} to hold response from request
		InputStream inputStream = null;

		try {
			// Establish connection to the url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Set request type
			urlConnection.setRequestMethod("GET");

			// Set read and connection timeout in milliseconds
			// Basically, setting how long to wait on the request
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(15000);

			// Establish connection to the url
			urlConnection.connect();

			// Check for successful connection
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Connection successfully established
				inputStream = urlConnection.getInputStream();
				jsonResponse = readFromStream(inputStream);
			} else {
				Log.e(LOG_TAG, "Error while connecting. Error Code: " + urlConnection.getResponseCode());
			}
		} catch (IOException e) {
			e.getMessage();
			Log.e(LOG_TAG, "Problem encountered while retrieving book results");
		} finally {
			if (urlConnection != null) {
				// Disconnect the connection after successfully making the HTTP request
				urlConnection.disconnect();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return jsonResponse;
	}

	private static String readFromStream(InputStream inputStream) throws IOException {
		StringBuilder output = new StringBuilder();
		if (inputStream != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = reader.readLine();

			while (line != null) {
				output.append(line);
				line = reader.readLine();
			}
		}

		return output.toString();
	}
}
