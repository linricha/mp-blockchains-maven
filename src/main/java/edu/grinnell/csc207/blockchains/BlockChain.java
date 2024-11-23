package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.Node2;

/**
 * A full blockchain.
 *
 * @author Richard Lin, Maral Bat-Erdene
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /** 
   * Validator for hash values, used to check the validity of blocks.
   */
  HashValidator checker;

  /** 
   * Hash of the previous block, used for validating the chain integrity.
   */
  Hash prevHash;

  /** 
   * The first node in the blockchain. It holds the first block in the chain.
   */
  Node2 first;

  /** 
   * The last node in the blockchain. It holds the most recent block in the chain.
   */
  Node2 last;

  /** 
   * The number of blocks in the blockchain
   */
  int size;

  /** 
   * An associative array that tracks the balances of users in the blockchain.
   */
  AssociativeArray<String, Integer> balances;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check
   *   The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.checker = check;
    this.prevHash = new Hash(new byte[] {});
    Block firstB = new Block(0, new Transaction("", "", 0), this.prevHash, this.checker);
    this.prevHash = firstB.getPrevHash();
    this.first = new Node2(firstB);
    this.last = this.first;
    this.size = 1;
    this.balances = new AssociativeArray<>();
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Update the balance for a user, initializing it to 0 if not present.
   *
   * @param user The user whose balance will be updated.
   * @param amount The amount to add to the user's balance (can be negative).
   * @throws IllegalArgumentException if the user's key is null or the update fails.
   */
  private void updateBalance(String user, int amount) throws IllegalArgumentException {
    // Check if user key is valid
    if (user == null) {
      throw new IllegalArgumentException();
    } // if

    // Default the balance to 0 if key is not found
    int prevBalance = 0;
    try {
      prevBalance = this.balances.get(user);
    } catch (Exception ex) {
      // Do Nothing
    } // try/catch

    // Attempt to set the new balance and the user
    try {
      this.balances.set(user, prevBalance + amount);
      int curBalance = this.balances.get(user);
      if (curBalance < 0) {
        this.balances.set(user, curBalance - amount);
        throw new IllegalArgumentException();
      } // if
    } catch (Exception ex) {
      throw new IllegalArgumentException();
    } // try/catch
  } // updateBalance(String, int)
  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that
   * block.
   *
   * @param t
   *   The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    return new Block(this.size, t, this.prevHash, this.checker);
  } // mine(Transaction)  

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk
   *   The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b)
   *   the hash is not appropriate for the contents, or (c) the previous
   *   hash is incorrect.
   */
  public void append(Block blk) throws IllegalArgumentException {
    // Validate the block
    Block validationBlock = mine(blk.getTransaction());
    if (!checker.isValid(blk.getHash())
      || !blk.getHash().equals(validationBlock.getHash())
      || !blk.getPrevHash().equals(this.prevHash)) {
      throw new IllegalArgumentException();
    } // if

    Transaction newTran = blk.getTransaction();
    try {
      // Update source balance
      this.updateBalance(newTran.getSource(), -newTran.getAmount());
      // Update target balance
      this.updateBalance(newTran.getTarget(), newTran.getAmount());
    } catch (Exception ex) {
      throw new IllegalArgumentException();
    } // try/catch

    // Insert the new block with the Node2 function
    this.last = this.last.insertAfter(blk);
    this.prevHash = blk.getHash();
    this.size++;
    } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    if (this.size <= 1) {
      return false;
    } // if
    // Retrieve the transaction to be removed
    Transaction lastTransaction = this.last.block.getTransaction();

    // Rollback balances to reverse the effects of the last transaction
    try {
      this.updateBalance(lastTransaction.getSource(), lastTransaction.getAmount());
      this.updateBalance(lastTransaction.getTarget(), -lastTransaction.getAmount());
    } catch (Exception ex) {
      return false;
    } // try/catch

    // Remove the last block
    this.last.next.remove();
    this.prevHash = this.last.block.getPrevHash();
    this.size--;
    return true;
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.last.block.getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    // Temporary array to track balances during validation.
    AssociativeArray<String, Integer> tempBalances = new AssociativeArray<>();
    Node2 currentNode = this.first.next;

    // Initial previous hash.
    Hash prevHash = new Hash(new byte[] {});
    Transaction currentTran;

    // Iterate over the chain to validate each block.
    for (int i = 0; i < this.size; i++) {
      Block currentBlock = currentNode.block;
      // every block has a correct previous hash field
      if (!currentBlock.getPrevHash().equals(prevHash)) {
        return false;
      } // if

      // that every block has a hash that is correct for its contents
      currentTran = currentBlock.getTransaction();
      Block validBlock = new Block(i, currentTran, prevHash, this.checker);
      if (!validBlock.getHash().equals(currentBlock.getHash())) {
        return false;
      } // if

      // that every block has a valid hash
      if (!this.checker.isValid(currentBlock.getHash())) {
        return false;
      } // if

      // the balances are legal/correct at every step
      String source = currentTran.getSource();
      String target = currentTran.getTarget();
      int amount = currentTran.getAmount();

      try {
        // Update source balance
        if (source != null) {
          int sourceBalance = tempBalances.get(source) - amount;
          if (sourceBalance < 0) {
            // Negative balance is invalid.
            return false;
          } // if
          tempBalances.set(source, sourceBalance);
        } // if

        // Update target balance
        if (target != null) {
          int targetBalance = tempBalances.get(target) + amount;
          tempBalances.set(target, targetBalance);
        } // if
      } catch (Exception e) {
        return false;
      } // try/catch

      // Travel to the next node
      prevHash = currentBlock.getHash();
      currentNode = currentNode.next;
    } // for
    return true;
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception
   *   If things are wrong at any block.
   */
  public void check() throws Exception {
    if (!this.isCorrect()) {
      throw new Exception();
    } // if
  } // check()

  /**
   * Return an iterator of all the people who participated in the
   * system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    Iterator<String> userIterator = balances.keyIterator();
    return userIterator;
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user
   *   The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    int balance = 0;
    try {
      balance = this.balances.get(user);
    } catch (KeyNotFoundException ex) {
      // Do Nothing
    } // try/catch

    return balance;
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      private Node2 currentNode = first;
      public boolean hasNext() {
        return currentNode != null && currentNode.block.getNum() < size - 1;
      } // hasNext()

      public Block next() {
        Block currentBlock = currentNode.block;
        currentNode = currentNode.next;
        return currentBlock;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the transactions in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      private Node2 currentNode = first;

      public boolean hasNext() {
        return currentNode != null && currentNode.block.getNum() < size - 1;
      } // hasNext()

      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Transaction currentTran = currentNode.block.getTransaction();
        currentNode = currentNode.next;
        return currentTran;
      } // next()
    };
  } // iterator()

} // class BlockChain
