package at.steell.spring.rest.utils.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The request objected intended to query all instances for the given identifier values.
 *
 * @author Markus Jessenitschnig (XJM)
 * @param <T> The concrete type of the identifiers
 */
public final class IdentifierQueryRequest<T extends Serializable & Comparable<T>> extends AbstractRequest
{
    private static final long serialVersionUID = 1L;

    private final Set<T> ids = new HashSet<>();

    /**
     * Default constructor required for JSON marshaling
     */
    public IdentifierQueryRequest()
    {
        super();
    }

    /**
     * @param ids The {@link Collection} of identifiers to query for
     */
    public IdentifierQueryRequest(final Collection<T> ids)
    {
        setIds(ids);
    }

    /**
     * @return The {@link Set} of identifiers to query for
     */
    public Set<T> getIds()
    {
        return Collections.unmodifiableSet(ids);
    }

    /**
     * @param ids The {@link Collection} of identifiers
     */
    public void setIds(final Collection<T> ids)
    {
        this.ids.clear();
        if (ids != null)
        {
            this.ids.addAll(ids);
        }
    }

    /**
     * @return Indicator whether the request contains any identifiers to query for
     */
    public boolean isEmpty()
    {
        return this.ids.isEmpty();

    }

    @Override
    public int hashCode()
    {
        return ids.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final IdentifierQueryRequest<?> other = (IdentifierQueryRequest<?>) obj;
        return ids.equals(other.ids);
    }

    /**
     * Custom toString to allow correct serialization for feign client requests
     *
     * @return the values, comma separated
     */
    @Override
    public String toString()
    {
        //use sorted ids in order to let caching work properly
        return this.ids.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Used for deserialization for feign client requests
     *
     * @param source the data as received from {@link #toString()}
     * @param converter a function to convert every identifier into the correct type
     * @param <T> The concrete type of the identifier
     * @return an {@link IdentifierQueryRequest} with all identifiers in the correct type
     */
    public static <T extends Serializable & Comparable<T>> IdentifierQueryRequest<T> fromString(final String source,
        final Function<String, T> converter)
    {
        if (source == null)
        {
            return null;
        }

        final IdentifierQueryRequest<T> request = new IdentifierQueryRequest<>();
        request.setIds(Stream.of(source.split(",")).map(converter).collect(Collectors.toSet()));
        return request;
    }

    /**
     * Constructs a {@link IdentifierQueryRequest} for the given identifier values
     *
     * @param ids The identifier values
     * @param <T> The concrete type of the identifier
     * @return The newly created {@link IdentifierQueryRequest}
     */
    @SafeVarargs
    public static <T extends Serializable & Comparable<T>> IdentifierQueryRequest<T> of(final T... ids)
    {
        final IdentifierQueryRequest<T> request = new IdentifierQueryRequest<>();
        if (ids != null)
        {
            request.setIds(Arrays.asList(ids));
        }
        return request;
    }

    /**
     * Constructs a {@link IdentifierQueryRequest} for the given identifier values
     *
     * @param ids The identifier values
     * @param <T> The concrete type of the identifier
     * @return The newly created {@link IdentifierQueryRequest}
     */
    public static <T extends Serializable & Comparable<T>> IdentifierQueryRequest<T> of(final Collection<T> ids)
    {
        final IdentifierQueryRequest<T> request = new IdentifierQueryRequest<>();
        if (ids != null)
        {
            request.setIds(ids);
        }
        return request;
    }
}
