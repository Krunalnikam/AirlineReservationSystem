// Import Scanner class to read user input from keyboard
import java.util.Scanner;

/* =========================================================
                        FLIGHT CLASS
   Represents an airplane flight with all its details
   ========================================================= */
class Flight {

    // Flight information variables
    String flightNo;        // Unique identifier like "AI101"
    String source;          // Departure city
    String destination;     // Arrival city
    int type;               // 1 = Domestic, 2 = International
    int time;               // Departure time in HHMM format (like 930 for 9:30 AM)
    int totalSeats;         // Total seats available on the flight
    int availableSeats;     // Currently available seats
    double fare;            // Ticket price in rupees
    String[] seatType;      // Array to track seat status: "Free" or "Booked"

    /* -------- NESTED CLASS: SEAT -------- */
    // Represents a single seat on the flight
    class Seat {
        int seatNo;         // Seat number (1, 2, 3...)
        int seatType;       // 1.Window 2.Middle 3.Aisle
        
        // Constructor to create a new seat
        Seat(int seatNo, int seatType) {
            this.seatNo = seatNo;
            this.seatType = seatType;
        }
    }

    /* -------- FLIGHT CONSTRUCTOR -------- */
    // Creates a new flight with initial values
    Flight(String flightNo, String source, String destination,
           int type, int time, int seats, double fare) {
        this.flightNo = flightNo;
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.time = time;
        this.totalSeats = seats;
        this.availableSeats = seats;  // Initially all seats are available
        this.fare = fare;
        
        // Create array for seat tracking (index 1 to seats)
        seatType = new String[seats + 1];
        
        // Initialize all seats as "Free"
        for (int i = 1; i <= seats; i++) {
            seatType[i] = "Free";
        }
    }

    /* -------- TIME FORMAT CONVERTER -------- */
    // Converts 24-hour time (HHMM) to 12-hour format with AM/PM
    String getFormattedTime() {
        int hour = time / 100;     // Extract hours (930/100 = 9)
        int min = time % 100;      // Extract minutes (930%100 = 30)
        
        // Determine AM or PM
        String period = (hour >= 12) ? "PM" : "AM";
        
        // Convert 24-hour to 12-hour format
        if (hour > 12) hour -= 12;  // 13 becomes 1, 14 becomes 2, etc.
        if (hour == 0) hour = 12;   // 00:00 becomes 12:00 AM
        
        // Return formatted time like "09:30 AM"
        return String.format("%02d:%02d %s", hour, min, period);
    }

    /* -------- DISPLAY FLIGHT DETAILS -------- */
    // Shows all information about this flight
    void displayFlight() {
        System.out.println("--------------------------------");
        System.out.println("Flight No : " + flightNo);
        System.out.println("Route    : " + source + " -> " + destination);
        System.out.println("Type     : " + (type == 1 ? "Domestic" : "International"));
        System.out.println("Time     : " + getFormattedTime());
        System.out.println("Fare     : ₹" + fare);
        System.out.println("Seats    : " + availableSeats + "/" + totalSeats);
    }

    /* -------- ASSIGN SEAT TO PASSENGER -------- */
    // Finds and assigns a seat based on passenger preference
    Seat assignSeat(int pref) {
        // Try to find preferred seat type first (1=Window, 2=Middle, 3=Aisle)
        for (int i = 1; i <= totalSeats; i++) {
            // Check if seat is free and matches preference
            if (seatType[i].equals("Free") && (pref == 0 || pref == (i % 3 == 0 ? 3 : i % 3))) {
                seatType[i] = "Booked";  // Mark as booked
                availableSeats--;        // Reduce available seats count
                // Create and return Seat object
                return new Seat(i, pref == 0 ? i % 3 == 0 ? 3 : i % 3 : pref);
            }
        }
        
        // If preferred seat not available, find any free seat
        for (int i = 1; i <= totalSeats; i++) {
            if (seatType[i].equals("Free")) {
                seatType[i] = "Booked";
                availableSeats--;
                return new Seat(i, i % 3 == 0 ? 3 : i % 3);
            }
        }
        
        return null; // No seats available
    }

    /* -------- CANCEL A SEAT -------- */
    // Frees up a previously booked seat
    void cancelSeat(int seatNo) {
        // Check if seat number is valid and currently booked
        if (seatNo >= 1 && seatNo <= totalSeats && seatType[seatNo].equals("Booked")) {
            seatType[seatNo] = "Free";  // Mark as free
            availableSeats++;           // Increase available seats count
        }
    }
}

/* =========================================================
                        PASSENGER CLASS
   Represents a passenger with booking information
   ========================================================= */
