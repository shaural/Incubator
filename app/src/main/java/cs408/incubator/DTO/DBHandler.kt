package cs408.incubator.DTO


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHandler(val context: Context) : SQLiteOpenHelper(context,
        cs408.incubator.DTO.DB_NAME, null,
        cs408.incubator.DTO.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoItemTable =
                "CREATE TABLE ${cs408.incubator.DTO.TABLE_TODO_ITEM} (" +
                        "${cs408.incubator.DTO.COL_ID} integer PRIMARY KEY AUTOINCREMENT," +
                        "${cs408.incubator.DTO.COL_CREATED_AT} datetime DEFAULT CURRENT_TIMESTAMP," +
                        "${cs408.incubator.DTO.COL_TODO_ID} integer," +
                        "${cs408.incubator.DTO.COL_ITEM_NAME} varchar," +
                        "${cs408.incubator.DTO.COL_IS_COLPLETED} integer);"

        db.execSQL(createToDoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }



    fun updateToDoItemCompletedStatus(todoId: Long, isCompleted: Boolean) {
        val db = writableDatabase
        val queryResult = db.rawQuery(
                "SELECT * FROM ${cs408.incubator.DTO.TABLE_TODO_ITEM} WHERE ${cs408.incubator.DTO.COL_TODO_ID}=$todoId",
                null
        )

        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(cs408.incubator.DTO.COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(cs408.incubator.DTO.COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(cs408.incubator.DTO.COL_ITEM_NAME))
                item.isCompleted = isCompleted
                updateToDoItem(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
    }



    fun addToDoItem(item: ToDoItem): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(cs408.incubator.DTO.COL_ITEM_NAME, item.itemName)
        cv.put(cs408.incubator.DTO.COL_TODO_ID, item.toDoId)
        cv.put(cs408.incubator.DTO.COL_IS_COLPLETED, item.isCompleted)

        val result = db.insert(cs408.incubator.DTO.TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }

    fun updateToDoItem(item: ToDoItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(cs408.incubator.DTO.COL_ITEM_NAME, item.itemName)
        cv.put(cs408.incubator.DTO.COL_TODO_ID, item.toDoId)
        cv.put(cs408.incubator.DTO.COL_IS_COLPLETED, item.isCompleted)

        db.update(
                cs408.incubator.DTO.TABLE_TODO_ITEM,
                cv,
                "${cs408.incubator.DTO.COL_ID}=?",
                arrayOf(item.id.toString())
        )
    }

    fun deleteToDoItem(itemId: Long) {
        val db = writableDatabase
        db.delete(cs408.incubator.DTO.TABLE_TODO_ITEM, "${cs408.incubator.DTO.COL_ID}=?", arrayOf(itemId.toString()))
    }

    fun getToDoItems(todoId: Long): MutableList<ToDoItem> {
        val result: MutableList<ToDoItem> = ArrayList()

        val db = readableDatabase
        val queryResult = db.rawQuery(
                "SELECT * FROM ${cs408.incubator.DTO.TABLE_TODO_ITEM} WHERE ${cs408.incubator.DTO.COL_TODO_ID}=$todoId",
                null
        )

        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(cs408.incubator.DTO.COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(cs408.incubator.DTO.COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(cs408.incubator.DTO.COL_ITEM_NAME))
                item.isCompleted =
                        queryResult.getInt(queryResult.getColumnIndex(cs408.incubator.DTO.COL_IS_COLPLETED)) == 1
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

}