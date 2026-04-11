# FaultStream — Geliştirme Yol Haritası (Master Roadmap)

> Her versiyon için zorunlu görevler, önerilen eklemeler ve mimari notlar bu dosya üzerinden takip edilmektedir.

---

## [x] v1.0.0 — The Foundation (Tamamlandı)
- [x] Docker Compose altyapısının kurulması (PostgreSQL, Kafka, Zookeeper, Redis).
- [x] Spring Security ve JWT altyapısı (Role-Based Access Control).
- [x] User Domain: Kimlik doğrulama, Kayıt, Login işlemleri.
- [x] Equipment Domain: Ekipman envanter yönetimi, servis mantığı ve Mockito/MockMvc birim testleri.

## [x] v2.0.0 — Clean Code & Operations Dashboard (Tamamlandı)
- [x] Özel Python betiği ile projedeki tüm teknik yorumların temizlenmesi (Clean Code).
- [x] Next.js (App Router) ile bağımsız bir frontend uygulamasının (faultstream-dashboard) kurulması.
- [x] Siber Güvenlik / NOC esintili minimalist "Dark Mode" arayüzünün oluşturulması.
- [x] Lucide ikonları ve Recharts ile simüle edilmiş (Mock) gerçek zamanlı gösterge panelinin entegre edilmesi.

---

## [ ] v3.0.0 — Sensor Data & Event Streaming

**Hedef:** Makinelerin nabzını dinlemeye ve verileri akıtmaya başlamak.

### Zorunlu Görevler

- [ ] `Sensor` Entity oluştur (id, name, equipmentId, type, unit, location)
- [ ] `SensorReading` Entity oluştur (id, sensorId, value, timestamp, status)
- [ ] `SensorRepository` ve `SensorReadingRepository` yaz
- [ ] `SensorService` — CRUD + son okuma sorgulama
- [ ] Flyway `V3__create_sensor_table.sql` yaz
- [ ] Flyway `V4__create_sensor_reading_table.sql` yaz
- [ ] `SensorSimulatorScheduler` — @Scheduled ile arka planda rastgele değer üret, Kafka'ya bas
- [ ] Kafka topic `sensor-readings` konfigürasyonunu tamamla
- [ ] `SensorDataConsumer` — Kafka'dan oku, SensorReading olarak kaydet
- [ ] `SensorController` — REST endpoint'leri (GET /sensors, GET /sensors/{id}/readings)
- [ ] Birim testleri: SensorService için Mockito testleri

### Önerilen Eklemeler

- [ ] **Sensör tipi enum'u** (TEMPERATURE, VIBRATION, HUMIDITY, PRESSURE, CURRENT) — ilerideki threshold kurallarını kolaylaştırır
- [ ] **Veri kalite filtresi** — Consumer içinde NaN, null veya fizik dışı değerleri (örn. sıcaklık -999) at, loglama yap
- [ ] **MQTT adapter** — `SensorSimulatorScheduler` yerine opsiyonel MQTT gateway desteği ekle; gerçek donanımla bağlantıyı hazırlar
- [ ] **TimescaleDB migration araştırması** — Zaman serisi sorguları PostgreSQL'de yavaşlar; `CREATE TABLE ... PARTITION BY RANGE (timestamp)` ile başla
- [ ] **Son N okuma sliding window endpoint'i** — `GET /sensors/{id}/readings?last=100` gibi; dashboard için kritik
- [ ] **Sensör sağlık durumu** (ONLINE / OFFLINE / DEGRADED) — Son X saniyede okuma gelmediyse OFFLINE işaretle

---

## [ ] v4.0.0 — Autonomous Alerting & Work Orders

**Hedef:** Sisteme "beyin" eklemek. İnsan müdahalesi olmadan tehditleri algılayıp müdahale başlatan yapı.

### Zorunlu Görevler

