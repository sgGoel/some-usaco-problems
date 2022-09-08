/*
ID: ms.swat1
LANG: JAVA
TASK: cbarn2
*/

//dynamic programming
package swati.usaco.training.combocontestgold1;
import java.util.StringTokenizer;
import java.io.*;

public class cbarn2 {
	int debug = 0;
	int N; int K;
	int[] barn;
	long[][][] yaCalculado;
	long[][] numDist;
	long minDistTraveled = -1;

	public static void main(String[] args) throws IOException{
		cbarn2 data = new cbarn2();
		if (args.length != 0){
			data.debug = Integer.parseInt(args[0]);
		}
		data.readIn();
		data.compute();
		data.output();
	}
	
	public void readIn() throws IOException{
		BufferedReader br = new BufferedReader(
                new FileReader("cbarn2.in"));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		barn = new int[N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			barn[i] = Integer.parseInt(st.nextToken());
		}
	}
	
	public void compute(){
		compDist();
		yaCalculado = new long[K+1][N][N];
		for (int k = 0; k <= K; k++) {
			for (int f = N-1; f >= 0; f--) {
				for (int p = N-1; p >= f; p--) {
					computeInner(f, p, k);
				}
			}
		}
		
		if (debug == -1) {
			System.out.println("numDist total");
			for (long[] r : numDist) {
				for (long c : r) {
					System.out.print(c + ", ");
				}
				System.out.print('\n');
			}
		}
		
		if (debug == -1) {
			System.out.println("yaCalculado K-1, partial");
			for (long[] r : yaCalculado[K-1]) {
				for (long c : r) {
					System.out.print(c + ", ");
				}
				System.out.print('\n');
			}
		}
		
		for (int i = 0; i < N; i++) {
			long d = yaCalculado[K-1][i][i];
			if (minDistTraveled == -1 || minDistTraveled > d) minDistTraveled = d;
		}
	}
	
	void computeInner(int f, int p, int k) {
		//f, p, and c are positions; f = first door placed, p = most recent door placed, n = next position for door
		if (k == 0 || p == N-1) { 
			//option where no other door is placed before end of array
			yaCalculado[k][f][p] = numDist[p][f];
			//if (f == p) yaCalculado[k][f][p] = numDist[f][p];
			return;
		}
		long t = -1;
		for (int n = p+1; n < N; n++) { 
			//option where at least one more door is placed before end of array
			long t2 = yaCalculado[k-1][f][n] + numDist[p][n];
			if (t == -1 || t > t2) t = t2;
		}
		yaCalculado[k][f][p] = t;
	}
	
	void compDist() {
		numDist = new long[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = i+1; j < N; j++) {
				numDist[i][j] = numDist[i][j-1]+(j-i-1)*barn[j-1];
			}
			/*for (int j = 1; j <= i; j++) {
				numDist[i][j] = numDist[i][j-1]+(j+N-i)*barn[j-1];
			}*/
			
			for (int j = 0; j <= i; j++) {
				if (j == 0) {
					numDist[i][j] = numDist[i][N-1]+(j+N-i-1)*barn[N-1];
				}
				else numDist[i][j] = numDist[i][j-1]+(j+N-i-1)*barn[j-1];
			}
		}
	}
	
	public void output() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("cbarn2.out")));
		out.println(minDistTraveled);
		out.close();
	}

	//13:30 for first draft
	//1:36:00 to solve completely
	//so I think I can do this if I focus!
}
