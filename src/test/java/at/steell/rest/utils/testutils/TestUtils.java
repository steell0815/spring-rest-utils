package at.steell.rest.utils.testutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TestUtils
{
    /**
     * splits a given set into a subsets, each with a given maximal size. one set can be smaller
     *
     * @param items the ids to split
     * @param subsetSize max size of subsets
     * @param <T> generic type
     * @return a collection of subsets
     */
    public static <T> Collection<Set<T>> partitionSet(Set<T> items, int subsetSize)
    {
        if (subsetSize <= 0)
        {
            throw new IllegalArgumentException("subsetSize has to be greater then 0");
        }
        if (items == null || items.isEmpty())
        {
            return Collections.emptyList();
        }

        return doPartitionSet(items, subsetSize);
    }

    private static <T> Collection<Set<T>> doPartitionSet(Set<T> items, int subsetSize)
    {
        Collection<Set<T>> superSet = new ArrayList<>();
        Set<T> currentSet = null;
        int count = subsetSize;
        for (T id : items)
        {
            if (currentSet == null)
            {
                currentSet = new HashSet<>();
                superSet.add(currentSet);
            }
            currentSet.add(id);
            count--;
            if (count == 0)
            {
                count = subsetSize;
                currentSet = null;
            }
        }
        return superSet;
    }

    /**
     * prevent instantiation
     */
    private TestUtils()
    {
    }
}
