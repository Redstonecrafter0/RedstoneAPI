package net.redstonecraft.redstoneapi.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class Bucket {

    private final ExecutorService threadPool;
    private final boolean blocking;
    private final int bucketSize;
    private int currentBucketLevel;
    private boolean run = true;

    public Bucket(int bucketSize, int fullRefillTime) {
        this(bucketSize, 0, fullRefillTime, true);
    }

    public Bucket(int bucketSize, int fullRefillTime, boolean blocking) {
        this(bucketSize, 0, fullRefillTime, blocking);
    }

    public Bucket(int bucketSize, int initialLevel, long fullRefillTime, boolean blocking) {
        this.blocking = blocking;
        this.bucketSize = bucketSize;
        this.currentBucketLevel = initialLevel;
        if (this.blocking) {
            threadPool = null;
        } else {
            threadPool = Executors.newCachedThreadPool();
        }
        new Thread(() -> {
            while (run) {
                try {
                    Thread.sleep(currentBucketLevel < bucketSize ? 10 : fullRefillTime / bucketSize);
                } catch (InterruptedException ignored) {
                }
                if (currentBucketLevel < bucketSize) {
                    currentBucketLevel++;
                }
            }
        }).start();
    }

    public void execute(Runnable consumer) {
        if (currentBucketLevel > 0) {
            currentBucketLevel--;
            if (blocking) {
                consumer.run();
            } else {
                threadPool.submit(consumer);
            }
        }
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public int getCurrentBucketLevel() {
        return currentBucketLevel;
    }

    public void close() {
        run = false;
    }

}
