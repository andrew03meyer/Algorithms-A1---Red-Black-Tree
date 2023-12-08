/**
 * RBT
 * Red-Black Tree Insert
 * @author INSERT_YOUR_USERNAME_HERE
 */
import java.sql.SQLOutput;
import java.util.*;
public class RBT {
    public Node root;

    public RBT() {}

    public boolean isRed(Node x) {
        if (x == null) return false;
        return x.getColor() == Node.Color.RED;
    }
    
    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(int x) {
        return nodeContainsData(root, x);
    }

    private boolean nodeContainsData(Node r, int x) {
        while (r != null) {
            if (r.getData() - x < 0) {
                r = r.getLeft();
            } else if (r.getData() - x > 0) {
                r = r.getRight();
            } else {
                return true;
            }
        }
        return false;
    }

    public List<Integer> serializeTree() {
        return serializeTree(root);
    }

    private List<Integer> serializeTree(Node r) {
        if (r == null) return new LinkedList<>();
        int data = r.getData();
        List<Integer> left = serializeTree(r.getLeft());
        List<Integer> right = serializeTree(r.getRight());
        left.add(data);
        left.addAll(right);
        return left;
    }

    public int maxHeight() {
        return maxHeight(root);
    }

    private int maxHeight(Node r) {
        if (r==null) return 0;        
        return 1 + Math.max(maxHeight(r.getLeft()), maxHeight(r.getRight()));
    }


    // ************************************************************************
    // * INSERT INTO RED-BLACK TREE
    // ************************************************************************


    public void insert(int x) {
        root = nodeInsertData(root, x);
        //rebalance(getNodeFromValue(root, x));
        root.setColor(Node.Color.BLACK);        //Case 1 - x is the root
    }

    private Node nodeInsertData(Node r, int x) {
        if (r == null) return new Node(x, Node.Color.RED);      //Once it finds leaf, creates new node
        else {
            //If x > current root node, go right
            if(x > r.getData()){
                r.setRight(nodeInsertData(r.getRight(), x));
            }
            //If x < current root node, go left
            else if(x < r.getData()) {
                r.setLeft(nodeInsertData(r.getLeft(), x));
            }
            return r;        //If x == r.getData(), return the node
        }
    }

    private void rotateRight(Node h) {
        assert (h != null) && isRed(h.getLeft());
        if(h.getLeft() != null) {

            //Save all the needed links between nodes
            Node newRoot = h.getLeft();
            Node newRootRight = h;
            Node newRootRightLeft = newRoot.getRight();

            /*
            * Set the new node's right to the old root node
            * Set the old root node's left to the new root node's old right
            */
            newRoot.setRight(h);
            newRoot.getRight().setLeft(newRootRightLeft);

            //Reset the root
            if(h == root){
                root = newRoot;
            }
            //If it's not the root
            else{
                //Set parent's left child to newRoot
                if(getParent(root, h.getData()).getLeft() == h){
                    getParent(root, h.getData()).setLeft(newRoot);
                }
                //Set parent's right child to newRoot
                else{
                    getParent(root, h.getData()).setRight(newRoot);
                }
            }
        }
    }
 
    private void rotateLeft(Node h) {
        assert (h != null) && isRed(h.getRight());

        //Save all the needed links as new nodes
        if(h.getRight() != null) {
            Node newRoot = h.getRight();
            Node newRootLeftRight = newRoot.getLeft();

            //Reset the new root node's left, to the old root node
            //Reset the old root nodes right, to the new node's old left
            newRoot.setLeft(h);
            newRoot.getLeft().setRight(newRootLeftRight);

            //If the node to rotate is the root of the whole tree, reset it to newRoot
            if(h == root){
                root = newRoot;
            }
            else{
                //Set parent's left child to newRoot
                if(getParent(root, h.getData()).getLeft() == h){
                    getParent(root, h.getData()).setLeft(newRoot);
                }
                //Set parent's right child to newRoot
                else{
                    getParent(root, h.getData()).setRight(newRoot);
                }
            }
        }
    }

