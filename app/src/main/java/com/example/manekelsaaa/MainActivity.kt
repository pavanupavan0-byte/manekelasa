package com.example.manekelsaaa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Data Model based on "Worker Profile"
data class Worker(
    val id: Int,
    val name: String,
    val skill: String,
    val dailyRate: String,
    val phone: String,
    val isAvailable: Boolean,
    val location: String,
    val trustCount: Int = 0 // Rating/Trust logic
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManeKelsaTheme {
                ManeKelsaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManeKelsaApp() {
    val workers = remember { mutableStateListOf<Worker>() }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Sample data initialized from resources
    val worker1 = Worker(1, stringResource(R.string.worker_1_name), stringResource(R.string.worker_1_skill), stringResource(R.string.worker_1_rate), stringResource(R.string.worker_1_phone), true, stringResource(R.string.worker_1_location), 5)
    val worker2 = Worker(2, stringResource(R.string.worker_2_name), stringResource(R.string.worker_2_skill), stringResource(R.string.worker_2_rate), stringResource(R.string.worker_2_phone), true, stringResource(R.string.worker_2_location), 12)
    val worker3 = Worker(3, stringResource(R.string.worker_3_name), stringResource(R.string.worker_3_skill), stringResource(R.string.worker_3_rate), stringResource(R.string.worker_3_phone), false, stringResource(R.string.worker_3_location), 2)

    LaunchedEffect(Unit) {
        if (workers.isEmpty()) {
            workers.addAll(listOf(worker1, worker2, worker3))
        }
    }

    if (showRegisterDialog) {
        RegisterWorkerDialog(
            onDismiss = { showRegisterDialog = false },
            onConfirm = { newWorker ->
                workers.add(newWorker.copy(id = (workers.maxOfOrNull { it.id } ?: 0) + 1))
                showRegisterDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E), titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRegisterDialog = true },
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.register_worker))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            itemsIndexed(workers) { index, worker ->
                WorkDirectoryCard(
                    worker = worker,
                    onRateClick = {
                        // Update the worker in the list to trigger recomposition
                        workers[index] = worker.copy(trustCount = worker.trustCount + 1)
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterWorkerDialog(onDismiss: () -> Unit, onConfirm: (Worker) -> Unit) {
    var name by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.register_worker)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.worker_name_hint)) })
                TextField(value = skill, onValueChange = { skill = it }, label = { Text(stringResource(R.string.worker_skill_hint)) })
                TextField(value = rate, onValueChange = { rate = it }, label = { Text(stringResource(R.string.worker_rate_hint)) })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.worker_phone_hint)) })
                TextField(value = location, onValueChange = { location = it }, label = { Text(stringResource(R.string.worker_location_hint)) })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(Worker(0, name, skill, rate, phone, true, location))
            }) {
                Text(stringResource(R.string.add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

@Composable
fun WorkDirectoryCard(worker: Worker, onRateClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = worker.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Text(text = worker.skill, fontSize = 18.sp, color = Color(0xFF455A64))
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onRateClick) {
                        Icon(Icons.Default.ThumbUp, contentDescription = stringResource(R.string.trust_desc), tint = Color(0xFF1A237E))
                    }
                    Text(text = stringResource(R.string.trust_count, worker.trustCount), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(if (worker.isAvailable) Color.Green else Color.Red, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (worker.isAvailable) stringResource(R.string.available) else stringResource(R.string.not_available),
                    fontWeight = FontWeight.Bold,
                    color = if (worker.isAvailable) Color(0xFF2E7D32) else Color.Red
                )
            }

            Text(text = stringResource(R.string.rate_label, worker.dailyRate), fontSize = 18.sp, modifier = Modifier.padding(top = 4.dp))
            Text(text = stringResource(R.string.area_label, worker.location), fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${worker.phone}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.call_now), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ManeKelsaTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
