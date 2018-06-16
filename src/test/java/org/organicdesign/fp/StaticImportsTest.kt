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

package org.organicdesign.fp

import org.junit.Assert.assertEquals
import org.junit.Test
import org.organicdesign.fp.collections.ImList
import org.organicdesign.fp.collections.ImSortedMap
import org.organicdesign.fp.collections.ImSortedSet
import org.organicdesign.fp.collections.PersistentHashMap
import org.organicdesign.fp.collections.PersistentHashSet
import org.organicdesign.fp.collections.PersistentTreeMap
import org.organicdesign.fp.collections.PersistentTreeSet
import org.organicdesign.fp.collections.PersistentVector
import org.organicdesign.fp.function.Fn0
import org.organicdesign.fp.tuple.Tuple2
import org.organicdesign.fp.tuple.Tuple3
import org.organicdesign.fp.xform.Xform
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import kotlin.collections.Map.Entry

class StaticImportsTest {

    @Test
    @Throws(Exception::class)
    fun mutableMapTest() {
        assertEquals(emptyMap<Any, Any>(), mutableMap<Any, Any>())
        assertEquals(Collections.singletonMap(35, "Thirty Five"),
                     mutableMap(tup(35, "Thirty Five")))

        val control = HashMap<String, Int>()
        control.put("one", 1)
        control.put("two", 2)
        control.put("three", 3)
        assertEquals(control,
                     mutableMap(tup("one", 1),
                                tup("two", 2),
                                tup("three", 3)))
    }

    @Test
    @Throws(Exception::class)
    fun mutableSetTest() {
        assertEquals(emptySet<Any>(), mutableSet<Any>())
        assertEquals(setOf("Thirty Five"),
                     mutableSet("Thirty Five"))

        val control = HashSet<Int>()
        control.add(1)
        control.add(2)
        control.add(3)
        assertEquals(control,
                     mutableSet(1, 2, 3))
    }

    @Test
    @Throws(Exception::class)
    fun mutableVecTest() {
        assertEquals(emptyList<Any>(), mutableVec<Any>())
        assertEquals(listOf("Thirty Five"),
                     mutableVec("Thirty Five"))

        val control = ArrayList<Int>()
        control.add(1)
        control.add(2)
        control.add(3)
        assertEquals(control,
                     mutableVec(1, 2, 3))
    }

    @Test
    @Throws(Exception::class)
    fun testMap() {
        assertEquals(PersistentHashMap.EMPTY, map<Any,Any>())
//        assertEquals(PersistentHashMap.EMPTY, map<Any,Any>(*null as Array<Entry<Any, Any>>?))
        @Suppress("UNCHECKED_CAST")
        assertEquals(PersistentHashMap.EMPTY,
                     map(*arrayOfNulls<Entry<Any,Any>>(0) as Array<out Entry<Any, Any>>))
        val phm = PersistentHashMap.empty<String, Int>()
                .assoc("Hi", 43)
        assertEquals(phm, map(tup("Hi", 43)))
    }

    @Test
    @Throws(Exception::class)
    fun testSet() {
        assertEquals(PersistentHashSet.EMPTY, set<Any>())
//        assertEquals(PersistentHashSet.EMPTY, set<Entry>(*null as Array<Entry<*, *>>?))
        @Suppress("UNCHECKED_CAST")
        assertEquals(PersistentHashSet.EMPTY, set(*arrayOfNulls<Any>(0) as Array<out Any>))
        val phm = PersistentHashSet.empty<String>()
                .put("Hi")
        assertEquals(phm, set("Hi"))
    }

