package com.velto.config;

import com.velto.dto.RegisterRequest;
import com.velto.model.Booking;
import com.velto.model.BookingStatus;
import com.velto.model.Notification;
import com.velto.model.PaymentStatus;
import com.velto.pattern.strategy.PricingType;
import com.velto.repository.BookingRepository;
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
 * Idempotent DataSeeder that guarantees demo accounts, rides, and system data exist.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final RideRepository rideRepository;
    private final NotificationRepository notificationRepository;
    private final BookingRepository bookingRepository;

    public DataSeeder(UserRepository userRepository,
                      UserService userService,
                      RideRepository rideRepository,
                      NotificationRepository notificationRepository,
                      BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.rideRepository = rideRepository;
        this.notificationRepository = notificationRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Velto DataSeeder: Verifying presence of required demo accounts and rides...");

        // 1. Ensure Admin Exists
        User admin = userRepository.findByEmail("admin@velto.com").orElseGet(() -> {
            RegisterRequest req = new RegisterRequest("Velto Admin", "admin@velto.com", "Admin@123", Role.ADMIN, "9999999999");
            User u = userService.registerUser(req);
            log.info("Seeded Admin: {} ({})", u.getName(), u.getEmail());
            return u;
        });

        // 2. Ensure Drivers Exist
        User driver1 = userRepository.findByEmail("rajesh.driver@velto.com").orElseGet(() -> {
            RegisterRequest req = new RegisterRequest("Rajesh Kumar", "rajesh.driver@velto.com", "Driver@123", Role.DRIVER, "9876543211");
            req.setVehicleNumber("MH-12-AB-1234");
            req.setLicenseNumber("DL-PUN-2022-0098");
            User u = userService.registerUser(req);
            log.info("Seeded Driver 1: {} ({})", u.getName(), u.getEmail());
            return u;
        });

        User driver2 = userRepository.findByEmail("amit.driver@velto.com").orElseGet(() -> {
            RegisterRequest req = new RegisterRequest("Amit Sharma", "amit.driver@velto.com", "Driver@123", Role.DRIVER, "9876543212");
            req.setVehicleNumber("MH-14-XY-5678");
            req.setLicenseNumber("DL-PUN-2021-0045");
            User u = userService.registerUser(req);
            log.info("Seeded Driver 2: {} ({})", u.getName(), u.getEmail());
            return u;
        });

        // 3. Ensure Passengers Exist
        User pass1 = userRepository.findByEmail("priya.passenger@velto.com").orElseGet(() -> {
            RegisterRequest req = new RegisterRequest("Priya Patel", "priya.passenger@velto.com", "Passenger@123", Role.PASSENGER, "9876543210");
            User u = userService.registerUser(req);
            log.info("Seeded Passenger 1: {} ({})", u.getName(), u.getEmail());
            return u;
        });

        User pass2 = userRepository.findByEmail("rohit.passenger@velto.com").orElseGet(() -> {
            RegisterRequest req = new RegisterRequest("Rohit Verma", "rohit.passenger@velto.com", "Passenger@123", Role.PASSENGER, "9123456780");
            User u = userService.registerUser(req);
            log.info("Seeded Passenger 2: {} ({})", u.getName(), u.getEmail());
            return u;
        });

        // 4. Ensure Rides Exist
        if (rideRepository.count() == 0) {
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

            // Seed demo booking for Priya on ride1
            Booking demoBooking = new Booking(
                    ride1.getId(),
                    pass1.getId(),
                    pass1.getName(),
                    2,
                    500.0,
                    PricingType.SHARED,
                    PaymentStatus.PAID,
                    BookingStatus.CONFIRMED
            );
            bookingRepository.save(demoBooking);
            log.info("Seeded demo booking for {} on ride1.", pass1.getName());
        }

        // Ensure demo booking exists even if rides were already seeded
        if (bookingRepository.count() == 0) {
            java.util.List<com.velto.model.Ride> existingRides = rideRepository.findAll();
            if (!existingRides.isEmpty()) {
                userRepository.findByEmail("priya.passenger@velto.com").ifPresent(priya -> {
                    Booking demoBooking = new Booking(
                            existingRides.get(0).getId(),
                            priya.getId(),
                            priya.getName(),
                            2,
                            500.0,
                            PricingType.SHARED,
                            PaymentStatus.PAID,
                            BookingStatus.CONFIRMED
                    );
                    bookingRepository.save(demoBooking);
                    log.info("Seeded fallback demo booking for {}.", priya.getName());
                });
            }
        }

        // 5. Ensure Welcome Notification
        if (notificationRepository.count() == 0) {
            Notification welcomeNotif = new Notification(
                    pass1.getId(),
                    "Welcome to Velto! Enjoy seamless, cost-effective campus & city travel.",
                    "INFO"
            );
            notificationRepository.save(welcomeNotif);
        }

        log.info("==================================================================");
        log.info("? VELTO DEMO DATA READY!");
        log.info("   Admin Account     : admin@velto.com / Admin@123");
        log.info("   Driver Accounts   : rajesh.driver@velto.com / Driver@123");
        log.info("                       amit.driver@velto.com / Driver@123");
        log.info("   Passenger Accounts: priya.passenger@velto.com / Passenger@123");
        log.info("                       rohit.passenger@velto.com / Passenger@123");
        log.info("==================================================================");
    }
}
