package com.zhuxiao.AlgrothimDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class GreedyAlgorithm extends Algorithm {
	private static double gridSize = 0.01;
	private static PrintStream ps=Scenario.ps;
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		Reader readers[] = new Reader[num];
		int cnt = 0;
		int N = nodes.length;
		int K = readers.length;
		int round = 0;
		ps.println("reader个数： "+num);
		for(int i = 0 ; i < K; ++ i) {
			readers[i] = new Reader(circle.getRandomPoint());
			
		}
		
		ps.println("====================node========================");
		for(int i=0; i<nodes.length; i++)
		{
			nodes[i].setPower(0.0);;
			ps.println("\tnode "+(i+1) +" : "+nodes[i]);
		}
		ps.println("===================reader=======================");
		for(int i=0;i<readers.length;i++)
		{
			ps.println("\treader "+(i+1)+" : "+readers[i]);
		}
		ps.println("===============reader-over===================");

		int minNodeId = getMinPower(nodes, readers);
		double curMin = nodes[minNodeId].getRatio();
		
		ps.println("===============minNodeId================");
		ps.println("\tminNodeId = "+(minNodeId+1));
		ps.println("\tcurMin = "+curMin);
		ps.println("===============minNodeId-over===========");
		double Pmin = Double.MIN_VALUE;
		//----------------进行反复的readers 拓扑改进，直到无法进一步改进为止--------------------------------
		ps.println("=======进入循环===========");
		while (curMin > Pmin) {
			round ++;
			ps.println("++++++++++++++++rount: "+round);
//			for(Reader r : readers) {
//				if(circle.isOutside(r.getPosition()))
//					System.out.println("!!!!reader " + r + " is outside ~");
//			}
			//更新Pmin和Reader的位置
			Pmin = curMin;
			//Node minNodes[] = new Node[N];      //用来存放Pmin最小的传感器节点编号，因为有多个所以用数组形式保存
			
			//找出所有最糟糕节点的编号，存入数组minNodes[]
			///cnt = 0;
			List<Integer> list=new ArrayList<Integer>();
			for (int nodeNum = 0; nodeNum < N ; nodeNum++) {
				Node node = nodes[nodeNum];
				if (Math.abs(node.getRatio() - curMin) <= Point.ESP) {
					list.add(nodeNum);
					//minNodes[cnt] = node;
					//cnt++;
				}
			}
			//为传感器节点i存放离得最近的Reader编号到NearestReaderID[i]里面；这里没有考虑有多个距离最近的Reader的情况
			//对已一个node保持按照顺序保持和它相邻的readers
			//--找离节点i最近的reader，存放到NearestReaderID[i]里面
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
			for(Integer nodeNum : nodeNumSet)
			{
				ps.println("糟糕node:"+(nodeNum+1)+"  对应的reader:"+(moveReaders.get(nodeNum)+1));
				
			}
		    //对于每一个功率最小的传感器节点同时进行位置调整
			for(Integer nodeNum : moveReaders.keySet()) {
				double ratio;
				int readerNum = moveReaders.get(nodeNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				ps.println("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整前reader位置：  "+ readers[readerNum]);
				ps.println("\t调整前d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[moveReaders.get(nodeNum)].getPosition()));
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
				ps.println("\tnode"+(nodeNum+1)+"对应的reader"+(readerNum+1)+"\t调整后reader位置：  "+readers[readerNum]);
				ps.println("\t调整后的d(node-reader)距离:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));  
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
			ps.println("\t最糟糕的node： "+(nodeNum+1));
			ps.println("\t最糟糕node的ratio:  "+ curMin);
			
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
				ps.println("\t\t round " + round );
			}
		}  //-- end while
//			System.out.println("move reader num = " + moveNum + ", min ratio = " + curMin);

		return null;
	}
	
	public String toString() {
		return "Greedy Algorithm";
	}
}
