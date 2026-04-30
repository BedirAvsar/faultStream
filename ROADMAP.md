# FaultStream — Development Roadmap (Master Roadmap)

> Mandatory tasks, recommended additions, and architectural notes for each version are tracked through this file.

---

## [x] v1.0.0 — The Foundation (Completed)
- [x] Setting up Docker Compose infrastructure (PostgreSQL, Kafka, Zookeeper, Redis).
- [x] Spring Security and JWT infrastructure (Role-Based Access Control).
- [x] User Domain: Authentication, Registration, Login operations.
- [x] Equipment Domain: Equipment inventory management, service logic, and Mockito/MockMvc unit tests.

## [x] v2.0.0 — Clean Code & Operations Dashboard (Completed)
- [x] Cleaning all technical comments in the project with a custom Python script (Clean Code).
- [x] Setting up an independent frontend application (faultstream-dashboard) with Next.js (App Router).
- [x] Creating a minimalist "Dark Mode" interface inspired by Cyber Security / NOC.
- [x] Integrating a simulated (Mock) real-time dashboard using Lucide icons and Recharts.

---

## [x] v3.0.0 — Sensor Data & Event Streaming

**Goal:** Start listening to the machines' pulse and stream the data.

- [x] **Sensor Domain:** Sensor and Sensor Readings (SensorReading) Entity, Repo, Service modules.
- [x] **Flyway:** Writing V3 and V4 database migration SQLs.
- [x] **Kafka Producer:** Generating sensor values in the background with `SensorSimulatorScheduler` and pushing to Kafka (sensor-readings).
- [x] **Kafka Consumer:** Writing the main `SensorDataConsumer` service that reads sensor data from Kafka.

### Mandatory Tasks

- [x] Create `Sensor` Entity (id, name, equipmentId, type, unit, location)
- [x] Create `SensorReading` Entity (id, sensorId, value, timestamp, status)
- [x] Write `SensorRepository` and `SensorReadingRepository`
- [x] `SensorService` — CRUD + query latest reading
- [x] Write Flyway `V3__create_sensor_table.sql`
- [x] Write Flyway `V4__create_sensor_reading_table.sql`
- [x] `SensorSimulatorScheduler` — Generate random values in the background with @Scheduled, push to Kafka
- [x] Complete Kafka topic `sensor-readings` configuration
- [x] `SensorDataConsumer` — Read from Kafka, save as SensorReading
- [x] `SensorController` — REST endpoints (GET /sensors, GET /sensors/{id}/readings)
- [x] Unit tests: Mockito tests for SensorService

### Recommended Additions

- [x] **Sensor type enum** (TEMPERATURE, VIBRATION, HUMIDITY, PRESSURE, CURRENT) — eases future threshold rules
- [ ] **Data quality filter** — Drop NaN, null, or non-physical values (e.g., temperature -999) inside the Consumer, add logging
- [ ] **MQTT adapter** — Add optional MQTT gateway support instead of `SensorSimulatorScheduler`; prepares connection with real hardware
- [ ] **TimescaleDB migration research** — Time series queries slow down in PostgreSQL; start with `CREATE TABLE ... PARTITION BY RANGE (timestamp)`
- [x] **Last N readings sliding window endpoint** — Like `GET /sensors/{id}/readings?last=100`; critical for the dashboard
- [ ] **Sensor health status** (ONLINE / OFFLINE / DEGRADED) — Mark OFFLINE if no reading is received in the last X seconds

---

## [x] v4.0.0 — Autonomous Alerting & Work Orders

**Goal:** Add a "brain" to the system. A structure that detects threats and initiates intervention without human involvement.

- [x] **Requirements:** Coding Alert and WorkOrder Domain modules (Entity, Service, etc.).
- [x] **Flyway:** Creating V5 and V6 database tables.
- [x] **Autonomous Detection:** Adding "Threshold" checks into the Kafka Consumer.
- [x] **Automation:** Generating an automatic ALERT if the value is exceeded, assigning a Work Order directly to the TECHNICIAN independent of the engineer if the level is CRITICAL.
- [x] **Performance:** Keeping instant alert statuses and Dashboard data in memory using Redis Cache (80% database load reduction).

