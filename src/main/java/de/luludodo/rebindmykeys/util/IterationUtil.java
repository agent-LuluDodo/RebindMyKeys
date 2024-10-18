package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class IterationUtil {
    public static class ConditionalIterable<E> implements Iterable<E> {
        private final Iterable<E> iterable;
        private final Predicate<E> condition;
        public ConditionalIterable(Iterable<E> iterable, Predicate<E> condition) {
            this.iterable = iterable;
            this.condition = condition;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new ConditionalIterator<>(iterable.iterator(), condition);
        }
    }

    public static class ConditionalIterator<E> implements Iterator<E> {
        private final Iterator<E> iterator;
        private final Predicate<E> condition;
        private E next = null;
        public ConditionalIterator(Iterator<E> iterator, Predicate<E> condition) {
            this.iterator = iterator;
            this.condition = condition;

            getNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();

            return getNext();
        }

        public E getNext() {
            E cur = next;
            next = null;
            while (this.next == null && iterator.hasNext()) {
                E maybeNext = iterator.next();
                if (condition.test(maybeNext))
                    this.next = maybeNext;
            }
            return cur;
        }
    }

    public static <E> Iterable<E> iterable(Iterable<E> iterable, Predicate<E> condition) {
        return new ConditionalIterable<>(iterable, condition);
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterable<E> iterable(Iterable<?> iterable, final Class<E> targetCl) {
        return (Iterable<E>) new ConditionalIterable<>(iterable, targetCl::isInstance);
    }
}
