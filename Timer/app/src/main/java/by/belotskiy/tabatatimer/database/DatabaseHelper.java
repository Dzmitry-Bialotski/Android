package by.belotskiy.tabatatimer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "WorkoutDatabase2.db";
    private static final int version = 1;
    private static final String TABLE_NAME_TRAINING = "training";
    private static final String[] Columns = new String[] {"id", "workout_name", "preparation_time", "stretch_time",
            "work_time", "relax_time", "cycles_number", "sets_number", "relax_after_set_time", "workout_order", "workout_order_time", "color"};


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRAINING);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = createMainTableCommand();
        sqLiteDatabase.execSQL(createTable);
    }

    private String createMainTableCommand() {
        String createTable = "CREATE TABLE " + TABLE_NAME_TRAINING + " (id INTEGER PRIMARY KEY AUTOINCREMENT, workout_name TEXT, ";
        for (String value: Columns) {
            if (!value.equals("id") && !value.equals("workout_name") && !value.equals("workout_order") && !value.equals("workout_order_time") && !value.equals("color")) {
                String insertColumn = value + " INTEGER, ";
                createTable = createTable + insertColumn;
            }
        }
        createTable += "workout_order TEXT, workout_order_time TEXT, color INTEGER)";

        return createTable;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRAINING);
        onCreate(sqLiteDatabase);
    }

    protected ContentValues createRow(String workout_name, int preparation_time, int stretch_time, int work_time,
                                      int relax_time, int cycles_number, int sets_number, int relax_after_set_time, String workout_order, String workout_order_time, Integer color) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns[1], workout_name);
        contentValues.put(Columns[2], preparation_time);
        contentValues.put(Columns[3], stretch_time);
        contentValues.put(Columns[4], work_time);
        contentValues.put(Columns[5], relax_time);
        contentValues.put(Columns[6], cycles_number);
        contentValues.put(Columns[7], sets_number);
        contentValues.put(Columns[8], relax_after_set_time);
        contentValues.put(Columns[9], workout_order);
        contentValues.put(Columns[10], workout_order_time);
        contentValues.put(Columns[11], color);
        return contentValues;
    }

    public boolean insertData(String workout_name, int preparation_time, int stretch_time, int work_time,
                              int relax_time, int cycles_number, int sets_number, int relax_after_set_time, String workout_order, String workout_order_time, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = createRow(workout_name, preparation_time, stretch_time, work_time, relax_time, cycles_number, sets_number, relax_after_set_time, workout_order, workout_order_time, color);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_TRAINING + " WHERE workout_name= '" + workout_name +"'", null);
        if (cursor.getCount() <= 0) {
            long insertedResult = db.insert(TABLE_NAME_TRAINING, null, contentValues);
            return insertedResult != -1;
        }
        else
            return false;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME_TRAINING, null);
    }

    public void updateData(int id, String workout_name, int preparation_time, int stretch_time,
                           int work_time, int relax_time, int cycles_number, int sets_number, int relax_after_set_time,
                           String workout_order, String workout_order_time, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = createRow(workout_name, preparation_time, stretch_time, work_time, relax_time, cycles_number, sets_number, relax_after_set_time, workout_order, workout_order_time, color);
        contentValues.put(Columns[0], id);
        db.update(TABLE_NAME_TRAINING, contentValues, "id = ?", new String[] { String.valueOf(id) });
    }

    public int deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_TRAINING, "id = ?", new String[] {id });
    }


}
