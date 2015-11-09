package com.zhuxiao.AlgrothimDemo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GreedyAlgorithm extends Algorithm {
	private static double gridSize = 0.01;
	
	public GreedyAlgorithm() {
		super.isLog = false;
	}
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		Reader readers[] = new Reader[num];
		int N = nodes.length;
		int K = readers.length;
		int round = 0;
		log("reader个数： "+num);
		for(int i = 0 ; i < K; ++ i) {
			readers[i] = new Reader(circle.getRandomPoint());
			
		}
		
		log("====================node========================");
		for(int i=0; i<nodes.length; i++)
		{
			nodes[i].setPower(0.0);;
			log("\tnode "+(i+1) +" : "+nodes[i]);
		}
		log("===================reader=======================");
		for(int i=0;i<readers.length;i++)
		{
			log("\treader "+(i+1)+" : "+readers[i]);
		}
		log("===============reader-over===================");

		int minNodeId = getMinPower(nodes, readers);
		double curMin = nodes[minNodeId].getRatio();
		
		log("===============minNodeId================");
		log("\tminNodeId = "+(minNodeId+1));
		log("\tcurMin = "+curMin);
		log("===============minNodeId-over===========");
		double Pmin = Double.MIN_VALUE;
		//----------------进行反复的readers 拓扑改进，直到无法进一步改进为止--------------------------------
		log("=======进入循环===========");
		while (curMin > Pmin) {
			round ++;
			log("++++++++++++++++round: "+round);
//			for(Reader r : readers) {
//				if(circle.isOutside(r.getPosition()))
//					System.out.println("!!!!reader " + r + " is outside ~");
//			}
			Pmin = curMin;
			
			//找出所有最糟糕节点的编号，存入数组minNodes[]
			List<Integer> list=new ArrayList<Integer>();
			for (int nodeNum = 0; nodeNum < N ; nodeNum++) {
				Node node = nodes[nodeNum];
				if (node.getRatio() < 1.0) {
					int j = 0;
					for(j = 0 ; j < list.size() ; ++ j) {
						if(nodes[list.get(j)].getRatio() > node.getRatio()) {
							break;
						}
					}
					list.add(j, nodeNum);
				}
			}
			
			log("node ratio less than 1 are : " + list);
			
			Map<Integer, Integer> moveReaders = new HashMap<Integer, Integer>();
			for(int i = 0 ; i < list.size() ; ++ i) {
				double minDistance = Double.MAX_VALUE;
				int readerNum=0;
				for(int j = 0 ; j < K ; ++ j) {
					double curDistance = nodes[list.get(i)].getPosition().distanceTo(readers[j].getPosition());
					if(moveReaders.get(j) == null && curDistance < minDistance) {
						readerNum=j;
						minDistance = curDistance;
					}
				}
				
				Integer nodeId = moveReaders.get(readerNum);
				if(nodeId == null || nodes[nodeId].getRatio() > nodes[list.get(i)].getRatio()) {
					moveReaders.put(readerNum, list.get(i));
				}
			}
			Set<Integer> readerNumSet=moveReaders.keySet();
			for(Integer readerNum : readerNumSet)
			{
				int nodeNum = moveReaders.get(readerNum);
				log("糟糕node : "+(nodeNum+1)+"  对应的reader : "+(readerNum+1));
			}
		    //对于每一个功率最小的传感器节点同时进行位置调整
			for(Integer readerNum : moveReaders.keySet()) {
				double ratio;
				int nodeNum = moveReaders.get(readerNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				log("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整前reader位置：  "+ readers[readerNum]);
				log("\t调整前d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
				log("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整后reader位置：  "+readers[readerNum]);
				log("\t调整后的d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));  
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
			log("\t最糟糕的node： "+(nodeNum+1));
			log("\t最糟糕node的ratio:  "+ curMin);
			
			if(curMin > 1.0) {
/*				System.out.println("Circle : " + circle);
				for(int i = 0 ; i < nodes.length ; ++ i) {
					System.out.println("Node " + (i + 1) + " : " + nodes[i]);
				}
				for(int i = 0 ; i < readers.length ; ++ i) {
					System.out.println("Reader " + (i + 1) + " : " + readers[i]);
				}
*/				return readers;
			}
			if(round % 100 == 0) {
				log("\t\t round " + round );
			}
		}  //-- end while
//			System.out.println("move reader num = " + moveNum + ", min ratio = " + curMin);

		return null;
	}

	
	public String toString() {
		return "Greedy Algorithm";
	}
}
