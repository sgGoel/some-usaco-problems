/*
ID: ms.swat1
LANG: JAVA
TASK: hps
*/

package swati.usaco.training.jan2017gold;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.io.*;

public class hps {
	int debug = 0;
	int maxWins = 0;
	int N; int K;
	int[] fjMoves;
	HashMap<Long, Integer> yaCalculado = new HashMap<Long, Integer>();
	LinkedList<int[]> queue = new LinkedList<int[]>();

	public static void main(String[] args) throws IOException{
		hps data = new hps();
		if (args.length != 0){
			data.debug = Integer.parseInt(args[0]);
		}
		data.readIn();
		data.compute();
		data.output();
	}
	
	public void readIn() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("hps.in"));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		fjMoves = new int[N];
		for (int i = 0; i < N; i++){
			st = new StringTokenizer(br.readLine());
			String move = st.nextToken();
			int m = 0;
			if (move.equals("P")) m = 1;
			if (move.equals("S")) m = 2;
			fjMoves[i] = m;
		}
	}
	
	public void compute(){
		int o1 = computeInner(0, K, 0);
		int o2 = computeInner(0, K, 1);
		int o3 = computeInner(0, K, 2);
		maxWins = Math.max(o1,  o2);
		maxWins = Math.max(maxWins,  o3);
	}
	
	int computeInner(int i, int k, int prev){
		if (i == N) return 0;
		long l = makeKey(i, k, prev);
		if (yaCalculado.containsKey(l)) return yaCalculado.get(l);
		int futureWins = ( ((prev+2)%3 == fjMoves[i]) ? 1 : 0) + computeInner(i+1, k, prev);
		if (k > 0){
			int w1 = ( ((prev)%3 == fjMoves[i]) ? 1 : 0) + computeInner(i+1, k-1, (prev+1)%3);
			int w2 = ( ((prev+1)%3 == fjMoves[i]) ? 1 : 0) + computeInner(i+1, k-1, (prev+2)%3);
			int w = Math.max(w1,  w2);
			futureWins = Math.max(futureWins, w);
		}
		yaCalculado.put(l,  futureWins);
		return futureWins;
	}
	
	
	long makeKey(int i, int k, int prev){
		long l = i + (k << 17) + (prev << 22);
		return l;
	}
	
	public void output() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("hps.out")));
		out.println(maxWins);
		out.close();
	}

}
