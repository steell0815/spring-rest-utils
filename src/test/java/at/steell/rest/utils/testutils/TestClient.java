package at.steell.rest.utils.testutils;

import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;
import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.AbstractClientInvoker;

/**
 * Test client for testing the {@link AbstractClientInvoker}
 *
 * @author Stefan Ellersdorfer (xel)
 */
public class TestClient
{
    /**
     * Requests ressources
     *
     * @param ids the ids to request
     * @return a typed response of the given ids
     */
    public TypedResponse<String> getRessources(final IdentifierQueryRequest<String> ids)
    {
        TypedResponse<String> response = new TypedResponse<>();
        response.setElements(ids.getIds());
        return response;
    }
}
