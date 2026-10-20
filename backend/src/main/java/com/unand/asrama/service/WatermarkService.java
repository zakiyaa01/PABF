package com.unand.asrama.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
public class WatermarkService {

    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String processAndSaveWatermarkedPhoto(
            String base64Image,
            String nim,
            String nama,
            String namaGedung,
            Double latitude,
            Double longitude,
            Double accuracyMeters,
            Double distanceMeters,
            LocalDateTime serverTimestamp
    ) {
        if (base64Image == null || base64Image.trim().isEmpty()) {
            return null;
        }

        try {
            // Clean base64 header if present
            String cleanBase64 = base64Image;
            if (cleanBase64.contains(",")) {
                cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
            }

            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64.trim());
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (originalImage == null) {
                // If decoding fails, save raw bytes directly
                return saveRawFallback(imageBytes, nim);
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Create canvas for drawing watermark
            BufferedImage watermarked = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = watermarked.createGraphics();

            // Draw original image
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Watermark Banner Dimensions
            int bannerHeight = Math.max(90, (int) (height * 0.18));
            int bannerY = height - bannerHeight;

            // Semi-transparent dark background banner
            g2d.setColor(new Color(15, 23, 42, 210)); // Dark navy slate with alpha
            g2d.fillRect(0, bannerY, width, bannerHeight);

            // Accent border line (Gold / Royal Blue)
            g2d.setColor(new Color(245, 158, 11)); // Amber / Gold
            g2d.setStroke(new BasicStroke(Math.max(2, (float) (height * 0.005))));
            g2d.drawLine(0, bannerY, width, bannerY);

            // Font configurations
            int baseFontSize = Math.max(12, (int) (bannerHeight * 0.16));
            Font titleFont = new Font("SansSerif", Font.BOLD, (int) (baseFontSize * 1.15));
            Font textFont = new Font("SansSerif", Font.PLAIN, baseFontSize);

            int textX = Math.max(15, (int) (width * 0.03));
            int lineSpacing = (int) (baseFontSize * 1.35);
            int currentY = bannerY + (int) (bannerHeight * 0.25);

            // Line 1: Header / Branding
            g2d.setFont(titleFont);
            g2d.setColor(new Color(251, 191, 36)); // Light Amber
            g2d.drawString("SIA-ASRAMA UNIVERSITAS ANDALAS | GEOTAGGED", textX, currentY);

            // Line 2: Identity & Gedung
            currentY += lineSpacing;
            g2d.setFont(textFont);
            g2d.setColor(Color.WHITE);
            String line2 = "NIM: " + (nim != null ? nim : "-") + " | " + (nama != null ? nama : "-") + " | " + (namaGedung != null ? namaGedung : "-");
            g2d.drawString(line2, textX, currentY);

            // Line 3: Server Timestamp
            currentY += lineSpacing;
            g2d.setColor(new Color(226, 232, 240));
            String formattedTime = serverTimestamp != null ? serverTimestamp.format(FORMATTER) : LocalDateTime.now().format(FORMATTER);
            String line3 = "Waktu Server: " + formattedTime + " WIB (Anti-Jam HP)";
            g2d.drawString(line3, textX, currentY);

            // Line 4: Geolocation Coordinates & Precision
            currentY += lineSpacing;
            g2d.setColor(new Color(148, 163, 184));
            String line4 = String.format("Koordinat: %.6f, %.6f | Akurasi: %.1fm | Jarak Gedung: %.1fm",
                    latitude != null ? latitude : 0.0,
                    longitude != null ? longitude : 0.0,
                    accuracyMeters != null ? accuracyMeters : 0.0,
                    distanceMeters != null ? distanceMeters : 0.0);
            g2d.drawString(line4, textX, currentY);

            g2d.dispose();

            // Ensure destination directory exists
            File dir = new File(uploadBaseDir + "/presensi");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = "presensi_" + (nim != null ? nim : "user") + "_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(dir, filename);
            ImageIO.write(watermarked, "jpg", outputFile);

            return "/uploads/presensi/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveRawFallback(byte[] imageBytes, String nim) {
        try {
            File dir = new File(uploadBaseDir + "/presensi");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filename = "presensi_raw_" + (nim != null ? nim : "user") + "_" + UUID.randomUUID() + ".jpg";
            File outputFile = new File(dir, filename);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(imageBytes);
            }
            return "/uploads/presensi/" + filename;
        } catch (Exception e) {
            return null;
        }
    }
}
