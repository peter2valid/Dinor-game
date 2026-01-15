# Dino Run - TikTok Live Integration Guide

## 🎯 Overview

This Android Dino Game responds to **TikTok Live gift events** via WebSocket communication with a Node.js backend server. Perfect for interactive live streaming!

---

## 🏗️ Architecture

```
┌─────────────────┐         WebSocket         ┌──────────────────┐
│  Android App    │ ◄─────────────────────── │  Node.js Server  │
│  (Dino Game)    │         Commands          │   (Port 8080)    │
└─────────────────┘                            └──────────────────┘
        ▲                                              ▲
        │                                              │
        │ Touch Input                         TikTok Live API
        │                                      (Gift Events)
```

---

## 📱 Android App Setup

### 1. Update WebSocket Server URL

Edit `GameView.java` line 32:

```java
private static final String WEBSOCKET_SERVER = "ws://YOUR_SERVER_IP:8080";
```

**Options:**
- Local testing: `ws://10.0.2.2:8080` (Android emulator)  
- Same WiFi: `ws://192.168.1.100:8080` (your computer's local IP)
- Public server: `ws://your-server.com:8080`

### 2. Build and Install

```bash
# In Android Studio:
1. Open DinoRunAndroid project
2. Sync Gradle files
3. Connect Android device via USB
4. Click Run ▶️
```

### 3. Find Your Server IP

**Windows:**
```cmd
ipconfig
```
Look for `IPv4 Address` under your WiFi adapter

**macOS/Linux:**
```bash
ifconfig | grep "inet "
```

---

## 🖥️ Node.js Server Setup

### 1. Install Dependencies

```bash
cd DinoRunAndroid/server
npm install
```

### 2. Start Server

```bash
npm start
```

You should see:
```
WebSocket server started on port 8080
Waiting for Android app to connect...

📡 HTTP API server started on port 3000
```

### 3. Test Connection

Open the Android app. Server should print:
```
✅ New client connected: 192.168.1.XXX
```

---

## 🎮 Supported Commands

The Android game responds to these WebSocket messages:

| Event | JSON Command | Action |
|-------|-------------|--------|
| **Jump** | `{"event": "jump"}` | Dino jumps over obstacles |
| **Restart** | `{"event": "restart"}` | Restart game after game over |
| **Speed Boost** | `{"event": "speed-up"}` | 2x speed for ~6 seconds |
| **Duck** | `{"event": "duck"}` | Dino ducks (placeholder) |

---

## 🧪 Testing Commands

### Method 1: HTTP API (Easiest)

Open browser and visit:
- http://localhost:3000/jump
- http://localhost:3000/speedboost
- http://localhost:3000/restart
- http://localhost:3000/status

### Method 2: curl Commands

```bash
curl http://localhost:3000/jump
curl http://localhost:3000/speedboost
curl http://localhost:3000/restart
```

### Method 3: WebSocket Client (Chrome Extension)

1. Install "Simple WebSocket Client" extension
2. Connect to: `ws://localhost:8080`
3. Send: `{"event": "jump"}`

---

## 🎁 TikTok Live Integration

### Step 1: Install TikTok Live Connector

```bash
npm install tiktok-live-connector
```

### Step 2: Update `websocket-server.js`

Uncomment the TikTok integration section (lines 78-109) and add:

```javascript
const { WebcastPushConnection } = require('tiktok-live-connector');
const tiktokUsername = 'YOUR_TIKTOK_USERNAME';

const tiktokLive = new WebcastPushConnection(tiktokUsername);

tiktokLive.connect().then(state => {
    console.log(`🎥 Connected to TikTok Live!`);
}).catch(err => {
    console.error('Failed to connect:', err);
});

// Map gifts to game commands
tiktokLive.on('gift', data => {
    console.log(`🎁 ${data.uniqueId} sent ${data.giftName}`);
    
    switch(data.giftName) {
        case 'Rose':
            triggerJump();
            break;
        case 'TikTok':
            triggerSpeedBoost();
            break;
        case 'Heart':
        case 'Finger Heart':
            triggerJump();
            break;
        default:
            triggerJump();
    }
});
```

### Step 3: Test TikTok Live

1. Start the Node.js server
2. Go live on TikTok from another device
3. Server will connect to your live stream
4. Send gifts from viewers → Dino responds!

---

## 🔧 Troubleshooting

### Android App Won't Connect

**Check WiFi:**  
- Phone and server must be on the same network
- Find your computer's IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)

**Update Server URL:**
```java
// GameView.java line 32
private static final String WEBSOCKET_SERVER = "ws://192.168.1.XXX:8080";
```

**Check Firewall:**
```bash
# Allow port 8080 on Windows Firewall
# Or temporarily disable firewall for testing
```

### "Connection Refused"

- Ensure Node.js server is running
- Check if port 8080 is already in use
- Try changing to different port (update both server and Android app)

### WebSocket Disconnects

- The app auto-reconnects every 3 seconds
- Check server logs for disconnection reason
- Verify phone doesn't go to sleep (keep screen on)

---

## 📊 Performance Tips

### Maintain 60 FPS

- Keep screen on during streaming
- Close background apps
- Use high-performance mode on phone

### Optimize Network

- Use 5GHz WiFi when possible
- Keep phone close to router
- Use cable connection for Node.js server

---

##🎥 TikTok Live Streaming Setup

### Recommended Flow:

1. **Start Node.js server** on your computer
2. **Connect Android phone** to WiFi
3. **Launch Android app** (verify WebSocket connection in server logs)
4. **Start screen recording** on phone or use screen mirroring
5. **Go live on TikTok** and share your screen
6. **Viewers send gifts** → Dino responds automatically!

### Screen Sharing Options:

- **Built-in (Samsung, Xiaomi):** Screen Recording feature
- **Apps:** AZ Screen Recorder, Mobizen
- **Desktop mirroring:** scrcpy (USB), Vysor, ApowerMirror

---

## 🎨 Customization

### Modify Gift Mappings

Edit `websocket-server.js`:

```javascript
tiktokLive.on('gift', data => {
    // Custom gift mappings
    if (data.giftName === 'Galaxy') {
        triggerSpeedBoost();
    } else if (data.repeatCount > 5) {
        // Combo gifts trigger special actions
        triggerSpeedBoost();
    }
});
```

### Adjust Speed Boost Duration

Edit `GameView.java`:

```java
private final int SPEED_BOOST_DURATION = 200; // frames (~3.3 seconds at 60fps)
```

### Change Game Physics

Edit `Constants.java`:

```java
public static final int CACTUS_SPEED = 15; // Make game harder
public static final int JUMP_VELOCITY = -40; // Higher jumps
```

---

## 📝 API Reference

### External Trigger Methods (GameView.java)

```java
// Callable from WebSocket or other sources
public void externalJumpTrigger()      // Make dino jump
public void externalRestartTrigger()   // Restart after game over
public void externalSpeedBoost()       // Activate speed boost
public void externalDuck()             // Duck (future feature)
```

### WebSocket Message Format

```json
{
  "event": "jump" | "restart" | "speed-up" | "duck"
}
```

---

## 🚀 Example: Live Stream Setup

**Step-by-step for TikTok Live:**

1. **Terminal 1** (Server):
   ```bash
   cd DinoRunAndroid/server
   npm start
   ```

2. **Android Phone** (Game):
   - Open Dino Run app
   - Verify "WebSocket Connected!" in server logs

3. **Test** (Browser):
   - Visit http://YOUR_IP:3000/jump
   - Confirm dino jumps

4. **Go Live** (TikTok):
   - Start screen recording
   - Begin TikTok Live broadcast
   - Share game screen with viewers

5. **Interact**:
   - Viewers send gifts
   - Dino responds automatically
   - Engage with audience!

---

## 📚 Resources

- [Android Studio](https://developer.android.com/studio)
- [TikTok Live Connector](https://github.com/zerodytrash/TikTok-Live-Connector)
- [WebSocket Protocol](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)
- [OkHttp WebSocket](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-web-socket/)

---

**Ready to go live?** 🦖🎮📱

Configure your server IP, build the APK, and start entertaining your TikTok audience with an interactive Dino game! 🎉
