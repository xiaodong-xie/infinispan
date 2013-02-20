/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.infinispan.jcache;

import org.infinispan.AdvancedCache;

import javax.cache.Cache;

/**
 * Infinispan implementation of {@link Cache.MutableEntry<K, V>} designed to
 * be passed as parameter to {@link Cache.EntryProcessor#process(javax.cache.Cache.MutableEntry)}.
 *
 * @param <K> the type of key maintained by this cache entry
 * @param <V> the type of value maintained by this cache entry
 * @author Galder Zamarreño
 * @since 5.3
 */
public final class MutableJCacheEntry<K, V> implements Cache.MutableEntry<K, V> {

   private final AdvancedCache<K, V> cache;

   private final K key;

   private final V oldValue;

   private V value; // mutable

   private boolean removed;

   public MutableJCacheEntry(AdvancedCache<K, V> cache, K key, V value) {
      this.cache = cache;
      this.key = key;
      this.oldValue = value;
   }

   @Override
   public boolean exists() {
      if (value != null)
         return true;
      else if (!removed)
         return cache.containsKey(key);

      return false;
   }

   @Override
   public void remove() {
      removed = true;
      value = null;
   }

   @Override
   public void setValue(V value) {
      this.value = value;
      removed = false;
   }

   @Override
   public K getKey() {
      return key;
   }

   @Override
   public V getValue() {
      if (value != null)
         return value;

      return oldValue;
   }

   V getNewValue() {
      return value;
   }

   boolean isRemoved() {
      return removed;
   }

}