- [ ] `Alert` Entity oluştur (id, sensorId, equipmentId, level, message, triggeredAt, resolvedAt, status)
- [ ] `WorkOrder` Entity oluştur (id, alertId, assignedTechnicianId, status, createdAt, completedAt, notes)
- [ ] `AlertRepository` ve `WorkOrderRepository` yaz
- [ ] `AlertService` — alarm oluşturma, çözümleme, durum güncelleme
- [ ] `WorkOrderService` — iş emri oluşturma, atama, tamamlama
- [ ] Flyway `V5__create_alert_table.sql` yaz
- [ ] Flyway `V6__create_work_order_table.sql` yaz
- [ ] `SensorDataConsumer` içine threshold kontrol mantığı ekle
- [ ] Threshold aşılınca otomatik `Alert` oluştur (WARNING / CRITICAL seviye ayrımı)
- [ ] CRITICAL seviyede direkt teknisyene `WorkOrder` ata (mühendis onayı gerektirmesin)
- [ ] Redis Cache — aktif alarm durumlarını ve dashboard özetlerini cache'e al
- [ ] Redis TTL stratejisi belirle (alarm için 5 dk, dashboard için 30 sn önerilir)
- [ ] `AlertController` ve `WorkOrderController` REST endpoint'leri

### Önerilen Eklemeler

- [ ] **Threshold kurallarını DB'ye taşı** — Kod içinde sabit eşik değerleri yerine `ThresholdRule` entity'si; her sensör için farklı kural ayarlanabilsin
- [ ] **Alert susturma (snooze) mekanizması** — "Bu alarmı 2 saat sustur" kuralı; mesai saati dışı gürültüyü azaltır
- [ ] **Alarm eskalasyon zinciri** — 30 dk içinde teknisyen yanıt vermezse mühendise ilet; mühendis de yanıt vermezse yöneticiye
- [ ] **SMS / E-posta bildirimi** — Spring Mail + Twilio SMS entegrasyonu; CRITICAL alarmda anlık bildirim
- [ ] **Shift takvimi** — Hangi teknisyen hangi vardiyada? İş emirleri aktif vardiyaya atansın
- [ ] **Alarm patlama koruması (alert storm)** — Aynı sensörden 1 dakikada 50 alarm gelirse tek alarm üret; Kafka consumer'a debounce ekle
- [ ] **WorkOrder mobil görünümü** — Dashboard'a teknisyen için basit mobil-first iş emri ekranı ekle

---

## [ ] v5.0.0 — Maintenance Tracking & Full API Integration

**Hedef:** Teknolojiyi birleştirmek. Next.js ile Spring Boot'un gerçek anlamda haberleşmesi ve bakım geçmişinin tutulması.

### Zorunlu Görevler

- [ ] `MaintenanceLog` Entity oluştur (id, workOrderId, equipmentId, technicianId, action, duration, parts, cost, createdAt)
- [ ] `MaintenanceLogRepository` ve `MaintenanceLogService` yaz
- [ ] Flyway `V7__create_maintenance_log_table.sql` yaz
- [ ] `DashboardController` — frontend'in ihtiyaç duyduğu istatistik endpoint'leri
  - [ ] `GET /dashboard/summary` — toplam ekipman, aktif alarm, açık iş emri sayısı
  - [ ] `GET /dashboard/sensor-stream` — SSE endpoint (Server-Sent Events)
  - [ ] `GET /dashboard/alerts/recent` — son 24 saat alarmları
  - [ ] `GET /dashboard/equipment/health` — ekipman bazında sağlık skoru
- [ ] Frontend'deki mock döngüyü sil, SSE ile gerçek veriye bağla
- [ ] Next.js `useEffect` + `EventSource` ile canlı güncelleme hook'u yaz
- [ ] H2 testlerini kaldır, Testcontainers konfigürasyonu ekle
- [ ] PostgreSQL container ile end-to-end integration testleri yaz

### Önerilen Eklemeler

