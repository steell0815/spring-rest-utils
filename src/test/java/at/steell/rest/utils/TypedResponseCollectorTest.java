package at.steell.rest.utils;

import static at.steell.rest.utils.testutils.TestUtils.partitionSet;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import at.steell.spring.rest.utils.TypedResponseCollector;
import at.steell.spring.rest.utils.dto.TypedResponse;

public class TypedResponseCollectorTest
{

    @Test
    public void testCollector()
    {
        /* given */
        Set<String> payloads = createRandomPayloads();
        Collection<TypedResponse<String>> responses = splitPayloadsToMultipleResponses(payloads);
        TypedResponse<String> collectedResponse = responses.stream().collect(new TypedResponseCollector<String>());
        collectedResponse.getElements().stream().forEach(responseElement ->
        {
            assertTrue(payloads.contains(responseElement));
            payloads.remove(responseElement);
        });
        assertTrue(payloads.isEmpty());
    }

    private Set<String> createRandomPayloads()
    {
        Set<String> payloads = new HashSet<>();
        for (int i = 0; i < RandomUtils.nextInt(1000, 10000); i++)
        {
            payloads.add(RandomStringUtils.randomAlphanumeric(100));
        }
        return payloads;
    }

    private Set<TypedResponse<String>> splitPayloadsToMultipleResponses(Set<String> payloads)
    {
        Collection<Set<String>> listOfPayloads = partitionSet(payloads, 100);
        Set<TypedResponse<String>> responses = new HashSet<>();
        listOfPayloads.stream().forEach(set -> responses.add(new TypedResponse<>(set)));
        return responses;
    }
}
