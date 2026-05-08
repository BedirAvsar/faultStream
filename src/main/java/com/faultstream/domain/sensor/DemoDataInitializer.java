package com.faultstream.domain.sensor;
import com.faultstream.config.DemoDataProperties;
import com.faultstream.config.SimulatorProperties;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.equipment.EquipmentType;
import com.faultstream.domain.sensor.stream.SensorSimulationService;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import com.faultstream.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {
    private final DemoDataProperties demoDataProperties;
    private final SimulatorProperties simulatorProperties;
    private final EquipmentRepository equipmentRepository;
    private final SensorRepository sensorRepository;
    private final SensorSimulationService sensorSimulationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!demoDataProperties.isSeedDataEnabled()) {
            return;
        }
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (equipmentRepository.count() == 0) {
            seedEquipmentAndSensors();
        }
        if (simulatorProperties.isEnabled() && sensorRepository.count() > 0) {
            sensorSimulationService.seedInitialHistory(4);
        }
    }

    private void seedEquipmentAndSensors() {
        Equipment trb = equipmentRepository.save(createEquipment("TRB-01", EquipmentType.COMPRESSOR, "Turbine Hall"));
        Equipment cnv = equipmentRepository.save(createEquipment("CNV-A", EquipmentType.PUMP, "Line A"));
        Equipment pmp = equipmentRepository.save(createEquipment("PMP-B", EquipmentType.PUMP, "Utility Room"));
        Equipment hub = equipmentRepository.save(createEquipment("HUB-X", EquipmentType.AHU, "Control Floor"));
        Equipment gen = equipmentRepository.save(createEquipment("GEN-04", EquipmentType.CHILLER, "Energy Center"));

        List<Sensor> sensors = List.of(
                createSensor(trb, "Bearing Temp", SensorType.TEMPERATURE, "C", 55.0, 92.0),
                createSensor(trb, "Rotor Vibration", SensorType.VIBRATION, "mm/s", 0.5, 4.2),
                createSensor(cnv, "Hydraulic Pressure", SensorType.PRESSURE, "bar", 2.5, 7.5),
                createSensor(cnv, "Motor Current", SensorType.CURRENT, "A", 10.0, 50.0),
                createSensor(pmp, "Seal Temp", SensorType.TEMPERATURE, "C", 48.0, 88.0),
                createSensor(pmp, "Line Humidity", SensorType.HUMIDITY, "%", 25.0, 60.0),
                createSensor(hub, "Network Vibration", SensorType.VIBRATION, "mm/s", 0.3, 3.4),
                createSensor(hub, "Cooling Current", SensorType.CURRENT, "A", 8.0, 42.0),
                createSensor(gen, "Chamber Pressure", SensorType.PRESSURE, "bar", 2.0, 6.8),
                createSensor(gen, "Ambient Humidity", SensorType.HUMIDITY, "%", 30.0, 65.0)
        );
        sensorRepository.saveAll(sensors);
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
                createUser("admin@faultstream.local", "Faultstream123!", "FaultStream Admin", UserRole.ADMIN, "Operations"),
                createUser("engineer@faultstream.local", "Faultstream123!", "Demo Engineer", UserRole.ENGINEER, "Reliability"),
                createUser("technician@faultstream.local", "Faultstream123!", "Demo Technician", UserRole.TECHNICIAN, "Maintenance")
        ));
    }

    private Equipment createEquipment(String name, EquipmentType type, String location) {
        return Equipment.builder()
                .name(name)
                .type(type)
                .location(location)
                .build();
    }

    private Sensor createSensor(
            Equipment equipment,
            String name,
            SensorType type,
            String unit,
            Double thresholdMin,
            Double thresholdMax) {
        return Sensor.builder()
                .equipment(equipment)
                .name(name)
                .type(type)
                .unit(unit)
                .thresholdMin(thresholdMin)
                .thresholdMax(thresholdMax)
                .build();
    }

    private User createUser(String email, String rawPassword, String fullName, UserRole role, String department) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .role(role)
                .department(department)
                .build();
    }
}
