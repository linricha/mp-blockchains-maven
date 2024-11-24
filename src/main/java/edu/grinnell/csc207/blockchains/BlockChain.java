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
   * The number of blocks in the blockchain.
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
    this.prevHash = firstB.getHash();
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
   * @param balances Stores the balances of each user
   * @throws IllegalArgumentException if the user's key is null or the update fails.
   */
  private static void updateBalance(String user, int amount,
      AssociativeArray<String, Integer> balances) throws IllegalArgumentException {
    // Check if user key is valid
    if (user == null) {
      throw new IllegalArgumentException();
    } // if

    // Do not count
    if (user.equals("")) {
      return;
    } // if

    // Default the balance to 0 if key is not found
    int prevBalance = 0;
    try {
      prevBalance = balances.get(user);
    } catch (Exception ex) {
      // Do Nothing
    } // try/catch

    // Attempt to set the new balance and the user
    try {
      balances.set(user, prevBalance + amount);
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
        || !blk.getPrevHash().equals(this.prevHash)
        || blk.getNonce() != validationBlock.getNonce()) {
      throw new IllegalArgumentException();
    } // if

    Transaction newTran = blk.getTransaction();
    try {
      // Update source balance
      updateBalance(newTran.getSource(), -1 * newTran.getAmount(), this.balances);
      // Update target balance
      updateBalance(newTran.getTarget(), newTran.getAmount(), this.balances);
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
      updateBalance(lastTransaction.getSource(), lastTransaction.getAmount(), this.balances);
      updateBalance(lastTransaction.getTarget(), -lastTransaction.getAmount(), this.balances);
    } catch (Exception ex) {
      return false;
    } // try/catch

    // Remove the last block
    this.prevHash = this.last.block.getPrevHash();
    this.last = this.last.prev;
    this.last.next.remove();
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
    // Return true since the first block is always valid
    if (this.first.next == null) {
      return true;
    } // if
    Node2 currentNode = this.first.next;

    // Initial previous hash.
    Hash pHash = currentNode.block.prevHash;
    Transaction currentTran;

    // Iterate over the chain to validate each block.
    for (int i = 1; i < this.size; i++) {
      Block currentBlock = currentNode.block;
      // every block has a correct previous hash field
      if (!currentBlock.getPrevHash().equals(pHash)) {
        return false;
      } // if

      // that every block has a hash that is correct for its contents
      currentTran = currentBlock.getTransaction();
      Block validBlock = new Block(i, currentTran, pHash, this.checker);
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
        if (source != null && !source.equals("")) {
          updateBalance(source, -amount, tempBalances);
          int curBalance = tempBalances.get(source);
          if (amount < 0 || (!source.equals("") && curBalance < 0)) {
            return false;
          } // if
        } // if

        // Update target balance
        if (target != null && !target.equals("")) {
          updateBalance(target, amount, tempBalances);
          int curBalance = tempBalances.get(target);
          if (amount < 0 || (!target.equals("") && curBalance < 0)) {
            return false;
          } // if
        } // if
      } catch (Exception e) {
        return false;
      } // try/catch

      // Travel to the next node
      pHash = currentBlock.getHash();
      if (currentNode.next != null) {
        currentNode = currentNode.next;
      } // if
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
    Node2 currentNode = this.first.next;
    Transaction currentTran;
    AssociativeArray<String, Integer> tempBalances = new AssociativeArray<>();
    // Iterate over the chain to validate each block.
    for (int i = 1; i < this.size; i++) {
      Block currentBlock = currentNode.block;
      currentTran = currentBlock.getTransaction();
      // the balances are legal/correct at every step
      String source = currentTran.getSource();
      String target = currentTran.getTarget();
      int amount = currentTran.getAmount();

      try {
        // Update source balance
        if (source != null && !source.equals("")) {
          updateBalance(source, -amount, tempBalances);
        } // if

        // Update target balance
        if (target != null && !target.equals("")) {
          updateBalance(target, amount, tempBalances);
        } // if
      } catch (Exception e) {
        return balance;
      } // try/catch
      currentNode = currentNode.next;
    } // for

    try {
      return tempBalances.get(user);
    } catch (KeyNotFoundException e) {
      return balance;
    } // try/catch
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
        return currentNode != null && currentNode.block.getNum() < size;
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
        return currentNode != null && currentNode.block.getNum() < size;
      } // hasNext()

      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Transaction currentTran = currentNode.block.getTransaction();
        currentNode = currentNode.next;
        return currentTran;
      } // next()
    };
  } // iterator()

} // class BlockChain
