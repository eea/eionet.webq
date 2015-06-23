package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class CollectionUtil {
    
    public static <T> boolean equals(Collection<T> c1, Collection<T> c2, Comparator<T> comparator) {
        return equals(c1, c2, comparator, comparator);
    }
    
    public static <T> boolean equals(Collection<T> c1, Collection<T> c2, Comparator<T> sortComparator, Comparator<T> equalityComparator) {
        if (c1.size() != c2.size()) {
            return false;
        }
        
        ArrayList<T> l1 = new ArrayList<T>(c1);
        ArrayList<T> l2 = new ArrayList<T>(c2);
        Collections.sort(l1, sortComparator);
        Collections.sort(l2, sortComparator);
        Iterator<T> it1 = l1.iterator();
        Iterator<T> it2 = l2.iterator();
        
        while (it1.hasNext()) {
            T item1 = it1.next();
            T item2 = it2.next();
            
            if (equalityComparator.compare(item1, item2) != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    private CollectionUtil() { }
}
