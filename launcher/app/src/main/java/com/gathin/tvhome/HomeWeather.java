package com.gathin.tvhome;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class HomeWeather {
    private static final long REFRESH_MS = 15 * 60_000L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TextView view;
    private final String endpoint;
    private final Runnable refresh = this::refresh;
    private long nextRefresh;
    private boolean active;
    private boolean loading;

    HomeWeather(TextView view, double latitude, double longitude) {
        this.view = view;
        endpoint = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude
                + "&longitude=" + longitude + "&current=temperature_2m,weather_code,is_day";
    }

    void start() {
        active = true;
        handler.postDelayed(refresh, Math.max(0, nextRefresh - SystemClock.elapsedRealtime()));
    }

    void stop() {
        active = false;
        handler.removeCallbacks(refresh);
    }

    private void refresh() {
        if (!active || loading) return;
        loading = true;
        new Thread(() -> {
            String weather = readWeather();
            handler.post(() -> {
                loading = false;
                nextRefresh = SystemClock.elapsedRealtime() + REFRESH_MS;
                view.setText(weather);
                view.setVisibility(weather == null ? View.GONE : View.VISIBLE);
                if (active) handler.postDelayed(refresh, REFRESH_MS);
            });
        }, "home-weather").start();
    }

    private String readWeather() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(false);
            try (InputStream input = connection.getInputStream()) {
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    if (data.size() + count > 16 * 1024) return null;
                    data.write(buffer, 0, count);
                }
                JSONObject current = new JSONObject(new String(data.toByteArray(), StandardCharsets.UTF_8))
                        .getJSONObject("current");
                double temperature = current.getDouble("temperature_2m");
                if (!Double.isFinite(temperature)) return null;
                return view.getContext().getString(R.string.home_weather, Math.round(temperature),
                        condition(current.getInt("weather_code"), current.optInt("is_day") == 1));
            }
        } catch (IOException | JSONException e) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String condition(int code, boolean day) {
        int label;
        if (code == 0) label = day ? R.string.weather_sunny : R.string.weather_clear;
        else if (code == 1 || code == 2) label = R.string.weather_partly_cloudy;
        else if (code == 3) label = R.string.weather_cloudy;
        else if (code == 45 || code == 48) label = R.string.weather_fog;
        else if ((code >= 71 && code <= 77) || code == 85 || code == 86) label = R.string.weather_snow;
        else if (code >= 95) label = R.string.weather_thunderstorms;
        else if (code >= 51 && code <= 82) label = R.string.weather_rain;
        else label = R.string.weather_conditions_unknown;
        return view.getContext().getString(label);
    }
}