### Mandatory Tasks

- [x] Create `Alert` Entity (id, sensorId, equipmentId, level, message, triggeredAt, resolvedAt, status)
- [x] Create `WorkOrder` Entity (id, alertId, assignedTechnicianId, status, createdAt, completedAt, notes)
- [x] Write `AlertRepository` and `WorkOrderRepository`
- [x] `AlertService` — alert creation, resolution, status update
- [x] `WorkOrderService` — work order creation, assignment, completion
- [x] Write Flyway `V5__create_alert_table.sql`
- [x] Write Flyway `V6__create_work_order_table.sql`
- [x] Add threshold check logic into `SensorDataConsumer`
- [x] Auto-create `Alert` when threshold is exceeded (WARNING / CRITICAL level distinction)
- [x] Directly assign `WorkOrder` to technician at CRITICAL level (no engineer approval needed)
- [x] Redis Cache — cache active alert statuses and dashboard summaries
- [x] Determine Redis TTL strategy (5 min for alerts, 30 sec for dashboard recommended)
- [x] `AlertController` and `WorkOrderController` REST endpoints

### Recommended Additions

- [ ] **Move threshold rules to DB** — `ThresholdRule` entity instead of hardcoded threshold values; allowing different rules per sensor
- [ ] **Alert snooze mechanism** — "Snooze this alert for 2 hours" rule; reduces noise outside working hours
- [ ] **Alert escalation chain** — Escalate to engineer if technician doesn't respond in 30 mins; escalate to manager if engineer doesn't respond
- [ ] **SMS / Email notification** — Spring Mail + Twilio SMS integration; instant notification on CRITICAL alerts
- [ ] **Shift schedule** — Which technician is on which shift? Assign work orders to the active shift
- [ ] **Alert storm protection** — Generate a single alert if 50 alerts come from the same sensor in 1 minute; add debounce to Kafka consumer
- [ ] **WorkOrder mobile view** — Add a simple mobile-first work order screen for technicians to the Dashboard

---

## [ ] v5.0.0 — Maintenance Tracking & Full API Integration

**Goal:** Connect the technologies. Real communication between Next.js and Spring Boot and keeping a maintenance history.

- [ ] **Maintenance Domain:** Coding the Maintenance Logs (MaintenanceLog) structure and V7 SQL script.
- [ ] **Dashboard API:** Preparing Redis-backed `DashboardController` endpoints for the statistics needed by the frontend.
- [ ] **Frontend Live Integration:** Removing the "Mock" loop in the Next.js Dashboard and connecting to real Spring Boot data via SSE or REST.
- [ ] **Testcontainers:** Canceling local H2 database tests and writing end-to-end Integration Tests on real Docker-Postgres containers.

### Mandatory Tasks

- [ ] Create `MaintenanceLog` Entity (id, workOrderId, equipmentId, technicianId, action, duration, parts, cost, createdAt)
- [ ] Write `MaintenanceLogRepository` and `MaintenanceLogService`
- [ ] Write Flyway `V7__create_maintenance_log_table.sql`
- [ ] `DashboardController` — statistic endpoints needed by the frontend
  - [ ] `GET /dashboard/summary` — total equipment, active alerts, open work orders count
  - [ ] `GET /dashboard/sensor-stream` — SSE endpoint (Server-Sent Events)
  - [ ] `GET /dashboard/alerts/recent` — last 24 hours alerts
  - [ ] `GET /dashboard/equipment/health` — equipment-based health score
- [ ] Remove mock loop in frontend, connect to real data via SSE
- [ ] Write Next.js `useEffect` + `EventSource` live update hook
- [ ] Remove H2 tests, add Testcontainers configuration
- [ ] Write end-to-end integration tests with PostgreSQL container

### Recommended Additions

