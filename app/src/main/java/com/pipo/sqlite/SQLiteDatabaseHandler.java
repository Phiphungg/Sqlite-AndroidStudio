package com.pipo.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {
    public SQLiteDatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactData";
    private static final String TABLE_CONTACT = "Contact";
    private static final String KEY_ID = "id";
    private static final String NAME ="Name";
    private static final String PHONE = "Phone";
    private static final String EMAIL = "Email";
    private static final String IMAGE = "Image";

    public  SQLiteDatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                + PHONE + " TEXT," + EMAIL + " TEXT," + IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        onCreate(db);
    }

    void addContact(Contact contact){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, contact.getName());
        values.put(PHONE, contact.getPhone());
        values.put(EMAIL, contact.getEmail());
        values.put(IMAGE, contact.getImage());

        db.insert(TABLE_CONTACT,null,values);
        db.close();
    }

    Contact getContact(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACT, new String[] {KEY_ID,
        PHONE, NAME, EMAIL }, KEY_ID + "=?",
                new String[] {String.valueOf(id) }, null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4));

        return contact;
    }

    public List<Contact> getAllContact(){
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setImage(cursor.getString(4));
                // Adding country to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }


        return contactList;
    }


    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, contact.getName());
        values.put(PHONE, contact.getPhone());
        values.put(EMAIL, contact.getEmail());
        values.put(IMAGE, contact.getImage());

        return db.update(TABLE_CONTACT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }


    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    public void deleteAllContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT,null,null);
        db.close();
    }

    public int getContactCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