- [ ] **MTTR (Mean Time To Repair) hesaplama** — WorkOrder kapanış sürelerinden otomatik; "Bu ekipman ortalama 2.3 saatte tamir ediliyor" göster
- [ ] **OEE (Overall Equipment Effectiveness) temel hesabı** — availability × performance × quality; üretim tesisleri için kritik metrik
- [ ] **Maliyet takibi** — MaintenanceLog'daki `cost` alanından aylık/yıllık bakım harcaması raporu
- [ ] **Yedek parça stok uyarısı** — Sık kullanılan parçaların stok eşiği; "Rulman stoğu 2 kaldı" uyarısı
- [ ] **PDF rapor dışa aktarma** — Aylık bakım özeti; müşteri yöneticisine gönderilebilir
- [ ] **ERP webhook** — MaintenanceLog kaydedilince dışarıya POST; SAP/Logo gibi sistemlerle entegrasyon kapısı
- [ ] **Mobil responsive tam desteği** — Teknisyen tabletten/telefondan iş emrini görebilmeli
- [ ] **Multi-tenant mimari hazırlığı** — Her müşteri (fabrika) verisini izole et; `tenantId` tüm entity'lere ekle

---

## [ ] v6.0.0 — Observability & Artificial Intelligence

**Hedef:** Sistemi kurumsal bir dev haline getirmek.

### Zorunlu Görevler

- [ ] Spring Boot Actuator endpoint'lerini etkinleştir
- [ ] Micrometer + Prometheus bağlantısı kur
- [ ] `prometheus.yml` yapılandır, Spring Boot'u scrape hedefi olarak ekle
- [ ] Grafana kur (Docker Compose'a ekle)
- [ ] Grafana dashboard'larını oluştur:
  - [ ] JVM RAM / CPU kullanımı
  - [ ] Kafka consumer lag
  - [ ] HTTP istek gecikmesi (p95, p99)
  - [ ] Aktif alarm sayısı zaman serisi
- [ ] Spring AI bağımlılığını ekle (OpenAI API)
- [ ] `DiagnosisService` — Belirli ekipmanın bakım loglarını okuyup AI'a analiz ettir
- [ ] `GET /ai/diagnosis/{equipmentId}` endpoint'i — AI tahmini döndür
- [ ] AI prompt'unu yapılandır: geçmiş arızalar, ortalama aralıklar, sensör trendi dahil edilsin

### Önerilen Eklemeler

- [ ] **Anomali tespiti (ML tabanlı)** — OpenAI yerine ya da ek olarak yerel bir isolation forest modeli; internet bağlantısı gerektirmeyen tesisler için kritik
- [ ] **Enerji tüketim tahmini** — Sensör verilerinden ekipmanın anormal enerji çektiğini tespit et; fabrikaya enerji tasarrufu sat
- [ ] **Doğal dil sorgulama** — "Geçen ay en çok arıza hangi makinede oldu?" sorusunu doğal dille sor, AI SQL üretsin
- [ ] **Alert summary AI** — Her sabah 08:00'de otomatik e-posta: "Dün gece 3 CRITICAL alarm vardı, tahminimiz şu"
- [ ] **Kamera entegrasyonu (görsel arıza tespiti)** — IP kamera görüntüsü + Vision API; duman, sızıntı, aşırı ısı tespiti
- [ ] **SLA raporlama modülü** — Müşteriye verilen uptime / response time garantisinin otomatik raporlanması
- [ ] **Managed Grafana abonelik paketi** — Her müşteriye özel Grafana workspace; ek gelir kapısı
- [ ] **Kubernetes Helm chart'ı** — Ürünü cloud-native olarak paketle; Azure / AWS Marketplace'e çıkış hazırlığı

---

## Genel Mimari Öneriler (Tüm Versiyonlar)

- [ ] **API versiyonlama** — `/api/v1/...` pattern'ini şimdiden uygula; ilerideki breaking change'leri yönet
- [ ] **Rate limiting** — Spring Cloud Gateway veya Bucket4j ile API kota koruması
- [ ] **Audit log** — Kim ne zaman ne yaptı? Ayrı bir `AuditLog` tablosu; compliance için zorunlu
- [ ] **Swagger / OpenAPI** — Her endpoint otomatik dokümante edilsin; müşteri entegrasyon ekibi için
- [ ] **GitHub Actions CI/CD** — Her PR'da test + build + Docker image push otomatik çalışsın
- [ ] **Secrets yönetimi** — `.env` dosyası yerine HashiCorp Vault veya AWS Secrets Manager
- [ ] **GDPR / KVKK uyumu** — Kullanıcı ve teknisyen verisini saklamak için yasal uyumluluk katmanı
