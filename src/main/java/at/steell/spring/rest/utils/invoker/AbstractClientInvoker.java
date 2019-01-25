package at.steell.spring.rest.utils.invoker;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import at.steell.spring.rest.utils.IdentifierQueryRequestSplitter;
import at.steell.spring.rest.utils.TypedResponseCollector;
import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;
import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.thread.ContextAwareExecutor;

/**
 * Abstract base class for all client invoker implementation. Client invokers are intended to interact with the feign
 * clients and should implement parameter validation. The invokers should also implement request splitting (e.g. when
 * requesting domain objects for a huge list of identifier values)
 *
 * @param <CLIENT> the type of client to be used
 * @author Markus Jessenitschnig (XJM)
 * @author Stefan Ellersdorfer (xel)
 */
public abstract class AbstractClientInvoker<CLIENT>
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClientInvoker.class);
    private final CLIENT client;
    private final ContextAwareExecutor executor;

    /**
     * Constructor
     *
     * @param client the {@link CLIENT} to be used for performing the request in a real world scenario
     * @param executor the {@link ContextAwareExecutor} to create parallel requesting threads
     */
    public AbstractClientInvoker(final CLIENT client, final ContextAwareExecutor executor)
    {
        this.client = client;
        this.executor = executor;
    }

    /**
     * Protected function to create parallel requests by splitting the given request parameters to a suitable size for
     * the given degree of parallelism. In case degree of parallelism is higher than the amount of identifiers
     * requested, that degree is decreased to the amount of identifiers.
     *
     * @param function the clients function to be invoked in parallel, capable of taking a get resource or get
     *     resources
     *     request that requests a {@link IdentifierQueryRequest} and responds with a {@link TypedResponse}
     * @param request the request detail that should be split and processed in parallel
     * @param <ID> the identifier type of {@link IdentifierQueryRequest} to process
     * @param <RESPONSE> the response type
     * @return the collected {@link TypedResponse}
     */
    @SuppressWarnings("squid:S2142") //InterruptedException should not be ignored: will be handled differently here
    protected <RESPONSE extends Serializable, ID extends Serializable & Comparable<ID>> TypedResponse<RESPONSE> invoke(
        final Function<IdentifierQueryRequest<ID>, TypedResponse<RESPONSE>> function,
        final IdentifierQueryRequest<ID> request)
    {
        final Locale locale = LocaleContextHolder.getLocale();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final List<ContextAwareCallable<RESPONSE, ID>> callables =
            IdentifierQueryRequestSplitter.split(request).stream()
                .map(req -> {
                    LOG.debug("prepare context aware callable");
                    return new ContextAwareCallable<>(function, req, locale, authentication);
                })
                .collect(Collectors.toList());

        try
        {
            final TypedResponse<RESPONSE> response = executor.invokeAll(callables).stream()
                .map(this::getResponse).collect(new TypedResponseCollector<RESPONSE>());
            return response;
        }
        catch (final InterruptedException e)
        {
            throw new IllegalStateException(e);
        }
        catch (final IllegalStateException e)
        {
            if (e.getCause() instanceof RuntimeException)
            {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * protected getter to give derived classes access to the client
     *
     * @return the client
     */
    protected CLIENT getClient()
    {
        return client;
    }

    private <RESPONSE extends Serializable> TypedResponse<RESPONSE> getResponse(
        final Future<TypedResponse<RESPONSE>> future)
    {
        try
        {
            return future.get();
        }
        catch (final Throwable t)
        {
            throw new IllegalStateException(t);
        }
    }
}
