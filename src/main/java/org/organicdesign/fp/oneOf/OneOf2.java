// Copyright 2015 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.type.RuntimeTypes.union2Str;

/**
 This is designed to represent a union of 2 types, meaning an object that can be one type, or another.
 Instead of a get() method, pass 2 functions to match(), one to handle the case where this contains the first thing,
 the other if it contains the second thing.  In theory, this could work with two things of the same type, but
 Java has polymorphism to handle that more easily.  Before using a OneOf2, make sure you don't really need a
 {@link org.organicdesign.fp.tuple.Tuple2}.

 OneOf2 is designed to be sub-classed so you can add descriptive names.  The safest way
 to use Union classes is to always call match() because it forces you to think about how to
 handle each type you could possibly receive.

 Usage:
 <pre><code>
thingy.match(fst -&gt; fst.doOneThing(),
                 sec -&gt; sec.doSomethingElse());
 </code></pre>

 Sometimes it's a programming error to pass one type or another and you may want to throw an
 exception.
 <pre><code>
oneOf.match(fst -&gt; fst.doOneThing(),
                sec -&gt; { throw new IllegalStateException("Asked for a 2nd; only had a 1st."); });
 </code></pre>

 For the shortest syntax and best names, define your own subclass.  This is similar to sub-classing Tuples.
 <pre><code>
static class String_Integer extends OneOf2&lt;String,Integer&gt; {

    // Private Constructor because the o parameter is not type safe.
    private String_Integer(Object o, int n) { super(o, String.class, Integer.class, n); }

    // Static factory methods ensure type-safe construction.
    public static String_Integer ofStr(String o) { return new String_Integer(o, 0); }
    public static String_Integer ofInt(Integer o) { return new String_Integer(o, 1); }
}</code></pre>

 equals(), hashcode(), and toString() are all taken care of for you.

 Now you use descriptive and extremely brief syntax:
 <pre><code>
// Type-safe switching - always works at runtime.
x.match(s -> (s == null) ? null : s.lowerCase(),
n -> "This is the number " + n);

// If not a String at runtime throws "Expected a(n) String but found a(n) Integer"
x.str().contains("goody!");

 // If not an Integer at runtime throws "Expected a(n) Integer but found a(n) String"
3 + x.integer();

 </code></pre>
 */
// TODO: Should this implement javax.lang.model.type.UnionType somehow?
public class OneOf2<A,B> {

    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    /**
     Protected constructor for subclassing.  A, B, and C parameters can be null, but if one is non-null, the index
     must specify the non-null value (to keep you from assigning a bogus index value).

     @param o the item
     @param aClass class 0 (to have at runtime for descriptive error messages and toString()).
     @param bClass class 1 (to have at runtime for descriptive error messages and toString()).
     @param index 0 means this represents an A, 1 represents a B, 2 represents a C, 3 means D
     */
    protected OneOf2(Object o, Class<A> aClass, Class<B> bClass, int index) {
        types = RuntimeTypes.registerClasses(aClass, bClass);
        sel = index;
        item = o;
        if (index < 0) {
            throw new IllegalArgumentException("Selected item index must be 0-1");
        } else if (index > 1) {
            throw new IllegalArgumentException("Selected item index must be 0-1");
        }
        if ( (o != null) && (!types.get(index).isInstance(o)) ) {
            throw new ClassCastException("You specified index " + index + ", indicating a(n) " +
                                         types.get(index).getCanonicalName() + "," +
                                         " but passed a " + o.getClass().getCanonicalName());
        }
    }

    /**
     Languages that have union types built in have a match statement that works like this method.
     Exactly one of these functions will be executed - determined by which type of item this object holds.
     @param fa the function to be executed if this OneOf stores the first type.
     @param fb the function to be executed if this OneOf stores the second type.
     @return the return value of whichever function is executed.
     */
    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb) {
        if (sel == 0) {
            return fa.apply((A) item);
        }
        return fb.apply((B) item);
    }

//    // The A parameter ensures that this is used in the proper branch of the guard
//    // Not sure this is a good idea.  Using match() is probably better.
//    @SuppressWarnings("UnusedParameters")
//    protected <R> R throw1(A a) {
//        throw new ClassCastException("Expected a(n) " +
//                                     RuntimeTypes.name(types.get(0)) + " but found a(n) " +
//                                     RuntimeTypes.name(types.get(1)));
//    }
//
//    // The B parameter ensures that this is used in the proper branch of the guard
//    @SuppressWarnings("UnusedParameters")
//    protected <R> R throw2(B b){
//        throw new ClassCastException("Expected a(n) " +
//                                     RuntimeTypes.name(types.get(1)) + " but found a(n) " +
//                                     RuntimeTypes.name(types.get(0)));
//    }

    public int hashCode() {
        // Simplest way to make the two items different.
        return Objects.hashCode(item) + sel;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf2)) { return false; }

        OneOf2 that = (OneOf2) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override public String toString() { return union2Str(item, types); }
}