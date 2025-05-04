package com.rushikesh.earaid

import SoundLoopbackService
import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun SoundLoopbackScreen() {
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }
    var outputDevice by remember { mutableStateOf("Detecting...") }
    var volume by remember { mutableStateOf(5f) }

    val micPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission is required", Toast.LENGTH_LONG).show()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Notification permission is required to show status", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun detectOutputDevice(): String {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val devices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        } else return "Unknown"

        return when {
            devices.any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO } -> "Bluetooth Headphones"
            devices.any { it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET } -> "Wired Headphones"
            else -> "Phone Speaker"
        }
    }

    LaunchedEffect(isRunning) {
        outputDevice = detectOutputDevice()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EarAid - Live Audio Loopback", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Output: $outputDevice", color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!isRunning) {
                    ContextCompat.startForegroundService(context, Intent(context, SoundLoopbackService::class.java))
                } else {
                    context.stopService(Intent(context, SoundLoopbackService::class.java))
                }
                isRunning = !isRunning
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color.Red else Color.Green
            ),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(if (isRunning) "Stop Loopback" else "Start Loopback")
        }

        Spacer(modifier = Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = "Volume")
            Slider(
                value = volume,
                onValueChange = {
                    volume = it
                    SoundLoopbackService.setVolume(it)
                },
                valueRange = 0f..20f,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
