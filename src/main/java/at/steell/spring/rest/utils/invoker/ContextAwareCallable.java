package at.steell.spring.rest.utils.invoker;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;
import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.thread.ExecutionContext;

/**
 * Context aware callable, encapsulating a function call together with context information, containing tenant, locale
 * and security context information. This information is used to provide the callers context to multiple child threads
 * created for parallel request processing
 *
 * @author Stefan Ellersdorfer (xel)
 * @param <RESPONSE> the response payload type
 * @param <ID> the id type used in requests
 */
public class ContextAwareCallable<RESPONSE extends Serializable, ID extends Serializable & Comparable<ID>>
    implements Callable<TypedResponse<RESPONSE>>
{
    private final Function<IdentifierQueryRequest<ID>, TypedResponse<RESPONSE>> function;
    private final IdentifierQueryRequest<ID> request;
    private final Locale locale;
    private final Authentication authentication;
    private final ExecutionContext executionContext;

    /**
     * Constructs the callable
     *
     * @param function the function pointer to invoke in a dedicated thread
     * @param request the request used to apply with the function
     * @param tenantId the tenant id to setup a tenant context in the thread performing the invocation
     * @param locale the locale to setup in the thread performing the invocation
     * @param authentication the authentication to setup the security context in the thread performing the invocation
     * @param shouldCacheBeCleared flag which indicate if the {@link FeignCacheStateContext} is set to clear the cache
     * @param requestId the unique id given by {@link FeignCacheStateContext}
     * @param rtId the request tracking id
     */
    public ContextAwareCallable(final Function<IdentifierQueryRequest<ID>, TypedResponse<RESPONSE>> function,
        final IdentifierQueryRequest<ID> request,
        final Locale locale,
        final Authentication authentication)
    {
        this.function = function;
        this.request = request;
        this.locale = locale;
        this.authentication = authentication;

        this.executionContext = ExecutionContext.start();
        this.executionContext.recordStackTrace();
    }

    @Override
    public TypedResponse<RESPONSE> call() throws Exception
    {
        return function.apply(request);
    }

    /**
     * Gets called before the actual call is done. This method is used to prepare shared context holders.
     */
    public void beforeCall()
    {
        LocaleContextHolder.setLocale(locale);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ExecutionContext.set(this.executionContext);
    }

    /**
     * Gets called after the actual call is done. This method is used to clean up shared context holders.
     */
    public void afterCall()
    {
        LocaleContextHolder.resetLocaleContext();
        SecurityContextHolder.clearContext();
        ExecutionContext.clear();
    }
}
