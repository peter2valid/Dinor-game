package com.dinorun.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.concurrent.TimeUnit;

public class WebSocketClient extends WebSocketListener {
    private static final String TAG = "WebSocketClient";
    private static final long RECONNECT_DELAY_MS = 3000; // 3 seconds

    private WebSocket webSocket;
    private OkHttpClient client;
    private String serverUrl;
    private Handler reconnectHandler;
    private boolean shouldReconnect = true;
    private WebSocketEventListener eventListener;

    public interface WebSocketEventListener {
        void onJump();

        void onRestart();

        void onSpeedBoost();

        void onDuck();
    }

    public WebSocketClient(String serverUrl, WebSocketEventListener listener) {
        this.serverUrl = serverUrl;
        this.eventListener = listener;
        this.reconnectHandler = new Handler(Looper.getMainLooper());

        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS) // No timeout for long-lived connection
                .build();
    }

    public void connect() {
        if (webSocket != null) {
            webSocket.close(1000, "Reconnecting");
        }

        Request request = new Request.Builder()
                .url(serverUrl)
                .build();

        webSocket = client.newWebSocket(request, this);
        Log.d(TAG, "Connecting to WebSocket: " + serverUrl);
    }

    public void disconnect() {
        shouldReconnect = false;
        reconnectHandler.removeCallbacksAndMessages(null);

        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");
            webSocket = null;
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "WebSocket Connected!");
        shouldReconnect = true;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Received message: " + text);

        try {
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();

            if (json.has("event")) {
                String event = json.get("event").getAsString();
                handleEvent(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing message: " + e.getMessage());
        }
    }

    private void handleEvent(String event) {
        // Run on main thread to interact with GameView
        reconnectHandler.post(() -> {
            if (eventListener != null) {
                switch (event.toLowerCase()) {
                    case "jump":
                        eventListener.onJump();
                        break;
                    case "restart":
                        eventListener.onRestart();
                        break;
                    case "speed-up":
                    case "speedboost":
                        eventListener.onSpeedBoost();
                        break;
                    case "duck":
                        eventListener.onDuck();
                        break;
                    default:
                        Log.w(TAG, "Unknown event: " + event);
                }
            }
        });
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket Error: " + t.getMessage());

        if (shouldReconnect) {
            scheduleReconnect();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "WebSocket Closing: " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "WebSocket Closed: " + reason);

        if (shouldReconnect) {
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        reconnectHandler.postDelayed(() -> {
            Log.d(TAG, "Attempting to reconnect...");
            connect();
        }, RECONNECT_DELAY_MS);
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }
}
