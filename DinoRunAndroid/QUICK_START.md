# Dino Run Android - Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Update Server IP

Edit the WebSocket server URL in `GameView.java` (line 32):

```java
private static final String WEBSOCKET_SERVER = "ws://YOUR_IP_ADDRESS:8080";
```

To find your IP address:
- **Windows**: Run `ipconfig` in Command Prompt
- **Mac/Linux**: Run `ifconfig` in Terminal
- Look for your local IP (e.g., `192.168.1.100`)

### Step 2: Start the Node.js Server

```bash
cd DinoRunAndroid/server
npm install
npm start
```

You should see:
```
WebSocket server started on port 8080
📡 HTTP API server started on port 3000
```

### Step 3: Build & Run Android App

1. Open `DinoRunAndroid` in Android Studio
2. Connect your Android device via USB (enable USB debugging)
3. Click the green Run button ▶️
4. The game should start in full screen

### ✅ Verify Connection

- Server console should print: `✅ New client connected`
- Test commands: Visit `http://YOUR_IP:3000/jump` in browser
- Dino should jump!

---

## 🎮 Control Methods

### 1. Touch Screen
- Tap anywhere to make dino jump

### 2. WebSocket Commands (TikTok Live)
- Server sends JSON: `{"event": "jump"}` → Dino jumps
- Server sends JSON: `{"event": "speed-up"}` → Speed boost
- Server sends JSON: `{"event": "restart"}` → Restart game

### 3. HTTP API (Testing)
```bash
curl http://YOUR_IP:3000/jump
curl http://YOUR_IP:3000/speedboost
curl http://YOUR_IP:3000/restart
```

---

## 🎥 For TikTok Live Streaming

1. **Connect** phone and server to same WiFi
2. **Open** Dino Game app on phone
3. **Verify** WebSocket connection in server logs
4. **Start screen recording** on phone
5. **Go live** on TikTok
6. **Share screen** with viewers
7. **Viewers send gifts** → Your Node.js backend triggers game actions!

See `TIKTOK_LIVE_GUIDE.md` for full TikTok integration details.

---

## 🎁 Gift Mapping Example

Edit `websocket-server.js` to map TikTok gifts to actions:

```javascript
tiktokLive.on('gift', data => {
    switch(data.giftName) {
        case 'Rose':        triggerJump(); break;
        case 'TikTok':      triggerSpeedBoost(); break;
        case 'Heart':       triggerJump(); break;
    }
});
```

---

## 📁 Project Structure

```
DinoRunAndroid/
├── app/src/main/java/com/dinorun/
│   ├── MainActivity.java       # Full screen setup
│   ├── GameView.java           # Game loop + WebSocket
│   ├── models/                 # Game objects (Dino, Cactus, etc.)
│   └── utils/                  # WebSocket client, collision detection
├── server/
│   ├── websocket-server.js     # Node.js WebSocket server
│   └── package.json
└── TIKTOK_LIVE_GUIDE.md        # Detailed integration guide
```

---

## 🛠️ Troubleshooting

**Can't connect to server?**
- Ensure phone and computer are on same WiFi
- Check firewall isn't blocking port 8080
- Verify IP address is correct in GameView.java

**Connection keeps dropping?**
- App auto-reconnects every 3 seconds
- Keep phone screen on during streaming
- Check WiFi signal strength

**Need help?**
See `TIKTOK_LIVE_GUIDE.md` for detailed troubleshooting.

---

**You're ready to go!** 🦖✨
