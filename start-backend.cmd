@echo off
REM Set environment variables for Supabase connection
set SPRING_PROFILES_ACTIVE=dev
set SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres
set SPRING_DATASOURCE_USERNAME=postgres.rnyliyffaplunttdtckx
set SPRING_DATASOURCE_PASSWORD=petmarketdb
set APP_JWT_SECRET=dev-secret-key-256bit-this-is-only-for-development-testing-purposes
set APP_BOOTSTRAP_ADMIN_ENABLED=true
set APP_BOOTSTRAP_ADMIN_EMAIL=admin@petmarket.local
set APP_BOOTSTRAP_ADMIN_PASSWORD=AdminPassword123!
set APP_CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

REM Change to backend directory and start
cd /d "C:\Users\Julius Cesar Gamallo\Documents\petmarket_backend\petmarket-backend"
call mvnw.cmd spring-boot:run -DskipTests

pause