class Passenger {
    String bookingId;    // Unique booking ID
    String name;         // Passenger name
    int age;             // Passenger age
    String flightNo;     // Flight number booked
    Flight.Seat seat;    // Seat assigned to passenger
    double paidAmount;   // Amount paid for ticket

    // Constructor to create a new passenger
    Passenger(String bookingId, String name, int age,
              String flightNo, Flight.Seat seat, double paidAmount) {
        this.bookingId = bookingId;
        this.name = name;
        this.age = age;
        this.flightNo = flightNo;
        this.seat = seat;
        this.paidAmount = paidAmount;
    }

    /* -------- DISPLAY PASSENGER DETAILS -------- */
    void displayPassenger() {
        System.out.println("--------------------------------");
        System.out.println("Booking ID : " + bookingId);
        System.out.println("Name       : " + name);
        System.out.println("Age        : " + age);
        System.out.println("Flight No  : " + flightNo);
        System.out.println("Seat No    : " + seat.seatNo);
        System.out.println("Amount     : ₹" + paidAmount);
    }
}

/* =========================================================
                        SENIOR CITIZEN CLASS
   Special type of passenger with potential discounts
   (Inherits from Passenger class)
   ========================================================= */
class SeniorCitizen extends Passenger {
    // Constructor - same as Passenger but identifies as Senior Citizen
    SeniorCitizen(String bookingId, String name, int age,
                  String flightNo, Flight.Seat seat, double paidAmount) {
        // Call parent class (Passenger) constructor
        super(bookingId, name, age, flightNo, seat, paidAmount);
    }
}

/* =========================================================
              MAIN SYSTEM CLASS: AIRLINE RESERVATION SYSTEM
   This is where the program starts and runs
   ========================================================= */
public class AirlinereservationSystem {

    // Scanner object to read user input
    static Scanner sc = new Scanner(System.in);

    // Arrays to store flight and passenger data
    static Flight[] flights = new Flight[30];      // Can store up to 30 flights
    static Passenger[] passengers = new Passenger[100]; // Can store up to 100 passengers

    // Counters to track how many flights/passengers are currently stored
    static int flightCount = 0;
    static int passengerCount = 0;

    /* -------- MAIN METHOD - PROGRAM STARTING POINT -------- */
    public static void main(String[] args) {
        // Add some sample flights to the system
        flights[flightCount++] = new Flight("AI101", "Delhi", "Mumbai", 1, 930, 10, 4500);
        flights[flightCount++] = new Flight("AI102", "Delhi", "Dubai", 2, 1130, 8, 25000);
        flights[flightCount++] = new Flight("AI103", "Mumbai", "London", 2, 2130, 6, 52000);
        flights[flightCount++] = new Flight("AI104", "Surat", "Ahmedabad", 1, 700, 12, 2800);

        // Start the main menu
        mainMenu();
    }

    /* ===================== MAIN MENU ===================== */
    // Shows the first menu users see when program starts
    static void mainMenu() {
        int choice;  // Variable to store user's menu choice
        
        do {
            // Display menu options
            System.out.println("\n=== AIRLINE RESERVATION SYSTEM ===");
            System.out.println("1. Admin Login");
            System.out.println("2. User");
            System.out.println("3. Exit");
            System.out.println("4. Enter your Choice");
            
            // Read user's choice
            choice = sc.nextInt();

            // Based on choice, call appropriate function
            if (choice == 1) adminLogin();   // Go to admin section
            else if (choice == 2) userMenu(); // Go to user section
            
        } while (choice != 3);  // Continue until user chooses Exit (3)
    }

    /* ===================== USER MENU ===================== */
    // Menu for regular users (passengers)
    static void userMenu() {
        int ch;  // Variable for user's choice
        
        do {
            // Display user options
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. View Domestic Flights");
            System.out.println("2. View International Flights");
            System.out.println("3. View Flights (Sorted by Time)");
            System.out.println("4. Book Ticket");
            System.out.println("5. Cancel Ticket");
            System.out.println("6. View My Ticket");
            System.out.println("7. Back");
            
            ch = sc.nextInt();  // Read user choice

            // Call appropriate function based on choice
            if (ch == 1) viewFlightsByType(1);      // Domestic flights
            else if (ch == 2) viewFlightsByType(2); // International flights
            else if (ch == 3) viewFlightsSorted();  // All flights sorted by time
            else if (ch == 4) bookTicket();         // Book a new ticket
            else if (ch == 5) cancelTicket();       // Cancel existing ticket
            else if (ch == 6) viewMyTicket();       // View ticket details

        } while (ch != 7);  // Continue until user chooses Back (7)
    }

    /* ===================== VIEW ALL FLIGHTS ===================== */
    // Displays all available flights
    static void viewFlights() {
        // Loop through all flights and display each one
        for (int i = 0; i < flightCount; i++) {
            flights[i].displayFlight();
        }
    }

