@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.lucas.listadetarefas.ui.views

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import br.com.lucas.listadetarefas.database.TarefaDBHelper
import br.com.lucas.listadetarefas.model.Tarefa
import java.util.Calendar
import java.util.Date

@Composable
fun TelaPrincipal() {

    val dbHelper = TarefaDBHelper(LocalContext.current)
    val tarefas = remember { mutableStateListOf<Tarefa>() }
    val novaTarefa = remember { mutableStateOf(TextFieldValue()) }
    val palavraChave = remember { mutableStateOf(TextFieldValue()) }
    val tarefaEditando = remember { mutableStateOf<Tarefa?>(null) }
    var mostrarOrdenacoes by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    //Criação da Data
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()


    val datePickerDialog = DatePickerDialog(
        contexto,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            data = String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth)
        }, year, month, day
    )

    LaunchedEffect(Unit) {
        tarefas.addAll(dbHelper.getTarefas())
    }

    val onDeleteTarefa: (Int) -> Unit = { taskId ->
        dbHelper.deleteTarefa(taskId)
        tarefas.removeAll { it.id == taskId }
    }

    // Box Principal
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Blue
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //Barra de pesquisa e botão de pesquisa e filtro de ordenação
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    //drop down aqui
                    IconButton(
                        onClick = { mostrarOrdenacoes = !mostrarOrdenacoes }
                    ) {
                        Icon(Icons.Default.List, "Menu de ordenações", Modifier.fillMaxSize())
                    }

                    DropdownMenu(
                        expanded = mostrarOrdenacoes,
                        onDismissRequest = { mostrarOrdenacoes = false }
                    ) {
                        DropdownMenuItem(text= { Text(text = "Ordenar por inserção")},
                            onClick = {
                                tarefas.sortBy { it.id }
                                Toast.makeText(contexto, "Ordenando por inserção...", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(text= { Text(text = "Ordenar por data de validade")},
                            onClick = {
                                tarefas.sortBy { it.data }
                                Toast.makeText(contexto, "Ordenando por validade...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    //TF da barra de pesquisa
                    TextField(
                        value = palavraChave.value,
                        onValueChange = { palavraChave.value = it },
                        placeholder = { Text("Filtrar por palavras-chave...") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

                //BT de pesuisa
                Button(
                    onClick = {
                        val palavraChaveTexto = palavraChave.value.text
                        if (palavraChaveTexto.isNotBlank()) {
                            val tarefasFiltradas = dbHelper.procurarTarefas(palavraChaveTexto)
                            tarefas.clear()
                            tarefas.addAll(tarefasFiltradas)
                        } else {
                            tarefas.clear()
                            tarefas.addAll(dbHelper.getTarefas())
                        }
                        novaTarefa.value = TextFieldValue()
                    },
                    modifier = Modifier
                        .size(60.dp)
                ){
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Filtrar",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            //Lista de tarefas
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(items = tarefas) { tarefa ->
                    ItensDeTarefa(tarefa, onDeleteTarefa) {
                        tarefaEditando.value = it
                        novaTarefa.value =
                            TextFieldValue(it.descricao)
                    }
                }
            }

            //Campo de texto e botão para atualizar/inserir tarefas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Caso de adição de nova tarefa
                if (tarefaEditando.value == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextField(
                            value = novaTarefa.value,
                            onValueChange = { novaTarefa.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("Digite aqui sua nova tarefa...") },
                        )
                    }

                    IconButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Data",
                            tint = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp)) // Espaçamento entre o TextField e o Button

                    Button(
                        onClick = {
                            val descricao = novaTarefa.value.text

                            if (descricao.isNotBlank() && data.isNotBlank()) {
                                dbHelper.insertTarefa(Tarefa(null, descricao, data))
                                tarefas.clear()
                                tarefas.addAll(dbHelper.getTarefas())
                                novaTarefa.value = TextFieldValue()
                                data = ""
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Adicionar",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                } else {
                    //Caso de edição (A data não pode ser modificada)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextField(
                            value = novaTarefa.value,
                            onValueChange = { novaTarefa.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        )
                    }

                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        val descricao = novaTarefa.value.text
                        if (descricao.isNotBlank()) {
                            val tarefaAtualizada =
                                tarefaEditando.value!!.copy(descricao = descricao)
                            dbHelper.updateTarefa(tarefaAtualizada)
                            tarefas.clear()
                            tarefas.addAll(dbHelper.getTarefas())
                            novaTarefa.value = TextFieldValue()
                            tarefaEditando.value = null
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .width(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Salvar edição",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    }
                }
            }
        }
    }
}
