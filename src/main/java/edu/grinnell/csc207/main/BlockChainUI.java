package edu.grinnell.csc207.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;
import edu.grinnell.csc207.util.IOUtils;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Maral and Richard
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 0;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen
   *   The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
      Valid commands:
        mine: discovers the nonce for a given transaction
        append: appends a new block onto the end of the chain
        remove: removes the last block from the end of the chain
        check: checks that the block chain is valid
        users: prints a list of users
        balance: finds a user's balance
        transactions: prints out the chain of transactions
        blocks: prints out the chain of blocks (for debugging only)
        help: prints this list of commands
        quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args
   *   Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator =
        (hash) -> (hash.length() >= 3) && (hash.get(0) == 0)
        && (hash.get(1) == 0) && (hash.get(2) == 0);
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source;
    String target;
    int amount;
    long nonce;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          nonce = IOUtils.readInt(pen, eyes, "Nonce: ");
          Block newb = new Block(chain.getSize(), new Transaction(source, target, amount),
              chain.getHash(), nonce);
          chain.append(newb);

          pen.println("Appended: " + newb.toString());
          break;

        case "balance":
          // Prompt for the user
          String user = IOUtils.readLine(pen, eyes, "User: ");
          // Get the user's balance from the blockchain
          int balance = chain.balance(user);

          // Print the user's balance
          pen.printf("%s's balance is %d\n", user, balance);
          break;

        case "blocks":
          Iterator<Block> blockIterator = chain.blocks();

          // Iterate through the blocks and print details
          while (blockIterator.hasNext()) {
            Block currentBlock = blockIterator.next();
            pen.println(currentBlock.toString());
          } // while
          break;

        case "check":
          try {
            chain.check();
            pen.println("The blockchain checks out.");
          } catch (Exception e) {
            pen.println("The blockchain does NOT check out.");
          } // try/catch
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          Block b = chain.mine(new Transaction(source, target, amount));
          pen.println("Use non6rce: " + b.getNonce());
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          if (!chain.removeLast()) {
            pen.println("Unsuccessful remove. No blocks to remove.");
          } else {
            pen.println("Removed last element.");
          } // if/else
          break;

        case "transactions":
          Iterator<Transaction> tranIterator = chain.iterator();

          // Iterate through the blocks and print details
          while (tranIterator.hasNext()) {
            Transaction curTransaction = tranIterator.next();
            pen.println(curTransaction.toString());
          } // while
          break;

        case "users":
          Iterator<String> userIterator = chain.users();

          // Iterate through the blocks and print details
          while (userIterator.hasNext()) {
            String currentUser = userIterator.next();
            pen.println(currentUser);
          } // while
          break;

        default:
          pen.printf("invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