    /* ===================== VIEW FLIGHTS BY TYPE ===================== */
    // Shows only domestic (type=1) or international (type=2) flights
    static void viewFlightsByType(int type) {
        boolean found = false;  // Flag to check if any flights found
        
        // Loop through all flights
        for (int i = 0; i < flightCount; i++) {
            // Check if flight type matches requested type
            if (flights[i].type == type) {
                flights[i].displayFlight();  // Display the flight
                found = true;                // Mark as found
            }
        }
        
        // If no flights found, show message
        if (!found) {
            System.out.println("No flights of this type available!");
        }
    }

    /* ===================== VIEW SORTED FLIGHTS ===================== */
    // Shows all flights sorted by departure time (earliest first)
    static void viewFlightsSorted() {
        // Bubble sort algorithm to sort flights by time
        for (int i = 0; i < flightCount - 1; i++) {
            for (int j = i + 1; j < flightCount; j++) {
                // If current flight time is later than next flight, swap them
                if (flights[i].time > flights[j].time) {
                    Flight temp = flights[i];  // Temporary variable for swapping
                    flights[i] = flights[j];
                    flights[j] = temp;
                }
            }
        }
        // Display sorted flights
        viewFlights();
    }

    /* ===================== BOOK A NEW TICKET ===================== */
    static void bookTicket() {
        sc.nextLine();  // Clear the input buffer
        
        // Get passenger information
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();  // Clear buffer after reading integer
        
        System.out.print("Enter Flight Number: ");
        String fno = sc.nextLine();

        // Find the selected flight
        Flight selectedFlight = null;  // Start with no flight selected
        
        // Search through all flights
        for (int i = 0; i < flightCount; i++) {
            if (flights[i].flightNo.equalsIgnoreCase(fno)) {
                selectedFlight = flights[i];  // Found the flight
                break;  // Stop searching
            }
        }

        // If flight not found, show error and return
        if (selectedFlight == null) {
            System.out.println("Flight not found!");
            return;
        }

        // Show flight details to user
        System.out.println("\n--- FLIGHT DETAILS ---");
        selectedFlight.displayFlight();

        // Check if seats are available
        if (selectedFlight.availableSeats <= 0) {
            System.out.println("Sorry! No seats available on this flight.");
            return;
        }

        // Confirm booking with user
        System.out.print("Do you want to proceed with booking this flight? (Y/N): ");
        char confirm = sc.nextLine().toUpperCase().charAt(0);
        if (confirm != 'Y') {
            System.out.println("Booking cancelled by user.");
            return;
        }

        // Get seat preference from user
        System.out.print("Choose Seat Type (1.Window 2.Middle 3.Aisle): ");
        int pref = sc.nextInt();

        // Assign a seat (try for preference, otherwise any available)
        Flight.Seat seat = selectedFlight.assignSeat(pref);

        // Get payment method
        System.out.print("Payment Method (1.Cash 2.UPI): ");
        int pay = sc.nextInt();

        // Calculate final fare (with discount for senior citizens)
        double finalFare = selectedFlight.fare;

        // Apply 20% discount for senior citizens (age 60+)
        if (age >= 60) {
            System.out.println("Senior Citizen Discount Applied: 20%");
            finalFare *= 0.8;  // Reduce fare by 20%
        }

        // Show payment confirmation
        if (pay == 1) System.out.println("Cash Payment Successful: ₹" + finalFare);
        else if (pay == 2) System.out.println("UPI Payment Successful: ₹" + finalFare);
        else System.out.println("Invalid Payment, assuming Cash: ₹" + finalFare);

        // Generate unique booking ID using current time
        String bookingId = "BK" + System.currentTimeMillis();
        
        // Create passenger object (SeniorCitizen if age >= 60, else regular Passenger)
        Passenger p;
        if (age >= 60) {
            p = new SeniorCitizen(bookingId, name, age, fno, seat, finalFare);
        } else {
            p = new Passenger(bookingId, name, age, fno, seat, finalFare);
        }

        // Add passenger to array and increase count
        passengers[passengerCount++] = p;

        // Show booking confirmation
        System.out.println("\n✅ Ticket Booked Successfully!");
        System.out.println("Booking ID: " + bookingId);
        p.displayPassenger();  // Show passenger details
    }

