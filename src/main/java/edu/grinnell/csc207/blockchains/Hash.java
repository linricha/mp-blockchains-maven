package edu.grinnell.csc207.blockchains;

/**
 * Encapsulated hashes.
 *
 * @author Richard Lin, Maral Bat-Erdene
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Stores an array of bytes.
   */
  byte[] dataArr;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data
   *   The data to copy into the hash.
   */
  public Hash(byte[] data) {
    this.dataArr = data.clone();
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.dataArr.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i
   *   The index of the byte to get, between 0 (inclusive) and
   *   length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return this.dataArr[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client
   * cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    return this.dataArr.clone();
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    if (this.dataArr.length == 0) {
      return "null";
    } // if

    StringBuilder construct = new StringBuilder();

    for (int i = 0; i < this.dataArr.length; i++) { // check if this actually works as intended.
      construct.append(String.format("%02X", Byte.toUnsignedInt(this.dataArr[i])));
    } // for

    return construct.substring(0);
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other
   *   The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false
   *   otherwise.
   */
  public boolean equals(Object other) {
    if (other instanceof Hash) {
      if (this.length() != ((Hash) other).length()) {
        return false;
      } // if

      for (int i = 0; i < this.length(); i++) {
        if (this.get(i) != ((Hash) other).get(i)) {
          return false;
        } // if
      } // for
      return true;
    } // if
    return false;
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
