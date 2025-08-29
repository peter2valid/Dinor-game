import dotenv from 'dotenv';
import express from 'express';
import { WebSocketServer } from 'ws';
import { WebcastPushConnection } from 'tiktok-live-connector';
import path from 'path';
import { fileURLToPath } from 'url';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const server = app.listen(process.env.PORT || 3000, () => {
  console.log(`Server running on http://localhost:${server.address().port}`);
});

// Serve static files (index.html)
app.use(express.static(path.join(__dirname, '..')));

// WebSocket to browser clients
const wss = new WebSocketServer({ server });
const clients = new Set();
wss.on('connection', (ws) => {
  clients.add(ws);
  ws.on('close', () => clients.delete(ws));
});

function broadcast(type, payload) {
  const message = JSON.stringify({ type, payload, ts: Date.now() });
  for (const ws of clients) {
    if (ws.readyState === ws.OPEN) ws.send(message);
  }
}

// TikTok Live connection
const username = process.env.TIKTOK_USERNAME || '';
if (!username) {
  console.warn('Set TIKTOK_USERNAME in .env (e.g., @yourname) to connect to TikTok Live. Running without TikTok.');
}

let tiktok;
async function connectTikTok() {
  if (!username) return;
  try {
    tiktok = new WebcastPushConnection(username);

    tiktok.on('connected', (state) => {
      console.log('Connected to TikTok roomId:', state.roomId);
      broadcast('system', { status: 'connected', roomId: state.roomId });
    });

    tiktok.on('disconnected', () => {
      console.log('Disconnected from TikTok');
      broadcast('system', { status: 'disconnected' });
    });

    // Likes / Like events
    tiktok.on('like', (data) => {
      broadcast('like', { uniqueId: data.uniqueId, likeCount: data.likeCount, totalLikeCount: data.totalLikeCount });
    });

    // Comments
    tiktok.on('chat', (data) => {
      broadcast('comment', { uniqueId: data.uniqueId, comment: data.comment });
    });

    // Gifts
    tiktok.on('gift', (data) => {
      const giftName = data.giftName || '';
      broadcast('gift', {
        uniqueId: data.uniqueId,
        giftId: data.giftId,
        giftName,
        repeatEnd: data.repeatEnd,
        diamondCount: data.diamondCount,
        streakId: data.repeatEnd ? undefined : data.repeatCount,
      });
    });

    // Follows
    tiktok.on('follow', (data) => {
      broadcast('follow', { uniqueId: data.uniqueId });
    });

    // Shares
    tiktok.on('share', (data) => {
      broadcast('share', { uniqueId: data.uniqueId });
    });

    await tiktok.connect();
  } catch (err) {
    console.error('Failed to connect to TikTok:', err.message);
  }
}

connectTikTok();



