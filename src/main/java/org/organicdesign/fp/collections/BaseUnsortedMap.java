// Copyright 2017 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.collections;

/**
 Adds {@link #equator()} to {@link BaseMap} which is an unsorted-only operation.
 Lowest common ancestor of {@link MutableMap} and {@link ImMap}.
 */
public interface BaseUnsortedMap<K,V> extends BaseMap<K,V> {
    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();
}
