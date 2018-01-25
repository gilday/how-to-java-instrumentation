package com.github.gilday.stringcount.jmx;

import java.util.Date;

import com.github.gilday.stringcount.StringsAllocated;
import lombok.Data;
import org.threeten.bp.DateTimeUtils;

/**
 * Open Type compatible representation of {@link com.github.gilday.stringcount.StringsAllocated} for use with JMX
 */
@Data
public class StringsAllocatedBean {

    public static StringsAllocatedBean fromRecord(final StringsAllocated record) {
        return new StringsAllocatedBean(record.count(), DateTimeUtils.toDate(record.timestamp()));
    }

    private final int count;

    /**
     *
     */
    private final Date timestamp;
}
