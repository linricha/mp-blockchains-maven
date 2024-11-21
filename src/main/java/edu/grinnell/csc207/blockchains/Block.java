package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
  Transaction finishedDeal;

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
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash,
      HashValidator check) {
    this.blockNum = num;
    this.finishedDeal = transaction;
    this.prevHash = prevHash;

    byte[] lbytes = ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array(); // NOT DONE


    // STUB
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param nonce
   *   The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this.blockNum = num;
    this.finishedDeal = transaction;
    this.prevHash = prevHash;
    this.nonce = nonce;
    try {
      this.hash = new Hash(computeHash(this));
    } catch (Exception e){
    } // try/catch
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  static byte[] computeHash(Block cube) throws NoSuchAlgorithmException{ // Can update to compile into single methods probably.

    MessageDigest hashCreator = MessageDigest.getInstance("sha-256");
    //Should not throw exception since sha-256 should be valid, unless exception
    // is for something else.

    // BlockNum of cube
    hashCreator.update(Integer.valueOf(cube.getNum()).byteValue());

    // Source of finishedDeal of cube
    hashCreator.update(cube.getTransaction().getSource().getBytes());

    // Target of finishedDeal of cube
    hashCreator.update(cube.getTransaction().getTarget().getBytes());

    // Amount of finishedDeal of cube
    hashCreator.update(Integer.valueOf(cube.getTransaction().getAmount()).byteValue());

    // PrevHash of cube
    hashCreator.update(cube.getPrevHash().getBytes());

    // Nonce of cube
    hashCreator.update(Long.valueOf(cube.getNonce()).byteValue());


    byte[] hash = hashCreator.digest();
    return hash;
  } // computeHash()

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
    return this.finishedDeal;
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
    return "";  // STUB
  } // toString()
} // class Block
