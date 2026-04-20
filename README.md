# FaultStream

FaultStream, Spring Boot + Next.js tabanli bir endustriyel izleme projesidir. Su anki calisan cekirdek akis:

- backend tarafinda auth, equipment, sensor ve dashboard domain'leri
- simulator ile demo sensor verisi uretimi
- sensor reading'lerin veritabanina yazilmasi
- dashboard'in backend'den gercek veri cekmesi

Bu README, projeyi sifirdan kurup ayaga kaldirmak icin yazildi.

## Su an ne calisiyor

Bugun itibariyla repo icinde aktif olarak bulunan ana parcalar:

- `Spring Boot` backend
- `PostgreSQL`, `Kafka`, `Zookeeper`, `Redis` altyapisi
- `Next.js` dashboard
- demo equipment + sensor seed verisi
- demo user seed verisi
- alert, work-order ve maintenance log domain'leri
- `/api/v1/dashboard/terminal` endpoint'i
- Swagger UI

Planlanip henuz tam bitmemis alanlar:

- alert / work-order domain'leri
- maintenance log
- AI modulleri
- tam entegrasyon testleri

## Gereksinimler

Projeyi lokal makinede calistirmak icin sunlar gerekli:

- `Java 21`
- `Node.js 20+`
- `Docker Desktop` veya Docker Engine
- `Docker Compose`

Opsiyonel:

- `Maven 3.9+`

Not:

- Repo icinde `mvnw` ve `mvnw.cmd` var. Maven kurulu degilse once wrapper ile deneyin.
- PostgreSQL container'i host tarafinda `5433` portuna aciliyor. `5432` degil.

## Hizli Baslangic

Asagidaki adimlar, projeyi ilk kez kuran biri icin en kisa yol:

### 1. Repoyu klonla

```bash
git clone https://github.com/kullanici-adin/faultstream.git
cd faultstream
```

### 2. Altyapi servislerini kaldir

```bash
docker compose up -d
docker compose ps
```

Saglikli durumda olmasi beklenen servisler:

- `postgres`
- `zookeeper`
- `kafka`
- `redis`

### 3. Backend'i baslat

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Eger wrapper yerine lokal Maven kullanmak istersen:

```bash
mvn spring-boot:run
```

Backend acildiginda temel adres:

- `http://localhost:8080`

Swagger:

- `http://localhost:8080/swagger-ui.html`

Dashboard API:

- `http://localhost:8080/api/v1/dashboard/terminal`

### 4. Frontend'i baslat

Yeni bir terminal ac:

```bash
cd faultstream-dashboard
npm install
```

Windows PowerShell / macOS / Linux:

```bash
npm run dev
```

Frontend adresi:

- `http://localhost:3000`

## Beklenen Davranis

Her sey dogru calisiyorsa:

- backend acilisinda demo equipment ve sensor verisi olusur
- backend acilisinda demo admin / engineer / technician kullanicilari olusur
- simulator periyodik sensor reading uretir
- Kafka ayaktaysa reading once Kafka'ya gider
- Kafka yoksa backend direct persistence fallback ile calismaya devam eder
- kritik reading'ler alert ve work-order uretebilir
- dashboard birkaç saniye icinde veri gostermeye baslar

## Kurulum Detayi

### Backend konfigurasyonu

Backend konfigurasyonu ana olarak `src/main/resources/application.yml` dosyasinda bulunur.

Onemli varsayilanlar:

- PostgreSQL adresi: `jdbc:postgresql://localhost:5433/faultstream`
- Kafka adresi: `localhost:9092`
- Redis adresi: `localhost:6379`
- JWT secret: env yoksa default deger kullanilir
- simulator: varsayilan olarak acik

## Ortam Degiskenleri

En cok kullanilan degiskenler:

Backend:

```env
DATABASE_URL=jdbc:postgresql://localhost:5433/faultstream
DATABASE_USER=faultstream_user
DATABASE_PASSWORD=faultstream_pass
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=uzun-ve-guvenli-bir-secret
SIMULATOR_ENABLED=true
SIMULATOR_INTERVAL_MS=5000
KAFKA_TOPIC_SENSOR_READINGS=sensor-readings
```

