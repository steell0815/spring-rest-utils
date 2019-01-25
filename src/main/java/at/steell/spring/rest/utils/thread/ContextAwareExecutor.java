package at.steell.spring.rest.utils.thread;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.ContextAwareCallable;

/**
 * interface for contextaware restcall executors
 *
 * @author Matthias Ableidinger (ext.mableidinger)
 */
public interface ContextAwareExecutor
{

    /**
     * invoke all restcalls wrapped in a contextawarecallable
     *
     * @param callables restcalls to execute
     * @return list of futures
     * @throws InterruptedException if errors happen
     */
    //CHECKSTYLE:OFF too long generics
    <RESPONSE extends Serializable, ID extends Serializable & Comparable<ID>> List<Future<TypedResponse<RESPONSE>>> invokeAll(
        List<ContextAwareCallable<RESPONSE, ID>> callables) throws InterruptedException;
    //CHECKSTYLE:ON

}
