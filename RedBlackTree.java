package swati.util;
import java.util.LinkedList;
import java.util.ArrayList;

public class RedBlackTree<KeyType extends Comparable<KeyType>, AugmentVal, AugmentType extends Augmentor<KeyType, AugmentVal,AugmentType>> {
	AugmentType augmentor = null;
	RedBlackTreeNode<KeyType> root = new RedBlackTreeNode<>();
	int debug = -2;

	public RedBlackTree(KeyType key, AugmentType augmentor){
		this.augmentor = augmentor;
		root = new RedBlackTreeNode<KeyType>(key);
	}
	
	public RedBlackTree(AugmentType augmentor){
		this.augmentor = augmentor;
	}
	
	@Override
	public String toString(){
		LinkedList<RedBlackTreeNode<KeyType>> l = new LinkedList<RedBlackTreeNode<KeyType>>();
		LinkedList<RedBlackTreeNode<KeyType>> l2 = new LinkedList<RedBlackTreeNode<KeyType>>();
		l.add(root);
		String s = "";
		while (!l.isEmpty()){
			while (!l.isEmpty()){
				RedBlackTreeNode<KeyType> N = l.removeFirst();
				s += N.toString() + "  ";
				if (!isNull(N)) {
					l2.addLast(N.children[0]);
					l2.addLast(N.children[1]);
				}
			}
			s += "\n";
			l = l2;
			l2 = new LinkedList<RedBlackTreeNode<KeyType>>();
		}
		return s;
	}
	
	public static void main(String[] args) {
		
	}
	
	class RedBlackTreeNode<KeyType extends Comparable<KeyType>>{
		KeyType key;
		RedBlackTreeNode[] children;
		RedBlackTreeNode parent;
		boolean red;
		boolean isNull;
		AugmentVal a;
		
		public RedBlackTreeNode(){
			//this.key = key; //?
			children = new RedBlackTreeNode[2];
			children[0] = children[1] = null;
			parent = null;
			red = false;
			isNull = true;
			if (augmentor != null) a = augmentor.nullVal();
		}

		public RedBlackTreeNode(KeyType key){
			this.key = key;
			children = new RedBlackTreeNode[2];
			children[0] = new RedBlackTreeNode();
			children[1] = new RedBlackTreeNode();
			children[0].parent = children[1].parent = this;
			parent = null;
			red = true;
			isNull = false;
			a = augmentor.initialVal(key);
		}

		public boolean keyEquals(Object other){ 
			if (other == null) return (this == null);
			if (this == null) return false;
			RedBlackTreeNode o = (RedBlackTreeNode) other;
			return ((this.key == o.key));
		}
		@Override
		public String toString(){
			String s = isNull? "null" : key.toString();
			s += " : " + ((red) ? " r" : " b");
			if (!isNull) {
				s += " " + !isNull(children[0]);
				s += " " + !isNull(children[1]);
			}
			return s;
		}

	}
	
	public boolean isEmpty(){
		return (isNull(root));
	}
	
	/*//testing function
	public RedBlackTreeNode<KeyType> getRoot(){
		return root;
	}
	//testing function
	public KeyType getKey(RedBlackTreeNode<KeyType> n){
		return n.key;
	}*/

	//not sure this is correct
	RedBlackTreeNode<KeyType> searchGreaterEq(KeyType k){
		return searchGreaterEq(k, this.root, null);
	}
	
	RedBlackTreeNode<KeyType> searchGreaterEq(KeyType k,
			RedBlackTreeNode<KeyType> currentNode,
			RedBlackTreeNode<KeyType> bestSoFar) { //finds smallest node >= k
		if (isNull(currentNode)) {
			return bestSoFar;
		}
		if (k.equals(currentNode.key)) {
			return currentNode;
		}
		int c = k.compareTo(currentNode.key);
		if (c == 0) {
			return currentNode;
		}
		if (c == 1){
			return searchGreaterEq(k, currentNode.children[1], bestSoFar);
		}
		return searchGreaterEq(k, currentNode.children[0], currentNode);
	}
		
	RedBlackTreeNode<KeyType> getSibling(RedBlackTreeNode<KeyType> n){
		if (isNull(n.parent)) return null;
		RedBlackTreeNode<KeyType> p = n.parent;
		int i = 0;
		if (n.equals(p.children[i])) i = (i+1)%2;
		return (p.children[i]);
	}
	
	RedBlackTreeNode<KeyType> getParent(RedBlackTreeNode<KeyType> n){
		return n.parent;
	}
	
	RedBlackTreeNode<KeyType> getGrandparent(RedBlackTreeNode<KeyType> n){
		if (isNull(n.parent)) return null;
		return n.parent.parent;
	}
	
	RedBlackTreeNode<KeyType> getUncle(RedBlackTreeNode<KeyType> n){
		RedBlackTreeNode<KeyType> p = n.parent;
		return (isNull(p)) ? null : getSibling(p);
	}
	
	boolean isNull (RedBlackTreeNode<KeyType> node){
		return (node == null || node.isNull);
	}
	
