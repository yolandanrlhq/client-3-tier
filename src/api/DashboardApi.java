package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.DashboardModel;

public class DashboardApi {
    private static final String BASE_URL = "http://localhost/sewa-app-tier/public/index.php?menu=dashboard";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public DashboardModel getStatistics() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // FIX UTAMA: Gunakan .trim() untuk membuang karakter sampah/spasi dari PHP
        String rawJson = response.body().trim();
        
        // Debug: Aktifkan ini jika masih error untuk melihat apa yang sebenarnya dikirim PHP
        // System.out.println("RAW JSON: " + rawJson);

        try {
            // Parsing menggunakan ApiResponse
            ApiResponse<DashboardModel> apiResp = gson.fromJson(rawJson,
                    new TypeToken<ApiResponse<DashboardModel>>() {}.getType());

            if (apiResp == null) {
                throw new Exception("Server mengembalikan response kosong.");
            }

            if (!apiResp.success) {
                throw new Exception(apiResp.message != null ? apiResp.message : "Gagal mengambil data");
            }

            return apiResp.data;
            
        } catch (Exception e) {
            // Jika parsing gagal, kemungkinan besar PHP mengirimkan Error Teks (bukan JSON)
            throw new Exception("Gagal membaca data dari server. Pastikan backend tidak error.");
        }
    }

    // Pastikan field di inner class ini sesuai dengan key di JSON PHP
    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }
}