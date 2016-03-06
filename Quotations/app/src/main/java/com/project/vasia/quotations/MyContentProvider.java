package com.project.vasia.quotations;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Vasia on 04.03.2016.
 * I know, I`ll do it.)
 */
public class MyContentProvider extends ContentProvider {
    //Константи для бази даних
    static final String DB_NAME = "mydb";
    final String LOG_TAG = "myLogs";
    static final int DB_VERSION = 1;

    // Таблиці
    static final String THEMES_TABLE = "themes";
    static final String QUOTES_TABLE = "quotes";
    static final String AUTHORS_TABLE = "authors";
    static final String FAVOURITES_TABLE = "favourites";
    // Поля для таблиці 1
    static final String THEMES_ID = "_id";
    static final String THEMES_NAME = "name";
    static final String THEMES_UDATATIME = "udatetime";
    // Поля для таблиці 2
    static final String QUOTES_ID = "_id";
    static final String QUOTES_NAME = "quotes";
    static final String QUOTES_THEME_ID = "theme_id";
    static final String QUOTES_TIMESTAMP = "timestamp";
    static final String QUOTES_AUTHOR_ID = "author_id";
    // Поля для таблиці 3
    static final String AUTHORS_ID = "_id";
    static final String AUTHORS_NAME = "name";
    static final String AUTHORS_TIME = "timestamp";
    // Поля для таблиці 4
    static final String FAVOURITES_ID = "_id";
    static final String FAVOURITES_QOUTES = "quote";
    // Скрипт создания таблицы
    static final String DB_THEMES_CREATE = "create table " + THEMES_TABLE + "("
            + THEMES_ID + " integer primary key, "
            + THEMES_NAME + " text, " + THEMES_UDATATIME + " integer" + ");";
    static final String DB_QUOTES_CREATE = "create table " + QUOTES_TABLE + "("
            + QUOTES_ID + " integer primary key, "
            + QUOTES_NAME + " text, " + QUOTES_THEME_ID + " integer ,"
            + QUOTES_TIMESTAMP + " integer, " + QUOTES_AUTHOR_ID + " integer" +");";
    static final String DB_AUTHORS_CREATE = "create table " + AUTHORS_TABLE + "("
            + AUTHORS_ID + " integer primary key, "
            + AUTHORS_NAME + " text, " + AUTHORS_TIME + " integer" + ");";
    static final String DB_FAVOURITES_CREATE = "create table " + FAVOURITES_TABLE + "("
            + FAVOURITES_ID + " integer primary key autoincrement, "
            + FAVOURITES_QOUTES + " text, " + ");";

    // // Uri
    // authority
    static final String AUTHORITY = "ua.vasia.Quotations";
    // path
    static final String THEMES_PATH = "themes";
    static final String QUOTES_PATH = "quotes";
    static final String AUTHORS_PATH = "authors";
    static final String FAVOURITES_PATH = "favourites";
    // Общий Uri
    public static final Uri THEMES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + THEMES_PATH);
    public static final Uri QUOTES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QUOTES_PATH);
    public static final Uri AUTHORS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + AUTHORS_PATH);
    public static final Uri FAVOURITES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FAVOURITES_PATH);
    // Типы данных
    // набор строк
    static final String THEMES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + THEMES_PATH;
    static final String QUOTES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + QUOTES_PATH;
    static final String AUTHORS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + AUTHORS_PATH;
    static final String FAVOURITES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + FAVOURITES_PATH;
    // одна строка
    static final String THEMES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + THEMES_PATH;
    static final String QUOTES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + QUOTES_PATH;
    static final String AUTHORS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + AUTHORS_PATH;
    static final String FAVOURITES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + FAVOURITES_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_THEMES = 1;
    static final int URI_QUOTES = 3;
    static final int URI_AUTHORS = 5;
    static final int URI_FAVOURITES = 7;
    // Uri с указанным ID
    static final int URI_THEMES_ID = 2;
    static final int URI_QUOTES_ID = 4;
    static final int URI_AUTHORS_ID = 6;
    static final int URI_FAVOURITES_ID = 8;
    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, THEMES_PATH, URI_THEMES);
        uriMatcher.addURI(AUTHORITY, THEMES_PATH + "/#", URI_THEMES_ID);
        uriMatcher.addURI(AUTHORITY, QUOTES_PATH, URI_QUOTES);
        uriMatcher.addURI(AUTHORITY, QUOTES_PATH + "/#", URI_QUOTES_ID);
        uriMatcher.addURI(AUTHORITY, AUTHORS_PATH, URI_AUTHORS);
        uriMatcher.addURI(AUTHORITY, AUTHORS_PATH + "/#", URI_AUTHORS_ID);
        uriMatcher.addURI(AUTHORITY, FAVOURITES_PATH, URI_FAVOURITES);
        uriMatcher.addURI(AUTHORITY, FAVOURITES_PATH + "/#", URI_FAVOURITES_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;
    public static String NEED_TABLE;
    public static Uri NEED_CONTENT_URI;
    public static int URI_NEED;

    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }
    // чтение
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());

        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_THEMES: // общий Uri
                Log.d(LOG_TAG, "URI_THEMES");
                NEED_TABLE = THEMES_TABLE;
                NEED_CONTENT_URI = THEMES_CONTENT_URI;
                URI_NEED = URI_THEMES;
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = THEMES_NAME + " ASC";
                }
                break;
            case URI_THEMES_ID: // Uri с ID
                String id1 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_THEMES_ID, " + id1);
                NEED_TABLE = THEMES_TABLE;
                NEED_CONTENT_URI = THEMES_CONTENT_URI;
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = THEMES_ID + " = " + id1;
                } else {
                    selection = selection + " AND " + THEMES_ID + " = " + id1;
                }
                break;
            case URI_QUOTES: // общий Uri
                Log.d(LOG_TAG, "URI_QUOTES");
                NEED_TABLE = QUOTES_TABLE;
                NEED_CONTENT_URI = QUOTES_CONTENT_URI;
                URI_NEED = URI_QUOTES;
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = QUOTES_NAME + " ASC";
                }
                break;
            case URI_QUOTES_ID: // Uri с ID
                String id2 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_QUOTES_ID, " + id2);
                NEED_TABLE = QUOTES_TABLE;
                NEED_CONTENT_URI = QUOTES_CONTENT_URI;
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = QUOTES_ID + " = " + id2;
                } else {
                    selection = selection + " AND " + QUOTES_ID + " = " + id2;
                }
                break;
            case URI_AUTHORS: // общий Uri
                Log.d(LOG_TAG, "URI_AUTHORS");
                NEED_TABLE = AUTHORS_TABLE;
                NEED_CONTENT_URI = AUTHORS_CONTENT_URI;
                URI_NEED = URI_AUTHORS;
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = AUTHORS_NAME + " ASC";
                }
                break;
            case URI_AUTHORS_ID: // Uri с ID
                String id3 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_AUTHORS_ID, " + id3);
                NEED_TABLE = AUTHORS_TABLE;
                NEED_CONTENT_URI = AUTHORS_CONTENT_URI;
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = AUTHORS_ID + " = " + id3;
                } else {
                    selection = selection + " AND " + AUTHORS_ID + " = " + id3;
                }
                break;
            case URI_FAVOURITES: // общий Uri
                Log.d(LOG_TAG, "URI_FAVOURITES");
                NEED_TABLE = FAVOURITES_TABLE;
                NEED_CONTENT_URI = FAVOURITES_CONTENT_URI;
                URI_NEED = URI_FAVOURITES;
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = FAVOURITES_QOUTES + " ASC";
                }
                break;
            case URI_FAVOURITES_ID: // Uri с ID
                String id4 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_FAVOURITES_ID, " + id4);
                NEED_TABLE = FAVOURITES_TABLE;
                NEED_CONTENT_URI = FAVOURITES_CONTENT_URI;
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = FAVOURITES_ID + " = " + id4;
                } else {
                    selection = selection + " AND " + FAVOURITES_ID + " = " + id4;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(NEED_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в THEMES_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                NEED_CONTENT_URI);
        return cursor;
    }


    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_NEED)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(NEED_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(NEED_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_THEMES:
                Log.d(LOG_TAG, "URI_THEMES");
                NEED_TABLE = THEMES_TABLE;
                break;
            case URI_THEMES_ID:
                NEED_TABLE = THEMES_TABLE;
                String id1 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_THEMES_ID, " + id1);
                if (TextUtils.isEmpty(selection)) {
                    selection = THEMES_ID + " = " + id1;
                } else {
                    selection = selection + " AND " + THEMES_ID + " = " + id1;
                }
                break;
            case URI_QUOTES:
                Log.d(LOG_TAG, "URI_QUOTES");
                NEED_TABLE = QUOTES_TABLE;
                break;
            case URI_QUOTES_ID:
                NEED_TABLE = QUOTES_TABLE;
                String id2 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_QUOTES_ID, " + id2);
                if (TextUtils.isEmpty(selection)) {
                    selection = QUOTES_ID + " = " + id2;
                } else {
                    selection = selection + " AND " + QUOTES_ID + " = " + id2;
                }
                break;
            case URI_AUTHORS:
                Log.d(LOG_TAG, "URI_AUTHORS");
                NEED_TABLE = AUTHORS_TABLE;
                break;
            case URI_AUTHORS_ID:
                NEED_TABLE = AUTHORS_TABLE;
                String id3 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_AUTHORS_ID, " + id3);
                if (TextUtils.isEmpty(selection)) {
                    selection = AUTHORS_ID + " = " + id3;
                } else {
                    selection = selection + " AND " + AUTHORS_ID + " = " + id3;
                }
                break;
            case URI_FAVOURITES:
                Log.d(LOG_TAG, "URI_FAVOURITES");
                NEED_TABLE = FAVOURITES_TABLE;
                break;
            case URI_FAVOURITES_ID:
                NEED_TABLE = FAVOURITES_TABLE;
                String id4 = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_FAVOURITES_ID, " + id4);
                if (TextUtils.isEmpty(selection)) {
                    selection = FAVOURITES_ID + " = " + id4;
                } else {
                    selection = selection + " AND " + FAVOURITES_ID + " = " + id4;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(NEED_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_THEMES:
                Log.d(LOG_TAG, "URI_THEMES");

                break;
            case URI_THEMES_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_THEMES_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = THEMES_ID + " = " + id;
                } else {
                    selection = selection + " AND " + THEMES_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(THEMES_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_THEMES:
                return THEMES_CONTENT_TYPE;
            case URI_THEMES_ID:
                return THEMES_CONTENT_ITEM_TYPE;
            case URI_QUOTES:
                return QUOTES_CONTENT_TYPE;
            case URI_QUOTES_ID:
                return QUOTES_CONTENT_ITEM_TYPE;
            case URI_AUTHORS:
                return AUTHORS_CONTENT_TYPE;
            case URI_AUTHORS_ID:
                return AUTHORS_CONTENT_ITEM_TYPE;
            case URI_FAVOURITES:
                return FAVOURITES_CONTENT_TYPE;
            case URI_FAVOURITES_ID:
                return FAVOURITES_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_THEMES_CREATE);
            db.execSQL(DB_QUOTES_CREATE);
            db.execSQL(DB_AUTHORS_CREATE);
            db.execSQL(DB_FAVOURITES_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
