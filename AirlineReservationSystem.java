import java.util.*;

// -------------------- Passenger Class --------------------
class Passenger {
    private int passengerId;
    private String name;
    private int age;

    public Passenger(int passengerId, String name, int age) {
        this.passengerId = passengerId;
        this.name = name;
        this.age = age;
    }

    public int getPassengerId() { return passengerId; }
    public String getName() { return name; }
}

// -------------------- Flight Class --------------------
class Flight {
    private String flightNo;
    private int totalSeats;
    private int availableSeats;
    private double basePrice;

    public Flight(String flightNo, int totalSeats, double basePrice) {
        this.flightNo = flightNo;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.basePrice = basePrice;
    }

    // Book seat & return price (dynamic pricing)
    public double bookSeat() {
        if (availableSeats > 0) {
            availableSeats--;
            return calculatePrice();
        }
        return -1; // No seats
    }

    public void cancelSeat() {
        if (availableSeats < totalSeats) {
            availableSeats++;
        }
    }

    private double calculatePrice() {
        // Dynamic pricing: Price increases 10% when seats < 50%
        if (availableSeats < totalSeats / 2.0) {
            return basePrice * 1.10;
        }
        return basePrice;
    }

    public String getFlightNo() { return flightNo; }
    public int getAvailableSeats() { return availableSeats; }
}

// -------------------- Reservation Class --------------------
class Reservation {
    private int reservationId;
    private Passenger passenger;
    private Flight flight;
    private double price;
    private Date date;

    public Reservation(int reservationId, Passenger passenger, Flight flight, double price) {
        this.reservationId = reservationId;
        this.passenger = passenger;
        this.flight = flight;
        this.price = price;
        this.date = new Date();
    }

    public int getReservationId() { return reservationId; }
    public double getPrice() { return price; }

    public void display() {
        System.out.println("Reservation ID : " + reservationId);
        System.out.println("Passenger Name : " + passenger.getName());
        System.out.println("Flight Number  : " + flight.getFlightNo());
        System.out.println("Price (Rs)     : " + price);
        System.out.println("Date           : " + date);
        System.out.println("--------------------------------");
    }
}

// -------------------- Airline System Class --------------------
class AirlineSystem {
    private Scanner sc = new Scanner(System.in);
    private Flight flight = new Flight("AI-101", 5, 4500); // small seats for demo
    private Map<Integer, Reservation> reservations = new HashMap<>();
    private int reservationCounter = 1001;
    private double totalRevenue = 0;

    public void menu() {
        while (true) {
            System.out.println("\n====== Airline Reservation System ======");
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. View All Reservations");
            System.out.println("4. Daily Business Report");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> bookTicket();
                case 2 -> cancelTicket();
                case 3 -> showReservations();
                case 4 -> dailyReport();
                case 5 -> { System.out.println("Thank You!"); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void bookTicket() {
        if (flight.getAvailableSeats() == 0) {
            System.out.println("No seats available!");
            return;
        }

        System.out.print("Enter Passenger ID: ");
        int pid = sc.nextInt();
        sc.nextLine(); // consume newline
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();

        Passenger p = new Passenger(pid, name, age);

        // Transaction-safe booking
        double price = flight.bookSeat();
        if (price != -1) {
            Reservation r = new Reservation(reservationCounter++, p, flight, price);
            reservations.put(r.getReservationId(), r);
            totalRevenue += price;

            System.out.println("\nTicket Booked Successfully!");
            System.out.println("Reservation ID: " + r.getReservationId());
            System.out.println("Price: Rs " + price);
        } else {
            System.out.println("Booking failed! No seats available.");
        }
    }

    private void cancelTicket() {
        System.out.print("Enter Reservation ID: ");
        int rid = sc.nextInt();

        Reservation r = reservations.remove(rid);
        if (r != null) {
            flight.cancelSeat();
            totalRevenue -= r.getPrice();
            System.out.println("Ticket Cancelled Successfully!");
        } else {
            System.out.println("Reservation not found!");
        }
    }

    private void showReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found!");
            return;
        }
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }

    private void dailyReport() {
        System.out.println("\n------ Daily Business Report ------");
        System.out.println("Total Tickets Sold : " + reservations.size());
        System.out.println("Available Seats    : " + flight.getAvailableSeats());
        System.out.println("Total Revenue (Rs) : " + totalRevenue);
        System.out.println("----------------------------------");
    }
}

// -------------------- Main Class --------------------
public class AirlineReservationSystem {
    public static void main(String[] args) {
        AirlineSystem system = new AirlineSystem();
        system.menu();
    }
}
