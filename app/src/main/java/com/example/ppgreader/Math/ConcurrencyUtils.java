package com.example.ppgreader.Math;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ConcurrencyUtils {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new CustomThreadFactory(new CustomExceptionHandler()));

    private static int THREADS_BEGIN_N_1D_FFT_2THREADS = 8192;

    private static int THREADS_BEGIN_N_1D_FFT_4THREADS = 65536;

    private static int THREADS_BEGIN_N_2D = 65536;

    private static int THREADS_BEGIN_N_3D = 65536;

    private static int NTHREADS = prevPow2(getNumberOfProcessors());

    private ConcurrencyUtils() {

    }

    private static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {
            e.printStackTrace();
        }

    }

    private static class CustomThreadFactory implements ThreadFactory {
        private static final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        private final Thread.UncaughtExceptionHandler handler;

        CustomThreadFactory(Thread.UncaughtExceptionHandler handler) {
            this.handler = handler;
        }

        public Thread newThread(Runnable r) {
            Thread t = defaultFactory.newThread(r);
            t.setUncaughtExceptionHandler(handler);
            return t;
        }
    };


    public static int getNumberOfProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }


    public static int getNumberOfThreads() {
        return NTHREADS;
    }

    public static void setNumberOfThreads(int n) {
        NTHREADS = prevPow2(n);
    }


    public static int getThreadsBeginN_1D_FFT_2Threads() {
        return THREADS_BEGIN_N_1D_FFT_2THREADS;
    }


    public static int getThreadsBeginN_1D_FFT_4Threads() {
        return THREADS_BEGIN_N_1D_FFT_4THREADS;
    }

    public static int getThreadsBeginN_2D() {
        return THREADS_BEGIN_N_2D;
    }
    public static int getThreadsBeginN_3D() {
        return THREADS_BEGIN_N_3D;
    }
    public static void setThreadsBeginN_1D_FFT_2Threads(int n) {
        if (n < 512) {
            THREADS_BEGIN_N_1D_FFT_2THREADS = 512;
        } else {
            THREADS_BEGIN_N_1D_FFT_2THREADS = n;
        }
    }
    public static void setThreadsBeginN_1D_FFT_4Threads(int n) {
        if (n < 512) {
            THREADS_BEGIN_N_1D_FFT_4THREADS = 512;
        } else {
            THREADS_BEGIN_N_1D_FFT_4THREADS = n;
        }
    }
    public static void setThreadsBeginN_2D(int n) {
        THREADS_BEGIN_N_2D = n;
    }
    public static void setThreadsBeginN_3D(int n) {
        THREADS_BEGIN_N_3D = n;
    }
    public static void resetThreadsBeginN_FFT() {
        THREADS_BEGIN_N_1D_FFT_2THREADS = 8192;
        THREADS_BEGIN_N_1D_FFT_4THREADS = 65536;
    }

    public static void resetThreadsBeginN() {
        THREADS_BEGIN_N_2D = 65536;
        THREADS_BEGIN_N_3D = 65536;
    }

    public static int nextPow2(int x) {
        if (x < 1)
            throw new IllegalArgumentException("x must be greater or equal 1");
        if ((x & (x - 1)) == 0) {
            return x;
        }
        x |= (x >>> 1);
        x |= (x >>> 2);
        x |= (x >>> 4);
        x |= (x >>> 8);
        x |= (x >>> 16);
        x |= (x >>> 32);
        return x + 1;
    }


    public static int prevPow2(int x) {
        if (x < 1)
            throw new IllegalArgumentException("x must be greater or equal 1");
        return (int) Math.pow(2, Math.floor(Math.log(x) / Math.log(2)));
    }

    public static boolean isPowerOf2(int x) {
        if (x <= 0)
            return false;
        else
            return (x & (x - 1)) == 0;
    }


    public static void sleep(long millis) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Future<?> submit(Runnable task) {
        return THREAD_POOL.submit(task);
    }


    public static void waitForCompletion(Future<?>[] futures) {
        int size = futures.length;
        try {
            for (int j = 0; j < size; j++) {
                futures[j].get();
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
