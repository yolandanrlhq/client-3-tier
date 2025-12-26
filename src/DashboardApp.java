import view.FrameUtama; // WAJIB: Agar Java tahu FrameUtama ada di package 'view'
import javax.swing.SwingUtilities;

public class DashboardApp {
    public static void main(String[] args) {
        // 1. Setup LookAndFeel (Tema) agar modern seperti di FrameUtama kamu
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Gagal memuat tema modern, menggunakan tema standar.");
        }

        // 2. Jalankan UI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                FrameUtama frame = new FrameUtama();
                
                // Opsional: Karena di FrameUtama kamu belum ada pack(), tambahkan di sini
                frame.pack(); 
                frame.setLocationRelativeTo(null); // Muncul di tengah layar
                frame.setVisible(true);
                
                System.out.println("Aplikasi Client-Tier Berhasil Dijalankan!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}