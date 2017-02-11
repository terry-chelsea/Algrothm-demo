package com.zhuxiao.Simulation;

import java.util.Random;

public class RandomAngleAlgorithm extends AngleAlgorithm {
	private static Random rand = new Random();

	@Override
	protected Reader[] runInternal(Node[] nodes, int readerNum, Circle circle) {
		Reader[] readers = new Reader[readerNum];
		
		for(int i = 0; i < readerNum ; ++ i) {
			Point pos = circle.getRandomPoint();
			int angle = rand.nextInt(ReaderWithAngle.MAX_ANGLE);
			
			readers[i] = new ReaderWithAngle(pos, COVER_ANGLE, angle);
		}
		
		if(super.isSatisfied(nodes, readers, circle) == true)
			return readers;
		else 
			return null;
	}

}
