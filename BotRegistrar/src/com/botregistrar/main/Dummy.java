package com.botregistrar.main;

import java.util.Random;

public class Dummy {
	
	public static void main(String[] args) {
		
		Random r = new Random();
		r.nextInt(10);
		
		System.out.println(r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10));
	}

}