    /* ===================== CANCEL EXISTING TICKET ===================== */
    static void cancelTicket() {
        sc.nextLine();  // Clear input buffer
        
        System.out.print("Booking ID: ");
        String id = sc.nextLine();

        // Search for passenger with given booking ID
        for (int i = 0; i < passengerCount; i++) {
            if (passengers[i].bookingId.equalsIgnoreCase(id)) {
                
                // Free up the seat on the flight
                for (int j = 0; j < flightCount; j++) {
                    if (flights[j].flightNo.equalsIgnoreCase(passengers[i].flightNo)) {
                        flights[j].cancelSeat(passengers[i].seat.seatNo);
                    }
                }

                // Remove passenger by replacing with last passenger and reducing count
                passengers[i] = passengers[passengerCount - 1];
                passengerCount--;
                
                System.out.println("Ticket Cancelled!");
                return;  // Exit function after cancellation
            }
        }
        
        // If booking ID not found
        System.out.println("Booking ID not found!");
    }

    /* ===================== VIEW TICKET DETAILS ===================== */
    static void viewMyTicket() {
        sc.nextLine();  // Clear input buffer
        
        System.out.print("Booking ID: ");
        String id = sc.nextLine();

        // Search for passenger with given booking ID
        for (int i = 0; i < passengerCount; i++) {
            if (passengers[i].bookingId.equalsIgnoreCase(id)) {
                passengers[i].displayPassenger();  // Show passenger details
                return;  // Exit after showing
            }
        }
        
        // If ticket not found
        System.out.println("Ticket not found!");
    }

    /* ===================== ADMIN FUNCTIONS ===================== */
    
    /* -------- ADMIN LOGIN -------- */
    static void adminLogin() {
        sc.nextLine();  // Clear input buffer
        
        // Get login credentials
        System.out.print("Admin ID: ");
        String id = sc.nextLine();
        
        System.out.print("Password: ");
        String pass = sc.nextLine();

        // Check credentials (hardcoded for simplicity)
        if (id.equals("admin") && pass.equals("admin123")) {
            adminMenu();  // Go to admin menu if credentials correct
        } else {
            System.out.println("Invalid Login!");
        }
    }

    /* -------- ADMIN MENU -------- */
    static void adminMenu() {
        int ch;  // Variable for admin's choice
        
        do {
            // Display admin options
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add Flight");
            System.out.println("2. View All Flights");
            System.out.println("3. View Passengers by Flight");
            System.out.println("4. Logout");
            
            ch = sc.nextInt();  // Read admin's choice

            // Call appropriate function
            if (ch == 1) addFlight();               // Add new flight
            else if (ch == 2) viewFlights();        // View all flights
            else if (ch == 3) viewPassengersByFlight(); // View passengers

        } while (ch != 4);  // Continue until Logout (4)
    }

    /* -------- ADD NEW FLIGHT (ADMIN ONLY) -------- */
    static void addFlight() {
        sc.nextLine();  // Clear input buffer
        
        System.out.print("Flight No: ");
        String fno = sc.nextLine();

        // Check if flight number already exists
        for (int i = 0; i < flightCount; i++) {
            if (flights[i].flightNo.equalsIgnoreCase(fno)) {
                System.out.println("Flight with this number already exists!");
                return;  // Exit if flight already exists
            }
        }

        // Get flight details from admin
        System.out.print("Source: ");
        String src = sc.nextLine();
        
        System.out.print("Destination: ");
        String dest = sc.nextLine();
        
        System.out.print("Type (1.Domestic 2.International): ");
        int type = sc.nextInt();
        
        System.out.print("Time (HHMM): ");
        int time = sc.nextInt();
        
        System.out.print("Seats: ");
        int seats = sc.nextInt();
        
        System.out.print("Fare: ");
        double fare = sc.nextDouble();

        // Create new flight and add to array
        flights[flightCount++] = new Flight(fno, src, dest, type, time, seats, fare);
        System.out.println("Flight Added Successfully!");
    }

    /* -------- VIEW PASSENGERS BY FLIGHT (ADMIN ONLY) -------- */
    static void viewPassengersByFlight() {
        sc.nextLine();  // Clear input buffer
        
        System.out.print("Enter Flight Number: ");
        String fno = sc.nextLine();

        // First, check if flight exists
        boolean flightFound = false;
        for (int i = 0; i < flightCount; i++) {
            if (flights[i].flightNo.equalsIgnoreCase(fno)) {
                flightFound = true;
                break;  // Stop searching
            }
        }

        // If flight doesn't exist, show error
        if (!flightFound) {
            System.out.println("Flight not found.");
            return;
        }

        // Search for passengers on this flight
        boolean passengerFound = false;
        System.out.println("\n--- PASSENGERS FOR FLIGHT " + fno + " ---");

        // Loop through all passengers
        for (int i = 0; i < passengerCount; i++) {
            // If passenger is booked on this flight
            if (passengers[i].flightNo.equalsIgnoreCase(fno)) {
                passengers[i].displayPassenger();  // Show passenger details
                passengerFound = true;  // Mark as found
            }
        }

        // If no passengers found for this flight
        if (!passengerFound) {
            System.out.println("No passengers booked for this flight yet.");
        }
    }
        }
