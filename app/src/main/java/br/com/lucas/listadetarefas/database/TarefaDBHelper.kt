package br.com.lucas.listadetarefas.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.lucas.listadetarefas.model.Tarefa
import br.com.lucas.listadetarefas.util.TarefaContract

//Criação das tabelas, e métodos de CRUD de tarefas no banco
class TarefaDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Tarefas.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TarefaContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(TarefaContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertTarefa(tarefa: Tarefa) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TarefaContract.TarefaEntry.COLUMN_DESCRICAO, tarefa.descricao)
            put(TarefaContract.TarefaEntry.COLUMN_DATA, tarefa.data?.toString()) // Converta a data para String
        }
        val newRowId = db?.insert(TarefaContract.TarefaEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun deleteTarefa(taskId: Int) {
        val db = writableDatabase
        val selection = "${TarefaContract.TarefaEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(taskId.toString())
        db?.delete(TarefaContract.TarefaEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun updateTarefa(tarefa: Tarefa) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TarefaContract.TarefaEntry.COLUMN_DESCRICAO, tarefa.descricao)
            put(TarefaContract.TarefaEntry.COLUMN_DATA, tarefa.data?.toString()) // Converta a data para String
        }
        val selection = "${TarefaContract.TarefaEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(tarefa.id.toString())
        db?.update(
            TarefaContract.TarefaEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        db.close()
    }

    fun getTarefas(): List<Tarefa> {
        val db = readableDatabase
        val projection = arrayOf(
            TarefaContract.TarefaEntry.COLUMN_ID,
            TarefaContract.TarefaEntry.COLUMN_DESCRICAO,
            TarefaContract.TarefaEntry.COLUMN_DATA
        )
        val cursor = db?.query(
            TarefaContract.TarefaEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val tarefas = mutableListOf<Tarefa>()

        with(cursor) {
            while (this?.moveToNext() == true) {
                val id = getInt(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_ID))
                val descricao = getString(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_DESCRICAO))
                val data = getString(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_DATA))
                tarefas.add(Tarefa(id, descricao, data))
            }
        }
        cursor?.close()
        db?.close()
        return tarefas
    }

    fun procurarTarefas(palavraChave: String): List<Tarefa> {
        val db = readableDatabase
        val projection = arrayOf(
            TarefaContract.TarefaEntry.COLUMN_ID,
            TarefaContract.TarefaEntry.COLUMN_DESCRICAO,
            TarefaContract.TarefaEntry.COLUMN_DATA
        )
        val selection = "${TarefaContract.TarefaEntry.COLUMN_DESCRICAO} LIKE ?"
        val selectionArgs = arrayOf("%$palavraChave%")
        val cursor = db?.query(
            TarefaContract.TarefaEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val tarefas = mutableListOf<Tarefa>()

        with(cursor) {
            while (this?.moveToNext() == true) {
                val id = getInt(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_ID))
                val descricao = getString(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_DESCRICAO))
                val data = getString(getColumnIndexOrThrow(TarefaContract.TarefaEntry.COLUMN_DATA))
                tarefas.add(Tarefa(id, descricao, data))
            }
        }
        cursor?.close()
        db?.close()
        return tarefas
    }
}
