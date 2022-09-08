/*
ID: ms.swat1
LANG: JAVA
TASK: bphoto
*/

package swati.usaco.training.jan2017gold;

import java.util.StringTokenizer;

import swati.util.Node;

import java.io.*;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Random;

public class bphoto {
	int debug = 0;
	int N;
	int[] heights;
	HashMap<Integer, Integer> heightToCow = new HashMap<Integer, Integer>();
	int numUnbalanced = 0;

	public static void main(String[] args) throws IOException{
		bphoto data = new bphoto();
		if (args.length != 0){
			data.debug = Integer.parseInt(args[0]);
		}
		data.readIn();
		data.compute();
		data.output();
	}
	
	public void readIn() throws IOException{
		BufferedReader br = new BufferedReader(
                new FileReader("bphoto.in"));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		for (int i = 0; i < N; i++){
			int h = Integer.parseInt(st.nextToken());
			heights[i] = h;
			heightToCow.put(h, i);
		}
		Arrays.sort(heights);
	}
	
	public void compute(){
		int[] head = new int[heightToCow.get(heights[N-1])];
		SkipList sl = new SkipList(head);
		for (int i = N-2; i >= 0; i--){
			
		}
	}

	public class Node {
		int val;
		Node rightPointer;
		Node downPointer;
		Node leftPointer;
		public Node(){
		}
		public Node(int val){
			this.val = val;
		}
		public Node(Node n){
			this.val = n.val;
			this.downPointer=  n;
		}
		public boolean hasRightNeighbor(){
			return (rightPointer != null);
		}
		public boolean hasChild(){
			return (downPointer != null);
		}
		public boolean hasLeftNeighbor(){
			return (leftPointer != null);
		}
	}

	public class LocalLinkedList{
		Node head;
		
		Node peekFirst(){
			return head;
		}
		boolean hasHead(){
			return (head!=null);
		}
	}


	public class SkipList {
		public Node topHead;
		public SkipList(int[] elements){
			LocalLinkedList l = new LocalLinkedList();
			Node prev = new Node(elements[elements.length -1]);
			for (int i = elements.length-2; i >= 0; i--){ //makes bottom level
				Node node = new Node(elements[i]);
				node.rightPointer = prev;
				prev.leftPointer = node;
				prev = node;
				if (i == 0) l.head = node;
			}
			int levelSize = elements.length;
			Random randomNum = new Random();
			
			while (levelSize > 2){ //while the level is not small enough to be the top,
				//promote some elements to be the next level
				Node node = l.peekFirst();
				Node head = node;
				int nextLevelSize = 0;
				LocalLinkedList nextL = new LocalLinkedList();
				Node prevNode = new Node();
				for (int i = 0; i < levelSize; i++){ 
					int r = randomNum.nextInt(2);
					if (r == 0){ //if we're not going to promote this node
						node = node.rightPointer;
						continue;
					}
					
					if (!nextL.hasHead()){ //if this is the first node we're promoting on this level
						nextL.head = new Node(node);
						nextLevelSize++;
						prevNode = nextL.head;
					}
					
					else{
						Node nextNode = new Node(node);
						nextLevelSize++;
						nextNode.leftPointer = prevNode;
						prevNode.rightPointer = nextNode;
						prevNode = nextNode;
					}
					node = node.rightPointer;
				}
				levelSize = nextLevelSize;
				if (levelSize > 0) l = nextL;
			}
			topHead = l.peekFirst();
		}
		
		public Node getTopHead(){
			return topHead;
		}
		
		public void add(int val){
			addInner(val, topHead, true);
		}
		
		public boolean addInner(int val, Node n, boolean toAdd){
			boolean foundLocation = false;
			while (!foundLocation){ //keep going till you find the node n that
				//will be used as a down pointer
				if (n.val == val || belongsOnImmediateLeft(val, n) || belongsOnImmediateRight(val, n)) break;
				if (n.val > val){
					if (n.hasLeftNeighbor()){
						n = n.leftPointer;
						continue;
					}
					break;
				}
				if (n.val < val){
					if (n.hasRightNeighbor()){
						n = n.rightPointer;
						continue;
					}
				}
				break;
			}
			
			if (!n.hasChild()){ //meaning that this is the bottom level
				Node node = new Node(val);
				addNode(node, val, n);
				Random random = new Random();
				return random.nextBoolean(); 
			}
			
			toAdd = addInner(val, n.downPointer, toAdd);
			if (!toAdd) return toAdd;
			
			Node child = searchVal(val, n.downPointer);
			Node node = new Node(val);
			addNode(node, val, n);
			node.downPointer = child;
			
			Random random = new Random();
			return random.nextBoolean();
		}
		
		void addNode(Node node, int val, Node n){ //adds a node to a level
			if (val <= n.val){ //add node to the left of n
				if (n.hasLeftNeighbor()){
					Node leftNode = n.leftPointer;
					leftNode.rightPointer = node;
					node.leftPointer = leftNode;
				}
				n.leftPointer = node;
				node.rightPointer = n;
			}
			if (val > n.val){ //add node to the right of n
				if (n.hasRightNeighbor()){
					Node rightNode = n.rightPointer;
					rightNode.leftPointer = node;
					node.rightPointer = rightNode;
				}
				n.rightPointer = node;
				node.leftPointer = n;
			}
		}
		
		Node searchVal(int val, Node n){ //assumes that the node with the val
			//does in fact exist at this level; this breaks down if not
			while (val != n.val){
				if (val < n.val) n = n.leftPointer;
				else n = n.rightPointer;
			}
			return n;
		}
		
		boolean belongsOnImmediateLeft(int v, Node n){
			return ((n.val >= v) && (!n.hasLeftNeighbor() || n.leftPointer.val <= v));
		}
		
		boolean belongsOnImmediateRight(int v, Node n){
			return ((n.val <= v) && (!n.hasRightNeighbor() || n.rightPointer.val >= v));
		}
		
		@Override
		public String toString(){ //converts our SkipList to an easy to read String format
			Node n = this.topHead;
			String s = "";
			while (true){
				Node head = n;
				while (n.hasLeftNeighbor()){
					n = n.leftPointer;
				}
				while (n.hasRightNeighbor()){
					s += (n.val + " ");
					n = n.rightPointer;
				}
				s += (n.val) + "\n";
				if (!head.hasChild()) break;
				n = head.downPointer;
			}
			return s;
		}
		
		public int findVal(int val, Node n){ //this function is kind of useless, but I haven't the heart to delete it
			if (n.val == val || belongsOnImmediateLeft(val, n)){ //if we are in the correct range
				if (n.hasChild()){
					return findVal(val, n.downPointer);
				}
				return n.val;
			}
			if (n.val > val){ //if we need to move left
				if (n.hasLeftNeighbor()) return findVal(val, n.leftPointer);
				if (!n.hasChild()) return n.val;
			}
			if (n.val < val){ //if we need to move right
				if (n.hasRightNeighbor()) return findVal(val, n.rightPointer);
				if (!n.hasChild()) return -1;
			}
			return findVal(val, n.downPointer);
		}
	}

	
	public void output() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("bphoto.out")));
		out.println(numUnbalanced);
		out.close();
	}

}
