package swati.util;

import java.util.Arrays;
import java.util.ArrayList;

public class ConvexHull {
	int debug = 0;
	Point root;
	final double EPSILON = 0.00001;
	public Point[] points;
	public ArrayList<Point> hull;
	
	public ConvexHull(){
	}
	
	public ConvexHull(Point[] points){
		this.points = points;
	}
	
	public boolean triangleCheck(Point[] vertices, Point p){
		boolean b1 = sameSide(p, vertices[2], vertices[0], vertices[1]);
		boolean b2 = sameSide(p, vertices[0], vertices[1], vertices[2]);
		boolean b3 = sameSide(p, vertices[1], vertices[2], vertices[0]);
		if (debug == 1) System.out.println("b1: " + b1 + " b2: " + b2 + " b3: " + b3);
		return (b1 && b2 && b3);
	}
	
	public boolean sameSide(Point p, Point remainingVertex, Point v1, Point v2){
		double m;
		if (v2.x - v1.x == 0){ //this check takes care of the case where the slope is undefined (vertical line)
			if (p.x == v1.x){
				if (remainingVertex.x == v1.x) return onSegment(p, remainingVertex, v1, v2);
				return onSegment(p, v2, v2, v1);
			}
			return ((p.x < v1.x && remainingVertex.x <= v1.x) || (p.x > v1.x && remainingVertex.x >= v1.x));
		}
		m = ((v2.y-v1.y) * 1.0) / (v2.x-v1.x);
		double b = v1.y - m *v1.x;
		double pSign = m * p.x + b - p.y; double vSign = m * remainingVertex.x + b - remainingVertex.y;
		//the above works because two points on the same side of a line must have the same sign when plugged into the line equation
		//btw, if something is on the line, it can be counted as being on either side
		if (debug ==1) System.out.println("m: " + m + " b: " + b + " pSign: " + pSign + " vSign: " + vSign);
		if (vSign == 0){
			if (pSign != 0) return false;
			if (debug == 1) System.out.println("calling onSegment");
			//special check if all three vertices are on the same line
			return onSegment(p, remainingVertex, v1, v2);
		}
		return sameSign(pSign, vSign);
	}
	
	public boolean sameSign(double p, double v){
		if (p >= -1 * EPSILON && v > -1 * EPSILON) return true;
		if (p <= EPSILON && v < EPSILON) return true;
		return false;
	}
	
	public static boolean onSegment(Point p, Point v1, Point v2, Point v3){
		//checks if point p is within the line segment defined by v1, v2, and v3
		Point leftMost = findLeftMost(v1, v2, v3);
		Point rightMost = findRightMost(v1, v2, v3);
		if (leftMost.x <= p.x  && p.x <= rightMost.x){
			Point highest = findHighest(v1, v2, v3);
			Point lowest = findLowest(v1, v2, v3);
			return lowest.y <= p.y  && p.y <= highest.y;
		}
		return false;
	}
	
	public static Point findLeftMost(Point v1, Point v2, Point v3){
		Point leftMost = v1;
		if (v2.x < leftMost.x) leftMost = v2;
		if (v3.x < leftMost.x) leftMost = v3;
		return leftMost;
	}
	
	public static Point findRightMost(Point v1, Point v2, Point v3){
		Point rightMost = v1;
		if (v2.x > rightMost.x) rightMost = v2;
		if (v3.x > rightMost.x) rightMost = v3;
		return rightMost;
	}
	
	public static Point findHighest(Point v1, Point v2, Point v3){
		Point highest = v1;
		if (v2.y > highest.y) highest = v2;
		if (v3.y > highest.y) highest = v3;
		return highest;
	}
	
	public static Point findLowest(Point v1, Point v2, Point v3){
		Point lowest = v1;
		if (v2.y < lowest.y) lowest = v2;
		if (v3.y < lowest.y) lowest = v3;
		return lowest;
	}
	
	
	
	//functions for findAngles
	public void findAngles(){
		findRoot();
		findAnglesInner(points);
	}
	
	public void findAnglesInner(Point[] points){
		if (debug == 1) System.out.println("root: " + root);
		for (Point p : points){
			if (p.y == root.y && p.x == root.x){
				p.initAngle(0);
				continue;
			}
			Point pComparedtoRoot = new Point(p.x-root.x, p.y-root.y);
			Point horizontaltoRoot = new Point(1, 0);
			if (debug == 1){
				System.out.println("p: " + p + " becomes " + pComparedtoRoot);
				System.out.println(horizontaltoRoot);
			}
			double costheta = pComparedtoRoot.dotProduct(horizontaltoRoot) / (pComparedtoRoot.length() * horizontaltoRoot.length());
			double theta = Math.acos(costheta);
			p.initAngle(theta);
		}
	}
	
	public void findRoot(){
		root = points[0];
		for (Point p : points){
			if ((p.y < root.y) || (p.y == root.y && p.x < root.x)){
				root = p;
			}
		}
	}
	
	
	
	//functions for makeHull
	public void makeHull(){
		Arrays.sort(points);
		hull = new ArrayList<Point>();
		for (int i = 0; i < points.length; i++){
			Point p = points[i];
			hull.add(p);
			if (hull.size() <= 3) continue;
			while (hull.size() > 3){
				Point[] vertices = {p, hull.get(hull.size()-3), root};
				if (triangleCheck(vertices, hull.get(hull.size()-2))){ 
					hull.remove(hull.size()-2);
				}
				else{ break;}
			}
			if (debug == 2){
				System.out.println("Current hull: ");
				for (Point point : hull) System.out.println(point);
			}
		}
		if (hull.get(1).angle == hull.get(2).angle) hull.remove(1);
		if (hull.get(hull.size()-1).angle == hull.get(hull.size()-2).angle) hull.remove(hull.size()-1);
	}
	
	
	
	//functions for containsPoint
	public boolean containsPoint(Point z){
		Point[] ps = {z};
		findAnglesInner(ps);
		int i = findIndex(z);
		if (debug == 3) System.out.println(i);
		if (i == -1) return false;
		Point[] vertices = {hull.get(i), hull.get(i+1), root};
		return (triangleCheck(vertices, z));
	}
	
	public int findIndex(Point z){
		for (int i = 0; i < hull.size(); i++){
			if (hull.get(i).angle < z.angle) continue;
			if (i==0 && hull.get(i).angle == z.angle) return i;
			return i-1;
		}
		return -1;
	}
	
}