- [ ] **MTTR (Mean Time To Repair) calculation** — Automatic from WorkOrder closing times; show "This equipment is repaired in 2.3 hours on average"
- [ ] **Basic OEE (Overall Equipment Effectiveness) calculation** — availability × performance × quality; critical metric for manufacturing plants
- [ ] **Cost tracking** — Monthly/yearly maintenance expense report from the `cost` field in MaintenanceLog
- [ ] **Spare part stock warning** — Stock threshold for frequently used parts; "Only 2 bearings left" warning
- [ ] **PDF report export** — Monthly maintenance summary; can be sent to client manager
- [ ] **ERP webhook** — Outbound POST when MaintenanceLog is saved; integration gateway for systems like SAP/Logo
- [ ] **Full mobile responsive support** — Technician should be able to view the work order from tablet/phone
- [ ] **Multi-tenant architecture preparation** — Isolate each customer's (factory) data; add `tenantId` to all entities

---

## [ ] v6.0.0 — Observability & Artificial Intelligence

**Goal:** Turn the system into an enterprise giant.

- [ ] **Metric Monitoring:** Spring Boot Actuator and Prometheus integration.
- [ ] **Grafana:** Dumping RAM, CPU, Kafka Lag metrics onto visual Grafana panels.
- [ ] **AI Integration:** Adding Spring AI (OpenAI API) to create a "Smart Diagnosis" module (E.g., AI reading past maintenance logs and predicting *"Possible bearing failure in Turbine-01 motor"*).

### Mandatory Tasks

- [ ] Enable Spring Boot Actuator endpoints
- [ ] Establish Micrometer + Prometheus connection
- [ ] Configure `prometheus.yml`, add Spring Boot as scrape target
- [ ] Install Grafana (Add to Docker Compose)
- [ ] Create Grafana dashboards:
  - [ ] JVM RAM / CPU usage
  - [ ] Kafka consumer lag
  - [ ] HTTP request latency (p95, p99)
  - [ ] Active alerts count time series
- [ ] Add Spring AI dependency (OpenAI API)
- [ ] `DiagnosisService` — Read maintenance logs of specific equipment and have AI analyze them
- [ ] `GET /ai/diagnosis/{equipmentId}` endpoint — Return AI prediction
- [ ] Configure AI prompt: include past faults, average intervals, sensor trends

### Recommended Additions

- [ ] **Anomaly detection (ML-based)** — A local isolation forest model instead of or in addition to OpenAI; critical for facilities without internet connection
- [ ] **Energy consumption prediction** — Detect abnormal energy draw by equipment from sensor data; sell energy savings to the factory
- [ ] **Natural language query** — Ask "Which machine had the most faults last month?" in natural language, AI generates SQL
- [ ] **Alert summary AI** — Automated email at 08:00 every morning: "There were 3 CRITICAL alerts last night, here is our prediction"
- [ ] **Camera integration (visual fault detection)** — IP camera feed + Vision API; smoke, leak, excessive heat detection
- [ ] **SLA reporting module** — Automated reporting of the uptime / response time guarantee given to the customer
- [ ] **Managed Grafana subscription package** — Dedicated Grafana workspace for each customer; additional revenue stream
- [ ] **Kubernetes Helm chart** — Package the product as cloud-native; preparation for Azure / AWS Marketplace launch

---

## General Architectural Recommendations (All Versions)

- [ ] **API versioning** — Apply `/api/v1/...` pattern early; manage future breaking changes
- [ ] **Rate limiting** — API quota protection with Spring Cloud Gateway or Bucket4j
- [ ] **Audit log** — Who did what and when? A separate `AuditLog` table; mandatory for compliance
- [ ] **Swagger / OpenAPI** — Auto-document each endpoint; for customer integration teams
- [ ] **GitHub Actions CI/CD** — Auto-run test + build + Docker image push on every PR
- [ ] **Secrets management** — HashiCorp Vault or AWS Secrets Manager instead of `.env` file
- [ ] **GDPR / KVKK compliance** — Legal compliance layer to store user and technician data
