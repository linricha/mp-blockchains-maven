package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Maral Bat-Erdene
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> newArr = new AssociativeArray<K, V>();

    // Expand the new array if necessary
    while (this.size > newArr.pairs.length) {
      newArr.expand();
    } // while

    // Clone each pair
    for (int i = 0; i < this.size; i++) {
      newArr.pairs[i] = this.pairs[i].clone();
    } // for

    // adjust size of new array
    newArr.size = this.size;
    return newArr;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    String toStr = "{";
    for (int i = 0; i < (this.size - 1); i++) {
      toStr += this.pairs[i].toString() + ", ";
    } // for
    if (this.size > 0) {
      toStr += this.pairs[this.size - 1].toString();
    } // if
    toStr += "}";
    return toStr;
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key
   *   The key whose value we are seeting.
   * @param value
   *   The value of that key.
   *
   * @throws NullKeyException
   *   If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (this.size == this.pairs.length) {
      this.expand();
    } // if
    if (key == null) {
      throw new NullKeyException();
    } // if
    try {
      int index = find(key);
      this.pairs[index].val = value;
    } catch (Exception e) {
      this.pairs[this.size] = new KVPair<K, V>(key, value);
      this.size++;
    } // try/catch
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *   A key
   *
   * @return
   *   The corresponding value
   *
   * @throws KeyNotFoundException
   *   when the key is null or does not appear in the associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    try {
      int index = find(key);
      return this.pairs[index].val;
    } catch (Exception e) {
      throw new KeyNotFoundException();
    } // try/catch
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key, since it cannot appear.
   *
   * @param key
   *   The key we're looking for.
   *
   * @return true if the key appears and false otherwise.
   */
  public boolean hasKey(K key) {
    try {
      find(key);
      return true;
    } catch (Exception e) {
      return false;
    } // try/catch
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   *
   * @param key
   *   The key to remove.
   */
  public void remove(K key) {
    try {
      int index = find(key);
      // Replace the removed entry with the last entry
      this.pairs[index] = this.pairs[this.size - 1];
      // Nullify the last entry
      this.pairs[this.size - 1] = null;
      // Decrement the size
      this.size--;
    } catch (KeyNotFoundException e) {
      // If the key is not found, do nothing
    } // try/catch
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return The number of key/value pairs in the array.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Provides an iterator over the keys in the associative array.
   *
   * @return An iterator for the keys.
   */
  public Iterator<K> keyIterator() {
    return new Iterator<K>() {
      private int currentIndex = 0;

      public boolean hasNext() {
        return currentIndex < size;
      } // hasNext

      public K next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        return pairs[currentIndex++].key;
      } // next
    };
  } // keyIterator()

  /**
   * Provides an iterator over the values in the associative array.
   *
   * @return An iterator for the values.
   */
  public Iterator<V> valueIterator() {
    return new Iterator<V>() {
      private int currentIndex = 0;

      public boolean hasNext() {
        return currentIndex < size;
      } // hasNext

      public V next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        return pairs[currentIndex++].val;
      } // next
    };
  } // valueIterator()
  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key
   *   The key of the entry.
   *
   * @return
   *   The index of the key, if found.
   *
   * @throws KeyNotFoundException
   *   If the key does not appear in the associative array.
   */
  int find(K key) throws KeyNotFoundException {
    // Return the index if the key is found
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return i;
      } // if
    } // for
    throw new KeyNotFoundException();
  } // find(K)
} // class AssociativeArray
