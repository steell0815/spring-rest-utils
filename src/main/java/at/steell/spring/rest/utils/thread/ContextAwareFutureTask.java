package at.steell.spring.rest.utils.thread;

import java.io.Serializable;
import java.util.concurrent.FutureTask;

import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.ContextAwareCallable;

/**
 * Future task derived class for type safe future task thread management in {@link ThreadPoolExecutorImpl}
 *
 * @author Stefan Ellersdorfer (xel)
 * @param <RESPONSE> the payload type of responses to get from the future task
 * @param <ID> the id type of requests used in the future invocation
 */
public class ContextAwareFutureTask<RESPONSE extends Serializable, ID extends Serializable & Comparable<ID>>
    extends FutureTask<TypedResponse<RESPONSE>>
{
    private final ContextAwareCallable<RESPONSE, ID> callable;

    /**
     * Constructs the future task, type safe for {@link ContextAwareCallable<RESPONSE, ID>}
     *
     * @param callable the callable to be used
     */
    public ContextAwareFutureTask(final ContextAwareCallable<RESPONSE, ID> callable)
    {
        super(callable);
        this.callable = callable;
    }

    public ContextAwareCallable<RESPONSE, ID> getCallable()
    {
        return callable;
    }
}
