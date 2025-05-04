# 🎧 EarAid - Real-time Sound Loopback App

**EarAid** is an Android application designed to assist **hearing-impaired individuals** by streaming live environmental audio directly to their Bluetooth or wired headphones. With features like volume control, noise cancellation, and clean UI, this app enables seamless audio support for those who need it most.

---

## 🌟 Mission: Empowering Differently-Abled Individuals

This project is built with purpose — to **support hiring and empowering differently-abled people**, particularly those with hearing challenges. Our goal is to create inclusive, accessible tools that allow individuals to work, communicate, and engage with the world more confidently.

---

## 🎯 Features

- ✅ **Real-time Microphone-to-Headphone Loopback**
- 🎧 **Auto-detection** of output device: Bluetooth, Wired, or Phone Speaker
- 🔇 **Noise Suppression**: Cancel fan or ambient sounds with a single toggle
- 🔊 **Volume Control Slider** with logarithmic scaling
- 📢 **Foreground Notification Control** with Stop button
- 📲 **Works with both Bluetooth & Wired Headphones**
- 🔐 **Microphone & Notification Permissions** handled gracefully

---

## 📸 Screenshots

<!-- Insert screenshots here if available -->

---

## 🛠️ Tech Stack

- **Kotlin** & **Jetpack Compose** for UI
- **Android Foreground Services** for continuous audio loopback
- **AudioRecord / AudioTrack API** for low-latency audio routing
- **NoiseSuppressor**, **AGC**, **EchoCanceler** for enhanced audio clarity
- Compatible with **Android 8+**

---

## 🧑‍💻 Usage

1. Grant **Microphone** and **Notification** permissions on first launch.
2. Connect your **Bluetooth** or **Wired Headphones**.
3. Tap `Start Loopback` to begin hearing the environment in real-time.
4. Adjust volume or toggle **Noise Cancellation** as needed.
5. Stop the service using the **foreground notification** or in-app button.

---

## 📂 Project Structure

- `SoundLoopbackScreen.kt` - Main Composable with UI and controls
- `SoundLoopbackService.kt` - Background audio routing and notification logic

---

## 🛡️ Privacy

This app does **not collect, store, or transmit** any audio data. All processing happens on-device. User privacy and dignity are central to this project's values.

---

## 🤝 Contributing

We welcome developers who share the passion for inclusive tech. Contributions, suggestions, and collaborations are always appreciated.

---

## ❤️ Acknowledgments

This project is part of a broader initiative to **hire and support differently-abled individuals** through tech solutions that bridge communication gaps and empower independence.

---

## 📄 License

MIT License. Feel free to use, modify, and build upon this work with attribution.

