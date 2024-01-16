import java.io.PrintWriter;

public class Node<ContentType extends Comparable<ContentType>> {
    public String name;
    public ContentType GMS;
    public Node<ContentType> left = emptyTree;
    public Node<ContentType> right = emptyTree;
    private int height;

    private static final Node emptyTree = new Node<>("EMPTY TREE", null, null, null, null, -1);


    private Node(String name, ContentType GMS, Node<ContentType> left, Node<ContentType> right, Node<ContentType> sup, int height) {
        this.name = name;
        this.GMS = GMS;
        this.left = left;
        this.right = right;
        this.height = height;
    }
    public Node(String name, ContentType GMS) { // creating a root node
        this.name = name;
        this.GMS = GMS;
        this.left = emptyTree;
        this.right = emptyTree;
        this.height = 0;
    }

    public boolean isLeaf() { return ((this.left == emptyTree) && (this.right == emptyTree)); }
    public int height() {
        if (this.isLeaf()) return 0;
        else return this.height;
    }


    public void printTree() {
        if (this.isLeaf()) {
            System.out.println(this.name);
            return;
        }
        if (this.left != emptyTree) this.left.printTree();
        System.out.println(this.name);
        if (this.right != emptyTree) this.right.printTree();
    }

    public Node<ContentType> addMember(Node<ContentType> newMember, PrintWriter printWriter) {

        printWriter.println(this.name + " welcomed " + newMember.name);

        int comparison = newMember.GMS.compareTo(this.GMS); // newMember > current node if comparison > 0

        if (comparison < 0) { // newMember < currentNode, put newMember into left
            if (this.left == emptyTree) {
                this.left = newMember;
            }
            else this.left = this.left.addMember(newMember, printWriter);
        }
        else if (comparison > 0) { // newMember > currentNode, put newMember into right
            if (this.right == emptyTree) {
                this.right = newMember;
            }
            else this.right = this.right.addMember(newMember, printWriter);
        }

        return reorganise(this);
    }

    public Node<ContentType> reorganise(Node<ContentType> t) { // reorganise the tree starting with this node
        if (t == emptyTree) return t;
        if (t.right.height() - t.left.height() > 1) {
            if (t.right.right.height() >= t.right.left.height()) { // rr case
                t = t.rightRotation();
            }
            else { // rl case
                t = t.rightLeftRotation();
            }
        }
        else if (t.left.height() - t.right.height() > 1) {
            if (t.left.left.height() >= t.left.right.height()) { // ll case
                t = t.leftRotation();
            }
            else { // lr case
                t = t.leftRightRotation();
            }
        }
        t.height = Math.max(t.right.height(), t.left.height()) + 1;
        return t;
    }
    private Node<ContentType> rightRotation() { // node.rightRotation -> rotates the node with the right child, returns the new root
        Node<ContentType> newRoot = this.right;
        this.right = newRoot.left;
        newRoot.left = this;
        newRoot.height = Math.max(newRoot.left.height(), newRoot.right.height()) + 1;
        this.height = Math.max(this.left.height(), this.right.height()) + 1;
        return newRoot;
    }
    private Node<ContentType> leftRotation() { // node.leftRotation -> rotates the node with the left child, returns the new root
        Node<ContentType> newRoot = this.left;
        this.left = newRoot.right;
        newRoot.right = this;
        newRoot.height = Math.max(newRoot.left.height(), newRoot.right.height()) + 1;
        this.height = Math.max(this.left.height(), this.right.height()) + 1;
        return newRoot;
    }

    private Node<ContentType> leftRightRotation() {
        this.left = this.left.rightRotation();
        return this.leftRotation();
    }
    private Node<ContentType> rightLeftRotation() {
        this.right = this.right.leftRotation();
        return this.rightRotation();
    }

