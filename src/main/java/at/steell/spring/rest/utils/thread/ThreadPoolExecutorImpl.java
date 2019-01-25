package at.steell.spring.rest.utils.thread;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import at.steell.spring.rest.utils.dto.TypedResponse;
import at.steell.spring.rest.utils.invoker.ContextAwareCallable;

/**
 * Derived {@link ThreadPoolExecutor} utilizing {@link #beforeExecute(Thread, Runnable)} and
 * {@link #afterExecute(Runnable, Throwable)} methods to setup tenant-, locale- and security- context in case given
 * runnables are {@link ContextAwareCallable} instances. This bean is installed once in the application context in order
 * to centrally manage the number of threads used to handle parallel invocation of rest execution
 *
 * @author Stefan Ellersdorfer (xel)
 */
public class ThreadPoolExecutorImpl extends ThreadPoolExecutor implements ContextAwareExecutor
{

    /**
     * Creates a new {@code ThreadPoolExecutorImpl} with given initial parameters and default thread factory and
     * rejected execution handler.
     *
     * @param poolSize the number of threads to keep in the pool, even if they are idle, unless
     *            {@code allowCoreThreadTimeOut} is set, number of maximum threads as well
     * @throws IllegalArgumentException if one of the following holds:<br>
     *             {@code corePoolSize < 0}<br>
     *             {@code keepAliveTime < 0}<br>
     *             {@code maximumPoolSize <= 0}<br>
     *             {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue} is null
     */
    public ThreadPoolExecutorImpl(final int poolSize)
    {
        super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Overridden to control that instead of a regular {@link FutureTask} instance for the callable, a
     * {@link ContextAwareFutureTask} instance is created. This assures, that no instance of checks are needed in
     * {@link #beforeExecute(Thread, Runnable)} invocations, that are used to set the context<br />
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable)
    {
        return (RunnableFuture<T>) new ContextAwareFutureTask<>(
            (ContextAwareCallable<?, ?>) callable);
    }

    /**
     * Setup the threads context information, such as Tenant context, Locale and Security context<br />
     * {@inheritDoc}
     */
    @Override
    protected void beforeExecute(final Thread t, final Runnable r)
    {
        super.beforeExecute(t, r);
        final ContextAwareFutureTask<?, ?> futureTask = (ContextAwareFutureTask<?, ?>) r;
        futureTask.getCallable().beforeCall();
    }

    /**
     * Overridden to clean up the threads context information, for reusing threads without interfering with prior
     * usages<br />
     * {@inheritDoc}
     */
    @Override
    protected void afterExecute(final Runnable r, final Throwable t)
    {
        super.afterExecute(r, t);
        final ContextAwareFutureTask<?, ?> futureTask = (ContextAwareFutureTask<?, ?>) r;
        futureTask.getCallable().afterCall();
    }

    @Override
    //CHECKSTYLE:OFF too long generics
    public <RESPONSE extends Serializable, ID extends Serializable & Comparable<ID>> List<Future<TypedResponse<RESPONSE>>> invokeAll(
        final List<ContextAwareCallable<RESPONSE, ID>> callables) throws InterruptedException
    //CHECKSTYLE:ON
    {
        return super.invokeAll(callables);
    }
}
