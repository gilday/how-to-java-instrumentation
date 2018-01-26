package com.github.gilday.stringcount;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

/**
 * Stores counts for strings created in user requests
 *
 * TODO cap the number of records recorded
 */
public class StringsAllocatedRecordStore {

    private final LinkedList<StringsAllocated> records = new LinkedList<>();

    @Inject public StringsAllocatedRecordStore() { }

    public void add(final StringsAllocated record) {
        records.add(record);
    }

    public List<StringsAllocated> records() {
        return Collections.unmodifiableList(records);
    }
}