	int findD (RedBlackTreeNode<KeyType> N, RedBlackTreeNode<KeyType> P){
		int i = 0;
		if (!N.equals(P.children[i])) i = (i+1)%2;
		return i;
	}
	// Rotate the parent of N
	// d = 0 ==> right rotation, and assume that N is a left child
	// d = 1 ==> left rotation, and assume that N is a right child
	void rotate(RedBlackTreeNode<KeyType> N, int d){ 
		RedBlackTreeNode<KeyType> P = N.parent;
		if (isNull(P)) return; // N is the root, so nothing to rotate 
		if (!isNull(getGrandparent(N))) {
			int p = findD(P, P.parent);
			P.parent.children[p] = N;
		}
		else
			root = N;
		N.parent = P.parent;
		P.parent = N;
		P.children[d] = N.children[(d+1)%2];
		N.children[(d+1)%2] = P;
		P.children[d].parent = P;
		
		P.a = augmentor.updateVal(P, this);
		N.a = augmentor.updateVal(N, this);
	}
	
	public void insert(KeyType key){
		RedBlackTreeNode<KeyType> N = new RedBlackTreeNode<KeyType>(key);
		if (!isNull(root)){
			insertNode(N);
			repairTree(N);
			while (!isNull(N.parent)) N = N.parent;
		}
		root = N;
		root.red = false;
	}
	
	RedBlackTreeNode<KeyType> insertReturn(KeyType key){
		RedBlackTreeNode<KeyType> N = new RedBlackTreeNode<KeyType>(key);
		RedBlackTreeNode<KeyType> insertedNode = N;
		if (!isNull(root)){
			insertNode(N);
			repairTree(N);
			while (!isNull(N.parent)) N = N.parent;
		}
		root = N;
		root.red = false;
		return insertedNode;
	}
	
	public void insert(KeyType[] keys){
		for (KeyType key : keys) {
			insert(key);
			if (debug == 0) System.out.println(key);
		}
	}
	
	void insertNode(RedBlackTreeNode<KeyType> N){
		RedBlackTreeNode<KeyType> P = root;
		int i;
		while (true){
			i = 0;
			if (N.key.compareTo(P.key) == 1) i = 1;
			P.a = augmentor.incrementVal(P, N);
			if (isNull(P.children[i])) break;
			P = P.children[i];
		}
		P.children[i] = N;
		N.parent = P;
	}
	
	void repairTree(RedBlackTreeNode<KeyType> N){
		if (isNull(N.parent)) return;
		else if (!N.parent.red) return;
		else if (!isNull(getUncle(N)) && getUncle(N).red) insertC1(N);
		else {
			//(!getUncle(N).red); (or uncle is null)
			insertC2(N);
		}
	}
	
	void insertC1(RedBlackTreeNode<KeyType> N){
		if (debug == 0) System.out.println(N + "  C1");
		N.parent.red = false;
		getUncle(N).red = false;
		RedBlackTreeNode<KeyType> G = getGrandparent(N);
		G.red = true;
		repairTree(G);
	}
	
	void insertC2(RedBlackTreeNode<KeyType> N){
		if (debug == 0) System.out.println(N + "  C2");
		RedBlackTreeNode<KeyType> P = N.parent;
		RedBlackTreeNode<KeyType> G = getGrandparent(N);
		if (findD(N, P) != findD(P, G)){
			rotate(N, findD(N, P));
			if (debug == 0) System.out.println("rotate1");
			P = N;
		}
		//N, P, G may refer to different nodes than initially
		rotate(P, findD(P, G));
		P.red = false;
		G.red = true;
	}

	ArrayList<KeyType> inOrder() {
		ArrayList<KeyType> list = new ArrayList<KeyType>();
		return inOrder(root, list);
	}
	
	ArrayList<KeyType> inOrder(RedBlackTreeNode<KeyType> node, ArrayList<KeyType> list) {
		if (isNull(node)) {
			return list;
		}
		inOrder(node.children[0], list);
		list.add(node.key);
		inOrder(node.children[1], list);
		return list;
	}
	
	String checkTree() {
		// Returns either empty string or an error message
		if (isNull(root)) {
			return "";
		}
		return checkTree(root, false, 0, new ArrayList<Integer>());
	}
	
	String checkTree(RedBlackTreeNode<KeyType> node,
			boolean wasPreviousRed,
			int currentBlackHeight,
			ArrayList<Integer> previousBlackHeight) {
		if (isNull(node)) {
			if (previousBlackHeight.size() > 0 && previousBlackHeight.get(0) != currentBlackHeight) {
				return "Got black height of " + currentBlackHeight + " at " + node.key +
						" but previous blakc height was " + previousBlackHeight.get(0);
			}
			if (previousBlackHeight.size() == 0) {
				previousBlackHeight.add(currentBlackHeight);
			}
			return "";
		}
		
		if (node.red && wasPreviousRed) {
			return "Got second node in succession: " + node.key;
		}
		
		String checkNode = augmentor.checkNode(node, this);
		if (!checkNode.equals("")){
			return "Augmentation incorrect at " + node.key + " with following values -- " + checkNode;
		}
		
		wasPreviousRed = node.red;
		if (!node.red) {
			currentBlackHeight ++;
		}
		String s = checkTree(node.children[0], wasPreviousRed,
				currentBlackHeight, previousBlackHeight); 
		if (!s.equals("")) {
			return s; 
		}
		return checkTree(node.children[1], wasPreviousRed, currentBlackHeight, previousBlackHeight);
	}
	
