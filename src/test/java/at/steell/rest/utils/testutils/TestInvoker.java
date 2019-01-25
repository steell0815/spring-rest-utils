package at.steell.rest.utils.testutils;

import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;
import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.AbstractClientInvoker;
import at.steell.spring.rest.utils.thread.ThreadPoolExecutorImpl;

/**
 * A test invoker implementation to the {@link AbstractClientInvoker} and multithreading
 *
 * @author Stefan Ellersdorfer (xel)
 */
public class TestInvoker extends AbstractClientInvoker<TestClient>
{
    /**
     * Constructs the test invoker with default values
     */
    public TestInvoker()
    {
        super(new TestClient(), new ThreadPoolExecutorImpl(3));
    }

    public TypedResponse<String> getRessources(final IdentifierQueryRequest<String> ids)
    {
        return super.invoke(getClient()::getRessources, ids);
    }
}
