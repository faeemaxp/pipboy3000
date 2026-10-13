# PIP-BOY RADIO 3000 (Android)

RobCo Industries Termlink Protocol retro-futuristic radio receiver and audio frequency visualizer built natively for Android using Kotlin and Jetpack Compose.

## Features

- **RobCo Pip-Boy 3000 Terminal Aesthetic**:
  - CRT phosphor green color palette with amber and emergency red accents
  - Monospace retro typography and terminal framing
  - Dynamic 24-hour terminal clock and broadcast status indicators
- **Live Radio Streaming Engine**:
  - Full support for HLS (`.m3u8`) and direct MP3 audio streams powered by AndroidX Media3 / ExoPlayer
  - Background playback service (`RadioPlaybackService`) with media notifications and lock screen controls
  - 275+ cataloged radio stations across Akashvani (AIR), Vividh Bharati, FM Rainbow, SomaFM, and Nightwave Plaza
  - Live frequency search and category filtering (All, Wasteland, Vividh Bharati, FM, Akashvani)
- **Real-Time Visualizer**:
  - Dynamic Jetpack Compose Canvas waveform and frequency spectrum visualizer
  - Phosphor green animated spectrum bars with glow caps and oscilloscope sine standby
- **RobCo Vault-Tec Module**:
  - Rotating Vault-Tec survival tips and lore ticks in the terminal footer
  - RobCo personality system responding to station genres (Jazz, Classical, News, Wasteland)
  - CRT Overdrive mode with red emergency warning and screen shake
  - Vault-Tec compliance check alerts

## Building the Project

Run with Gradle:

```bash
gradle assembleDebug
```
