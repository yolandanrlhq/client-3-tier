package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import model.Pesanan;

public class PesananApi {
    private static final String BASE_URL = "http://localhost/sewa-app-tier-copy/public/index.php?menu=pesanan";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // 1. Ambil Semua Data (GET)
    public List<Pesanan> findAll(String keyword) throws Exception {
        String url = BASE_URL + "&keyword=" + keyword;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        ApiResponse<List<Pesanan>> apiResp = gson.fromJson(response.body(), 
                new TypeToken<ApiResponse<List<Pesanan>>>() {}.getType());

        if (!apiResp.success) throw new Exception(apiResp.message);
        return apiResp.data;
    }

    // 2. Simpan Data Baru (POST)
    public void create(Pesanan p) throws Exception {
        String json = gson.toJson(p);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleResponse(response);
    }

    // 3. Update Data (PUT) - TAMBAHKAN INI
    public void update(Pesanan p) throws Exception {
        String json = gson.toJson(p);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleResponse(response);
    }

    // 4. Hapus Data (DELETE)
    public void delete(String id) throws Exception {
        String url = BASE_URL + "&id=" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleResponse(response);
    }

    private void handleResponse(HttpResponse<String> response) throws Exception {
        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
        if (apiResp == null || !apiResp.success) {
            String msg = (apiResp != null) ? apiResp.message : "Gagal terhubung ke server";
            throw new Exception(msg);
        }
    }

    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }
}