package com.velto.config;

import com.velto.dto.RegisterRequest;
import com.velto.model.Notification;
import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.model.Role;
import com.velto.model.User;
import com.velto.repository.NotificationRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import com.velto.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Automatically seeds demo users, drivers, rides, and system data if the database is fresh.
 * Makes the application immediately demo-ready and viva-ready out of the box.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final RideRepository rideRepository;
    private final NotificationRepository notificationRepository;

    public DataSeeder(UserRepository userRepository,
                      UserService userService,
                      RideRepository rideRepository,
                      NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.rideRepository = rideRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Velto DataSeeder: Database already contains user records. Skipping automatic seeding.");
            return;
        }

        log.info("Velto DataSeeder: Fresh database detected! Seeding demo accounts and rides...");

        // 1. Seed Admin
        RegisterRequest adminReq = new RegisterRequest("Velto Admin", "admin@velto.com", "Admin@123", Role.ADMIN, "9999999999");
        User admin = userService.registerUser(adminReq);
        log.info("Seeded Admin: {} ({})", admin.getName(), admin.getEmail());

        // 2. Seed Drivers
        RegisterRequest driver1Req = new RegisterRequest("Rajesh Kumar", "rajesh.driver@velto.com", "Driver@123", Role.DRIVER, "9876543211");
        driver1Req.setVehicleNumber("MH-12-AB-1234");
        driver1Req.setLicenseNumber("DL-PUN-2022-0098");
        User driver1 = userService.registerUser(driver1Req);

        RegisterRequest driver2Req = new RegisterRequest("Amit Sharma", "amit.driver@velto.com", "Driver@123", Role.DRIVER, "9876543212");
        driver2Req.setVehicleNumber("MH-14-XY-5678");
        driver2Req.setLicenseNumber("DL-PUN-2021-0045");
        User driver2 = userService.registerUser(driver2Req);
        log.info("Seeded Drivers: {} and {}", driver1.getName(), driver2.getName());

        // 3. Seed Passengers
        RegisterRequest pass1Req = new RegisterRequest("Priya Patel", "priya.passenger@velto.com", "Passenger@123", Role.PASSENGER, "9876543210");
        User pass1 = userService.registerUser(pass1Req);

        RegisterRequest pass2Req = new RegisterRequest("Rohit Verma", "rohit.passenger@velto.com", "Passenger@123", Role.PASSENGER, "9123456780");
        User pass2 = userService.registerUser(pass2Req);
        log.info("Seeded Passengers: {} and {}", pass1.getName(), pass2.getName());

        // 4. Seed Rides using Ride.Builder Pattern
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);
        String dayAfter = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_DATE);

        Ride ride1 = new Ride.Builder()
                .driverId(driver1.getId())
                .driverName(driver1.getName())
                .pickup("Pune Railway Station")
                .destination("Hinjewadi Phase 1")
                .date(tomorrow)
                .time("09:00 AM")
                .seats(4)
                .price(250.0)
                .status(RideStatus.CONFIRMED)
                .build();
        rideRepository.save(ride1);

        Ride ride2 = new Ride.Builder()
                .driverId(driver2.getId())
                .driverName(driver2.getName())
                .pickup("Kothrud Depot")
                .destination("Viman Nagar IT Park")
                .date(tomorrow)
                .time("10:30 AM")
                .seats(3)
                .price(180.0)
                .status(RideStatus.CONFIRMED)
                .build();
        rideRepository.save(ride2);

        Ride ride3 = new Ride.Builder()
                .driverId(driver1.getId())
                .driverName(driver1.getName())
                .pickup("Baner High Street")
                .destination("Kharadi EON Free Zone")
                .date(dayAfter)
                .time("02:00 PM")
                .seats(4)
                .price(320.0)
                .status(RideStatus.CONFIRMED)
                .build();
        rideRepository.save(ride3);
        log.info("Seeded 3 demo rides successfully.");

        // 5. Seed Welcome Notification
        Notification welcomeNotif = new Notification(
                pass1.getId(),
                "Welcome to Velto! Enjoy seamless, cost-effective campus & city travel.",
                "INFO"
        );
        notificationRepository.save(welcomeNotif);

        log.info("==================================================================");
        log.info("? VELTO DEMO SEED DATA INITIALIZED SUCCESSFULLY!");
        log.info("   Admin Account     : admin@velto.com / Admin@123");
        log.info("   Driver Accounts   : rajesh.driver@velto.com / Driver@123");
        log.info("                       amit.driver@velto.com / Driver@123");
        log.info("   Passenger Accounts: priya.passenger@velto.com / Passenger@123");
        log.info("                       rohit.passenger@velto.com / Passenger@123");
        log.info("==================================================================");
    }
}
