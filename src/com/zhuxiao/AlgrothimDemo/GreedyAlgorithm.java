package com.zhuxiao.AlgrothimDemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreedyAlgorithm extends Algorithm {
	private static double gridSize = 0.02;
	private static int CHECK_DEADLOCK_SIZE = 40;
	public GreedyAlgorithm() {
		super.isLog = false;
	}
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		List<Integer> minNodeIndex = new ArrayList<Integer>(CHECK_DEADLOCK_SIZE);
		for(int i = 0 ; i < CHECK_DEADLOCK_SIZE ; ++ i) {
			minNodeIndex.add(-1);
		}
		
		Reader readers[] = new Reader[num];
		int N = nodes.length;
		int K = readers.length;
		int round = 0;
		double curMinSum = 0.0;
		double lastMinSum = 0.0;
		Node lastNode = null;

		for(int i = 0 ; i < K; ++ i) {
			readers[i] = new Reader(circle.getRandomPoint());
		}
		int minNodeId = getMinPower(nodes, readers);
		double curMin = nodes[minNodeId].getRatio();
		Node curNode = nodes[minNodeId];
		double Pmin = Double.MIN_VALUE;
		//----------------进行反复的readers 拓扑改进，直到无法进一步改进为止--------------------------------
		while (curMin > Pmin || lastNode != curNode) {
			round ++;
/*			
			for(Reader r : readers) {
				if(circle.isOutside(r.getPosition()))
					System.out.println("!!!!reader " + r + " is outside ~");
			}
*/			
			Pmin = curMin;
			lastMinSum = curMinSum = 0.0;
			lastNode = curNode;
			
			//找出所有最糟糕节点的编号，存入数组minNodes[]
			List<Integer> list=new ArrayList<Integer>();
			for (int nodeNum = 0; nodeNum < N ; nodeNum++) {
				Node node = nodes[nodeNum];
				double curRatio = node.getRatio();
				if (curRatio < 1.0) {
					int j = 0;
					for(j = 0 ; j < list.size() ; ++ j) {
						if(nodes[list.get(j)].getRatio() > curRatio) {
							break;
						}
					}
					list.add(j, nodeNum);
				}
			}
			
			log("Node ratio less than 1 are : " + list);
			Map<Integer, Integer> moveReaders = new HashMap<Integer, Integer>();
			for(int i = 0 ; i < list.size() ; ++ i) {
				int nodeId = list.get(i);
				double minDistance = Double.MAX_VALUE;
				int readerNum=0;
				for(int j = 0 ; j < K ; ++ j) {
					if(moveReaders.containsKey(j))
						continue;
					double curDistance = nodes[nodeId].getPosition().distanceTo(readers[j].getPosition());
					if(curDistance < minDistance) {
						readerNum=j;
						minDistance = curDistance;
					}
				}
				
				moveReaders.put(readerNum, list.get(i));
				lastMinSum += nodes[nodeId].getRatio();
			}
			log("Before current min node ratio sum is " + lastMinSum + ", current min node " + list.get(0) + ", ratio " + nodes[list.get(0)].getRatio());
			
		    //对于每一个功率最小的传感器节点同时进行位置调整
			for(Integer readerNum : moveReaders.keySet()) {
				double ratio;
				int nodeNum = moveReaders.get(readerNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				log("\tBefore move Node " + nodeNum + " : " + nodes[nodeNum] +" and reader " + readerNum + " : " + readers[readerNum]);
				log("\tDistance from node to reader :  "+ nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
				log("\tAfter move Node " + nodeNum + " : " + nodes[nodeNum] +" and reader " + readerNum + " : " + readers[readerNum]);
				log("\tDistance from node to reader :  "+ nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
			curNode = nodes[nodeNum];
			for(Integer nodeId : moveReaders.values()) {
				curMinSum += nodes[nodeId].getRatio();
			}
			log("After current min node ratio sum is " + curMinSum + ", current min node " + nodeNum + ", ratio " + nodes[nodeNum].getRatio());
			
			if(curMin > 1.0) {
				System.out.println("Successfully find readers after " + round + " rounds...");
				return readers;
			}
			if(round >= 1000)
				break;
/*			
			minNodeIndex.set((round - 1) % CHECK_DEADLOCK_SIZE, nodeNum);
			
			if(lastNode != curNode) {
				log("Current reader number " + num + ", round " + round + " dead lock check array : " + minNodeIndex, true);

				boolean deadlock = true;
				for(int i = 0 ; i < CHECK_DEADLOCK_SIZE - 3 && deadlock ; i += 2) {
					if(minNodeIndex.get(i) != minNodeIndex.get(i + 2)) {
						deadlock = false;
					}
				}
				for(int i = 1; i < CHECK_DEADLOCK_SIZE - 2 && deadlock ; i += 2) {
					if(minNodeIndex.get(i) != minNodeIndex.get(i + 2)) {
						deadlock = false;
					}
				}
				if(deadlock)
					break;
			}
*/			
		}
		System.out.println("Reader number " + num + " run " + round + " rounds ...");
		return null;
	}

	public String toString() {
		return "Greedy Algorithm";
	}
}
