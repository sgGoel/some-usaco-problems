package swati.util;

public class IntervalAugmentation implements Augmentor<ComparableInterval,Integer,IntervalAugmentation>{
	@Override
	public Integer updateVal(RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation> t){
		if (t.isNull(n)) return nullVal();
		Integer c1 = (Integer) n.children[0].a; Integer c2 = (Integer) n.children[1].a;
		if (t.isNull(n.children[0])) c1 = initialVal(n.key); 
		if (t.isNull(n.children[1])) c2 = initialVal(n.key); 
		return (Math.max(Math.max(c1, c2), initialVal(n.key)) );
	}
	@Override
	public Integer nullVal(){
		return 0;
	}
	@Override
	public Integer initialVal(Object o){
		ComparableInterval c = (ComparableInterval) o;
		return c.endValue;
	}
	@Override
	public Integer incrementVal(RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n1,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n2){
		return Math.max(n1.a, n2.key.endValue);
	}
	@Override
	public Integer decrementVal(RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n1,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n2,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation> t){
		return updateVal(n1, t);
		/*int i = 0;
		if (n1.children[i] != n2) i = 1;
		if (n1.children[i] != n2) return updateVal(n1, t);
		Integer c2 = (Integer) n1.children[(i+1)%2].a;
		return Math.max(n1.key.endValue, c2);*/
		//n1 is not parent of n2, plain-update
		//else, one-sided update
	}
	@Override
	public String checkNode(RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation> t){
		String s = "";
		if (t.isNull(n.parent)) return s;
		if (t.isNull(t.getSibling(n)) && n.parent.a == n.a) return s;
		Integer augVal = n.a;
		Integer sibVal = t.getSibling(n).a;
		Integer parVal = t.getParent(n).a;
		if (Math.max(Math.max(sibVal, augVal), initialVal(t.getParent(n).key)) == parVal) return s;
		return "Augmented Value: " + augVal + " Sibling Value: " + sibVal + " Parent Value: " + parVal + " Parent end-point: " + t.getParent(n).key;
	}
	@Override
	public boolean deletionFix(RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> n,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation> t){
		RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> r = t.root;
		RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> p = (t.isNull(n)) ? n : t.getParent(n);
		while (!t.isNull(p)){
			p.a = updateVal(p, t);
			p = t.getParent(p);
		}
		return true;
	}
	//no check that it is not the same interval in intervalSearch
	public RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval>
	intervalSearch(ComparableInterval n,
			RedBlackTree<ComparableInterval,Integer,IntervalAugmentation> t){
		RedBlackTree<ComparableInterval,Integer,IntervalAugmentation>.RedBlackTreeNode<ComparableInterval> x = t.root;
		while (!t.isNull(x) && !overlap(n, x.key)){
			if (!t.isNull(x.children[0]) && ((Integer) x.children[0].a) > n.compareValue) {
				x = x.children[0];
			}
			else x = x.children[1];
		}
		return x;
	}
	//defined so starting and ending at same point is not an overlap
	public boolean overlap(ComparableInterval n1, ComparableInterval n2){
		return ((n1.compareValue < n2.endValue) && (n1.endValue > n2.compareValue));
	}
	
}