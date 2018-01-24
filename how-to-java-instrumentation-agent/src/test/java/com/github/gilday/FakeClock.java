package com.github.gilday;

import static java.time.ZoneOffset.UTC;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Factory which creates a mutable {@link java.time.Clock} with a default date that is obviously old and should only be
 * used in tests. Provides convenience methods for traveling forward through time - careful
 */
public class FakeClock extends Clock {

    private Clock clock;

    public FakeClock() {
        set(LocalDateTime.of(1955, Month.NOVEMBER, 11, 22, 54));
    }

    public void set(final Instant instant, ZoneId zone) {
        clock = Clock.fixed(instant, zone);
    }

    public void set(final LocalDateTime localDateTime, final ZoneOffset offset) {
        final Instant instant = localDateTime.toInstant(offset);
        set(instant, offset);
    }

    /**
     * sets the clock using the given {@link LocalDateTime} and assumes {@link ZoneId#systemDefault()}
     */
    public void set(final LocalDateTime localDateTime) {
        final ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        set(localDateTime, offset);
    }

    public void fastForward(final Duration duration) {
        final Instant future = clock.instant().plusMillis(duration.toMillis());
        set(future, ZoneId.systemDefault());
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(final ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }

    public LocalDateTime utc() {
        return LocalDateTime.ofInstant(instant(), UTC);
    }

    public LocalDateTime localDateTime() {
        return LocalDateTime.ofInstant(instant(), getZone());
    }

    public String iso8601() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant().atZone(UTC));
    }

    public Date legacyDate() {
        return Date.from(clock.instant());
    }
}
