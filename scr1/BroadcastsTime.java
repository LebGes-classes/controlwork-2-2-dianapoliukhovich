package scr1;

public class BroadcastsTime implements Comparable<BroadcastsTime> {
    private final byte hours;
    private final byte minutes;

    public BroadcastsTime(String time) {
        String[] parts = time.split(":");
        this.hours = Byte.parseByte(parts[0]);
        this.minutes = Byte.parseByte(parts[1]);
    }

    public byte hour() {
        return hours;
    }

    public byte minutes() {
        return minutes;
    }

    public boolean after(BroadcastsTime t) {
        return compareTo(t) > 0;
    }

    public boolean before(BroadcastsTime t) {
        return compareTo(t) < 0;
    }

    public boolean between(BroadcastsTime t1, BroadcastsTime t2) {
        return after(t1) && before(t2);
    }

    @Override
    public int compareTo(BroadcastsTime o) {
        if (this.hours != o.hours) {
            return this.hours - o.hours;
        }
        return this.minutes - o.minutes;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }
}