package br.com.lucas.listadetarefas.util

import android.provider.BaseColumns

//Criação do modelo a se seguir da(s) tabela(s) do BD do projeto
object TarefaContract {
    object TarefaEntry : BaseColumns {
        const val TABLE_NAME = "tarefas"
        const val COLUMN_ID = "id"
        const val COLUMN_DESCRICAO = "descricao"
        const val COLUMN_DATA = "data"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${TarefaEntry.TABLE_NAME} (" +
                "${TarefaEntry.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${TarefaEntry.COLUMN_DESCRICAO} TEXT," +
                "${TarefaEntry.COLUMN_DATA} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TarefaEntry.TABLE_NAME}"
}
