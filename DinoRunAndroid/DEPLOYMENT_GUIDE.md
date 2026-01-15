# Android Dino Game - Deployment Checklist

## ✅ PROJECT VERIFICATION COMPLETE

### 1️⃣ All Required Files Verified ✅

**Core Files:**
- ✅ `MainActivity.java` - Entry point with full screen mode
- ✅ `GameView.java` - Game loop, WebSocket integration, AI auto-play

**Model Classes:**
- ✅ `Dino.java` - Player character with jump physics
- ✅ `Cactus.java` - Obstacles with collision detection
- ✅ `Cloud.java` - Background parallax clouds
- ✅ `Earth.java` - Ground rendering

**Utility Classes:**
- ✅ `ResourceManager.java` - Asset loading singleton
- ✅ `Constants.java` - Game configuration
- ✅ `CollisionDetector.java` - Collision detection
- ✅ `WebSocketClient.java` - Auto-reconnecting WebSocket client

---

## 2️⃣ WebSocket URL Updated ✅

**File:** `GameView.java`  
**Line:** 32

**CHANGED FROM:**
```java
private static final String WEBSOCKET_SERVER = "ws://192.168.1.100:8080";
```

**CHANGED TO:**
```java
private static final String WEBSOCKET_SERVER = "ws://10.238.40.77:8080";
```

✅ **Your computer's local IP:** `10.238.40.77`  
✅ **WebSocket server running on:** Port 8080  
✅ **HTTP API running on:** Port 3000

---

## 3️⃣ AndroidManifest.xml Verified ✅

**File:** `AndroidManifest.xml`  
**Line:** 6

✅ **Internet Permission:** `<uses-permission android:name="android.permission.INTERNET" />`  
✅ **Screen Orientation:** Locked to `landscape` (Line 18)  
✅ **Full Screen Theme:** `NoActionBar` (Line 14)  
✅ **Exported Activity:** Set to `true` (Line 20)

---

## 4️⃣ Build & Install Instructions

### Option A: Android Studio (Recommended)

1. **Open Project:**
   ```bash
   # Open Android Studio
   File → Open → Select DinoRunAndroid folder
   ```

2. **Sync Gradle:**
   - Wait for "Sync Now" prompt
   - Click "Sync Now"

3. **Connect Phone:**
   - Enable **USB Debugging** on phone:
     - Settings → About Phone → Tap "Build Number" 7 times
     - Settings → Developer Options → Enable "USB Debugging"
   - Connect phone via USB cable
   - Accept USB debugging prompt on phone

4. **Build & Run:**
   - Click green "Run" button (▶️)
   - Or press `Shift + F10`
   - Select your device from list
   - Wait for build (~1-2 minutes first time)

5. **App Installs Automatically**

### Option B: Command Line APK Build

```bash
cd /home/peter/Desktop/Dino-Game-Clone/DinoRunAndroid

# Build debug APK
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk

# Install to connected phone
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 5️⃣ Testing WebSocket Connection

### Before Running App:

1. **Ensure WebSocket server is running:**
   ```bash
   # You already have this running in terminal
   cd DinoRunAndroid/server
   npm start
   ```

2. **Verify phone is on same WiFi:**
   - Phone WiFi → Same network as computer
   - Computer IP: `10.238.40.77`

3. **Test HTTP API (optional):**
   ```bash
   curl http://10.238.40.77:3000/status
   ```

### After App Launches:

1. **Check server terminal** for connection message:
   ```
   ✅ New client connected: [your phone's IP]
   ```

2. **Test commands:**
   ```bash
   curl http://10.238.40.77:3000/jump       # Dino should jump
   curl http://10.238.40.77:3000/speedboost # Speed boost activates
   ```

3. **Watch AI play automatically** (if AUTO_PLAY_ENABLED = true)

---

## 6️⃣ Summary of Changes

### Files Modified: **1 file**

| File | Line | Change |
|------|------|--------|
| `GameView.java` | 32 | Updated WebSocket URL to `ws://10.238.40.77:8080` |

### Files Verified (No Changes Needed): **12 files**

- ✅ MainActivity.java
- ✅ AndroidManifest.xml (already has INTERNET permission)
- ✅ All model classes (Dino, Cactus, Cloud, Earth)
- ✅ All utility classes (ResourceManager, Constants, CollisionDetector, WebSocketClient)

---

## 🚨 Troubleshooting

### App Won't Install
- Enable "Unknown Sources" in phone settings
- Or Use Android Studio's Run button

### WebSocket Won't Connect
- Verify phone WiFi = same network as computer
- Check firewall isn't blocking port 8080:
  ```bash
  sudo ufw allow 8080/tcp
  ```
- Ping computer from phone browser:
  ```
  http://10.238.40.77:3000/status
  ```

### App Crashes on Start
- Check Android Studio Logcat for errors
- Verify all drawable assets are in `res/drawable/`
- Ensure R.java is generated (clean + rebuild)

---

## ✅ Ready to Deploy!

**Quick Start:**
1. Open Android Studio
2. Open `DinoRunAndroid` project
3. Connect phone via USB
4. Click Run ▶️
5. App installs and launches automatically
6. Watch Dino play itself!
7. Test with: `curl http://10.238.40.77:3000/jump`

**Your setup is complete!** 🦖🎮📱
