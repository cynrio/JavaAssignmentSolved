package com.maybank;

// Question 3
public class OutOfMemoryError {

    public static void main(String[] args) {
        try {
            simulateOutOfMemory();
        } catch (java.lang.OutOfMemoryError e) {
            System.err.println("OutOfMemoryError occurred: " + e.getMessage());
            System.out.println("Resolving with optimized approach...");

            // Resolving without GC
            resolveWithoutGC();

            // Resolving with GC
            resolveWithGC();
        }
    }

    // Method to simulate OutOfMemoryError
    private static void simulateOutOfMemory() {
        System.out.println("Simulating OutOfMemoryError...");
        StringBuffer buffer = new StringBuffer();

        // Append a large number of strings
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            buffer.append("This is a very large string to cause OutOfMemoryError. " + i);
        }
    }

    // Resolving without explicit garbage collection
    private static void resolveWithoutGC() {
        System.out.println("Processing data in chunks without GC...");
        int batchSize = 100000; // Number of records per batch
        int totalRecords = 1_000_000; // Total records to process

        for (int i = 0; i < totalRecords; i += batchSize) {
            StringBuffer buffer = new StringBuffer();
            for (int j = i; j < i + batchSize && j < totalRecords; j++) {
                buffer.append("Processing record " + j + "\n");
            }
            // Simulate writing the batch to disk or database
            System.out.println("Processed batch: " + i + " to " + (i + batchSize));
        }
    }

    // Resolving with garbage collection
    private static void resolveWithGC() {
        System.out.println("Processing data in chunks with GC...");
        int batchSize = 100000; // Number of records per batch
        int totalRecords = 1_000_000; // Total records to process

        for (int i = 0; i < totalRecords; i += batchSize) {
            StringBuffer buffer = new StringBuffer();
            for (int j = i; j < i + batchSize && j < totalRecords; j++) {
                buffer.append("Processing record " + j + "\n");
            }
            // Simulate writing the batch to disk or database
            System.out.println("Processed batch: " + i + " to " + (i + batchSize));

            // Explicitly dereference the buffer and suggest GC
            buffer = null;
            System.gc(); // Suggest garbage collection
        }
    }
}