    @Test
    @Throws(Exception::class)
    fun testSortedMap() {

        var ls: ImList<Entry<String, Int>> = PersistentVector.empty<Entry<String, Int>>()
        ls = ls.append(tup("Hi", 1))
                .append(Tuple2("Bye", -99))
                .append(Tuple2("hi", 33))
                .append(tup("bye", -9999))
        var refMap: ImSortedMap<String, Int> = PersistentTreeMap.ofComp(String.CASE_INSENSITIVE_ORDER, ls)
        var testMap = sortedMap(String.CASE_INSENSITIVE_ORDER, ls)
        assertEquals(refMap, testMap)
        // Because equals is meant to be compatible with an unsorted map, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refMap.comparator(), testMap.comparator())

        // Try using the default comparator.
        refMap = PersistentTreeMap.of(ls)
        testMap = sortedMap(ls)

        assertEquals(refMap, testMap)
        // Because equals is meant to be compatible with an unsorted map, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refMap.comparator(), testMap.comparator())
    }

    @Test
    @Throws(Exception::class)
    fun testSortedSet() {
        var ls: ImList<String> = PersistentVector.empty()
        ls = ls.append("Hi")
                .append("Bye")
                .append("hi")
                .append("bye")
        var refSet: ImSortedSet<String> = PersistentTreeSet.ofComp(String.CASE_INSENSITIVE_ORDER, ls)
        var testSet = sortedSet(String.CASE_INSENSITIVE_ORDER, ls)
        assertEquals(refSet, testSet)
        // Because equals is meant to be compatible with an unsorted set, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refSet.comparator(), testSet.comparator())

        // Try using the default comparator.
        refSet = PersistentTreeSet.of(ls)
        testSet = sortedSet(ls)

        assertEquals(refSet, testSet)
        // Because equals is meant to be compatible with an unsorted set, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refSet.comparator(), testSet.comparator())
    }

    @Test
    @Throws(Exception::class)
    fun testTup() {
        assertEquals(Tuple2("Hello", -32), tup("Hello", -32))
        assertEquals(Tuple3.of("Hello", -32, 9.5), tup("Hello", -32, 9.5))
    }

    @Test
    @Throws(Exception::class)
    fun testVec() {
        assertEquals(PersistentVector.EMPTY, vec<Any>())
//        assertEquals(PersistentVector.EMPTY, vec<Entry>(*null as Array<Entry<*, *>>?))
//        assertEquals(PersistentVector.EMPTY, vec<Entry>(*arrayOfNulls<Entry<*, *>>(0)))
        val ls = PersistentVector.empty<String>()
                .append("Hi")
        assertEquals(ls, vec("Hi"))
    }

    @Test
    @Throws(Exception::class)
    fun testXform() {
        assertEquals(Xform.of(Arrays.asList(1, 2, 3)), xform(Arrays.asList(1, 2, 3)))
    }

    @Test
    @Throws(Exception::class)
    fun xformArrayTest() {
        assertEquals(emptyList<Any>(), xformArray<Any>().toImList())
        assertEquals(listOf("Thirty Five"),
                     xformArray("Thirty Five").toImList())

        val control = ArrayList<Int>()
        control.add(1)
        control.add(2)
        control.add(3)
        assertEquals(control,
                     xformArray(1, 2, 3).toImList())
    }

    @Test
    fun testXformChars() {
        assertEquals(vec('H', 'e', 'l', 'l', 'o'), xformChars("Hello").toImList())
    }


    companion object {
        open class A {
            open fun foo(): String {
                return "A"
            }
        }

        open class B : A() {
            override fun foo(): String {
                return "B"
            }
        }

        @JvmStatic
        fun getAFromF(f: () -> A): A {
            return f.invoke()
        }
    }

//    @Test
//    fun testBasics() {
//        assertEquals("A", getAFromF({ A() }).foo())
//        assertEquals("A", getAFromF({ () -> A() }).foo())
//        assertEquals("B", getAFromF(Fn0<B> { B() }).foo())
//        assertEquals("C", getAFromF(object : Fn0<B> {
//            @Throws(Exception::class)
//            override fun invokeEx(): B? {
//                return null
//            }
//        }).foo())
//    }

}