    // flip the colors of a node
    private void flipColors(Node h) {
        if(h.getColor() == Node.Color.RED){
            h.setColor(Node.Color.BLACK);
        }
        else{
            h.setColor(Node.Color.RED);
        }
    }

    public Node gettyParent(Node r, int x){
        if(r == getNodeFromValue(root , x)){
            return r;
        }
        else if (r !=null){
            if(r.getLeft() != r){gettyParent(r.getLeft(), x);}
            if(r.getRight() != r){gettyParent(r.getRight(), x);}
        }
        return r;
    }
    public Node getRoot(){
        return root;
    }
    public void printNodeData(Node r){
        System.out.println(r.getData());
    }
    private Node getParent(Node r, int x){
        //If current node isn't the search node (isn't the root)
        if(x != r.getData()) {
            //If current node's left is target node, return current node
            if (r.getLeft() != null) {
                if (r.getLeft().getData() == x) {
                    return r;
                }
            }
            //If current node's right is target node, return current node
            if (r.getRight() != null) {
                if (r.getRight().getData() == x) {
                    return r;
                }
            }
            //If current node isn't the parent, and x < search node, go left
            if (x < r.getData() && r.getLeft() != null) {
                return getParent(r.getLeft(), x);
            }
            //If current node isn't the parent, and x > search node, go right
            if (x > r.getData() && r.getRight() != null) {
                return getParent(r.getRight(), x);
            }

            //If  right > x > left, value is found (therefore return itself until recursion finishes)
            return r;
        }
        //If current node is the search node, it is the root (therefore has no parent)
        else{
            return null;
        }
    }

    private Node getGrandparent(Node r, int x){
        //Calls get parent twice
        Node temp = getParent(r,x);
        //As parent can return null, check it's not null, so we don't get a syntax error from .getData()
        if(temp != null) {
            return getParent(root, temp.getData());
        }
        else{
            return null;
        }

    }

    private Node getUncle(Node r, int x){
        try {
            //If the parent is a left child of the grandparent, get the right
            if (getParent(r, x).getData() == getGrandparent(r, x).getLeft().getData()) {
                return getGrandparent(r, x).getRight();
            }
            //If the parent is a right child of the grandparent, get the left
            else if(getParent(r, x).getData() == getGrandparent(r, x).getRight().getData()){
                return getGrandparent(r, x).getLeft();
            }
            return null;
        }
        catch(Exception e){
            return null;
        }
    }
    private Node checkCase2(Node r){
        //Case 2: r uncle is red
        //fix: colour parent and uncle black, and grandparent red

        //Checks if the node has a grandparent and an uncle
        if(getGrandparent(root, r.getData()) != null && getUncle(root, r.getData()) != null) {

            //If the nodes uncle is red and it's parent is red
            if (getUncle(root, r.getData()).getColor() == Node.Color.RED && getParent(root, r.getData()).getColor() == Node.Color.RED) {
                flipColors(getParent(root, r.getData()));
                flipColors(getUncle(root, r.getData()));
                flipColors(getGrandparent(root, r.getData()));

                return getGrandparent(root, r.getData());
            }
        }

        //Returns itself
        return r;

    }
    private Node checkCase3(Node r){
        //Case 3: r uncle is black (triangle)
        //Fix: rotate r parent, to make x the parent

        //If r has both a grandparent and an uncle
        if(getGrandparent(root, r.getData()) != null && getUncle(root, r.getData()) != null) {

            //If that uncle is black & parent is red
            if (getUncle(root, r.getData()).getColor() == Node.Color.BLACK && getParent(root, r.getData()).getColor() == Node.Color.RED) {

                //If it's a right triangle
                if (checkTriangle(r) == 1) {
                    rotateRight(getParent(root, r.getData()));
                    return r.getRight();

                    //If it's a left triangle
                } else if (checkTriangle(r) == 2) {
                    rotateLeft(getParent(root, r.getData()));
                    return r.getLeft();
                }
            }
        }

        //Checks if the uncle is null and parent is red
        if(getGrandparent(root, r.getData()) != null && getUncle(root, r.getData()) == null && getParent(root, r.getData()).getColor() == Node.Color.RED) {

            //If right triangle
            if (checkTriangle(r) == 1) {
                rotateRight(getParent(root, r.getData()));
                return r.getRight();
            }

            //If left triangle
            else if (checkTriangle(r) == 2) {
                rotateLeft(getParent(root, r.getData()));
                return r.getLeft();
            }
        }
        //Returns itself if no case met
        return r;
    }
    private Node checkCase4(Node r){
        //Case 4: uncle is black (line)
        //Fix: rotate grandparent, recolour parent black and grandparent red
        //Checking the colour of the Uncle - does not include null values

        //If has grandparent and uncle
        if(getGrandparent(root, r.getData()) != null && getUncle(root, r.getData()) != null) {

            //If uncle is black & parent is red
            if (getUncle(root, r.getData()).getColor() == Node.Color.BLACK && getParent(root, r.getData()).getColor() == Node.Color.RED) {

                //Recolouring parent and grandparent
                getParent(root, r.getData()).setColor(Node.Color.BLACK);
                getGrandparent(root, r.getData()).setColor(Node.Color.RED);

                //If line to the right
                if (checkLine(r) == 1) {
                    rotateLeft(getGrandparent(root, r.getData()));
                }
                //If line to the left
                else if (checkLine(r) == 2) {
                    rotateRight(getGrandparent(root, r.getData()));
                }
            }
        }

        //Checks if the uncle is null & parent is red
        else if(getGrandparent(root, r.getData()) != null && getUncle(root, r.getData()) == null && getParent(root, r.getData()).getColor() == Node.Color.RED){

            //Recolouring
            getParent(root, r.getData()).setColor(Node.Color.BLACK);
            getGrandparent(root, r.getData()).setColor(Node.Color.RED);

            //If line right
            if (checkLine(r) == 1) {
                rotateLeft(getGrandparent(root, r.getData()));
            }

            //if line left
            else if (checkLine(r) == 2) {
                rotateRight(getGrandparent(root, r.getData()));
            }
        }
        //Returns itself
        return r;
    }

