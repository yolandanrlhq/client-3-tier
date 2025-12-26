package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.DashboardModel;

public class DashboardApi {
    private static final String BASE_URL = "http://localhost/penyewaan_kostum/public/index.php?menu=dashboard";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public DashboardModel getStatistics() throws Exception {
        // 1. Bangun Request (GET)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        // 2. Kirim dan terima response dalam bentuk String
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 3. Parsing menggunakan ApiResponse gaya dosenmu
        // Karena DashboardModel itu objek tunggal, kita sesuaikan Type-nya
        ApiResponse<DashboardModel> apiResp = gson.fromJson(response.body(),
                new TypeToken<ApiResponse<DashboardModel>>() {}.getType());

        // 4. Cek sukses atau tidak dari JSON-nya
        if (!apiResp.success) {
            throw new Exception(apiResp.message);
        }

        return apiResp.data;
    }

    // Inner class untuk membungkus format JSON dari PHP
    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }
}