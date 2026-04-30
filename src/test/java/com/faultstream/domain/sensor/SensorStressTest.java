package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorDataEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Disabled("Manuel stres testi için tasarlandı ve context hatasını önlemek için sınıfa taşındı")
class SensorStressTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Saniyede 10.000 veri akışı stres testi.
     * Bu test normal build sırasında çalışmaması için @Disabled edilmiştir.
     * İhtiyaç halinde manuel çalıştırılabilir.
     */
    @Test
    @Disabled("Manuel stres testi için tasarlandı")
    void stressTestKafkaProducer_10k_messages() throws InterruptedException {
        int messageCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(messageCount);
        StopWatch stopWatch = new StopWatch();

        UUID testSensorId = UUID.randomUUID();
        UUID testEqpId = UUID.randomUUID();

        stopWatch.start();
        for (int i = 0; i < messageCount; i++) {
            executorService.submit(() -> {
                try {
                    SensorDataEvent event = new SensorDataEvent(
                            testSensorId,
                            testEqpId,
                            SensorType.VIBRATION,
                            Math.random() * 100,
                            "mm/s",
                            LocalDateTime.now()
                    );
                    kafkaTemplate.send("sensor-data", testSensorId.toString(), event);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        stopWatch.stop();

        System.out.println("-------------------------------------------------");
        System.out.println("STRESS TEST SONUCU:");
        System.out.println(messageCount + " mesaj gönderildi.");
        System.out.println("Geçen süre: " + stopWatch.getTotalTimeMillis() + " ms");
        System.out.println("Mesaj/Saniye (Throughput): " + (messageCount / stopWatch.getTotalTimeSeconds()));
        System.out.println("-------------------------------------------------");
    }
}
