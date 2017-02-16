package com.zhuxiao.Simulation;

import java.util.Arrays;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomAngleAlgorithm extends AngleAlgorithm {
	private static Random rand = new Random();
	Logger logger = LoggerFactory.getLogger(RandomAngleAlgorithm.class);

	@Override
	protected Reader[] runInternal(Node[] nodes, int readerNum, Circle circle) {
		Reader[] readers = new Reader[readerNum];
		
		for(int i = 0 ; i < nodes.length ; ++ i)
			nodes[i].setPower(0);
		
		for(int i = 0; i < readerNum ; ++ i) {
			Arrays.sort(nodes);
			Node current = nodes[0];
			
			Circle nodeCircle = current.getEffectiveCircle();
			Point pos = nodeCircle.getRandomPoint();
			
			int step = AngleAlgorithm.COVER_ANGLE / 2;
			for(int angle = 0; angle < AngleAlgorithm.MAX_ANGLE; 
				angle += step) {
				int readerAngle = rand.nextInt(step) + angle;
				Reader reader = new ReaderWithAngle(pos, AngleAlgorithm.COVER_ANGLE, 
						readerAngle);
				if(reader.isNodeInside(current.getPosition())) {
					readers[i] = reader;
					break;
				}
			}
			
//			logger.info("node number : {}, reader number : {}.", nodes.length, readers.length);
			if(super.isSatisfied(nodes, readers, circle) == true)
				return readers;
		}
		
		if(super.isSatisfied(nodes, readers, circle) == true)
			return readers;
		else 
			return null;
	}
}
