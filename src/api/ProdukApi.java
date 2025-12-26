package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Kostum;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ProdukApi {
    // Pastikan URL ini sesuai dengan lokasi file index.php di Laragon kamu
    private final String BASE_URL = "http://localhost/sewa-app-tier-copy/public/index.php?menu=produk";
    private final HttpClient client = HttpClient.newBuilder().build();
    private final Gson gson = new Gson();

    public List<Kostum> getAll(String keyword) throws Exception {
        String url = BASE_URL;
        if (keyword != null && !keyword.trim().isEmpty()) {
            url += "&search=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();

        // VALIDASI: Cek apakah respon beneran JSON sebelum di-parsing
        if (!body.startsWith("{")) {
            System.err.println("Gagal memuat produk. Respon server bukan JSON: " + body);
            return new ArrayList<>();
        }

        ApiResponse<List<Kostum>> apiResp = gson.fromJson(body, 
                new TypeToken<ApiResponse<List<Kostum>>>(){}.getType());

        return (apiResp != null && apiResp.success && apiResp.data != null) ? apiResp.data : new ArrayList<>();
    }

    public boolean save(Kostum k) throws Exception {
        String json = gson.toJson(k);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();

        if (!body.startsWith("{")) return false;

        ApiResponse<Object> apiResp = gson.fromJson(body, ApiResponse.class);
        return apiResp != null && apiResp.success;
    }

    public boolean delete(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "&id=" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();

        if (!body.startsWith("{")) return false;

        ApiResponse<Object> apiResp = gson.fromJson(body, ApiResponse.class);
        return apiResp != null && apiResp.success;
    }

    // Class pembantu untuk standarisasi JSON dari PHP
    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }
}