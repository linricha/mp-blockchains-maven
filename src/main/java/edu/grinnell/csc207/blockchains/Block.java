package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Richard Lin, Maral Bat-Erdene
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Stores the number associated to this block.
   */
  int blockNum;

  /**
   * Stores the transaction of this block.
   */
  Transaction transaction;

  /**
   * The hash of the block before this block.
   */
  Hash prevHash;

  /**
   * The hash of this block.
   */
  Hash hash;

  /**
   * Used to calculate the hash of this block.
   */
  long nonce;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param num1
   *   The number of the block.
   * @param transaction1
   *   The transaction for the block.
   * @param prevHash1
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int num1, Transaction transaction1, Hash prevHash1,
      HashValidator check) {
    this.blockNum = num1;
    this.transaction = transaction1;
    this.prevHash = prevHash1;
    this.computeNonceAndHash(check);
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num1
   *   The number of the block.
   * @param transaction1
   *   The transaction for the block.
   * @param prevHash1
   *   The hash of the previous block.
   * @param nonce1
   *   The nonce of the block.
   */
  public Block(int num1, Transaction transaction1, Hash prevHash1, long nonce1) {
    this.blockNum = num1;
    this.transaction = transaction1;
    this.prevHash = prevHash1;
    this.nonce = nonce1;
    computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  void computeHash() { // Can update to compile into single methods probably.

    try {
      MessageDigest hashCreator = MessageDigest.getInstance("sha-256");

      // BlockNum of cube
      hashCreator.update(ByteBuffer.allocate(Integer.BYTES).putInt(this.getNum()).array());

      // Source of finishedDeal of cube
      hashCreator.update(this.getTransaction().getSource().getBytes());

      // Target of finishedDeal of cube
      hashCreator.update(this.getTransaction().getTarget().getBytes());

      // Amount of finishedDeal of cube
      hashCreator.update(ByteBuffer.allocate(Integer.BYTES).
          putInt(this.getTransaction().getAmount()).array());

      // PrevHash of cube
      if (this.prevHash != null) {
        hashCreator.update(this.getPrevHash().getBytes());
      } // if

      // Nonce of cube
      hashCreator.update(ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array());


      byte[] hashBytes = hashCreator.digest();
      this.hash = new Hash(hashBytes);

    } catch (Exception e) {
      //Should not throw exception since sha-256 should be valid, unless exception
      // is for something else.
    } // try/catch

  } // computeHash()

  /**
   * Computes the Nonce by checking if the hash compute form the nonce
   * is valid.
   *
   * @param checkHash The hash validator to check whether the nonce is valid
   * by creating a hash based on that nonce.
   */
  private void computeNonceAndHash(HashValidator checkHash) {
    this.nonce = 0;
    this.computeHash();

    while (!checkHash.isValid(this.getHash())) {
      this.nonce++;
      this.computeHash();
    } // while
  } // computeNonce(HashValidator)



  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.blockNum;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.hash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {

    return String.format("Block %d" + "(Transaction: %s, Nonce: %d, prevHash: %s, hash: %s)",
        this.getNum(), this.getTransaction().toString(), this.getNonce(),
        this.getPrevHash().toString(), this.getHash().toString());
  } // toString()
} // class Block
