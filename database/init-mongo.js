// VELTO - MongoDB Initialization Script
// Database: velto

use velto;

// 1. Create collections
db.createCollection("users");
db.createCollection("rides");
db.createCollection("bookings");
db.createCollection("payments");
db.createCollection("notifications");
db.createCollection("system_health");

// 2. Create Indexes
// Users: unique email
db.users.createIndex({ "email": 1 }, { unique: true });

// Rides: search index for pickup, destination, date, and status
db.rides.createIndex({ "pickup": 1, "destination": 1, "status": 1 });
db.rides.createIndex({ "driverId": 1 });

// Bookings: index by passengerId and rideId
db.bookings.createIndex({ "passengerId": 1 });
db.bookings.createIndex({ "rideId": 1 });

// Notifications: index by userId and read status
db.notifications.createIndex({ "userId": 1, "read": 1 });

print("✅ VELTO Database and collections initialized successfully!");