	void deleteKey(KeyType key){
		deleteKey(key, root);
	}
	
	void deleteKey(KeyType key, RedBlackTreeNode<KeyType> n){
		if (isNull(n)) return;
		if (n.key.equals(key)){
			deleteNode(n);
			return;
		}
		int i = 0;
		if (key.compareTo(n.key) == 1) i = 1;
		deleteKey(key, n.children[i]);
	}
	
	//need to propagate swap-out check up tree WITHOUT messing with orderstatisticsaugmentation
	void deleteNode(RedBlackTreeNode<KeyType> n){
		if (debug == 1) System.out.println("deleting n: " + n);
		if (debug == 1) System.out.println("tree is " + this);

		if (!(isNull(n.children[0]) || isNull(n.children[1]))) {
			RedBlackTreeNode<KeyType> y = getPredecessor(n);
			KeyType k = n.key; //added
			n.key = y.key; //key swap-out
			n.a = augmentor.updateVal(n, this);
			augmentor.deletionFix(n, this);
			
			//added
			y.key = k;
			y.a = augmentor.updateVal(y, this);
			augmentor.deletionFix(y, this);
			
			deleteNode(y);
			return;
		}
		//augmentor.deletionFix(n, this);
		//if (!augmentor.deletionFix(n, this)) return; //should never happen
		
		RedBlackTreeNode<KeyType> x = (isNull(n.children[0])) ? n.children[1] : n.children[0];
		x.parent = n.parent; // x is either red or null
		if (isNull(n.parent)){
			root = x; //bc n (prev root) will be deleted
		}
		else if (n.equals(n.parent.children[0])){
			n.parent.children[0] = x;
		}
		else n.parent.children[1] = x;
		if (debug == 1) {
			System.out.println("pre-fix: " + this);
			//System.out.println(x.parent);
		}
		if (!isNull(x.parent)) x.parent.a = augmentor.decrementVal(x.parent, x, this);
		augmentor.deletionFix(x.parent, this);
		if (!n.red) fixTree(x); //n is black and hence the back height changed, but remember that x is null 
		if (debug == 1) System.out.println("post-fix: " + this);

		root.red = false;
	}
	
	void fixTree(RedBlackTreeNode<KeyType> x){ // x is definitely black, and may also be null
		while (!root.equals(x) && !x.red){
			int d = findD(x, x.parent);
			RedBlackTreeNode<KeyType> w = x.parent.children[(d+1)%2]; // w is x's sibling
			if (w.red){
				w.red = false;
				x.parent.red = true;
				rotate(w, (d+1)%2);
				w = x.parent.children[(d+1)%2];
			}
			if (!w.children[d].red && !w.children[(d+1)%2].red){
				w.red = true;
				x = x.parent;	
			}
			else{
				if (!w.children[(d+1)%2].red){
					if (debug == -1){
						System.out.println("Case 3: " + this);
					}
					w.children[d].red = false;
					w.red = true;
					rotate(w.children[d], d);
					w = x.parent.children[(d+1)%2];
					if (debug == -1) System.out.println(this);
				}
				if (debug == 1){
					System.out.println("C4: " + this);
				}
				w.red = x.parent.red;
				x.parent.red = false;
				w.children[(d+1)%2].red = false;
				rotate(w, (d+1)%2);
				x = root;
			}
		}
		x.red = false;
	}
	
	RedBlackTreeNode<KeyType> getMinimum() {
		if (isNull(root)) {
			return root;
		}
		return getMinimum(root);
	}
	RedBlackTreeNode<KeyType> getMinimum(RedBlackTreeNode<KeyType> n) {
		while (!isNull(n.children[0]))
			n = n.children[0];
		return n;
	}	

	RedBlackTreeNode<KeyType> getMaximum() {
		if (isNull(root)) {
			return root;
		}
		return getMaximum(root);
	}
	RedBlackTreeNode<KeyType> getMaximum(RedBlackTreeNode<KeyType> n) {
		while (!isNull(n.children[1]))
			n = n.children[1];
		return n;
	}	

	
	RedBlackTreeNode<KeyType> getSuccessor(RedBlackTreeNode<KeyType> n){
		if (!isNull(n.children[1])) {
			return getMinimum(n.children[1]);
		}
		// else follow parent pointers to find the first time you are in the left subtree
		while (!isNull(n.parent) && n.equals(n.parent.children[1])) {
			n = n.parent;
		}
		return n.parent;
	}

	RedBlackTreeNode<KeyType> getPredecessor(RedBlackTreeNode<KeyType> n){
		if (!isNull(n.children[0])) {
			return getMaximum(n.children[0]);
		}
		// else follow parent pointers to find the first time you are in the right subtree
		while (!isNull(n.parent) && n.equals(n.parent.children[0])) {
			n = n.parent;
		}
		return n.parent;
	}

}