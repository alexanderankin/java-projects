package info.ankin.projects.ada;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.CharBuffer;
import java.util.Objects;

interface Ada4jUtils {
    static CharSequence stringView(String string, int start, int size) {
        return new DelegatingCharSequence(CharBuffer.wrap(string).slice(start, size));
    }

    /**
     * this is class is sort of wasteful, but the indirection will help later refactors
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    class DelegatingCharSequence implements CharSequence {
        @Getter
        private final CharBuffer delegate;

        @Override
        public int length() {
            return delegate.capacity();
        }

        @Override
        public char charAt(int index) {
            return delegate.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new DelegatingCharSequence(delegate.slice(start, end - start));
        }

        public String toString() {
            return delegate.toString();
        }
    }
}
