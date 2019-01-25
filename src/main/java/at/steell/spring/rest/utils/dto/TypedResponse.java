package at.steell.spring.rest.utils.dto;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import at.steell.spring.rest.utils.exception.NoResultException;
import at.steell.spring.rest.utils.exception.NonUniqueResultException;

/**
 * A typed response object for delivering result objects of the given type.
 *
 * @author Markus Jessenitschnig (XJM)
 * @param <T> The concrete Type the response delivers
 */
public class TypedResponse<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Set<T> elements = new HashSet<>();

    /**
     * Default constructor required for JSON marshaling
     */
    public TypedResponse()
    {
        super();
    }

    /**
     * @param ids The {@link Set} of elements
     */
    public TypedResponse(final Set<T> ids)
    {
        setElements(ids);
    }

    /**
     * @return The {@link Set} of elements
     */
    public Set<T> getElements()
    {
        return Collections.unmodifiableSet(elements);
    }

    /**
     * Returns a single result from the found elements. <br>
     * Needs to be @Transient so that it is not being serialized during network transmission
     *
     * @return the found element
     * @throws NoResultException - if there is no result
     * @throws NonUniqueResultException - if more than one result
     */
    @Transient
    public T getSingleResult() throws NoResultException, NonUniqueResultException
    {
        if (this.elements.isEmpty())
        {
            throw new NoResultException();
        }
        if (this.elements.size() > 1)
        {
            throw new NonUniqueResultException();
        }
        else
        {
            return this.elements.iterator().next();
        }
    }

    /**
     * Returns if at least one result is available
     *
     * @return if at least one result is available
     */
    public boolean hasResults()
    {
        return !elements.isEmpty();
    }

    /**
     * Test if exactly one result is available.
     *
     * @return true if exactly one result is available
     */
    public boolean hasSingleResult()
    {
        return elements.size() == 1;
    }

    /**
     * Returns a {@link Map} containing the elements indexed by applying the supplied function. </br>
     * </br>
     * <b>Example:</b></br>
     *
     * <pre>
     * TypedResult&lt;Foo&gt; result = ...;
     * Map&lt;String, Foo&gt; elements = result.getElementsIndexed(f -&gt; f.getId());
     * </pre>
     *
     * @param idFunction The {@link Function} to be used
     * @param <KEY_TYPE> The type of the key
     * @return The {@link Map} containing elements indexed by applying the supplied function
     */
    public <KEY_TYPE extends Serializable> Map<KEY_TYPE, T> getElementsIndexed(final Function<T, KEY_TYPE> idFunction)
    {
        return getElements().stream().collect(Collectors.toMap(idFunction, Function.identity()));
    }

    /**
     * Sets the new {@link Set} of elements.
     *
     * @param newElements The {@link Set} of elements
     * @return The {@link TypedResponse} to support a fluent API
     */
    public TypedResponse<T> setElements(final Set<T> newElements)
    {
        this.elements.clear();
        if (newElements != null)
        {
            this.elements.addAll(newElements);
        }
        return this;
    }

    /**
     * Adds the given element to {@link Set} of elements.
     *
     * @param element The element to be added
     * @return The {@link TypedResponse} to support a fluent API
     */
    public TypedResponse<T> add(T element)
    {
        if (element != null)
        {
            this.elements.add(element);
        }
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TypedResponse)
        {
            TypedResponse<?> other = (TypedResponse<?>) obj;
            return new EqualsBuilder().append(getElements(), other.getElements()).isEquals();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getElements()).toHashCode();
    }
}
