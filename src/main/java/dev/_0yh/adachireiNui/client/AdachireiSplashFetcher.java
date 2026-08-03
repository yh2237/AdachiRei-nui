package dev._0yh.adachireiNui.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev._0yh.adachireiNui.AdachireiNui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class AdachireiSplashFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdachireiNui.MOD_ID);
    private static final String API_URL = "https://adachidb.net/api/posts/random";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static final AtomicReference<CompletableFuture<String>> pendingFetch = new AtomicReference<>(null);
    private static final AtomicReference<String> cachedSplash = new AtomicReference<>(null);

    public static CompletableFuture<String> fetchAsync() {
        String cached = cachedSplash.get();
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        CompletableFuture<String> pending = pendingFetch.get();
        if (pending != null) {
            return pending;
        }

        CompletableFuture<String> future = new CompletableFuture<>();
        if (!pendingFetch.compareAndSet(null, future)) {
            return pendingFetch.get();
        }

        CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .build();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .timeout(TIMEOUT)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (json.has("text")) {
                        String text = json.get("text").getAsString();
                        if (text != null && !text.isBlank()) {
                            cachedSplash.set(text);
                            future.complete(text);
                            return;
                        }
                    }
                }
                LOGGER.warn("Unexpected API response: status={}", response.statusCode());
                future.complete(null);
            } catch (Exception e) {
                LOGGER.warn("Failed to fetch splash from API: {}", e.getMessage());
                future.complete(null);
            }
        });

        return future;
    }

    public static String getCachedSplash() {
        return cachedSplash.get();
    }

    public static String awaitSplash() {
        String cached = cachedSplash.get();
        if (cached != null) {
            return cached;
        }

        try {
            return fetchAsync().get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LOGGER.warn("Timed out waiting for custom splash");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve custom splash: {}", e.getMessage());
        }
        return null;
    }
}
