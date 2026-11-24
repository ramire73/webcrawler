package org.example.util;

public class RedBlackTree implements TreeInterface {
    private final int RED = 0;
    private final int BLACK = 1;

    class Node {
        long key;
        int color;
        Node left, right, parent;

        Node(long key) {
            this.key = key;
            this.color = RED; // New nodes are always red
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    private Node root = null;
    private final Node TNULL = new Node(0); // Sentinel node for null leaves

    public RedBlackTree() {
        TNULL.color = BLACK; // TNULL is always black
        root = TNULL; // Initialize root to point to sentinel node
    }

    // Insert a node into the Red-Black Tree
    public void insert(long key) {
        Node node = new Node(key);
        node.parent = null;
        node.left = TNULL;
        node.right = TNULL;

        Node y = null;
        Node x = this.root;

        while (x != TNULL) { // Find the correct position for the new node
            y = x;
            if (node.key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y; // Set the parent of the new node
        if (y == null) {
            root = node; // If the tree was empty, set root to the new node
        } else if (node.key < y.key) {
            y.left = node;
        } else {
            y.right = node;
        }

        if (node.parent == null) {
            node.color = BLACK; // The root node should be black
            return;
        }

        if (node.parent.parent == null) {
            return;
        }

        fixInsert(node); // Fix the tree to maintain Red-Black properties
    }

    // Fixing the red-black tree after insert
    private void fixInsert(Node k) {
        Node u;
        while (k.parent.color == RED) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left; // uncle
                if (u.color == RED) {
                    // Case 1: Uncle is RED
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        // Case 2: Uncle is BLACK and k is left child
                        k = k.parent;
                        rightRotate(k);
                    }
                    // Case 3: Uncle is BLACK and k is right child
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right; // uncle
                if (u.color == RED) {
                    // Mirror Case 1: Uncle is RED
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        // Mirror Case 2: Uncle is BLACK and k is right child
                        k = k.parent;
                        leftRotate(k);
                    }
                    // Mirror Case 3: Uncle is BLACK and k is left child
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = BLACK;
    }

    // Perform left rotation
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    // Perform right rotation
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // Search the tree
    public boolean search(long key) {
        return searchTreeHelper(this.root, key);
    }

    // Search the tree helper
    private boolean searchTreeHelper(Node node, long key) {
        if (node == TNULL || key == node.key) {
            return node != TNULL;
        }
        if (key < node.key) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }

    // Delete a node from the tree
    public void delete(long key) {
        deleteNodeHelper(this.root, key);
    }

    private void deleteNodeHelper(Node node, long key) {
        Node z = TNULL;
        Node x, y;
        while (node != TNULL) {
            if (node.key == key) {
                z = node;
            }
            if (node.key <= key) {
                node = node.right;
            } else {
                node = node.left;
            }
        }

        if (z == TNULL) {
            System.out.println("Couldn't find key in the tree");
            return;
        }

        y = z;
        int yOriginalColor = y.color;
        if (z.left == TNULL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == TNULL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minValueNode(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (yOriginalColor == BLACK) {
            fixDelete(x);
        }
    }

    private void rbTransplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private Node minValueNode(Node node) {
        while (node.left != TNULL) {
            node = node.left;
        }
        return node;
    }

    private void fixDelete(Node x) {
        Node s;
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                s = x.parent.right;
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    s = x.parent.right;
                }
                if (s.left.color == BLACK && s.right.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    if (s.right.color == BLACK) {
                        s.left.color = BLACK;
                        s.color = RED;
                        rightRotate(s);
                        s = x.parent.right;
                    }
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                s = x.parent.left;
                if (s.color == RED) {
                    s.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    s = x.parent.left;
                }
                if (s.left.color == BLACK && s.right.color == BLACK) {
                    s.color = RED;
                    x = x.parent;
                } else {
                    if (s.left.color == BLACK) {
                        s.right.color = BLACK;
                        s.color = RED;
                        leftRotate(s);
                        s = x.parent.left;
                    }
                    s.color = x.parent.color;
                    x.parent.color = BLACK;
                    s.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }
}
