package at.steell.rest.utils.invoker;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import at.steell.rest.utils.testutils.TestInvoker;
import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;
import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.AbstractClientInvoker;
import at.steell.spring.rest.utils.thread.ThreadPoolExecutorImpl;

public class TestInvokerTest
{

    /**
     * Test the invocation via {@link AbstractClientInvoker}, containing resource splitting and combining results after
     * each splitted request has been operated successfully. Each request is handled in a different thread utilizing the
     * {@link ThreadPoolExecutorImpl}
     */
    @Test
    public void testInvocation()
    {
        TestInvoker invoker = new TestInvoker();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            ids.add(UUID.randomUUID().toString());
        }
        TypedResponse<String> response = invoker.getRessources(new IdentifierQueryRequest<>(ids));
        assertTrue(response.getElements().size() == ids.size());
        assertTrue(ids.containsAll(response.getElements()));
    }
}
