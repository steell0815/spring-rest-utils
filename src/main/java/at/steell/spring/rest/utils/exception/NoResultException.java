package at.steell.spring.rest.utils.exception;

import at.steell.spring.rest.utils.dto.TypedResponse;

/**
 * Thrown by the {@link TypedResponse#getSingleResult()} because the caller expects a result but actually no result was
 * returned by the call.
 */
public class NoResultException extends RuntimeException
{
    private static final long serialVersionUID = 1;

    /**
     * Default constructor
     */
    public NoResultException()
    {
        super();
    }

    /**
     * @param message The message to be set for the exception
     */
    public NoResultException(String message)
    {
        super(message);
    }
}
