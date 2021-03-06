package de.mkammerer.argon2;

/**
 * Provides useful helper methods to work with Argon2.
 */
public final class Argon2Helper {
    /**
     * Nanoseconds in a millisecond.
     */
    private static final long MILLIS_IN_NANOS = 1000000L;

    /**
     * No instances allowed.
     */
    private Argon2Helper() {
    }

    /**
     * Finds the number of iterations so that the hash function takes at most the given number of milliseconds.
     *
     * @param argon2       Argon2 instance.
     * @param maxMillisecs Maximum number of milliseconds the hash function must take.
     * @param memory       Memory. See {@link Argon2#hash(int, int, int, char[])}.
     * @param parallelism  Parallelism. See {@link Argon2#hash(int, int, int, char[])}.
     * @return The number of iterations so that the hash function takes at most the given number of milliseconds.
     */
    public static int findIterations(Argon2 argon2, long maxMillisecs, int memory, int parallelism) {
        return findIterations(argon2, maxMillisecs, memory, parallelism, new NoopLogger());
    }

    /**
     * Finds the number of iterations so that the hash function takes at most the given number of milliseconds.
     *
     * @param argon2       Argon2 instance.
     * @param maxMillisecs Maximum number of milliseconds the hash function must take.
     * @param memory       Memory. See {@link Argon2#hash(int, int, int, char[])}.
     * @param parallelism  Parallelism. See {@link Argon2#hash(int, int, int, char[])}.
     * @param logger       Logger which gets called with the runtime of the tested iteration steps.
     * @return The number of iterations so that the hash function takes at most the given number of milliseconds.
     */
    public static int findIterations(Argon2 argon2, long maxMillisecs, int memory, int parallelism, IterationLogger logger) {
        char[] password = "password".toCharArray();

        long took;
        int iterations = 0;
        do {
            iterations++;
            long start = System.nanoTime() / MILLIS_IN_NANOS;
            argon2.hash(iterations, memory, parallelism, password);
            long end = System.nanoTime() / MILLIS_IN_NANOS;
            took = end - start;

            logger.log(iterations, took);
        } while (took <= maxMillisecs);

        return iterations - 1;
    }

    /**
     * Logger for the iteration tests.
     */
    public interface IterationLogger {
        /**
         * Is called after a hash call is done.
         *
         * @param iterations Number of iterations used.
         * @param millisecs  Time the hash call took in milliseconds.
         */
        void log(int iterations, long millisecs);
    }

    /**
     * Logs nothing.
     */
    public static class NoopLogger implements IterationLogger {
        @Override
        public void log(int iterations, long millisecs) {
            // Do nothing
        }
    }
}