    /**
     *
     * @param name name of the member that will be removed.
     * @param GMS GMS of the member that will be removed.
     * @return new root of the subtree
     */
    public Node<ContentType> removeMember(String name, ContentType GMS, PrintWriter printWriter) {

        Node<ContentType> newRoot = this; // new root after the removal
        int comparison = newRoot.GMS.compareTo(GMS);

        if (comparison > 0) { // root > wanted GMS, go left
            newRoot.left = newRoot.left.removeMember(name, GMS, printWriter);
        }
        else if (comparison < 0) { // root < wanted GMS, go right
            newRoot.right = newRoot.right.removeMember(name, GMS, printWriter);
        }
        else { // root = wanted GMS,  remove this root from its subtree
            if (newRoot.isLeaf()) { // do nothing
                newRoot = emptyTree;
            }
            else if (newRoot.left == emptyTree) { // connect newRoot.right with newRoot.parent
                newRoot = newRoot.right;
            }
            else if (newRoot.right == emptyTree) { // connect newRoot.left with newRoot.parent
                newRoot = newRoot.left;
            }
            else { // both children are non-empty
                Node<ContentType> smallestOfRight = newRoot.right.findMin();
                if (smallestOfRight != newRoot.right) {
                    Node<ContentType> parentOfMin = newRoot.right.parentOfFindMin(); // parent of smallestOfRight
                    newRoot.name = smallestOfRight.name;
                    newRoot.GMS = smallestOfRight.GMS;

                    // remove smallest of right from the right subtree
                    if (smallestOfRight.right == emptyTree) parentOfMin.left = emptyTree;
                    else parentOfMin.left = smallestOfRight.right;
                }
                else {
                    newRoot.name = newRoot.right.name;
                    newRoot.GMS = newRoot.right.GMS;
                    if (newRoot.right.right == emptyTree) newRoot.right = emptyTree;
                    else newRoot.right = newRoot.right.right;
                }
            }
            replaced(newRoot.name, name, printWriter);
        }
        return reorganise(newRoot);
    }
    private Node<ContentType> findMin() {
        Node<ContentType> min = this;
        if (min == emptyTree) return min;
        while (min.left != emptyTree) {
            min = min.left;
        }
        return min;
    }
    private Node<ContentType> parentOfFindMin() { // returns the parent of the returned node of the above method
        Node<ContentType> parentOfMin = this;
        if (parentOfMin == parentOfMin.findMin()) return parentOfMin;
        while(parentOfMin.left != parentOfMin.findMin()) {
            parentOfMin = parentOfMin.left;
        }
        return parentOfMin;
    }
    private void replaced(String name1, String name2, PrintWriter printWriter) {
        printWriter.print(name2 + " left the family, replaced by ");
        if (name1.equals("EMPTY TREE")) printWriter.println("nobody");
        else printWriter.println(name1);
    }

    public static Node<Double> lowestCommonSuperior(Node<Double> root, double GMS1, double GMS2) {
        double curGMS = root.GMS;
        if (GMS1 == curGMS || GMS2 == curGMS)
            return root;
        if (GMS1 < curGMS && curGMS < GMS2)
            return root;
        else if (GMS2 < curGMS && curGMS < GMS1)
            return root;
        else if (GMS1 < curGMS)
            return lowestCommonSuperior(root.left, GMS1, GMS2);
        else
            return lowestCommonSuperior(root.right, GMS1, GMS2);
    }

    public static int divideSelectionCounter = 0;
    /**
     * we're trying to find the max number of nodes that can be selected without any direct superior or inferior selected
     * @return true if the node is selected
     */
    public boolean divide() {
        if (this.isLeaf()) {
            divideSelectionCounter++;
            return true;
        }
        if (this == emptyTree) return false;

        boolean isLeftSelected = this.left.divide();
        boolean isRightSelected = this.right.divide();

        if (!(isRightSelected || isLeftSelected)) { // select this node only if left and right nodes are not selected
            divideSelectionCounter++;
            return true;
        }
        return false;
    }

    /**
     * rank finder function, I find thinking about distance to the boss more digestible than thinking about rank, so I used this name instead
     * @param GMS : GMS of the target
     * @return distance of the target to the boss
     */
    public int findDistanceToBoss(ContentType GMS, int curDistance) {
        if (this == emptyTree) return -1; // this case won't happen if the input isn't faulty, but I included it anyway

        if (this.GMS.compareTo(GMS) == 0) { // found the target
            return curDistance;
        }
        else if (this.GMS.compareTo(GMS) > 0) { // this > target, go left
            return this.left.findDistanceToBoss(GMS, curDistance+1);
        }
        else if (this.GMS.compareTo(GMS) < 0) { // target > this, go right
            return this.right.findDistanceToBoss(GMS, curDistance+1);
        }
        return curDistance;
    }

    public void printRank(int distanceToTargetLevel, PrintWriter printWriter) {
        if (this.height < distanceToTargetLevel + 1) return; // for efficiency. Prevents further checking
        if (this == emptyTree) return;
        if (distanceToTargetLevel == 0) this.printNode(printWriter);
        else {
            this.left.printRank(distanceToTargetLevel - 1, printWriter);
            this.right.printRank(distanceToTargetLevel - 1, printWriter);
        }
    }

    private void printNode(PrintWriter printWriter) {
        printWriter.print(" " + this.name + " " + String.format("%.3f", this.GMS));
        printWriter.flush();
    }
}
