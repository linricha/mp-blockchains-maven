package edu.grinnell.csc207.util;

import edu.grinnell.csc207.blockchains.Block;

/**
 * Nodes for doubly-linked structures.
 *
 * @author Samuel A. Rebelsky.
 * @author Maral and Richard
 *
 */
public class Node2 {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The previous node.
   */
  public Node2 prev;

  /**
   * The stored value.
   */
  public Block block;

  /**
   * The next node.
   */
  public Node2 next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new node.
   *
   * @param prevNode
   *   The previous node in the list (or null, if it's the front of the list).
   * @param val
   *   The value to be stored in the node.
   * @param nextNode
   *   The next node in the list (or null, if it's the end of the list).
   */
  public Node2(Node2 prevNode, Block val, Node2 nextNode) {
    this.prev = prevNode;
    this.block = val;
    this.next = nextNode;
  } // Node2(Node2<T>, T, Node2<T>)

  /**
   * Create a new node with no previous link (e.g., the front
   * of some kinds of lists).
   *
   * @param val
   *   The value to be stored in the node.
   * @param nextNode
   *   The next node in the list (or null, if it's the end of the list).
   */
  public Node2(Block val, Node2 nextNode) {
    this(null, val, nextNode);
  } // Node2(T, Node2<T>)

  /**
   * Create a new node with no next link (e.g., if it's at the end of
   * the list). Included primarily for symmetry.
   *
   * @param prevNode
   *   The previous node in the list (or null, if it's the front of the list).
   * @param val
   *   The value to be stored in the node.
   */
  public Node2(Node2 prevNode, Block val) {
    this(prevNode, val, null);
  } // Node2(Node2<T>, T)

  /**
   * Create a new node with no links.
   *
   * @param val
   *   The value to be stored in the node.
   */
  public Node2(Block val) {
    this(null, val, null);
  } // Node2(T)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Insert a new value after this node.
   *
   * @param val
   *   The value to insert.
   * @return
   *   The newly created node that contains the value.
   */
  public Node2 insertAfter(Block val) {
    Node2 tmp = new Node2(this, val, this.next);
    if (this.next != null) {
      this.next.prev = tmp;
    } // if
    this.next = tmp;
    return tmp;
  } // insertAfter

  /**
   * Insert a new value before this node.
   *
   * @param val
   *   The value to insert.
   * @return
   *   The newly created node that contains the value.
   */
  public Node2 insertBefore(Block val) {
    Node2 tmp = new Node2(this.prev, val, this);
    if (this.prev != null) {
      this.prev.next = tmp;
    } // if
    this.prev = tmp;
    return tmp;
  } // insertBefore

  /**
   * Remove this node.
   */
  public void remove() {
    if (this.prev != null) {
      this.prev.next = this.next;
    } // if
    if (this.next != null) {
      this.next.prev = this.prev;
    } // if
    this.prev = null;
    this.next = null;
    this.block = null;
  } // remove()

} // Node2<T>
