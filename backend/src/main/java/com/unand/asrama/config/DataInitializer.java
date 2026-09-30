package com.unand.asrama.config;

import com.unand.asrama.entity.Gedung;
import com.unand.asrama.entity.Kamar;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.GedungRepository;
import com.unand.asrama.repository.KamarRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private GedungRepository gedungRepository;

    @Autowired
    private KamarRepository kamarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding initial sample data for Asrama UNAND...");

            // 1. Gedung
            Gedung gedungA = gedungRepository.save(new Gedung("Gedung Asrama Putra A"));
            Gedung gedungB = gedungRepository.save(new Gedung("Gedung Asrama Putri B"));

            // 2. Kamar
            Kamar kamarA101 = kamarRepository.save(new Kamar("A.101", 1, gedungA));
            Kamar kamarB201 = kamarRepository.save(new Kamar("B.201", 2, gedungB));

            // 3. Admin Account
            User admin = new User(
                    "ADM001",
                    "Administrator Asrama",
                    "Padang",
                    User.JenisKelamin.L,
                    "admin@unand.ac.id",
                    passwordEncoder.encode("admin123"),
                    User.Role.Admin,
                    null
            );
            userRepository.save(admin);

            // 4. Fasil Account
            User fasil = new User(
                    "FAS001",
                    "Fasilitator Utama",
                    "Padang",
                    User.JenisKelamin.L,
                    "fasil@unand.ac.id",
                    passwordEncoder.encode("fasil123"),
                    User.Role.Fasil,
                    null
            );
            userRepository.save(fasil);

            // 5. Penghuni Accounts
            User penghuni1 = new User(
                    "2211522001",
                    "Budi Raharjo",
                    "Padang",
                    User.JenisKelamin.L,
                    "penghuni@unand.ac.id",
                    passwordEncoder.encode("penghuni123"),
                    User.Role.Penghuni,
                    kamarA101
            );
            userRepository.save(penghuni1);

            User penghuni2 = new User(
                    "2211522002",
                    "Siti Aminah",
                    "Bukittinggi",
                    User.JenisKelamin.P,
                    "siti@unand.ac.id",
                    passwordEncoder.encode("penghuni123"),
                    User.Role.Penghuni,
                    kamarB201
            );
            userRepository.save(penghuni2);

            System.out.println("✅ Sample data successfully initialized!");
            System.out.println("🔑 Credentials for Testing:");
            System.out.println("   - Admin: admin@unand.ac.id / admin123");
            System.out.println("   - Fasil: fasil@unand.ac.id / fasil123");
            System.out.println("   - Penghuni: penghuni@unand.ac.id / penghuni123 (NIM: 2211522001)");
        }
    }
}
