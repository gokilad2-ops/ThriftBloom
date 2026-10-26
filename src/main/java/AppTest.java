import database.DatabaseConnection;
import database.DatabaseInitializer;
import model.Item;
import model.ItemRequest;
import model.User;
import service.AuthenticationService;
import service.ItemService;
import service.RequestService;

import java.util.List;

/**
 * Automated end-to-end test runner validating database connectivity,
 * authentication, duplicate rejection, listing creation, search/persistence,
 * item requests, and authorization security rules.
 */
public class AppTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING thrift VERIFICATION TEST SUITE");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: Database Connection & Initialization
        System.out.print("[TEST 1] Database Connection & Initialization... ");
        boolean initOk = DatabaseInitializer.initialize();
        if (initOk && DatabaseConnection.testConnection()) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        AuthenticationService authService = new AuthenticationService();
        ItemService itemService = new ItemService();
        RequestService reqService = new RequestService();

        // Unique test registration numbers
        long timestamp = System.currentTimeMillis();
        String testReg1 = "TEST" + timestamp;
        String testEmail1 = "test" + timestamp + "@campus.edu";
        String testReg2 = "TEST" + (timestamp + 1);
        String testEmail2 = "test" + (timestamp + 1) + "@campus.edu";

        User student1 = null;
        User student2 = null;

        // Test 2: Student Registration
        System.out.print("[TEST 2] Student Registration... ");
        try {
            student1 = authService.register(
                    "Test Student One",
                    testReg1,
                    testEmail1,
                    "password123",
                    "password123",
                    "CSE",
                    "2nd Year",
                    "+91 99999 11111"
            );
            if (student1 != null && student1.getId() > 0) {
                System.out.println("PASSED (ID: " + student1.getId() + ")");
                passed++;
            } else {
                System.out.println("FAILED (User null or ID 0)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 3: Duplicate Account Prevention
        System.out.print("[TEST 3] Duplicate Account Prevention (Reg No)... ");
        try {
            authService.register(
                    "Duplicate Student",
                    testReg1, // Same reg number!
                    "other" + timestamp + "@campus.edu",
                    "password123",
                    "password123",
                    "ECE",
                    "1st Year",
                    ""
            );
            System.out.println("FAILED (Allowed duplicate reg number!)");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Correctly rejected: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAILED with unexpected exception: " + e.getMessage());
            failed++;
        }

        // Test 4: Correct Credentials Login
        System.out.print("[TEST 4] Valid Login (using registration number)... ");
        try {
            User loggedIn = authService.login(testReg1, "password123");
            if (loggedIn != null && loggedIn.getId() == student1.getId()) {
                System.out.println("PASSED (Logged in as " + loggedIn.getName() + ")");
                passed++;
            } else {
                System.out.println("FAILED (Returned user did not match)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 5: Incorrect Credentials Rejection
        System.out.print("[TEST 5] Invalid Password Login Rejection... ");
        try {
            authService.login(testReg1, "wrongPassword999");
            System.out.println("FAILED (Allowed login with invalid password)");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Correctly rejected: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Register Student 2 for multi-user tests
        try {
            student2 = authService.register(
                    "Test Student Two",
                    testReg2,
                    testEmail2,
                    "password123",
                    "password123",
                    "ECE",
                    "3rd Year",
                    "+91 88888 22222"
            );
        } catch (Exception e) {
            System.err.println("Setup failed for Student 2: " + e.getMessage());
        }

        // Test 6: Adding Listing
        System.out.print("[TEST 6] Add Item Listing (Student 1)... ");
        Item createdItem = null;
        try {
            createdItem = itemService.addItem(
                    "Advanced Java Textbook (Test Listing)",
                    "Clean textbook covering concurrency, swing, and JDBC.",
                    "Books",
                    "Like new",
                    "SELL",
                    220.0,
                    null,
                    null,
                    student1
            );
            if (createdItem != null && createdItem.getId() > 0) {
                System.out.println("PASSED (Item ID: " + createdItem.getId() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 7: Marketplace Listing Discovery & Search
        System.out.print("[TEST 7] Search Marketplace for Newly Added Item... ");
        try {
            final int createdId = createdItem.getId();
            List<Item> searchResults = itemService.searchAvailableItems("Advanced Java", "Books", "SELL", "NEWEST");
            boolean found = searchResults.stream().anyMatch(i -> i.getId() == createdId);
            if (found) {
                System.out.println("PASSED (Found in marketplace results)");
                passed++;
            } else {
                System.out.println("FAILED (Item not returned in query)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 8: Prevent Owner From Requesting Own Listing
        System.out.print("[TEST 8] Prevent Requesting Own Listing... ");
        try {
            reqService.requestItem(createdItem.getId(), student1, "Trying to request my own item");
            System.out.println("FAILED (Allowed student to request own listing!)");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Blocked: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 9: Student 2 Requesting Item
        System.out.print("[TEST 9] Student 2 Requests Item from Student 1... ");
        ItemRequest req = null;
        try {
            req = reqService.requestItem(createdItem.getId(), student2, "Hi, I need this for my Advanced Java class!");
            if (req != null && req.getId() > 0) {
                System.out.println("PASSED (Request ID: " + req.getId() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 10: Prevent Duplicate Requests
        System.out.print("[TEST 10] Prevent Duplicate Active Requests... ");
        try {
            reqService.requestItem(createdItem.getId(), student2, "Duplicate request attempt");
            System.out.println("FAILED (Allowed duplicate request!)");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Blocked duplicate: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 11: Security — Prevent Student 2 From Modifying Student 1's Listing
        System.out.print("[TEST 11] Security Authorization (Modify / Delete Other's Listing)... ");
        try {
            createdItem.setName("Hacked Item Name");
            itemService.updateItem(createdItem, student2.getId()); // student2 attempting to modify student1's item
            System.out.println("FAILED (Allowed unauthorized modification!)");
            failed++;
        } catch (SecurityException e) {
            System.out.println("PASSED (Blocked unauthorized update: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        // Test 12: Owner Approving Request & Reserving Item (Transaction Test)
        System.out.print("[TEST 12] Transactional Request Approval & Status Change... ");
        try {
            boolean approved = reqService.approveRequest(req.getId(), createdItem.getId(), student1.getId());
            Item refreshedItem = itemService.getItemById(createdItem.getId());
            if (approved && "RESERVED".equalsIgnoreCase(refreshedItem.getStatus())) {
                System.out.println("PASSED (Request APPROVED, Item status changed to RESERVED)");
                passed++;
            } else {
                System.out.println("FAILED (Status was: " + refreshedItem.getStatus() + ")");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("TEST SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("==================================================");

        if (failed == 0) {
            System.out.println("ALL VERIFICATION CHECKS PASSED SUCCESSFULLY! ✓");
        } else {
            System.exit(1);
        }
    }
}
