package com.carrack.track.config;

import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Invoice;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.FuelType;
import com.carrack.track.enums.PaymentMethod;
import com.carrack.track.enums.PaymentStatus;
import com.carrack.track.enums.ReminderStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.enums.ServiceStatus;
import com.carrack.track.enums.VehicleStatus;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.repository.InvoiceRepository;
import com.carrack.track.repository.MaintenanceReminderRepository;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
public class LocalDataInitializer {

    @Bean
    CommandLineRunner seedLocalData(AppUserRepository userRepository,
                                    VehicleRepository vehicleRepository,
                                    ServiceRecordRepository serviceRecordRepository,
                                    MaintenanceReminderRepository maintenanceReminderRepository,
                                    InvoiceRepository invoiceRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            AppUser admin = userRepository.findByEmailIgnoreCase("admin@cartrack.local")
                    .orElseGet(() -> {
                        AppUser user = new AppUser();
                        user.setFullName("System Admin");
                        user.setEmail("admin@cartrack.local");
                        user.setPhone("0770000000");
                        user.setPasswordHash(passwordEncoder.encode("Admin@1234"));
                        user.setRole(Role.ADMIN);
                        user.setStatus(AccountStatus.ACTIVE);
                        return userRepository.save(user);
                    });

            AppUser client = userRepository.findByEmailIgnoreCase("client1@cartrack.local")
                    .orElseGet(() -> {
                        AppUser user = new AppUser();
                        user.setFullName("Nadeesha Perera");
                        user.setEmail("client1@cartrack.local");
                        user.setPhone("0712345678");
                        user.setPasswordHash(passwordEncoder.encode("Client@1234"));
                        user.setRole(Role.CLIENT);
                        user.setStatus(AccountStatus.ACTIVE);
                        return userRepository.save(user);
                    });

            AppUser clientTwo = userRepository.findByEmailIgnoreCase("client2@cartrack.local")
                    .orElseGet(() -> {
                        AppUser user = new AppUser();
                        user.setFullName("Kasun Silva");
                        user.setEmail("client2@cartrack.local");
                        user.setPhone("0723456789");
                        user.setPasswordHash(passwordEncoder.encode("Client@1234"));
                        user.setRole(Role.CLIENT);
                        user.setStatus(AccountStatus.ACTIVE);
                        return userRepository.save(user);
                    });

            Vehicle vehicleOne = vehicleRepository.findByVehicleNumberIgnoreCase("WP CAB-4582")
                    .orElseGet(() -> saveVehicle(vehicleRepository, client, "WP CAB-4582", "Toyota", "Aqua", "Hatchback", FuelType.PETROL, 2018, 48200));

            Vehicle vehicleTwo = vehicleRepository.findByVehicleNumberIgnoreCase("CP KJ-9090")
                    .orElseGet(() -> saveVehicle(vehicleRepository, clientTwo, "CP KJ-9090", "Suzuki", "Wagon R", "Hatchback", FuelType.PETROL, 2019, 61500));

            ServiceRecord recordOne = serviceRecordRepository.findAll().stream()
                    .filter(record -> "WP CAB-4582".equalsIgnoreCase(record.getVehicle().getVehicleNumber())
                            && "Oil Change".equalsIgnoreCase(record.getServiceType()))
                    .findFirst()
                    .orElseGet(() -> saveServiceRecord(serviceRecordRepository, vehicleOne, "Oil Change",
                            LocalDate.now().minusDays(14), 48000, "City Service Center", new BigDecimal("7500.00")));

            ServiceRecord recordTwo = serviceRecordRepository.findAll().stream()
                    .filter(record -> "CP KJ-9090".equalsIgnoreCase(record.getVehicle().getVehicleNumber())
                            && "Brake Service".equalsIgnoreCase(record.getServiceType()))
                    .findFirst()
                    .orElseGet(() -> saveServiceRecord(serviceRecordRepository, vehicleTwo, "Brake Service",
                            LocalDate.now().minusDays(7), 61000, "Metro Auto Care", new BigDecimal("12500.00")));

            serviceRecordRepository.findAll().stream()
                    .filter(record -> record.getServiceStatus() == null)
                    .forEach(record -> {
                        record.setServiceStatus(ServiceStatus.COMPLETED);
                        serviceRecordRepository.save(record);
                    });

            if (maintenanceReminderRepository.count() == 0) {
                MaintenanceReminder reminder = new MaintenanceReminder();
                reminder.setVehicleNumber(vehicleOne.getVehicleNumber());
                reminder.setTitle("Next oil change");
                reminder.setReminderDate(LocalDate.now().plusMonths(2));
                reminder.setLastServiceDate(recordOne.getServiceDate());
                reminder.setMileageInterval(5000);
                reminder.setStatus(ReminderStatus.UPCOMING);
                reminder.setNotes("Call customer before due date.");
                maintenanceReminderRepository.save(reminder);
            }

            if (!invoiceRepository.existsByServiceRecordId(recordOne.getId())) {
                Invoice invoice = new Invoice();
                invoice.setServiceRecord(recordOne);
                invoice.setInvoiceNumber("INV-1001");
                invoice.setInvoiceDate(recordOne.getServiceDate());
                invoice.setTotalAmount(new BigDecimal("7500.00"));
                invoice.setPaymentStatus(PaymentStatus.PAID);
                invoice.setPaymentMethod(PaymentMethod.CASH);
                invoice.setNotes("Paid at counter.");
                invoiceRepository.save(invoice);
            }

            if (!invoiceRepository.existsByServiceRecordId(recordTwo.getId())) {
                Invoice invoice = new Invoice();
                invoice.setServiceRecord(recordTwo);
                invoice.setInvoiceNumber("INV-1002");
                invoice.setInvoiceDate(recordTwo.getServiceDate());
                invoice.setTotalAmount(new BigDecimal("12500.00"));
                invoice.setPaymentStatus(PaymentStatus.PENDING);
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                invoice.setNotes("Waiting for bank transfer.");
                invoiceRepository.save(invoice);
            }
        };
    }

    private Vehicle saveVehicle(VehicleRepository vehicleRepository,
                                AppUser owner,
                                String number,
                                String brand,
                                String model,
                                String type,
                                FuelType fuelType,
                                Integer year,
                                Integer mileage) {
        Vehicle vehicle = new Vehicle();
        vehicle.setOwner(owner);
        vehicle.setVehicleNumber(number);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setType(type);
        vehicle.setFuelType(fuelType);
        vehicle.setYear(year);
        vehicle.setMileage(mileage);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        return vehicleRepository.save(vehicle);
    }

    private ServiceRecord saveServiceRecord(ServiceRecordRepository serviceRecordRepository,
                                            Vehicle vehicle,
                                            String serviceType,
                                            LocalDate serviceDate,
                                            Integer mileage,
                                            String serviceCenter,
                                            BigDecimal cost) {
        ServiceRecord record = new ServiceRecord();
        record.setVehicle(vehicle);
        record.setServiceType(serviceType);
        record.setServiceDate(serviceDate);
        record.setMileageAtService(mileage);
        record.setServiceCenter(serviceCenter);
        record.setServiceStatus(ServiceStatus.COMPLETED);
        record.setCost(cost);
        record.setNotes("Seed data for local verification.");
        return serviceRecordRepository.save(record);
    }
}
