package twitter4j;

import java.io.*;

/**
 * A {@link java.io.InputStream} that limits input to a given number of bytes
 *
 * Created by jrbuckeridge on 7/5/16.
 */
public class LimitedInputStream extends InputStream {

    private InputStream inputStream;

    /**
     * The initial skip to read from file
     */
    protected final long skip;

    /**
     * Currently skipped bytes
     */
    private long currentSkipped = 0;

    /**
     * Maximum number of bytes to read from file
     */
    protected final long max;

    /**
     * Current count of bytes read
     */
    protected int count = 0;

    public LimitedInputStream(InputStream inputStream, long skip, long max) {
        this.inputStream = inputStream;
        this.skip = skip;
        this.max = max;
    }

    public long getSkip() {
        return skip;
    }

    public long getMax() {
        return max;
    }

    /**
     * Skips the initial bytes if necessary
     */
    private void doInitialSkip() throws IOException {
        while (currentSkipped < skip) {
            currentSkipped += skip(skip - currentSkipped);
        }
    }

    @Override
    public int read() throws IOException {
        doInitialSkip();
        if (count >= max) {
            return -1;
        }

        int read = this.inputStream.read();
        count++;
        return read;
    }

    @Override
    public int read(byte[] b) throws IOException {
        doInitialSkip();
        if (count >= max) {
            return -1;
        }

        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        doInitialSkip();
        if (count >= max) {
            return -1;
        }

        long pending = max - count;
        int read = this.inputStream.read(b, off, b.length < pending ? b.length : (int) pending);
        count += read;
        return read;
    }

    @Override
    public int available() throws IOException {
        int available = this.inputStream.available();
        return available > max - count ? (int) (max - count) : available;
    }
}
