// Sample Node.js WebSocket server for TikTok Live integration
// This demonstrates how to send commands to the Android Dino Game

const WebSocket = require('ws');

// Create WebSocket server on port 8080
const wss = new WebSocket.Server({ port: 8080 });

console.log('WebSocket server started on port 8080');
console.log('Waiting for Android app to connect...\n');

// Store connected clients
const clients = new Set();

wss.on('connection', (ws, req) => {
    const clientIp = req.socket.remoteAddress;
    console.log(`✅ New client connected: ${clientIp}`);
    clients.add(ws);

    // Send welcome message
    ws.send(JSON.stringify({ event: 'connected', message: 'Welcome to Dino Game Server!' }));

    ws.on('message', (message) => {
        console.log(`📨 Received from client: ${message}`);
    });

    ws.on('close', () => {
        console.log(`❌ Client disconnected: ${clientIp}`);
        clients.delete(ws);
    });

    ws.on('error', (error) => {
        console.error(`❗ WebSocket error: ${error.message}`);
        clients.delete(ws);
    });
});

// ==================== EXAMPLE COMMAND FUNCTIONS ====================

// Broadcast command to all connected clients
function broadcastCommand(event) {
    const message = JSON.stringify({ event });
    clients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
            console.log(`📤 Sent to client: ${message}`);
        }
    });
}

// Example: Trigger jump
function triggerJump() {
    broadcastCommand('jump');
}

// Example: Trigger restart
function triggerRestart() {
    broadcastCommand('restart');
}

// Example: Trigger speed boost
function triggerSpeedBoost() {
    broadcastCommand('speed-up');
}

// Example: Trigger duck
function triggerDuck() {
    broadcastCommand('duck');
}

// ==================== TESTING & TIKTOK INTEGRATION ====================

// Test commands (sends commands every few seconds for demonstration)
function runTestCommands() {
    console.log('\n🎮 Starting test command sequence...\n');

    setTimeout(() => {
        console.log('Testing: JUMP');
        triggerJump();
    }, 3000);

    setTimeout(() => {
        console.log('Testing: SPEED BOOST');
        triggerSpeedBoost();
    }, 6000);

    setTimeout(() => {
        console.log('Testing: JUMP');
        triggerJump();
    }, 9000);

    setTimeout(() => {
        console.log('Testing: DUCK');
        triggerDuck();
    }, 12000);
}

// Uncomment to test commands automatically
// runTestCommands();

// ==================== TIKTOK LIVE GIFT INTEGRATION EXAMPLE ====================

/*
 * For TikTok Live integration, you would use a library like:
 * - TikTok-Live-Connector (https://www.npmjs.com/package/tiktok-live-connector)
 * 
 * Example integration:
 * 
 * const { WebcastPushConnection } = require('tiktok-live-connector');
 * const tiktokLive = new WebcastPushConnection('YOUR_TIKTOK_USERNAME');
 * 
 * tiktokLive.connect().then(state => {
 *     console.log(`Connected to TikTok Live: ${state.roomId}`);
 * }).catch(err => {
 *     console.error('Failed to connect:', err);
 * });
 * 
 * // Listen for gifts
 * tiktokLive.on('gift', data => {
 *     console.log(`Gift received: ${data.giftName} from ${data.uniqueId}`);
 *     
 *     // Map gifts to game actions
 *     switch(data.giftName) {
 *         case 'Rose':
 *             triggerJump();
 *             break;
 *         case 'TikTok':
 *             triggerSpeedBoost();
 *             break;
 *         case 'Heart':
 *             triggerJump();
 *             break;
 *         default:
 *             triggerJump(); // Default action
 *     }
 * });
 * 
 * // Listen for comments (optional)
 * tiktokLive.on('chat', data => {
 *     const message = data.comment.toLowerCase();
 *     if (message.includes('jump')) triggerJump();
 *     if (message.includes('boost')) triggerSpeedBoost();
 *     if (message.includes('restart')) triggerRestart();
 * });
 */

// ==================== HTTP API FOR MANUAL TESTING ====================

const http = require('http');

const httpServer = http.createServer((req, res) => {
    res.setHeader('Content-Type', 'application/json');
    res.setHeader('Access-Control-Allow-Origin', '*');

    if (req.url === '/jump') {
        triggerJump();
        res.writeHead(200);
        res.end(JSON.stringify({ success: true, action: 'jump' }));
    } else if (req.url === '/restart') {
        triggerRestart();
        res.writeHead(200);
        res.end(JSON.stringify({ success: true, action: 'restart' }));
    } else if (req.url === '/speedboost') {
        triggerSpeedBoost();
        res.writeHead(200);
        res.end(JSON.stringify({ success: true, action: 'speed-up' }));
    } else if (req.url === '/duck') {
        triggerDuck();
        res.writeHead(200);
        res.end(JSON.stringify({ success: true, action: 'duck' }));
    } else if (req.url === '/status') {
        res.writeHead(200);
        res.end(JSON.stringify({
            connected: clients.size,
            server: 'Dino Game WebSocket Server',
            version: '1.0.0'
        }));
    } else {
        res.writeHead(404);
        res.end(JSON.stringify({ error: 'Not found' }));
    }
});

httpServer.listen(3000, () => {
    console.log('\n📡 HTTP API server started on port 3000');
    console.log('Test commands:');
    console.log('  - http://localhost:3000/jump');
    console.log('  - http://localhost:3000/speedboost');
    console.log('  - http://localhost:3000/restart');
    console.log('  - http://localhost:3000/duck');
    console.log('  - http://localhost:3000/status\n');
});
