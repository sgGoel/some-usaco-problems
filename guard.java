/*
ID: ms.swat1
LANG: JAVA
TASK: guard
*/

package swati.usaco.training.dec2014gold;

import java.util.Scanner;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;

public class guard {
	int debug = 0;
	int H; int N;
	Cow[] cows;
	int numSetsCows;
	int maxSafety;
	
	public static void main(String[] args) throws IOException{
		guard data = new guard();
		if (args.length != 0){
			data.debug = Integer.parseInt(args[0]);
		}
		data.readIn();
		data.compute();
		data.output();
	}
	
	public class Cow{
		int height;
		int weight;
		int strength;
		public Cow(int h, int w, int s){
			height = h;
			weight = w;
			strength = s;
		}
		
		public String toString(){
			String s = "";
			s += "h: " + height;
			s += " w: " + weight;
			s += " s: " + strength;
			return s;
		}
	}
	
	public void readIn() throws IOException{
		Scanner sc = new Scanner(new File("guard.in"));
		N = sc.nextInt();
		H = sc.nextInt();
		cows = new Cow[N];
		for (int i = 0; i < N; i++){
			int h = sc.nextInt(); 
			int w = sc.nextInt(); 
			int s = sc.nextInt(); 
			cows[i] = new Cow(h, w, s);
		}
		numSetsCows = (int) Math.pow(2,  N) -1;
		Arrays.sort(cows, new Comparator<Cow>(){ //sorts in decreasing order of strength + weight
			@Override
			public int compare(Cow a, Cow b){
				return Integer.compare(b.weight+b.strength, a.weight+a.strength);
			}
		});
	}
	
	public void compute(){
		maxSafety = -1;
		for (int i = 1; i <= numSetsCows; i++){
			int safety = computeSet(i);
			if (debug ==2) System.out.println(i + ": " + safety);
			if (safety == -1) continue;
			if (maxSafety < safety || maxSafety == -1) maxSafety = safety;
		}
	}
	
	int computeSet(int i){
		ArrayList<Cow> cowsUsed = new ArrayList<Cow>(); //list of the cows that we're using, 
		//using the info from i in binary
		for (int j = 0; j < N; j++){
			int c = (1 << j);
			if ((i & c) == c) cowsUsed.add(cows[j]);
		}
		if (debug == 2) System.out.println(cowsUsed);
		
		int weightConstraint = -1;
		int h = 0;
		for (int j = 0; j < cowsUsed.size(); j++){ //calculate the leftover safety of this particular arrangement of cows
			if (j == 0){
				weightConstraint = cowsUsed.get(j).strength;
				h += cowsUsed.get(j).height;
				continue;
			}
			int w = cowsUsed.get(j).weight;
			if (weightConstraint < w){
				weightConstraint = -1;
				break;
			}
			weightConstraint = Math.min(weightConstraint-w, cowsUsed.get(j).strength);
			h += cowsUsed.get(j).height;
		}
		if (h < H) weightConstraint = -1;
		return weightConstraint;
	}
	
	
	public void output() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("guard.out")));
		if (maxSafety >= 0) out.println(maxSafety);
		else out.println("Mark is too tall");
		out.close();
	}

}
