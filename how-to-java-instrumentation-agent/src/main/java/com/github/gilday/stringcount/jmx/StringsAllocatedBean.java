package com.github.gilday.stringcount.jmx;

import java.beans.ConstructorProperties;
import java.util.Date;

import com.github.gilday.stringcount.StringsAllocated;
import com.google.common.base.MoreObjects;
import java8.util.Objects;
import org.threeten.bp.DateTimeUtils;

/**
 * Open Type compatible representation of {@link StringsAllocated} for use with JMX
 */
public class StringsAllocatedBean {

    public static StringsAllocatedBean fromRecord(final StringsAllocated record) {
        return new StringsAllocatedBean(record.count(), DateTimeUtils.toDate(record.timestamp()));
    }

    @ConstructorProperties({"count", "timestamp"})
    public StringsAllocatedBean(final int count, final Date timestamp) {
        this.count = count;
        this.timestamp = timestamp;
    }

    private final int count;
    private final Date timestamp;

    public int getCount() {
        return this.count;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StringsAllocatedBean that = (StringsAllocatedBean) o;
        return count == that.count &&
            Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("count", count)
            .add("timestamp", timestamp)
            .toString();
    }
}
