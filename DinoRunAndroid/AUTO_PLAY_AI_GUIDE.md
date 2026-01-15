# Auto-Play AI Mode - How It Works

## 🤖 Overview

Your Dino Game now features **autonomous AI gameplay**! The Dino will play itself perfectly until TikTok viewers send gifts to interrupt the AI.

---

## ✨ Features Implemented

### 1. **Auto-Jump AI** ✅
- **Smart Detection**: AI calculates distance to approaching cactus
- **Base Detection Range**: 250 pixels (configurable in `Constants.java`)
- **Dynamic Range**: 1.5x farther during speed boost
- **Perfect Timing**: Jumps automatically when cactus enters range

### 2. **Gift Override System** 🎁
- **1-second pause**: Normal gifts disable AI for 60 frames (~1 second)
- **2-second pause**: Speed boost disables AI for 120 frames (~2 seconds)
- ** Auto-resume**: AI automatically takes over when override expires
- **Viewer Impact**: Gifts feel powerful because they break the AI streak

### 3. **Configuration** ⚙️

**Enable/Disable Auto-Play**  
Edit `Constants.java`:
```java
public static final boolean AUTO_PLAY_ENABLED = true;  // Set to false to disable AI
```

**Adjust Detection Range**  
Edit `Constants.java`:
```java
public static final int AUTO_JUMP_DETECTION_RANGE = 250;  // Pixels
```

Increase for earlier jumps, decrease for riskier gameplay.

---

## 🎮 How It Works

### Game Loop Integration

In `GameView.update()`:

```java
// ========== AUTO-PLAY AI MODE ==========
if (autoPlay && !giftOverride && !isGameOver) {
    // Smart detection range based on game speed
    int detectionRange = Constants.AUTO_JUMP_DETECTION_RANGE;
    if (speedBoostTimer > 0) {
        detectionRange = (int) (detectionRange * 1.5);  
    }
    
    // Check if any cactus is in range
    for (Cactus cactus : cacti) {
        int dinoX = dino.getX();
        int cactusX = cactus.getX();
        
        if (cactusX > dinoX && cactusX < dinoX + detectionRange && !dino.isJumping()) {
            dino.jump();
            break;
        }
    }
}
```

### Gift Override Logic

When TikTok viewer sends: a gift:

```java
externalJumpTrigger() {
    // Temporarily disable AI
    giftOverride = true;
    giftOverrideCounter = 60;  // 1 second
    
    dino.jump();  // Execute gift action
}
```

Every frame:
```java
if (giftOverride) {
    giftOverrideCounter--;
    if (giftOverrideCounter <= 0) {
        giftOverride = false;  // AI resumes
    }
}
```

---

## 🎁 TikTok Live Integration

### Viewer Experience

**AI Mode Active:**
- Game runs autonomously
- Dino never dies
- Score climbs forever

**Viewer Sends Gift:**
- AI pauses for 1-2 seconds
- Viewer's action matters
- Creates "viewers vs AI" dynamic
- Builds excitement and engagement

### Gift Mappings

From `websocket-server.js`:

```javascript
tiktokLive.on('gift', data => {
    switch(data.giftName) {
        case 'Rose':
            triggerJump();      // 1-second AI pause
            break;
        case 'TikTok':
            triggerSpeedBoost(); // 2-second AI pause + speed increase
            break;
    }
});
```

---

## 🧪 Testing

### Method 1: HTTP API

Test AI behavior from browser:

```bash
# AI will resume after 1 second
curl http://localhost:3000/jump

# AI will resume after 2 seconds + speed boost active
curl http://localhost:3000/speedboost
```

### Method 2: Build & Run

1. Build Android app in Android Studio
2. Run on device/emulator
3. Watch Dino jump automatically
4. Send WebSocket command
5. Observe AI pause and resume

---

## 📊 Configuration Examples

### Make AI More Aggressive (Earlier Jumps)

```java
public static final int AUTO_JUMP_DETECTION_RANGE = 350;
```

### Make AI More Risky (Later Jumps)

```java
public static final int AUTO_JUMP_DETECTION_RANGE = 180;
```

### Disable AI Completely

```java
public static final boolean AUTO_PLAY_ENABLED = false;
```

### Longer Gift Override

In `GameView.java`:
```java
private static final int GIFT_OVERRIDE_DURATION = 120;  // 2 seconds at 60fps
```

---

## 🎯 Live Stream Strategy

**Recommended Flow:**

1. **Start stream** with AI enabled
2. **Show AI perfection** - let it run for 30-60 seconds
3. **Announce**: "Send gifts to interrupt the AI!"
4. **Engage viewers**: AI vs Viewers battle
5. **Reward participation**: Thank gifters by name

**Chat Examples:**
- "The AI is unstoppable! Can you break its streak?"
- "Rose = 1 jump | TikTok = SPEED BOOST!"
- "Every gift makes the Dino do tricks!"

---

## 🐛 Troubleshooting

**AI not jumping?**
- Check `AUTO_PLAY_ENABLED = true` in `Constants.java`
- Verify `dino.getX()` and `cactus.getX()` methods exist
- Look for "AI auto-jump!" in Android Studio Logcat

**Gift override not working?**
- Confirm WebSocket connection is active
- Check `giftOverride` flag in debugger
- Verify gift commands are being received

**Jumps too early/late?**
- Adjust `AUTO_JUMP_DETECTION_RANGE` in `Constants.java`
- Test with different values (150-400)

---

## 📝 Code Changes Summary

**Files Modified:**

1. **[Constants.java](file:///home/peter/Desktop/Dino-Game-Clone/DinoRunAndroid/app/src/main/java/com/dinorun/utils/Constants.java)**
   - Added `AUTO_PLAY_ENABLED` flag
   - Added `AUTO_JUMP_DETECTION_RANGE` constant

2. **[Dino.java](file:///home/peter/Desktop/Dino-Game-Clone/DinoRunAndroid/app/src/main/java/com/dinorun/models/Dino.java)**
   - Added `getX()` method for position tracking
   - Added `isJumping()` method for AI decision-making

3. **[GameView.java](file:///home/peter/Desktop/Dino-Game-Clone/DinoRunAndroid/app/src/main/java/com/dinorun/GameView.java)**
   - Added `autoPlay` flag
   - Added `giftOverride` and `giftOverrideCounter` variables
   - Implemented AI jump logic in `update()`
   - Added gift override countdown
   - Modified external triggers to set gift override

---

**Your Dino Game is now a self-playing AI that viewers can control!** 🦖🤖🎮

Perfect for TikTok Live streams where engagement drives the entertainment! 🎁✨
