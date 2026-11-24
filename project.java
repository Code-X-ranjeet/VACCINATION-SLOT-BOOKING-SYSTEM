import java.util.*;
import java.io.*;

public class project {

    // Maximum number of vaccination slots
    static final int CAPACITY = 3;

    // Circular Queue using an array
    static String[] queue = new String[CAPACITY];

    static int front = 0;
    static int rear = -1;
    static int count = 0;

    // HashMap to store booking details
    static HashMap<String, Booking> bookings = new HashMap<>();

    // First booking ID
    static int nextBookingNumber = 101;

    static final String FILE_NAME = "bookings.txt";

    static Scanner sc = new Scanner(System.in);

    // Booking class
    static class Booking {
        String id;
        String name;
        int age;

        Booking(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    // Insert booking ID into circular queue
    static void enqueue(String id) {
        if (count == 0) {
            front = 0;
        }

        rear = (rear + 1) % CAPACITY;
        queue[rear] = id;
        count++;
    }

    // Remove booking ID from circular queue
    static String dequeue() {
        if (count == 0) {
            return null;
        }

        String id = queue[front];
        queue[front] = null;

        front = (front + 1) % CAPACITY;
        count--;

        if (count == 0) {
            front = 0;
            rear = -1;
        }

        return id;
    }

    // Book a vaccination slot
    static void bookSlot() {

        if (count == CAPACITY) {
            System.out.println(
                "Slots full. Please try the next session."
            );
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Invalid name. Booking not done.");
            return;
        }

        System.out.print("Enter Age: ");

        int age;

        try {
            age = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Booking not done.");
            return;
        }

        if (age < 18) {
            System.out.println(
                "Invalid age. Booking not done."
            );
            return;
        }

        // Generate unique booking ID
        String id = "V" + nextBookingNumber;
        nextBookingNumber++;

        // Create booking object
        Booking b = new Booking(id, name, age);

        // Store booking details in HashMap
        bookings.put(id, b);

        // Insert booking ID into circular queue
        enqueue(id);

        System.out.println(
            "Slot booked. ID: " + id +
            " | " + name +
            " | Position: " + count
        );
    }

    // Vaccinate the next person
    static void vaccinateNext() {

        if (count == 0) {
            System.out.println("No bookings available.");
            return;
        }

        // Remove the first booking (FIFO)
        String id = dequeue();

        // Retrieve details using HashMap
        Booking b = bookings.get(id);

        if (b != null) {
            System.out.println(
                b.name + " vaccinated. Slot freed."
            );

            // Remove completed booking
            bookings.remove(id);
        }
    }

    // Display current queue
    static void viewQueue() {

        if (count == 0) {
            System.out.println("No bookings in the queue.");
            return;
        }

        System.out.println("\nCurrent Vaccination Queue:");

        int index = front;

        for (int i = 0; i < count; i++) {

            String id = queue[index];

            // Look up booking details using HashMap
            Booking b = bookings.get(id);

            if (b != null) {
                System.out.println(
                    (i + 1) + ". " +
                    b.id + " | " + b.name
                );
            }

            index = (index + 1) % CAPACITY;
        }
    }

    // Search booking using booking ID
    static void searchBooking() {

        System.out.print("Enter Booking ID: ");
        String id = sc.nextLine().trim().toUpperCase();

        Booking b = bookings.get(id);

        if (b != null) {
            System.out.println("\nBooking Found:");
            System.out.println("ID   : " + b.id);
            System.out.println("Name : " + b.name);
            System.out.println("Age  : " + b.age);
        } else {
            System.out.println("Booking not found.");
        }
    }

    // Load existing bookings from file
    static void loadData() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No previous booking data found.");
            return;
        }

        try (BufferedReader br =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t", -1);

                if (data.length != 3) {
                    continue;
                }

                String id = data[0];
                String name = data[1];

                int age;

                try {
                    age = Integer.parseInt(data[2]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (id.isEmpty() || name.trim().isEmpty()
                        || age < 18 || bookings.containsKey(id)
                        || count >= CAPACITY) {
                    continue;
                }

                Booking b = new Booking(id, name, age);

                bookings.put(id, b);
                enqueue(id);

                // Find the next available booking number
                if (id.matches("V\\d+")) {
                    int number = Integer.parseInt(id.substring(1));
                    nextBookingNumber =
                        Math.max(nextBookingNumber, number + 1);
                }
            }

            System.out.println("Booking data loaded successfully.");

        } catch (IOException e) {
            System.out.println("Error loading booking data.");
        }
    }

    // Save current bookings into file
    static void saveData() {

        try (BufferedWriter bw =
                 new BufferedWriter(new FileWriter(FILE_NAME))) {

            int index = front;

            for (int i = 0; i < count; i++) {

                String id = queue[index];
                Booking b = bookings.get(id);

                if (b != null) {
                    bw.write(
                        b.id + "\t" +
                        b.name + "\t" +
                        b.age
                    );

                    bw.newLine();
                }

                index = (index + 1) % CAPACITY;
            }

            System.out.println("Booking data saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving booking data.");
        }
    }

    // Main menu
    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   VACCINATION SLOT BOOKING SYSTEM");
        System.out.println("==================================");

        // Load previous bookings
        loadData();

        while (true) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Book Vaccination Slot");
            System.out.println("2. Vaccinate Next Person");
            System.out.println("3. View Current Queue");
            System.out.println("4. Search Booking");
            System.out.println("5. Exit");
            System.out.println("===============================");

            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    bookSlot();
                    break;

                case "2":
                    vaccinateNext();
                    break;

                case "3":
                    viewQueue();
                    break;

                case "4":
                    searchBooking();
                    break;

                case "5":
                    saveData();

                    System.out.println(
                        "Thank you for using Vaccination Slot Booking System."
                    );

                    sc.close();
                    return;

                default:
                    System.out.println(
                        "Invalid choice. Please try again."
                    );
            }
        }
    }
}