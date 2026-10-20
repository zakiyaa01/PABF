package com.unand.asrama.config;

import com.unand.asrama.entity.Gedung;
import com.unand.asrama.entity.Izin;
import com.unand.asrama.entity.Kamar;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.GedungRepository;
import com.unand.asrama.repository.IzinRepository;
import com.unand.asrama.repository.KamarRepository;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private GedungRepository gedungRepository;

        @Autowired
        private KamarRepository kamarRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PresensiRepository presensiRepository;

        @Autowired
        private IzinRepository izinRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                System.out.println("🌱 Initializing & verifying Enterprise master accounts for Asrama UNAND...");

                // 1. Ensure Gedungs exist
                Gedung gedungA = gedungRepository.findAll().stream().findFirst().orElseGet(() -> {
                        Gedung g = new Gedung(
                                        "Gedung Asrama Putra A",
                                        new BigDecimal("-0.914231"),
                                        new BigDecimal("100.466012"),
                                        50.0,
                                        "70:3a:0e:aa:bb:cc");
                        return gedungRepository.save(g);
                });

                Gedung gedungB = gedungRepository.findAll().stream().skip(1).findFirst().orElseGet(() -> {
                        Gedung g = new Gedung(
                                        "Gedung Asrama Putri B",
                                        new BigDecimal("-0.914800"),
                                        new BigDecimal("100.466500"),
                                        50.0,
                                        "70:3a:0e:aa:bb:dd");
                        return gedungRepository.save(g);
                });

                // 2. Ensure Kamars exist
                Kamar kamarA101 = kamarRepository.findAll().stream().findFirst().orElseGet(() -> {
                        return kamarRepository.save(new Kamar("A.101", 1, "Lorong 1", gedungA));
                });

                Kamar kamarB201 = kamarRepository.findAll().stream().skip(1).findFirst().orElseGet(() -> {
                        return kamarRepository.save(new Kamar("B.201", 2, "Lorong Melati", gedungB));
                });

                // 3. Ensure Admin account exists & active
                User admin = userRepository.findByEmailOrNim("admin@unand.ac.id", "ADM001").orElseGet(() -> {
                        User u = new User();
                        u.setNim("ADM001");
                        u.setEmail("admin@unand.ac.id");
                        u.setNama("Administrator Asrama UNAND");
                        u.setRole(User.Role.Admin);
                        return u;
                });
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setIsActive(true);
                userRepository.save(admin);

                // 4. Ensure Fasil account exists & active
                User fasil = userRepository.findByEmailOrNim("fasil@unand.ac.id", "FAS001").orElseGet(() -> {
                        User u = new User();
                        u.setNim("FAS001");
                        u.setEmail("fasil@unand.ac.id");
                        u.setNama("Fasilitator Utama");
                        u.setRole(User.Role.Fasil);
                        return u;
                });
                fasil.setPassword(passwordEncoder.encode("fasil123"));
                fasil.setLorongBinaan("Lorong 1");
                fasil.setIsActive(true);
                fasil = userRepository.save(fasil);

                // 5. Ensure Farhan (2211521001) exists & active
                User farhan = userRepository.findByEmailOrNim("farhan@unand.ac.id", "2211521001").orElseGet(() -> {
                        User u = new User();
                        u.setNim("2211521001");
                        u.setEmail("farhan@unand.ac.id");
                        u.setNama("Muhammad Farhan");
                        u.setRole(User.Role.Penghuni);
                        return u;
                });
                farhan.setKamar(kamarA101);
                farhan.setFasil(fasil);
                farhan.setDeviceId("ANDROID_ID_A1B2C3D4");
                farhan.setBiometricEnabled(true);
                farhan.setPassword(passwordEncoder.encode("penghuni123"));
                farhan.setIsActive(true);
                userRepository.save(farhan);

                // 6. Ensure Siti Aminah (2211522002) exists & active
                User siti = userRepository.findByEmailOrNim("siti@unand.ac.id", "2211522002").orElseGet(() -> {
                        User u = new User();
                        u.setNim("2211522002");
                        u.setEmail("siti@unand.ac.id");
                        u.setNama("Siti Aminah");
                        u.setRole(User.Role.Penghuni);
                        return u;
                });
                siti.setKamar(kamarB201);
                siti.setFasil(fasil);
                siti.setDeviceId("ANDROID_ID_E5F6G7H8");
                siti.setBiometricEnabled(true);
                siti.setPassword(passwordEncoder.encode("penghuni123"));
                siti.setIsActive(true);
                userRepository.save(siti);

                // Ensure all users in database are marked as active
                userRepository.findAll().forEach(u -> {
                        if (u.getIsActive() == null || !u.getIsActive()) {
                                u.setIsActive(true);
                                userRepository.save(u);
                        }
                });

                System.out.println("✅ SIA-Asrama Enterprise Accounts Successfully Verified & Ready!");
                System.out.println("   - Penghuni: 2211521001 / penghuni123 (Muhammad Farhan)");
                System.out.println("   - Fasil:    FAS001 / fasil123 (Fasilitator Utama)");
                System.out.println("   - Admin:    ADM001 / admin123 (Administrator)");
        }
}
