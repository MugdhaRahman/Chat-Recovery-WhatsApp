package com.mrapps.chatrecovery.db

import android.content.ContentValues
import android.content.Context
import com.mrapps.chatrecovery.models.MessageModel

class MessageDBHelper(context: Context) : DBHelper(context) {

    companion object {
        const val MESSAGE_TABLE_NAME = "message_table"

        private const val COLUMN_ID = "id_message"
        private const val COLUMN_USER = "user_message"
        private const val COLUMN_MESSAGE = "message_message"
        private const val COLUMN_TIME = "time_message"
        private const val COLUMN_AVATAR_FILE_NAME = "avatar_file_name_message"
        private const val COLUMN_MESSAGE_SUMMARY = "message_summary_message"

        private const val SQL_CREATE_MESSAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS $MESSAGE_TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_USER TEXT,$COLUMN_MESSAGE TEXT,$COLUMN_TIME INTEGER,$COLUMN_AVATAR_FILE_NAME TEXT,$COLUMN_MESSAGE_SUMMARY TEXT)"

        private const val SQL_DELETE_MESSAGE_TABLE = "DROP TABLE IF EXISTS $MESSAGE_TABLE_NAME"

    }

    //  ------------------------------INIT------------------------------------

    init {
        val db = this.writableDatabase
        db.execSQL(SQL_CREATE_MESSAGE_TABLE)

    }

    fun dropMessageTable() {
        val db = this.writableDatabase
        db.execSQL(SQL_DELETE_MESSAGE_TABLE)
    }

    //  ------------------------------CREATE---------------------------------

    fun addMessageItem(messageModel: MessageModel): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, messageModel.user)
            put(COLUMN_MESSAGE, messageModel.message)
            put(COLUMN_TIME, messageModel.time)
            put(COLUMN_AVATAR_FILE_NAME, messageModel.avatarFileName)
            put(COLUMN_MESSAGE_SUMMARY, messageModel.messageSummary)
        }
        val mLong = db.insert(MESSAGE_TABLE_NAME, null, values)
        db.close()
        return mLong
    }

    //  ------------------------------READ-----------------------------------

    fun getAllMessage(): List<MessageModel> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $MESSAGE_TABLE_NAME", null)
        val messageList = ArrayList<MessageModel>()
        with(cursor) {
            while (moveToNext()) {
                val messageModel = MessageModel(
                    getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    getString(getColumnIndexOrThrow(COLUMN_USER)),
                    getString(getColumnIndexOrThrow(COLUMN_MESSAGE)),
                    getLong(getColumnIndexOrThrow(COLUMN_TIME)),
                    getString(getColumnIndexOrThrow(COLUMN_AVATAR_FILE_NAME)),
                    getString(getColumnIndexOrThrow(COLUMN_MESSAGE_SUMMARY))
                )
                messageList.add(messageModel)
            }
        }
        cursor.close()
        db.close()
        return messageList
    }

    fun getMessageById(id: Int): MessageModel {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $MESSAGE_TABLE_NAME WHERE $COLUMN_ID = $id", null)
        cursor.moveToFirst()
        val messageModel = MessageModel(
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)),
            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_FILE_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_SUMMARY))
        )
        cursor.close()
        db.close()
        return messageModel
    }

    fun getMessageByUser(user: String): List<MessageModel> {
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM $MESSAGE_TABLE_NAME WHERE $COLUMN_USER = '$user'", null)
        val messageList = ArrayList<MessageModel>()
        with(cursor) {
            while (moveToNext()) {
                val messageModel = MessageModel(
                    getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    getString(getColumnIndexOrThrow(COLUMN_USER)),
                    getString(getColumnIndexOrThrow(COLUMN_MESSAGE)),
                    getLong(getColumnIndexOrThrow(COLUMN_TIME)),
                    getString(getColumnIndexOrThrow(COLUMN_AVATAR_FILE_NAME)),
                    getString(getColumnIndexOrThrow(COLUMN_MESSAGE_SUMMARY))
                )
                messageList.add(messageModel)
            }
        }
        cursor.close()
        db.close()
        return messageList
    }

    fun getAllUsers(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_USER FROM $MESSAGE_TABLE_NAME", null)
        val userList = ArrayList<String>()
        with(cursor) {
            while (moveToNext()) {
                userList.add(getString(getColumnIndexOrThrow(COLUMN_USER)))
            }
        }

        cursor.close()
        db.close()
        return userList
    }

    //  ------------------------------UPDATE---------------------------------

    fun updateMessageItem(messageModel: MessageModel): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, messageModel.user)
            put(COLUMN_MESSAGE, messageModel.message)
            put(COLUMN_TIME, messageModel.time)
            put(COLUMN_AVATAR_FILE_NAME, messageModel.avatarFileName)
            put(COLUMN_MESSAGE_SUMMARY, messageModel.messageSummary)
        }
        val mInt = db.update(
            MESSAGE_TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(messageModel.id.toString())
        )
        db.close()
        return mInt
    }

    //  ------------------------------DELETE---------------------------------

    fun deleteMessageItem(id: Int): Int {
        val db = this.writableDatabase
        val mInt = db.delete(MESSAGE_TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return mInt
    }

    fun deleteByUser(user: String): Int {
        val db = this.writableDatabase
        val mInt = db.delete(MESSAGE_TABLE_NAME, "$COLUMN_USER = ?", arrayOf(user))
        db.close()
        return mInt
    }

    fun deleteAllMessage(): Int {
        val db = this.writableDatabase
        val mInt = db.delete(MESSAGE_TABLE_NAME, null, null)
        db.close()
        return mInt
    }


}