    private int checkTriangle(Node r){
        /*return 0 -> no triangle
        * return 1 -> triangle right (right rotate needed)
        * return 2 -> triangle left (left rotate needed)
        */

        //Triangle right
        if(getGrandparent(root, r.getData()).getRight() != null){
            if(getGrandparent(root, r.getData()).getRight().getLeft() == r){
                return 1;
            }
        }

        //Triangle left
        if(getGrandparent(root, r.getData()).getLeft() != null){
            if(getGrandparent(root, r.getData()).getLeft().getRight() == r){
                return 2;
            }
        }

        //No triangle
        return 0;
    }

    private int checkLine(Node r){
        /*return 0 -> no line
         * return 1 -> line right
         * return 2 -> line left
         */

        //Line left
        if(getGrandparent(root, r.getData()).getRight() != null){
            if(getGrandparent(root, r.getData()).getRight().getRight() == r){
                return 1;
            }
        }

        //Line right
        if(getGrandparent(root, r.getData()).getLeft() != null){
            if(getGrandparent(root, r.getData()).getLeft().getLeft() == r){
                return 2;
            }
        }

        //No line
        return 0;
    }

    private Node getNodeFromValue(Node r, int x){

        //Base case - if its value == desired value, return node
        if(r != null){
            if(r.getData() == x){
                return r;
            }
        }

        //Binary search for value
        if(x < r.getData() && r.getLeft() != null){return getNodeFromValue(r.getLeft(), x);}
        if(x > r.getData() && r.getRight() != null){return getNodeFromValue(r.getRight(), x);}

        return r;
    }
    private void rebalance(Node r){
        /*
        * While a node to be compared has a red parent
        * Node initially set to node to be inserted
        * Each checkCase returns the node that needs to be checked next (the ones they have effected)
        * Only stops once the nodes effected are all balanced
        */
        Node temp = r;
        while(getParent(root, temp.getData()) != null && getParent(root, temp.getData()).getColor() == Node.Color.RED){
            temp = checkCase2(temp);
            temp = checkCase3(temp);
            temp = checkCase4(temp);
        }
    }
}
