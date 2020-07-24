package de.hshannover.dqgui.core.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import de.hshannover.dqgui.core.util.comparators.DSLComponentComparator;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public final class DSLComponentCollection {
    private final EnumMap<DSLComponentType, Set<String>> lookup = new EnumMap<>(DSLComponentType.class);
    private final EnumMap<DSLComponentType, Set<String>> lookupCaseInsensitive = new EnumMap<>(DSLComponentType.class);
    private final EnumMap<DSLComponentType, Map<String, Integer>> refCount = new EnumMap<>(DSLComponentType.class);
    private final EnumMap<DSLComponentType, ObservableList<DSLComponent>> typeMap = new EnumMap<>(DSLComponentType.class);
    private final HashMap<DSLComponent, Map<String, String>> extraDataLookup = new HashMap<>();
    private final EnumMap<DSLComponentType, SortedList<DSLComponent>> sortedMap = new EnumMap<>(DSLComponentType.class);
    private final EnumMap<DSLComponentType, BooleanBinding> empty = new EnumMap<>(DSLComponentType.class);

    public DSLComponentCollection() {
        for(DSLComponentType type : DSLComponentType.values()) {
            lookup.put(type, new HashSet<>());
            lookupCaseInsensitive.put(type, new HashSet<>());
            refCount.put(type, new HashMap<>());
            typeMap.put(type, FXCollections.observableArrayList());
            sortedMap.put(type, new SortedList<>(typeMap.get(type), DSLComponentComparator.getInstance()));
            empty.put(type, Bindings.size(typeMap.get(type)).isEqualTo(0));
        }
    }
    
    public boolean exists(DSLComponent component) {
        return lookup.get(component.getType()).contains(component.getIdentifier());
    }
    
    public boolean existsCaseInsensitive(DSLComponent component) {
        return lookupCaseInsensitive.get(component.getType()).contains(component.getIdentifier().toLowerCase());
    }
    
    public Optional<Map<String, String>> requestExtradata(DSLComponent c) {
        return Optional.ofNullable(extraDataLookup.get(c));
    }

    public void add(DSLComponent component) {
        typeMap.get(component.getType()).add(component);
        lookupAdd(component);
    }

    public void remove(DSLComponent component) {
        typeMap.get(component.getType()).remove(component);
        lookupRemove(component);
    }

    public void addAll(List<DSLComponent> components) {
        internalAddAll(components);
    }
    
    public void removeAll(List<DSLComponent> selected) {
        EnumMap<DSLComponentType, List<DSLComponent>> presort = new EnumMap<>(DSLComponentType.class);
        for(DSLComponentType t : DSLComponentType.values())
            presort.put(t, new ArrayList<>(10));
        for(DSLComponent component : selected) {
            lookupRemove(component);
            presort.get(component.getType()).add(component);
        }
        presort.forEach((k, v) -> typeMap.get(k).removeAll(v));
    }

    public void repopulate(List<DSLComponent> newComponents) {
        typeMap.values().forEach(ObservableList::clear);
        lookup.values().forEach(Set::clear);
        lookupCaseInsensitive.values().forEach(Set::clear);
        refCount.values().forEach(Map::clear);
        internalAddAll(newComponents);
    }

    public FilteredList<DSLComponent> of(DSLComponentType type) {
        return new FilteredList<>(sortedMap.get(type), t -> true);
    }
    
    private void lookupAdd(DSLComponent c) {
        lookup.get(c.getType()).add(c.getIdentifier());
        extraDataLookup.put(c, c.getExtraData());
        String lower = c.getIdentifier().toLowerCase();
        if(refCount.get(c.getType()).containsKey(lower)) {
            refCount.get(c.getType()).compute(lower, (k, v) -> v + 1);
        } else {
            refCount.get(c.getType()).put(lower, 1);
            lookupCaseInsensitive.get(c.getType()).add(lower);
        }
    }
    
    private void lookupRemove(DSLComponent c) {
        lookup.get(c.getType()).remove(c.getIdentifier());
        extraDataLookup.remove(c);
        String lower = c.getIdentifier().toLowerCase();
        Integer i = refCount.get(c.getType()).get(lower);
        if(i == null || i.intValue() == 0)
            return;
        if(i.intValue() == 1) {
            refCount.get(c.getType()).remove(lower);
            lookupCaseInsensitive.get(c.getType()).remove(lower);
        } else {
            refCount.get(c.getType()).compute(lower, (k, v) -> v - 1);
        }
    }

    private void internalAddAll(List<DSLComponent> components) {
        EnumMap<DSLComponentType, List<DSLComponent>> presort = new EnumMap<>(DSLComponentType.class);
        for(DSLComponentType t : DSLComponentType.values())
            presort.put(t, new ArrayList<>(100));
        for(DSLComponent component : components)
            presort.get(component.getType()).add(component);
        presort.forEach((k, v) -> {
            typeMap.get(k).addAll(v);
            v.forEach(this::lookupAdd);
        });
    }

    public BooleanBinding isEmpty(DSLComponentType type) {
        return empty.get(type);
    }

    public void addListener(ListChangeListener<DSLComponent> cl, DSLComponentType type) {
        typeMap.get(type).addListener(cl);
    }
    
    public void removeListener(ListChangeListener<DSLComponent> cl, DSLComponentType type) {
        typeMap.get(type).removeListener(cl);
    }

    public void unloadAll() {
        lookup.forEach((k, v) -> v.clear());
        lookupCaseInsensitive.forEach((k, v) -> v.clear());
        refCount.forEach((k, v) -> v.clear());
        typeMap.forEach((k, v) -> v.clear());
    }
}
