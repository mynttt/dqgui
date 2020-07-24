package de.hshannover.dqgui.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

public class IntervalMap<T> {
    private final List<T> values = new ArrayList<>();
    private final TreeMap<Integer, T> map = new TreeMap<>();
    
    public T put(int low, int high, T data) {
        if(low > high)
            throw new IllegalArgumentException("low > high" + ": " + low + " > " + high);
        map.put(low, data);
        map.put(high, null);
        values.add(data);
        return data;
    }
    
    private T getInternal(int i) {
        Entry<Integer, T> e = map.floorEntry(i);
        if (e != null && e.getValue() == null) {
            e = map.lowerEntry(i);
        }
        return e == null ? null : e.getValue();
    }
    
    public Optional<T> get(int i) {
        return Optional.ofNullable(getInternal(i));
    }
    
    public boolean exists(int i) {
        return getInternal(i) != null;
    }

    @Override
    public String toString() {
        return "IntervalMap [map=" + map + "]";
    }

    public List<T> getValues() {
        return values;
    }
}
