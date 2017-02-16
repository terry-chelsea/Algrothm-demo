package com.zhuxiao.Simulation;

public class EvenAlgorithm extends Algorithm {
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		Reader readers[] = new Reader[num];
		double angle = 360 / (double) num;
		for(int i = 0 ; i < num ; ++ i) {
			readers[i] = new Reader(circle.newPoint(circle.getRedius() / 2, angle * i));
		}
		
		if(super.isSatisfied(nodes, readers, circle))
			return readers;
		else 
			return null;
	}
}
