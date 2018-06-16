// Copyright 2015-03-06 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.function

import java.util.concurrent.Callable
import java.util.function.Supplier

/**
 * This is like Java 8's java.util.function.Supplier, but retrofitted to turn checked exceptions
 * into unchecked ones.  It's also called a thunk when used to delay evaluation.
 */
@FunctionalInterface
interface Fn0<U> : Supplier<U>, Callable<U>, () -> U {
    /** Implement this one method and you don't have to worry about checked exceptions.  */
    @Throws(Exception::class)
    fun invokeEx(): U

    /**
     * The class that takes a consumer as an argument uses this convenience method so that it
     * doesn't have to worry about checked exceptions either.
     */
    @JvmDefault
    override fun invoke(): U {
        try {
            return invokeEx()
        } catch (re: RuntimeException) {
            throw re
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /** {@inheritDoc}  */
    @JvmDefault
    override fun get(): U {
        return invoke()
    }

    /** {@inheritDoc}  */
    @Throws(Exception::class)
    @JvmDefault
    override fun call(): U {
        return invokeEx()
    }

    companion object {
        // This should be more useful once Kotlin 1.3 gets rid of the .Companion. with the JvmStatic annotation.
        /** For Java users to be able to cast without explicitly writing out the types. */
        fun <U> toFn0(f:Fn0<U>):() -> U = f
    }

    // ========================================== Static ==========================================
    // Enums are serializable.  Anonymous classes and lambdas are not.

    //    /** One-of-a-kind Fn0's. */
    //    enum ConstObjObj implements Fn0<Object> {
    //        NULL {
    //            @Override public Object invokeEx() throws Exception { return null; }
    //        }
    //    }

    //    /**
    //     Wraps a value in a constant function.  If you need to "memoize" some really expensive
    //     operation, use a {@link org.organicdesign.fp.function.LazyRef}.
    //     */
    //    class Constant<K> implements Fn0<K>, Serializable {
    //        private static final long serialVersionUID = 201608281356L;
    //        private final K k;
    //        Constant(K theK) { k = theK; }
    //        @Override public K invokeEx() { return k; }
    //        @Override public int hashCode() { return (k == null) ? 0 : k.hashCode(); }
    //        @Override public boolean equals(Object o) {
    //            if (this == o) { return true; }
    //            if ( (o == null) || !(o instanceof Constant) ) { return false; }
    //            return k.equals(((Constant) o).get());
    //        }
    //        @Override public String toString() { return "() -> " + k; };
    //    }
    //
    //    static <K> Fn0<K> constantFunction(final K k) { return new Constant<>(k); }

    // Don't think this is necessary.  Is it?
    //    default Supplier<U> asSupplier() {
    //        return () -> invoke();
    //    }
}
