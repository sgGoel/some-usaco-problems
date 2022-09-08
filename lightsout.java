/*
ID: ms.swat1
LANG: JAVA
TASK: lightsout
*/

package swati.usaco.training.combocontestgold1;

import java.util.StringTokenizer;
import java.io.*;
import java.util.HashMap; import java.util.HashSet;

public class lightsout {
	int debug = 0;
	int N;
	int[][] vertices;
	int maxDiff = -1;

	public static void main(String[] args) throws IOException{
		lightsout data = new lightsout();
		if (args.length != 0){
			data.debug = Integer.parseInt(args[0]);
		}
		data.readIn();
		data.compute();
		data.output();
	}
	
	public void readIn() throws IOException{
		BufferedReader br = new BufferedReader(
                new FileReader("lightsout.in"));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		vertices = new int[N][2];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			vertices[i][0] = Integer.parseInt(st.nextToken());
			vertices[i][1] = Integer.parseInt(st.nextToken());
		}
	}
	
	public void compute(){
		//fill these two arrays
		int[] minDistances = new int[N];
		char[] turnTypes = new char[N];
		int[] newDistances = new int[N];
		int[][] clockwiseDistances = new int[N][N];
		
		for (int i = 1; i < N; i++) {
			int m = compDistance(vertices[i], vertices[i-1]);
			minDistances[i] = m+minDistances[i-1];
			turnTypes[i] = turnType(vertices[(i+1)%N], vertices[i], vertices[(i-1+N)%N]);
		}
		turnTypes[0] = turnType(vertices[1], vertices[0], vertices[N-1]);
		
		for (int i = N-1; i > 0; i--) {
			int m2 = compDistance(vertices[i], vertices[(i+1)%N])+minDistances[(i+1)%N];
			minDistances[i] = Math.min(minDistances[i], m2);
		} 
		
		for (int i = 0; i < N; i++) {
			for (int j = i+1; j < N; j++) {
				int d = compDistance(vertices[j], vertices[j-1]);
				clockwiseDistances[i][j] = clockwiseDistances[i][j-1] + d;
			}
		}
		
		HashMap<Integer, HashSet<Integer>> rollingHash = new HashMap<Integer, HashSet<Integer>>();
		int numSteps = 0;
		int lastId = 0;
		//initialize rollingHash
		rollingHash.put(0, new HashSet<Integer>());
		for (int i = 1; i < N; i++) rollingHash.get(0).add(i);
		
		while (!rollingHash.isEmpty()) { //the problem is somewhere in this block of code
			//I'd recommend defining specifically what each variable means
			HashMap<Integer, HashSet<Integer>> newHash = new HashMap<Integer, HashSet<Integer>>();
			for (int k : rollingHash.keySet()) {
				HashMap<String, HashSet<Integer>> tempHash = new HashMap<String, HashSet<Integer>>();
				for (int pos : rollingHash.get(k)) {
					int p = (numSteps + pos) % N;
					String s = "";
					if (numSteps == 0) s += turnTypes[p];
					s += compDistance(vertices[p], vertices[(p+1)%N]) + "" + turnTypes[(p+1)%N];
					if (!tempHash.containsKey(s)) tempHash.put(s, new HashSet<Integer>());
					tempHash.get(s).add(pos);
				}
				for (String s : tempHash.keySet()) {
					newHash.put(lastId+1, tempHash.get(s));
					//rollingHash.put(lastId+1, tempHash.get(s));
					lastId++;
				}
				//toRemove.add(k);
			}
			//rollingHash.keySet().removeAll(toRemove);
			rollingHash = newHash;
			
			HashMap<Integer, HashSet<Integer>> removals = new HashMap<Integer, HashSet<Integer>>();
			for (int k : rollingHash.keySet()) {
				//account for cases where either a string is unique or a cow starting
				//at a specific position would have reached the ending
				removals.put(k, new HashSet<Integer>());
				for (int p : rollingHash.get(k)) {
					if (rollingHash.get(k).size() == 1) {
						newDistances[p] = clockwiseDistances[p][(p+numSteps+1)%N]
								+ minDistances[(p+numSteps+1)%N];
						removals.get(k).add(p);
					}
					if ((p+numSteps+1)%N == 0) {
						newDistances[p] = clockwiseDistances[p][(p+numSteps+1)%N];
						removals.get(k).add(p);
					}
				}
			}
			
			for (int k : removals.keySet()) {
				rollingHash.get(k).removeAll(removals.get(k));
				if (rollingHash.get(k).size() == 0) rollingHash.remove(k);
			}
			
			numSteps++;
		}
		
		for (int i = 0; i < N; i++) {
			int diff = newDistances[i]-minDistances[i];
			if (diff > maxDiff) maxDiff = diff;
			if (debug == 1 && diff == 1342) System.out.println("1342: " + i + " " + newDistances[i] + ", " + minDistances[i]);
			if (debug == 1 && diff == 1326) System.out.println("1326: " + i);
		}
		
		if (debug == 1) {
			System.out.println("new + old min");
			for (int n : newDistances) System.out.print(n + ", ");
			System.out.print('\n');
			for (int n : minDistances) System.out.print(n + ", ");
			System.out.print('\n');
		}
	}
	
	int compDistance(int[] t1, int[] t2) {
		return Math.abs(compDInner(t1, t2));
	}
	
	int compDInner(int[] t1, int[] t2) {
		return t1[1]-t2[1] + t1[0]-t2[0];
	}
	
	char turnType(int[] t1, int[] t2, int[] p) {
		char typ = 't';
		int d1 = compDInner(t1, t2); int dp = compDInner(t2, p);
		boolean h = isHorizontal(t2, p);
		if (h) { //>= necessary?
			if (d1 > 0 && dp < 0 || d1 < 0 && dp > 0) typ = 'n';
		}
		else {
			if (d1 > 0 && dp > 0 || d1 < 0 && dp < 0) typ = 'n';
		}
		//positive and prev positive vertical --> 90
		//negative and prev positive horizontal --> 90
		//negative and prev negative vertical --> 90
		//positive and prev negative horizontal --> 90
		return typ;
	}
	
	boolean isHorizontal(int[] t1, int[] t2) {
		return (t1[1] - t2[1] == 0);
	}
	public void output() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("lightsout.out")));
		out.println(maxDiff);
		out.close();
	}
	
	//2:27:00 of 3 hr contest left = first draft, used some previously written code, probs spared me 20 mins

}
