package com.github.gilday.stringcount;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Stores counts for strings created in user requests
 */
@RequiredArgsConstructor
public class StringsAllocatedRecordStore {

    private final LinkedList<StringsAllocatedRecord> records = new LinkedList<>();

    public void add(final StringsAllocatedRecord record) {
        records.add(record);
    }

    public List<StringsAllocatedRecord> records() {
        return Collections.unmodifiableList(records);
    }
}
