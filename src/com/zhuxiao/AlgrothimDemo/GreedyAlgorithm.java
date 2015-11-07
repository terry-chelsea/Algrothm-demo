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
		ps.println("reader������ "+num);
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
		//----------------���з�����readers ���˸Ľ���ֱ���޷���һ���Ľ�Ϊֹ--------------------------------
		ps.println("=======����ѭ��===========");
		while (curMin > Pmin) {
			round ++;
			ps.println("++++++++++++++++rount: "+round);
//			for(Reader r : readers) {
//				if(circle.isOutside(r.getPosition()))
//					System.out.println("!!!!reader " + r + " is outside ~");
//			}
			//����Pmin��Reader��λ��
			Pmin = curMin;
			//Node minNodes[] = new Node[N];      //�������Pmin��С�Ĵ������ڵ��ţ���Ϊ�ж��������������ʽ����
			
			//�ҳ����������ڵ�ı�ţ���������minNodes[]
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
			for(Integer nodeNum : nodeNumSet)
			{
				ps.println("���node:"+(nodeNum+1)+"  ��Ӧ��reader:"+(moveReaders.get(nodeNum)+1));
				
			}
		    //����ÿһ��������С�Ĵ������ڵ�ͬʱ����λ�õ���
			for(Integer nodeNum : moveReaders.keySet()) {
				double ratio;
				int readerNum = moveReaders.get(nodeNum);
	
				double temp = readers[readerNum].getPosition().distanceTo(nodes[nodeNum].getPosition());
				ratio = gridSize / temp;
				double curX = readers[readerNum].getPosition().getX();
				double curY = readers[readerNum].getPosition().getY();
				ps.println("\tnode"+(nodeNum+1)+"��Ӧ��reader"+(readerNum+1)+"\t����ǰreaderλ�ã�  "+ readers[readerNum]);
				ps.println("\t����ǰd(node-reader)����:  "+nodes[nodeNum].getPosition().distanceTo(readers[moveReaders.get(nodeNum)].getPosition()));
				readers[readerNum].getPosition().setX(curX + ratio*(nodes[nodeNum].getPosition().getX() - curX));
				readers[readerNum].getPosition().setY(curY + ratio*(nodes[nodeNum].getPosition().getY() - curY));
				ps.println("\tnode"+(nodeNum+1)+"��Ӧ��reader"+(readerNum+1)+"\t������readerλ�ã�  "+readers[readerNum]);
				ps.println("\t�������d(node-reader)����:  "+nodes[nodeNum].getPosition().distanceTo(readers[readerNum].getPosition()));  
			}
			
			int nodeNum = getMinPower(nodes, readers);
			curMin=nodes[nodeNum].getRatio();
			ps.println("\t������node�� "+(nodeNum+1));
			ps.println("\t�����node��ratio:  "+ curMin);
			
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
