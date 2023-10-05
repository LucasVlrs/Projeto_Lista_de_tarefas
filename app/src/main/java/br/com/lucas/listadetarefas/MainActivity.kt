@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.lucas.listadetarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.lucas.listadetarefas.database.TarefaDBHelper
import br.com.lucas.listadetarefas.ui.theme.ListaDeTarefasTheme
import br.com.lucas.listadetarefas.ui.views.TelaPrincipal

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: TarefaDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = TarefaDBHelper(this)
        setContent {
            ListaDeTarefasTheme {
                // Superficie container do programa
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chamada da tela principal
                    TelaPrincipal()
                }
            }
        }
    }
}

