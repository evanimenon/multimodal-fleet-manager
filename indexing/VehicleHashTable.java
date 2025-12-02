package indexing;

import vehicles.Vehicle;

import java.util.Arrays;


public class VehicleHashTable {

    private static class Entry {
        final String key;
        Vehicle value;

        Entry(String key, Vehicle value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry[] table;
    private int size;
    private static final double LOAD_FACTOR = 0.75;

    public VehicleHashTable() {
        this.table = new Entry[64]; // default capacity
        this.size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % table.length;
    }
    private int findSlot(String key) {
        int idx = hash(key);
        int start = idx;
        while (true) {
            Entry e = table[idx];
            if (e == null || e.key.equals(key)) {
                return idx;
            }
            idx = (idx + 1) % table.length;
            if (idx == start) {
                // Table is full, caller should rehash
                return -1;
            }
        }
    }

    private void rehash() {
        Entry[] old = table;
        table = new Entry[old.length * 2];
        size = 0;
        for (Entry e : old) {
            if (e != null) {
                putInternal(e.key, e.value);
            }
        }
    }

    private void putInternal(String key, Vehicle value) {
        int slot = findSlot(key);
        if (slot < 0) {
            // should not happen after rehash
            return;
        }
        if (table[slot] == null) {
            table[slot] = new Entry(key, value);
            size++;
        } else {
            table[slot].value = value;
        }
    }

    public synchronized void put(String key, Vehicle value) {
        if (key == null || value == null) return;
        if ((size + 1.0) / table.length > LOAD_FACTOR) {
            rehash();
        }
        putInternal(key, value);
    }

    public synchronized Vehicle get(String key) {
        if (key == null) return null;
        int idx = hash(key);
        int start = idx;
        while (true) {
            Entry e = table[idx];
            if (e == null) {
                return null;
            }
            if (e.key.equals(key)) {
                return e.value;
            }
            idx = (idx + 1) % table.length;
            if (idx == start) {
                return null;
            }
        }
    }

    /**
     * Remove a key and recompact the probe cluster after it.
     */
    public synchronized void remove(String key) {
        if (key == null) return;
        int idx = hash(key);
        int start = idx;
        while (true) {
            Entry e = table[idx];
            if (e == null) {
                return;
            }
            if (e.key.equals(key)) {
                table[idx] = null;
                size--;
                // Re-insert cluster following this slot to preserve probing chain
                idx = (idx + 1) % table.length;
                while (table[idx] != null) {
                    Entry toRehash = table[idx];
                    table[idx] = null;
                    size--;
                    putInternal(toRehash.key, toRehash.value);
                    idx = (idx + 1) % table.length;
                }
                return;
            }
            idx = (idx + 1) % table.length;
            if (idx == start) {
                return;
            }
        }
    }

    public synchronized void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    public synchronized int size() {
        return size;
    }
}
