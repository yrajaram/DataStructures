package com.herakles.ds;

public class Main {
	public static void main(String[] ac) {
		ArrayHt<String, String> ht = new ArrayHt<String, String>(5, .8);

		AdialThread at;
		for(int i=0; i<10;i++){
			at = new AdialThread(i, ht);
			at.start();
		}
	}
}
class AdialThread extends Thread {
	ArrayHt<String, String> h;
	int id;
	AdialThread(int i, ArrayHt<String, String> ht){
		this.id = i;
		this.h = ht;
	}
	public void run() {
		for (int j=0; j<10;j++){
		int i = (int) (Math.random()*10);
		System.out.println("-------------ID:"+id+" Rand value:"+i);
		switch (i) {
		case 1:
			h.remove(""+i);
			System.out.println("ID:"+id+" size after removing "+i+":"+h.getSize());
			;;
		case 2:
			h.remove(""+i);
			System.out.println("ID:"+id+" size after removing "+i+":"+h.getSize());
			;;
		case 3:
			h.remove(""+i);
			System.out.println("ID:"+id+" size after removing "+i+":"+h.getSize());
			;;
		case 4:
			System.out.println("ID:"+id+" Getting "+i+" :"+h.get(""+i));
			;;
		case 5:
			h.put(""+i,"val:"+i);
			System.out.println("ID:"+id+" size after adding "+i+":"+h.getSize());
			;;
		case 6:
			h.put(""+i,"val:"+i);
			System.out.println("ID:"+id+" size after adding "+i+":"+h.getSize());
			;;
		case 7:
			System.out.println("ID:"+id+" getting "+i+" :"+h.get(""+i));
			;;
		default:
			h.put(""+i,"val:"+i);
			System.out.println("ID:"+id+" size after adding "+i+":"+h.getSize());
			;;
		}
		}
	}
}