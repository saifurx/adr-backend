package com.kasa.adr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BatchProcessor {

    public static <T> void processInBatches(Stream<T> stream, int batchSize, long intervalMillis, BatchHandler<T> handler) {
        List<T> batch = new ArrayList<>();
        stream.forEach(item -> {
            batch.add(item);
            if (batch.size() == batchSize) {
                handler.handleBatch(batch);
                batch.clear();
                try {
                    Thread.sleep(intervalMillis); // Pause for the specified interval
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Batch processing interrupted", e);
                }
            }
        });

        // Process any remaining items
        if (!batch.isEmpty()) {
            handler.handleBatch(batch);
        }
    }


    @FunctionalInterface
    public interface BatchHandler<T> {
        void handleBatch(List<T> batch);
    }
}