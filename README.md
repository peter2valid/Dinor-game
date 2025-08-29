Dino Runner + TikTok Live

Setup

1) Install Node.js LTS.
2) In this folder, create a .env file with:

TIKTOK_USERNAME=@your_username
PORT=3000

3) Install deps:

npm install

4) Run the server:

npm run dev

5) Open the game at http://localhost:3000

Event mappings

- Rose: Instant restart.
- Perfume/TikTok: Flip screen for 5s.
- Ice Cream: Dino turns into ice-cream for 8s.
- Lion: Spawns a chasing lion for 10s.
- Castle: Grants shield + slow-mo + magnet for 8s.
- GG/Hand Wave: Spawns ghost obstacles for 8s (no collision).
- Fire: Turbo speed 5s and score x2.
- Music Note: Warps music/sfx for duration (placeholder visual only).
- Diamond: Gem shower; collecting gems awards points.
- Like taps: Every 50 likes adds a cactus; every 500 likes enables auto-jump 10s.
- Follow: Grants a shield.
- Share: Spawns a random power-up.
- Comments with "jump" or "duck": Forces action.

Notes

- Uses tiktok-live-connector to receive events on the server and broadcasts to the browser via WebSocket.
- If not streaming, the game still runs; events simply won't arrive.


# Dinor-game
# Dinor-game