Frontend:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1
```

Demo kullanicilar:

```text
admin@faultstream.local
engineer@faultstream.local
technician@faultstream.local
password: Faultstream123!
```

Not:

- `docker-compose.yml` icindeki PostgreSQL container'i `DB_USER` ve `DB_PASSWORD` kullanir.
- Spring Boot tarafi ise `DATABASE_USER` ve `DATABASE_PASSWORD` bekler.
- Lokal gelistirmede varsayilan degerler zaten birbirleriyle uyumlu oldugu icin ekstra ayar yapmadan baslayabilirsin.

## Servis Portlari

| Servis | Port |
|---|---|
| Spring Boot API | `8080` |
| Next.js Dashboard | `3000` |
| PostgreSQL | `5433` |
| Kafka | `9092` |
| Zookeeper | `2181` |
| Redis | `6379` |

## Ilk Kontroller

Projeyi ayaga kaldirdiktan sonra sirasiyla su kontrolleri yap:

### 1. Swagger aciliyor mu

Tarayicida:

- `http://localhost:8080/swagger-ui.html`

### 2. Dashboard API veri donuyor mu

Tarayicida veya terminalde:

```bash
curl http://localhost:8080/api/v1/dashboard/terminal
```

Beklenen sey:

- `success: true`
- `stats`
- `stream`
- `patternAnalysis`
- `frequencyDensity`

### 3. Frontend veri cekiyor mu

Tarayicida:

- `http://localhost:3000`

Beklenen sey:

- `SENSOR_LINK: LIVE` ya da gecici olarak `DEGRADED`
- stream tablosunda event'ler
- sag tarafta pie/bar chart verileri

## Temel API Uclari

### Public uclar

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/dashboard/terminal`

### Equipment

- `GET /api/v1/equipments`
- `POST /api/v1/equipments`
- `GET /api/v1/equipments/{id}`
- `DELETE /api/v1/equipments/{id}`

### Sensor

- `GET /api/v1/sensors`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{id}`
- `GET /api/v1/sensors/{id}/readings?last=100`

### Alerts

- `GET /api/v1/alerts`
- `GET /api/v1/alerts/active`
- `GET /api/v1/alerts/{id}`
- `POST /api/v1/alerts/{id}/resolve`

### Work Orders

- `GET /api/v1/work-orders`
- `GET /api/v1/work-orders/{id}`
- `PUT /api/v1/work-orders/{id}/assign`
- `PUT /api/v1/work-orders/{id}/complete`

### Maintenance Logs

- `GET /api/v1/maintenance-logs`
- `GET /api/v1/maintenance-logs/work-order/{workOrderId}`
- `POST /api/v1/maintenance-logs`

## Proje Yapisi

Bu repo iki ana bolumden olusur:

### Backend

- `src/main/java/com/faultstream/common`
- `src/main/java/com/faultstream/config`
- `src/main/java/com/faultstream/domain/user`
- `src/main/java/com/faultstream/domain/equipment`
- `src/main/java/com/faultstream/domain/sensor`
- `src/main/java/com/faultstream/domain/dashboard`
- `src/main/resources/db/migration`

### Frontend

- `faultstream-dashboard/src/app`

## Sik Karsilasilan Sorunlar

### 1. Backend acilmiyor, Flyway veya DB hatasi veriyor

Kontrol et:

- Docker acik mi
- `docker compose ps` icinde `postgres` ayakta mi
- `5433` portu baska bir sey tarafindan kullaniliyor mu

### 2. Dashboard bos geliyor

Kontrol et:

- backend loglarinda uygulama tam acildi mi
- `SIMULATOR_ENABLED=true` mi
- `http://localhost:8080/api/v1/dashboard/terminal` veri donuyor mu

### 3. Frontend backend'e baglanamiyor

Kontrol et:

- frontend `3000` portunda mi
- backend `8080` portunda mi
- `NEXT_PUBLIC_API_BASE_URL` dogru mu
- CORS nedeniyle proxy veya farkli host kullaniyor musun

### 4. Kafka ayakta degil

Projede fallback davranisi var. Kafka calismazsa simulator veriyi dogrudan yazmayi dener. Yine de tavsiye edilen kurulum tum Docker servislerini ayaga kaldirmaktir.

### 5. Java surumu uyusmuyor

Bu proje `Java 21` hedefler. `java -version` ciktiinda `21` gormelisin.

## Yararlı Komutlar

Tum servislere yeniden baslamak icin:

```bash
docker compose down
docker compose up -d
```

Docker volume'lerini de sifirlamak icin:

```bash
docker compose down -v
docker compose up -d
```

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

```bash
cd faultstream-dashboard
npm run lint
```

## Gelistirme Notu

Eger amacin sadece projeyi gormekse:

1. Docker servislerini kaldir
2. Backend'i baslat
3. Frontend'i baslat
4. Dashboard'a gir

Eger amacin gelistirme yapmaksa:

1. `README` sonrasi `ROADMAP.md` dosyasini oku
2. `src/main/java/com/faultstream/domain` altindan ilgili domain'i ac
3. `faultstream-dashboard/src/app/page.tsx` ile dashboard akisina bak

## Lisans

Bu proje `MIT` lisansi altindadir.
