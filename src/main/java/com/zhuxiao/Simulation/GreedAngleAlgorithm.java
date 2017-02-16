package com.zhuxiao.Simulation;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuxiao.Simulation.Circle.IterablePoint;

public class GreedAngleAlgorithm extends AngleAlgorithm {
	Logger logger = LoggerFactory.getLogger(GreedAngleAlgorithm.class);

	@Override
	protected Reader[] runInternal(Node[] nodes, int readerNum, Circle circle) {
		Reader[] readers = new Reader[readerNum];
		
		for(int i = 0 ; i < nodes.length ; ++ i)
			nodes[i].setPower(0);
		
		for(int i = 0; i < readerNum ; ++ i) {
			Arrays.sort(nodes);
			Node current = nodes[0];
			Circle nodeCircle = current.getEffectiveCircle();
			IterablePoint ip = nodeCircle.iterator();
			
			int maxCoverCount = -1;
			Point bestPoint = null;
			double bestAngle = 0;
			ReaderWithAngle reader = null;
			while(ip.hasNext()) {
				Point plus = ip.next();
				Point p = new Point(current.getPosition().getX() + plus.getX(), 
						current.getPosition().getY() + plus.getY());
				reader = new ReaderWithAngle(p, current.getPosition(), COVER_ANGLE);
				while(true == reader.isNodeInside(current.getPosition())) {
					reader.setAngle(reader.getAngle() + 1);
					int cnt = getCoverNodes(reader, nodes);
					if(cnt > maxCoverCount) {
						maxCoverCount = cnt;
						bestPoint = p;
						bestAngle = reader.getAngle();
					}
				}
			}
			readers[i] = new ReaderWithAngle(bestPoint, COVER_ANGLE, bestAngle);
/*
			if(readerNum > 10) {
				System.out.println("Reader index = " + i + " cover : ");
				for(Node n : nodes) {
					if(readers[i].isNodeInside(n.getPosition())) {
						System.out.println(n);
					}
				}
				
				System.out.println("====================================");
			}
*/			
//			logger.info("node number : {}, reader number : {}.", nodes.length, readers.length);
			if(super.isSatisfied(nodes, readers, circle) == true)
				return readers;
		}
		
		if(super.isSatisfied(nodes, readers, circle) == true)
			return readers;
		else 
			return null;
	}
	
	private int getCoverNodes(Reader r, Node[] nodes) {
		int count = 0;
		for(Node node : nodes) {
			if(node.isEnoughPower() == false && r.isNodeInside(node.getPosition()))
				count ++;
		}
		
		return count;
	}

}
