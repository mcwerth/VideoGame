package com.example.vaultofluck.ui.screens.run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.ui.viewmodel.RunUiState
import com.example.vaultofluck.ui.viewmodel.RunViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RunRoute(viewModel: RunViewModel) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.state.collectLatest { state ->
            state.message?.let {
                snackbar.showSnackbar(it)
                viewModel.consumeMessage()
            }
        }
    }
    val uiState by viewModel.state.collectAsState()
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        RunScreen(
            state = uiState,
            onStart = viewModel::start,
            onFinish = viewModel::finish,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun RunScreen(
    state: RunUiState,
    modifier: Modifier = Modifier,
    onStart: (Int) -> Unit,
    onFinish: (Boolean, Int) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Roguelite Runs", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val run = state.activeRun
                if (run == null) {
                    Text(text = "No active run")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onStart(1) }) { Text("Start depth 1") }
                        Button(onClick = { onStart(5) }) { Text("Start depth 5") }
                    }
                } else {
                    Text(text = "Depth ${run.depth}")
                    Text(text = "Status ${run.status}")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onFinish(true, run.depth + 1) }) { Text("Push deeper") }
                        Button(onClick = { onFinish(false, run.depth) }) { Text("Retreat") }
                    }
                }
            }
        }
    }
}
