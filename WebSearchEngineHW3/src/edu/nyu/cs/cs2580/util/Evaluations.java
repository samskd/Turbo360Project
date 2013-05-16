package edu.nyu.cs.cs2580.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Evaluations {

	public static void main(String args[]){
		
		List<Integer> marriage = new ArrayList<Integer>();
		marriage.add(10);
		marriage.add(10);
		marriage.add(3);
		marriage.add(10);
		marriage.add(3);
		marriage.add(5);
		marriage.add(5);
		marriage.add(10);
		marriage.add(3);
		marriage.add(5);
		marriage.add(3);
		marriage.add(3);
		marriage.add(5);
		marriage.add(0);
		marriage.add(0);
		marriage.add(3);
		marriage.add(0);
		marriage.add(0);
		marriage.add(5);
		marriage.add(5);
		
		List<Integer> marriageF = new ArrayList<Integer>();
		marriageF.add(1);
		marriageF.add(1);
		marriageF.add(1);
		marriageF.add(1);
		marriageF.add(0);
		
		
		List<Integer> music = new ArrayList<Integer>();
		music.add(10);
		music.add(10);
		music.add(10);
		music.add(10);
		music.add(5);
		music.add(10);
		music.add(3);
		music.add(5);
		music.add(3);
		music.add(0);
		music.add(10);
		music.add(7);
		music.add(0);
		music.add(10);
		music.add(10);
		music.add(3);
		music.add(0);
		music.add(10);
		music.add(10);
		music.add(10);
		music.add(0);
		
		List<Integer> musicF = new ArrayList<Integer>();
		musicF.add(1);
		musicF.add(1);
		musicF.add(1);
		musicF.add(1);
		musicF.add(0);
		
		
		List<Integer> gun = new ArrayList<Integer>();
		gun.add(5);
		gun.add(10);
		gun.add(10);
		gun.add(7);
		gun.add(5);
		gun.add(3);
		gun.add(5);
		gun.add(5);
		gun.add(7);
		gun.add(10);
		gun.add(10);
		gun.add(10);
		gun.add(7);
		gun.add(5);
		gun.add(0);
		gun.add(3);
		gun.add(0);
		gun.add(7);
		gun.add(0);
		gun.add(0);
		gun.add(0);
		
		List<Integer> gunF = new ArrayList<Integer>();
		gunF.add(1);
		gunF.add(1);
		gunF.add(1);
		gunF.add(0);
		gunF.add(1);
		gunF.add(1);
		
		
		
		List<Integer> irs = new ArrayList<Integer>();
		irs.add(10);
		irs.add(10);
		irs.add(10);
		irs.add(10);
		irs.add(0);
		irs.add(0);
		irs.add(10);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(7);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		irs.add(0);
		
		List<Integer> irsF = new ArrayList<Integer>();
		irsF.add(1);
		irsF.add(1);
		irsF.add(1);
		irsF.add(0);
		irsF.add(1);
		irsF.add(1);
		
		
		List<Integer> mathematics = new ArrayList<Integer>();
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(0);
		mathematics.add(7);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(5);
		mathematics.add(10);
		mathematics.add(0);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		mathematics.add(10);
		
		List<Integer> mathematicsF = new ArrayList<Integer>();
		mathematicsF.add(0);
		mathematicsF.add(0);
		mathematicsF.add(0);
		mathematicsF.add(0);
		mathematicsF.add(0);
		mathematicsF.add(0);
		

		System.out.println("marriage");
		System.out.println("NDCG -> " + getNDCG(marriage));
		System.out.println("NDCF -> " + getNDCG(marriageF));
		System.out.println("Precision -> " + calculatePrecision(marriage));
		System.out.println("AveragePrecision -> " + calculateAveragePrecision(marriage));
		System.out.println("Reciprocal -> " + calculateReciprocalRank(marriage));
		System.out.println();
		System.out.println("music");
		System.out.println("NDCG -> " + getNDCG(music));
		System.out.println("NDCF -> " + getNDCG(musicF));
		System.out.println("Precision -> " + calculatePrecision(music));
		System.out.println("AveragePrecision -> " + calculateAveragePrecision(music));
		System.out.println("Reciprocal -> " + calculateReciprocalRank(music));
		System.out.println();
		System.out.println("gun");
		System.out.println("NDCG -> " + getNDCG(gun));
		System.out.println("NDCF -> " + getNDCG(gunF));
		System.out.println("Precision -> " + calculatePrecision(gun));
		System.out.println("AveragePrecision -> " + calculateAveragePrecision(gun));
		System.out.println("Reciprocal -> " + calculateReciprocalRank(gun));
		System.out.println();
		System.out.println("irs");
		System.out.println("NDCG -> " + getNDCG(irs));
		System.out.println("NDCF -> " + getNDCG(irsF));
		System.out.println("Precision -> " + calculatePrecision(irs));
		System.out.println("AveragePrecision -> " + calculateAveragePrecision(irs));
		System.out.println("Reciprocal -> " + calculateReciprocalRank(irs));
		System.out.println();
		System.out.println("mathematics");
		System.out.println("NDCG -> " + getNDCG(mathematics));
		System.out.println("NDCF -> " + getNDCG(mathematicsF));
		System.out.println("Precision -> " + calculatePrecision(mathematics));
		System.out.println("AveragePrecision -> " + calculateAveragePrecision(mathematics));
		System.out.println("Reciprocal -> " + calculateReciprocalRank(mathematics));

	}


	private static double getNDCG(List<Integer> results){

		double DCG = results.get(0);

		for(int i=1; i<results.size(); i++){
			DCG += results.get(i)/(Math.log(i+1)/Math.log(2));
		}

		List<Integer> resultsCopy = new ArrayList<Integer>();
		for(int value : results){
			resultsCopy.add(value);
		}
		Collections.sort(resultsCopy, Collections.reverseOrder());

		double DCGMax = resultsCopy.get(0);

		for(int i=1; i<resultsCopy.size(); i++){
			DCGMax += resultsCopy.get(i)/(Math.log(i+1)/Math.log(2));
		}
		
		return DCG/DCGMax;
	}
	
	
	private static double calculateReciprocalRank(List<Integer> results){

		for(int i=1; i<=results.size(); i++){
			if(results.get(i-1) > 5)
				return 1/i;
		}

		return 0;
	}
	
	private static double calculatePrecision(List<Integer> results){

		double RR=0.0;
		
		for(int i=0; i<results.size(); i++){
			if(results.get(i) > 5) {
				RR++;
			}
		}

		return RR/results.size();
	}
	
	private static double calculateAveragePrecision(List<Integer> results){

		double RR=0.0;
		double AP=0.0;
		
		for(int i=1; i<=results.size(); i++){
			
			if(results.get(i-1) > 5) {
				RR++;
				AP += RR/(double)i;
			}
		}
		
		return AP/RR;
	}
}