package at.steell.spring.rest.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import at.steell.spring.rest.utils.dto.IdentifierQueryRequest;

/**
 * Utility class for splitting up {@link IdentifierQueryRequest} into multiple {@link IdentifierQueryRequest}
 *
 * @author Stefan Ellersdorfer (xel)
 */
public final class IdentifierQueryRequestSplitter
{
    /**
     * the uniform ID type length, as we expect UUIDs of 36 chars length, plus 3 character for the delimiter because it
     * is encoded
     */
    private static final int UNIFORM_ID_LENGTH = 36 + 3;
    /** the maximum length of payload usable in a request */
    private static final int MAXIMUM_REQ_LENGTH = 1000;

    /**
     * Utility method to split a given request payload to not exceed a length of {@value #MAXIMUM_REQ_LENGTH} bytes
     *
     * @param request the request to be split
     * @param <T> the type of id contained in the given request to be split
     * @return a list of requests split into chunks
     */
    public static <T extends Serializable & Comparable<T>> List<IdentifierQueryRequest<T>> split(
        final IdentifierQueryRequest<T> request)
    {
        if (request == null)
        {
            return null;
        }

        if (exceedingRequestLength(request))
        {
            return splitRequest(request);
        }

        return Collections.singletonList(request);
    }

    /**
     * Internal utility method splitting a request payload to not exceed a length of {@value #MAXIMUM_REQ_LENGTH} bytes
     *
     * @param request the request to be split
     * @return a list of requests split into chunks
     */
    private static <T extends Serializable & Comparable<T>> List<IdentifierQueryRequest<T>> splitRequest(
        final IdentifierQueryRequest<T> request)
    {
        //sort the ids to allow caching to work properly
        return ListUtils.partition(request.getIds().stream().sorted().collect(Collectors.toList()),
            MAXIMUM_REQ_LENGTH / UNIFORM_ID_LENGTH).stream()
            .map(IdentifierQueryRequest::new)
            .collect(Collectors.toList());
    }

    /**
     * Internal utility to check if the given request payload exceeds the {@value #MAXIMUM_REQ_LENGTH}
     *
     * @param request the request with probably to long payload
     * @return <code>true</code> in case the request payload is too long, otherwise <code>false</code> is returned
     */
    private static <T extends Serializable & Comparable<T>> boolean exceedingRequestLength(
        final IdentifierQueryRequest<T> request)
    {
        final StringBuilder sb = new StringBuilder();
        request.getIds().forEach(sb::append);

        int n = request.getIds().size();

        /* the length of the concatenated identifier values + the length of the required delimiters */
        int len = sb.length() + (n - 1) * 3;
        return len > MAXIMUM_REQ_LENGTH;
    }

    /** prevent instantiation */
    private IdentifierQueryRequestSplitter()
    {
    }
}
