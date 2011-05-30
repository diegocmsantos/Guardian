package com.devforfun.guardian;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class GuardianProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri
			.parse("content://com.devforfun.provider.guardian/indices");

	// The underlying database
	private SQLiteDatabase guardianDB;
	private static final String TAG = "GuardianProvider";
	private static final String DATABASE_NAME = "indices.db";
	private static final int DATABASE_VERSION = 1;
	private static final String GUARDIAN_TABLE = "indices";

	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_VOZ = "voz";
	public static final String KEY_DADOS = "dados";
	public static final String KEY_DATA = "data";

	// Column indexes
	public static final int VOZ_COLUMN = 1;
	public static final int DADOS_COLUMN = 2;
	public static final int DATA_COLUMN = 3;

	// Create the constants used to differentiate between the different URI //
	// requests.
	private static final int INDICES = 1;
	private static final int INDICES_ID = 2;

	private static final UriMatcher uriMatcher;
	// Allocate the UriMatcher object, where a URI ending in ÔearthquakesÕ
	// will correspond to a request for all earthquakes, and ÔearthquakesÕ
	// with a trailing Ô/[rowID]Õ will represent a single earthquake row.
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.devforfun.provider.guardian", "indices", INDICES);
		uriMatcher.addURI("com.devforfun.provider.guardian", "indices/#",
				INDICES_ID);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {

		int count;

		switch (uriMatcher.match(uri)) {
		case INDICES:
			count = guardianDB.delete(GUARDIAN_TABLE, where, whereArgs);
			break;

		case INDICES_ID:
			String segment = uri.getPathSegments().get(1);
			count = guardianDB.delete(GUARDIAN_TABLE,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case INDICES:
			return "vnd.devforfun.cursor.dir/vnd.paad.guardian";
		case INDICES_ID:
			return "vnd.devforfun.cursor.item/vnd.paad.guardian";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		// Insert the new row, will return the row number if
		// successful.
		long rowID = guardianDB.insert(GUARDIAN_TABLE, "indice",
				_initialValues);

		// Return a URI to the newly inserted row on success.
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		guardiandogDatabaseHelper dbHelper;
		dbHelper = new guardiandogDatabaseHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
		guardianDB = dbHelper.getWritableDatabase();
		return (guardianDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(GUARDIAN_TABLE);

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case INDICES_ID:
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}

		// If no sort order is specified sort by date / time
		String orderBy;
		orderBy = sort;

		// Apply the query to the underlying database.
		Cursor c = qb.query(guardianDB, projection, selection, selectionArgs,
				null, null, orderBy);

		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);

		// Return a cursor to the query result.
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		int count;
		switch (uriMatcher.match(uri)) {
		case INDICES:
			count = guardianDB.update(GUARDIAN_TABLE, values, where,
					whereArgs);
			break;
		case INDICES_ID:
			String segment = uri.getPathSegments().get(1);
			count = guardianDB.update(GUARDIAN_TABLE, values,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	// Helper class for opening, creating, and managing // database version
	// control
	private static class guardiandogDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE = "create table "
				+ GUARDIAN_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, " + KEY_VOZ + " FLOAT, "
				+ KEY_DADOS + " FLOAT);";

		public guardiandogDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + GUARDIAN_TABLE);
			onCreate(db);
		}
	}

}
