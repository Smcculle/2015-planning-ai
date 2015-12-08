package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Makes a deep copy of the map data structures contained in ActionGraph
 *
 * @author Shane McCulley
 */
public final class DeepCloneMap {

    private DeepCloneMap(){}

    @SuppressWarnings("unchecked")
	public static <X> X deepClone(final X input) {
        if (input == null) {
            return input;
        } else if (input instanceof Map<?, ?>) {
            return (X) deepCloneMap((Map<?, ?>) input);
        } else if (input instanceof Collection<?>) {
            return (X) deepCloneCollection((Collection<?>) input);
        }
        
        return input;
            
    }

    private static <E> Collection<E> deepCloneCollection(final Collection<E> input) {
        Collection<E> clone;
        // this is of course far from comprehensive. extend this as needed
       if (input instanceof Set) {
            clone = new HashSet<E>();
       } else if (input instanceof List){
            clone = new ArrayList<E>();
       } else throw new IllegalArgumentException("Invalid collection type in map");
    
        for (E item : input) {
            clone.add(deepClone(item));
        }

        return clone;
    }

    private static <K, V> Map<K, V> deepCloneMap(final Map<K, V> map) {
        Map<K, V> clone;
        // this is of course far from comprehensive. extend this as needed
        if (map instanceof HashMap<?, ?>) {
            clone = new HashMap<K, V>();
        } else if (map instanceof TreeMap<?, ?>) {
            clone = new TreeMap<K, V>();
        } else {
            clone = new LinkedHashMap<K, V>();
        }

        for (Entry<K, V> entry : map.entrySet()) {
            clone.put(deepClone(entry.getKey()), deepClone(entry.getValue()));
        }

        return clone;
    }
}
