package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Pelanggan;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PelangganApi {
    // Sesuaikan URL dengan path di htdocs kamu
    private final String BASE_URL = "http://localhost/sewa-app-tier-copy/public/index.php?menu=pelanggan";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    /**
     * Mengambil semua data pelanggan (GET)
     */
    public List<Pelanggan> getAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Parsing menggunakan wrapper ApiResponse
        ApiResponse<List<Pelanggan>> apiResp = gson.fromJson(response.body().trim(), 
                new TypeToken<ApiResponse<List<Pelanggan>>>(){}.getType());

        return (apiResp != null && apiResp.success) ? apiResp.data : new ArrayList<>();
    }

    /**
     * Menyimpan atau Mengupdate data pelanggan (POST)
     * PHP kita menggunakan ON DUPLICATE KEY UPDATE, jadi satu method untuk dua fungsi.
     */
    public boolean save(Pelanggan p) throws Exception {
        String json = gson.toJson(p);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<Object> apiResp = gson.fromJson(response.body().trim(), ApiResponse.class);
        
        return apiResp != null && apiResp.success;
    }

    /**
     * Menghapus data pelanggan (DELETE)
     */
    public boolean delete(String id) throws Exception {
        // Mengirim ID lewat query parameter: ?menu=pelanggan&id=HD001
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "&id=" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<Object> apiResp = gson.fromJson(response.body().trim(), ApiResponse.class);
        
        return apiResp != null && apiResp.success;
    }

    // Class pembungkus sesuai format JSON PHP kamu
    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }
}