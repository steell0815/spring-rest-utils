package at.steell.spring.rest.utils;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import at.steell.spring.rest.utils.dto.TypedResponse;

/**
 * Java8 lamda collector for {@link TypedResponse} result collecting in stream operations
 *
 * @author Stefan Ellersdorfer (xel)
 * @param <TYPE> the type of payload to be used
 */
public class TypedResponseCollector<TYPE extends Serializable>
    implements Collector<TypedResponse<TYPE>, TypedResponse<TYPE>, TypedResponse<TYPE>>
{
    @Override
    public Set<Characteristics> characteristics()
    {
        return EnumSet.of(Characteristics.UNORDERED, Characteristics.CONCURRENT);
    }

    @Override
    public Supplier<TypedResponse<TYPE>> supplier()
    {
        return TypedResponse::new;
    }

    @Override
    public BiConsumer<TypedResponse<TYPE>, TypedResponse<TYPE>> accumulator()
    {
        return this::combine;
    }

    @Override
    public BinaryOperator<TypedResponse<TYPE>> combiner()
    {
        return this::combine;
    }

    @Override
    public Function<TypedResponse<TYPE>, TypedResponse<TYPE>> finisher()
    {
        return Function.identity();
    }

    /**
     * Internal utility to add payload of right to left and return left
     *
     * @param left the left {@link TypedResponse}
     * @param right the right {@link TypedResponse}
     * @return the combined left
     */
    private TypedResponse<TYPE> combine(final TypedResponse<TYPE> left, final TypedResponse<TYPE> right)
    {
        final Set<TYPE> combined = new HashSet<>();
        combined.addAll(left.getElements());
        combined.addAll(right.getElements());
        left.setElements(combined);
        return left;
    }
}
