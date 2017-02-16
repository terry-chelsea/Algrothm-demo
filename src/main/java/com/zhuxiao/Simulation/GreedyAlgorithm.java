package com.zhuxiao.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedyAlgorithm extends Algorithm {
	private static double gridSize = 0.01;
	private static Logger logger = LoggerFactory.getLogger(Scenario.class);
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		Reader readers[] = new Reader[num];
		int N = nodes.length;
		int K = readers.length;
		int round = 0;
		logger.debug("Reader number = {} ", num);
		for(int i = 0 ; i < K; ++ i) {
			readers[i] = new Reader(circle.getRandomPoint());
		}
		
		logger.debug("====================Node========================");
		for(int i = 0; i < nodes.length; i ++) {
			nodes[i].setPower(0.0);;
			logger.debug("\tNode {}, Position {}", i + 1, nodes[i]);
		}
		logger.debug("===================Reader=======================");
		for(int i = 0; i < readers.length; i ++) {
			logger.debug("\tReader {}, Position {}", i + 1, readers[i]);
		}
		logger.debug("===============Reader-over===================");

		int minNodeId = getMinPower(nodes, readers);
		double curMin = nodes[minNodeId].getRatio();
		
		logger.debug("===============minNodeId================");
		logger.debug("\tMin Node Index = {}.", minNodeId + 1);
		logger.debug("\tCurrent Min = {}.", curMin);
		logger.debug("===============minNodeId-over===========");
		double Pmin = Double.MIN_VALUE;
		//----------------���з�����readers ���˸Ľ���ֱ���޷���һ���Ľ�Ϊֹ-------------------------------
		while (curMin > Pmin) {
			round ++;
			logger.debug("Round = {} : ", round);
			Pmin = curMin;
			List<Integer> list=new ArrayList<Integer>();
			for (int nodeNum = 0; nodeNum < N ; nodeNum++) {
				Node node = nodes[nodeNum];
				if (Math.abs(node.getRatio() - curMin) <= Point.ESP) {
					list.add(nodeNum);
					//minNodes[cnt] = node;
					//cnt++;
				}
			}
			//Ϊ�������ڵ�i�����������Reader��ŵ�NearestReaderID[i]���棻����û�п����ж�����������Reader�����
			//����һ��node���ְ���˳�򱣳ֺ������ڵ�readers
			//--����ڵ�i�����reader����ŵ�NearestReaderID[i]����
			Map<Integer, Integer> moveReaders = new HashMap<Integer, Integer>();
			for(int i = 0 ; i < list.size() ; ++ i) {
				double minDistance = Double.MAX_VALUE;
				//Reader r = null;
				int readerNum=0;
				for(int j = 0 ; j < K ; ++ j) {
					double curDistance = nodes[list.get(i)].getPosition().distanceTo(readers[j].getPosition());
					if(curDistance < minDistance) {
						readerNum=j;
						minDistance = curDistance;
						//r = readers[j];
					}
				}
				
				moveReaders.put(list.get(i), readerNum);
			}
			Set<Integer> nodeNumSet=moveReaders.keySet();
			for(Integer nodeNum : nodeNumSet) {
				logger.debug("Bad Node index {}, Moving reader {}.", nodeNum + 1, moveReaders.get(nodeNum)+1);
				
			}
		    //����ÿһ��������С�Ĵ������ڵ�ͬʱ����λ�õ���
			for(Integer nodeNum : moveReaders.keySet()) {
				double ratio;
				int readerNum = moveReaders.get(nodeNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
			
			if(curMin > 1.0) {
				return readers;
			}
			if(round % 100 == 0) {
				logger.info("\t\t Round = {} : ", round );
			}
		}  //-- end while

		return null;
	}
}
