package com.zhuxiao.AlgrothimDemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GreedyAlgorithm extends Algorithm {
	private static double gridSize = 0.02;
	private Node lastNode = null;
	private double nodeBestRatio[] = null;
	
	public GreedyAlgorithm() {
		super.isLog = false;
	}
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		nodeBestRatio = new double[nodes.length];
		for(int i = 0 ; i < nodes.length ; ++ i)
			nodeBestRatio[i] = 0.0;
		
		Reader readers[] = new Reader[num];
		int N = nodes.length;
		int K = readers.length;
		int round = 0;
		double curMinSum = 0.0;
		double lastMinSum = 0.0;
		log("reader个数： "+num);
		for(int i = 0 ; i < K; ++ i) {
			readers[i] = new Reader(circle.getRandomPoint());
		}
/*		
		log("====================node========================");
		for(int i=0; i<nodes.length; i++)
		{
			nodes[i].setPower(0.0);
			log("\tnode "+(i+1) +" : "+nodes[i]);
		}
		log("===================reader=======================");
		for(int i=0;i<readers.length;i++)
		{
			log("\treader "+(i+1)+" : "+readers[i]);
		}
		log("===============reader-over===================");
*/
		int minNodeId = getMinPower(nodes, readers);
		double curMin = nodes[minNodeId].getRatio();
		Node curNode = nodes[minNodeId];
/*		
		log("===============minNodeId================");
		log("\tminNodeId = "+(minNodeId+1));
		log("\tcurMin = "+curMin);
		log("===============minNodeId-over===========");
*/
		double Pmin = Double.MIN_VALUE;
		//----------------进行反复的readers 拓扑改进，直到无法进一步改进为止--------------------------------
		log("=======进入循环===========");
		while (curMin > Pmin || lastNode != curNode && curMinSum > lastMinSum && round < 1100) {
//		while(true) {
			round ++;
			log("++++++++++++++++round: "+round);
//			for(Reader r : readers) {
//				if(circle.isOutside(r.getPosition()))
//					System.out.println("!!!!reader " + r + " is outside ~");
//			}
			Pmin = curMin;
			lastNode = curNode;
			lastMinSum = curMinSum = 0.0;
			
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
/*					
					if(nodeBestRatio[nodeNum] < curRatio)
						nodeBestRatio[nodeNum] = curRatio;
*/						
				}
			}
			
//			log("node ratio less than 1 are : " + list);
			
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
			if(round > 1000) {
				Set<Integer> readerNumSet=moveReaders.keySet();
				for(Integer readerNum : readerNumSet) {
					int nodeNum = moveReaders.get(readerNum);
					log("Bad node : "+ nodes[nodeNum] + "  and reader : "+ readers[readerNum], round >= 1000);
				}
				log("Current bad node sum is " + lastMinSum, round >= 1000);
			}
		    //对于每一个功率最小的传感器节点同时进行位置调整
			for(Integer readerNum : moveReaders.keySet()) {
				double ratio;
				int nodeNum = moveReaders.get(readerNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				log("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整前reader位置：  "+ readers[readerNum], round >= 1000);
				log("\t调整前d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()), round >= 1000);
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
				log("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整后reader位置：  "+readers[readerNum], round >= 1000);
				log("\t调整后的d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()), round >= 1000);  
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
//			log("\t最糟糕的node： "+(nodeNum+1));
//			log("\t最糟糕node的ratio:  "+ curMin);
			curNode = nodes[nodeNum];
			for(Integer nodeId : moveReaders.values()) {
				curMinSum += nodes[nodeId].getRatio();
			}
			log("After move reader , current sum node ratio is " + curMinSum, round >= 1000);
			
			if(curMin > 1.0) {
				System.out.println("Successfully find readers after " + round + " rounds...");
				return readers;
			}
/*	
			if(nodeBestRatio[nodeNum] > curMin) {
				log("best ratio " + nodeBestRatio[nodeNum] + ", cur ratio " + curMin, true);
				break;
			}
*/			
		}  //-- end while
//			System.out.println("move reader num = " + moveNum + ", min ratio = " + curMin);
		
		System.out.println("Reader number " + num + " run " + round + " rounds ...");
		return null;
	}

	protected void init() {
		lastNode = null;
	}
	
	public String toString() {
		return "Greedy Algorithm";
	}
}
