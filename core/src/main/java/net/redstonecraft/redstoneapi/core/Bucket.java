package net.redstonecraft.redstoneapi.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class to limit the execution of {@link Runnable}s in a bucket.
 * Every execution will reduce the bucket level.
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
@SuppressWarnings("unused")
public class Bucket {

    private final ExecutorService threadPool;
    private final boolean blocking;
    private final int bucketSize;
    private int currentBucketLevel;
    private final long fullRefillTime;
    private long lastRefill = System.currentTimeMillis();

    /**
     * Constructs a new full bucket that takes the specified time to fill.
     * This bucket is blocking on execution.
     *
     * @param bucketSize size and initial level of the bucket
     * @param fullRefillTime how long it takes to fill the bucket from empty to full in ms
     */
    public Bucket(int bucketSize, int fullRefillTime) {
        this(bucketSize, bucketSize, fullRefillTime, true);
    }

    /**
     * Constructs a new full bucket that takes the specified time to fill.
     *
     * @param bucketSize size and initial level of the bucket
     * @param fullRefillTime how long it takes to fill the bucket from empty to full in ms
     * @param blocking weather to use a thread pool or to be synchronous
     */
    public Bucket(int bucketSize, int fullRefillTime, boolean blocking) {
        this(bucketSize, bucketSize, fullRefillTime, blocking);
    }

    /**
     * Constructs a new bucket that takes the specified time to fill and an initial level.
     *
     * @param bucketSize size of the bucket
     * @param initialLevel initial level of the bucket
     * @param fullRefillTime how long it takes to fill the bucket from empty to full in ms
     * @param threadPool custom thread pool
     */
    public Bucket(int bucketSize, int initialLevel, long fullRefillTime, ExecutorService threadPool) {
        this.blocking = false;
        this.bucketSize = bucketSize;
        this.currentBucketLevel = initialLevel;
        this.threadPool = threadPool;
        this.fullRefillTime = fullRefillTime;
    }

    /**
     * Constructs a new bucket that takes the specified time to fill and an initial level.
     *
     * @param bucketSize size of the bucket
     * @param initialLevel initial level of the bucket
     * @param fullRefillTime how long it takes to fill the bucket from empty to full in ms
     * @param blocking weather to use a thread pool or to be synchronous
     */
    public Bucket(int bucketSize, int initialLevel, long fullRefillTime, boolean blocking) {
        this.blocking = blocking;
        this.bucketSize = bucketSize;
        this.currentBucketLevel = initialLevel;
        this.fullRefillTime = fullRefillTime;
        if (this.blocking) {
            threadPool = null;
        } else {
            threadPool = Executors.newCachedThreadPool();
        }
    }

    /**
     * Run (or not) a task on the bucket.
     *
     * @param consumer the task to run if the bucket is full enough
     * @return whether the task gets (if asynchronous) / got (if synchronous) executed
     */
    public boolean execute(Runnable consumer) {
        long diff = System.currentTimeMillis() - lastRefill;
        int n = (int) Math.min(bucketSize, Math.floor(((double) bucketSize / fullRefillTime * diff) + currentBucketLevel));
        if (n > 0) {
            currentBucketLevel = n;
            lastRefill += diff;
        }
        if (currentBucketLevel > 0) {
            currentBucketLevel--;
            if (blocking) {
                consumer.run();
            } else {
                threadPool.submit(consumer);
            }
            return true;
        }
        return false;
    }

    /**
     * Gets the bucket size
     *
     * @return the bucket size
     */
    public int getBucketSize() {
        return bucketSize;
    }

    /**
     * Gets the current bucket level
     *
     * @return the current bucket level
     */
    public int getCurrentBucketLevel() {
        return currentBucketLevel;
    }

    /**
     * Gets the bucket blocking mode
     *
     * @return if this bucket is in blocking mode
     */
    public boolean isBlocking() {
        return blocking;
    }

}
