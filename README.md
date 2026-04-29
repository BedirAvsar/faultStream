# FaultStream

FaultStream, Spring Boot ve Next.js ile gelistirilmis bir endustriyel izleme projesidir. Bu repo su anda tek bir demo akisinda uca uca calisiyor:

- auth
- equipment ve sensor yonetimi
- sensor simulatoru ve ingestion
- dashboard terminal snapshot API
- alert, work-order ve maintenance log akisi
- Swagger UI
- temel unit ve integration testleri

Bu README, projeyi sifirdan alan birinin repo'yu lokalinde kaldirabilmesi icin yazildi.

## Gereksinimler

- `Java 21`
- `Docker`
- `Docker Compose`
- `Node.js 20+`

Notlar:

- Repo icinde `mvnw` ve `mvnw.cmd` var; sistemde Maven kurulu olmasi gerekmiyor.
- PostgreSQL host makinede `5433` portuna acilir.
- En hizli lokal kurulum icin `local` profili kullanilabilir; bu profil demo JWT secret, demo data ve simulator ayarlarini hazir getirir.
- Ortam degiskenlerini elle yonetmek istersen `.env.example` ve `faultstream-dashboard/.env.local.example` dosyalarini referans al.

## Hizli Kurulum

### 1. Frontend ortam dosyasini hazirla

Frontend dizininde:

```powershell
Copy-Item faultstream-dashboard/.env.local.example faultstream-dashboard/.env.local
```

### 2. Altyapi servislerini kaldir

```powershell
docker compose up -d
docker compose ps
```

Beklenen servisler:

- `postgres`
- `zookeeper`
- `kafka`
- `redis`

### 3. Backend'i baslat

En rahat lokal gelistirme yolu:

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

macOS / Linux:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Ortam degiskenlerini kendin yonetmek istersen standart profil ile de calistirabilirsin:

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Backend ana adresi:

- `http://localhost:8080`

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

Dashboard snapshot:

- `http://localhost:8080/api/v1/dashboard/terminal`

### 4. Frontend'i baslat

```powershell
Set-Location faultstream-dashboard
npm install
npm run dev
```

Frontend adresi:

- `http://localhost:3000`

## Varsayilan Davranis

`local` profili ile acildiginda:

- demo user'lar seed edilir
- demo equipment ve sensor kayitlari seed edilir
- simulator acik gelir
- sensor reading'ler once Kafka'ya gonderilmeye calisir
- gerekli oldugunda direct persistence fallback devreye girer
- kritik reading'ler alert ve work-order uretebilir
- maintenance log API work-order ile bagli calisir
- dashboard backend'den gercek veri ceker

Demo kullanicilar:

- `admin@faultstream.local`
- `engineer@faultstream.local`
- `technician@faultstream.local`
- sifre: `Faultstream123!`

## Ortam Degiskenleri

Backend tarafinda kullanilan ana degiskenler:

- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`
- `SIMULATOR_ENABLED`
- `SIMULATOR_INTERVAL_MS`
- `FAULTSTREAM_DEMO_DATA_ENABLED`
- `FAULTSTREAM_PUBLIC_REGISTRATION_ENABLED`
- `KAFKA_TOPIC_SENSOR_READINGS`

Frontend tarafinda:

- `NEXT_PUBLIC_API_BASE_URL`

Ornek dosyalar:

- `.env.example`
- `faultstream-dashboard/.env.local.example`

Not:

- Root dizindeki `.env` dosyasi Docker Compose ozellestirmesi icindir.
- Spring Boot, terminale export edilmemis `.env` dosyasini otomatik okumaz.
- Bu nedenle sifirdan kurulumda `local` profili en sorunsuz secenektir.

## Ilk Kontroller

Kurulumdan sonra su kontrolleri yap:

### 1. Swagger aciliyor mu

- `http://localhost:8080/swagger-ui.html`

### 2. Dashboard veri donuyor mu

```powershell
curl http://localhost:8080/api/v1/dashboard/terminal
```

Beklenen alanlar:

- `success: true`
- `data.stats`
- `data.stream`
- `data.patternAnalysis`
- `data.frequencyDensity`

### 3. Frontend backend'e baglaniyor mu

- `http://localhost:3000`

Beklenen gorunum:

- `SENSOR_LINK: LIVE` veya kisa sureli `DEGRADED`
- anomaly stream satirlari
- pie ve bar chart verileri

## Aktif API Alanlari

Public:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/dashboard/terminal`

Equipment:

- `GET /api/v1/equipments`
- `POST /api/v1/equipments`
- `GET /api/v1/equipments/{id}`
- `DELETE /api/v1/equipments/{id}`

Sensor:

- `GET /api/v1/sensors`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{id}`
- `GET /api/v1/sensors/{id}/readings?last=100`

Alert:

- `GET /api/v1/alerts`
- `GET /api/v1/alerts/active`
- `GET /api/v1/alerts/{id}`
- `POST /api/v1/alerts/{id}/resolve`

Work order:

- `GET /api/v1/work-orders`
- `GET /api/v1/work-orders/{id}`
- `PUT /api/v1/work-orders/{id}/assign`
- `PUT /api/v1/work-orders/{id}/complete`

Maintenance log:

- `GET /api/v1/maintenance-logs`
- `GET /api/v1/maintenance-logs/work-order/{workOrderId}`
- `POST /api/v1/maintenance-logs`

## Testler

Backend testleri:

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

macOS / Linux:

```bash
./mvnw test
```

Frontend lint:

```powershell
Set-Location faultstream-dashboard
npm run lint
```

## Sik Karsilasilan Sorunlar

### Backend acilmiyor

Kontrol et:

- `JWT_SECRET` tanimli mi
- local profil ile aciliyorsa komutta `spring-boot.run.profiles=local` gecti mi
- `docker compose ps` icinde `postgres` ayakta mi
- `5433` portu dolu mu
- daha once olusmus eski DB semasi varsa volume temizligi gerekiyor mu

### Dashboard bos geliyor

Kontrol et:

- `SIMULATOR_ENABLED=true`
- `FAULTSTREAM_DEMO_DATA_ENABLED=true`
- backend loglarinda hata var mi
- `http://localhost:8080/api/v1/dashboard/terminal` veri donuyor mu

### Frontend API'ye baglanamiyor

Kontrol et:

- `faultstream-dashboard/.env.local` icindeki `NEXT_PUBLIC_API_BASE_URL`
- backend `8080` portunda mi
- frontend `3000` portunda mi

### Docker servislerini sifirlamak gerekiyor

```powershell
docker compose down -v
docker compose up -d
```

## Proje Yapisi

Backend:

- `src/main/java/com/faultstream/common`
- `src/main/java/com/faultstream/config`
- `src/main/java/com/faultstream/domain`
- `src/main/resources/db/migration`

Frontend:

- `faultstream-dashboard/src/app`

Dokumantasyon:

- `ROADMAP.md`
- `docs/swot-analizi.md`
- `docs/degisiklik-raporu.md`
