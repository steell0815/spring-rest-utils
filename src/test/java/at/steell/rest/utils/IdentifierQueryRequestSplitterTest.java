package at.steell.rest.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import at.steell.spring.rest.utils.IdentifierQueryRequestSplitter;
import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;

/**
 * Unit tests for {@link IdentifierQueryRequestSplitter} utility
 *
 * @author Stefan Ellersdorfer (xel)
 */
public class IdentifierQueryRequestSplitterTest
{
    @Test
    public void testSplit()
    {
        /* given */
        int amount = RandomUtils.nextInt(100, 10000);
        List<String> ids = createIds(amount);
        IdentifierQueryRequest<String> request = new IdentifierQueryRequest<>(ids);

        /* when */
        List<IdentifierQueryRequest<String>> result = IdentifierQueryRequestSplitter.split(request);

        /* then */
        for (IdentifierQueryRequest<String> req : result)
        {
            for (String id : req.getIds())
            {
                assertTrue(ids.contains(id));
                ids.remove(id);
            }
        }
        assertTrue(ids.isEmpty());
    }

    @Test
    public void testSplitNull()
    {
        assertNull(IdentifierQueryRequestSplitter.split(null));
    }

    @Test
    public void testSplitOne()
    {
        /* given */
        List<String> ids = createIds(1);
        IdentifierQueryRequest<String> request = new IdentifierQueryRequest<>(ids);

        /* when */
        List<IdentifierQueryRequest<String>> result = IdentifierQueryRequestSplitter.split(request);

        /* then */
        assertEquals(result.size(), 1);
        for (IdentifierQueryRequest<String> req : result)
        {
            for (String id : req.getIds())
            {
                assertTrue(ids.contains(id));
                ids.remove(id);
            }
        }
        assertTrue(ids.isEmpty());
    }

    /**
     * Test utility to create some ID's for testing
     *
     * @param amount the amount of ID's to create
     * @return the created list of ID's
     */
    private List<String> createIds(int amount)
    {
        List<String> idList = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++)
        {
            idList.add(UUID.randomUUID().toString());
        }
        return idList;
    }
}
