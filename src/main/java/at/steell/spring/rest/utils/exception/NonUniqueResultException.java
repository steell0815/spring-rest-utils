package at.steell.spring.rest.utils.exception;

import at.steell.spring.rest.utils.dto.TypedResponse;

/**
 * Thrown by the {@link TypedResponse#getSingleResult()} because the caller expects one result but actually multiple
 * results were returned by the call.
 */
public class NonUniqueResultException extends RuntimeException
{
    private static final long serialVersionUID = 1;

    /**
     * Default constructor
     */
    public NonUniqueResultException()
    {
        super();
    }

    /**
     * @param message The message to be set for the exception
     */
    public NonUniqueResultException(String message)
    {
        super(message);
    }
}
