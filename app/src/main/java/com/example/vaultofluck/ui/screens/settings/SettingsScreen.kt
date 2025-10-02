package com.example.vaultofluck.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.ui.viewmodel.SettingsUiState
import com.example.vaultofluck.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsRoute(viewModel: SettingsViewModel) {
    val uiState by viewModel.state.collectAsState()
    Scaffold { padding ->
        SettingsScreen(
            state = uiState,
            onToggleAnimations = viewModel::toggleAnimations,
            onToggleLargeText = viewModel::toggleLargeText,
            onVolumeChanged = viewModel::setVolume,
            onSeedChanged = viewModel::setDebugSeed,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    modifier: Modifier = Modifier,
    onToggleAnimations: (Boolean) -> Unit,
    onToggleLargeText: (Boolean) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onSeedChanged: (Int?) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Animations")
                Switch(
                    checked = state.preferences.animationsEnabled,
                    onCheckedChange = onToggleAnimations,
                    colors = SwitchDefaults.colors()
                )
                Text("Large text")
                Switch(
                    checked = state.preferences.largeText,
                    onCheckedChange = onToggleLargeText,
                    colors = SwitchDefaults.colors()
                )
                Text("SFX Volume ${(state.preferences.sfxVolume * 100).toInt()}%")
                Slider(value = state.preferences.sfxVolume, onValueChange = onVolumeChanged)
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Debug Seed")
                var seedText by remember(state.preferences.debugSeed) { mutableStateOf(TextFieldValue(state.preferences.debugSeed?.toString() ?: "")) }
                OutlinedTextField(
                    value = seedText,
                    onValueChange = {
                        seedText = it
                        onSeedChanged(it.text.toIntOrNull())
                    },
                    label = { Text("Seed") }
                )
                Button(onClick = { onSeedChanged(null); seedText = TextFieldValue("") }) { Text("Clear Seed") }
            }
        }
    }
}
