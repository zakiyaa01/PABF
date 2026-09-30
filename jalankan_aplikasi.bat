@echo off
echo ========================================================
echo Memulai Aplikasi Absensi Asrama UNAND
echo ========================================================

echo 1. Menjalankan Backend Spring Boot...
cd backend
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot
start "Backend Spring Boot" cmd /k ".\mvnw.cmd spring-boot:run"

echo 2. Menjalankan Frontend React Vite...
cd ../frontend
start "Frontend React" cmd /k "npm run dev"

echo Selesai! Pastikan MySQL/XAMPP sudah Anda nyalakan manual.
pause
