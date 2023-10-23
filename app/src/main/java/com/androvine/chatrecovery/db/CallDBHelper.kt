package com.androvine.chatrecovery.db

import android.content.ContentValues
import android.content.Context
import com.androvine.chatrecovery.models.CallModel
import com.androvine.chatrecovery.models.CallStatus
import com.androvine.chatrecovery.models.CallType

class CallDBHelper(context: Context) : DBHelper(context) {


    companion object {
        const val CALL_TABLE_NAME = "call_table"

        private const val COLUMN_ID = "id_call"
        private const val COLUMN_NAME = "name_call"
        private const val COLUMN_CALL_TYPE = "call_type_call"
        private const val COLUMN_CALL_STATUS = "call_status_call"
        private const val COLUMN_TIME = "time_call"
        private const val COLUMN_AVATAR_FILE_NAME = "avatar_file_name_call"

        private const val SQL_CREATE_CALL_TABLE = "CREATE TABLE IF NOT EXISTS $CALL_TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_CALL_TYPE TEXT," +
                "$COLUMN_CALL_STATUS TEXT," +
                "$COLUMN_TIME INTEGER," +
                "$COLUMN_AVATAR_FILE_NAME TEXT)"


        private const val SQL_DELETE_CALL_TABLE = "DROP TABLE IF EXISTS $CALL_TABLE_NAME"

    }

    //  ------------------------------INIT------------------------------------

    init {
        val db = this.writableDatabase
        db.execSQL(SQL_CREATE_CALL_TABLE)

    }

    fun dropCallTable() {
        val db = this.writableDatabase
        db.execSQL(SQL_DELETE_CALL_TABLE)
    }

    //  ------------------------------CREATE---------------------------------

    fun addCallItem(callModel: CallModel): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, callModel.user)
            put(COLUMN_CALL_TYPE, callModel.callType.toString())
            put(COLUMN_CALL_STATUS, callModel.callStatus.toString())
            put(COLUMN_TIME, callModel.time)
            put(COLUMN_AVATAR_FILE_NAME, callModel.avatarFileName)
        }
        val mLong = db.insert(CALL_TABLE_NAME, null, values)
        db.close()
        return mLong
    }


    //  ------------------------------READ-----------------------------------

    // get all call, sorted by time, newest first
    fun getAllCall(): List<CallModel> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $CALL_TABLE_NAME ORDER BY $COLUMN_TIME DESC", null)
        val callList = ArrayList<CallModel>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val callType = getString(getColumnIndexOrThrow(COLUMN_CALL_TYPE))
                val callStatus = getString(getColumnIndexOrThrow(COLUMN_CALL_STATUS))
                val time = getLong(getColumnIndexOrThrow(COLUMN_TIME))
                val avatarFileName = getString(getColumnIndexOrThrow(COLUMN_AVATAR_FILE_NAME))
                val callModel = CallModel(
                    id = id,
                    user = name,
                    callType = CallType.valueOf(callType),
                    callStatus = CallStatus.valueOf(callStatus),
                    time = time,
                    avatarFileName = avatarFileName
                )

                callList.add(callModel)
            }
        }
        return callList
    }

    // ------------------------------UPDATE---------------------------------

    fun updateCallItem(callModel: CallModel): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, callModel.user)
            put(COLUMN_CALL_TYPE, callModel.callType.toString())
            put(COLUMN_CALL_STATUS, callModel.callStatus.toString())
            put(COLUMN_TIME, callModel.time)
            put(COLUMN_AVATAR_FILE_NAME, callModel.avatarFileName)
        }
        val mInt =
            db.update(CALL_TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(callModel.id.toString()))
        db.close()
        return mInt
    }

    // ------------------------------DELETE---------------------------------

    fun deleteCallItem(id: Int): Int {
        val db = this.writableDatabase
        val mInt = db.delete(CALL_TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return mInt
    }

    fun deleteByUser(user: String): Int {
        val db = this.writableDatabase
        val mInt = db.delete(CALL_TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(user))
        db.close()
        return mInt
    }

    fun deleteAllCall(): Int {
        val db = this.writableDatabase
        val mInt = db.delete(CALL_TABLE_NAME, null, null)
        db.close()
        return mInt
    }


}