import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SoundLoopbackService : Service() {
    private var isLooping = false
    private lateinit var loopbackThread: Thread
    private var audioManager: AudioManager? = null

    companion object {
        var volumeLevel = 5f
        fun setVolume(vol: Float) {
            volumeLevel = vol
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification(true))
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_LOOPBACK") {
            stopSelf()
            return START_NOT_STICKY
        }

        if (isLooping) return START_STICKY
        isLooping = true

        loopbackThread = Thread {
            val sampleRate = 44100
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val audioTrack = AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            try {
                audioRecord.startRecording()
                audioTrack.play()

                val buffer = ByteArray(bufferSize)
                while (isLooping && !Thread.interrupted()) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        val vol = (1 - Math.log((15 - volumeLevel + 1).toDouble()) / Math.log(15.0)).toFloat()
                        audioTrack.setVolume(vol.coerceIn(0f, 1f))
                        audioTrack.write(buffer, 0, read)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                audioRecord.release()
                audioTrack.release()
            }
        }
        loopbackThread.start()

        return START_STICKY
    }

    override fun onDestroy() {
        isLooping = false
        loopbackThread.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(isRunning: Boolean): Notification {
        val stopIntent = Intent(this, SoundLoopbackService::class.java).apply {
            action = "STOP_LOOPBACK"
        }
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, "loopback_channel")
            .setContentTitle("EarAid Running")
            .setContentText("Tap to stop loopback audio")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(isRunning)
            .addAction(android.R.drawable.ic_media_pause, "Stop", pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "loopback_channel",
                "Loopback Audio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for audio loopback service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}