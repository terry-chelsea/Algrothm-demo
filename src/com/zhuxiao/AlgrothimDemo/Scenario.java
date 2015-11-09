package com.zhuxiao.AlgrothimDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Scenario {
	// �������node�ڵ�ĳ�����С
	private static final double INIT_MAX_SIZE = 16.0;   
	 //node�ڵ�ĳ�ʼ������
	private static final int NODE_NUM = 20;
	//node�ڵ����С���ܹ���
	private static final double MIN_POWERS[] = {0.0001, 0.0002, 0.0003, 0.0004};
	//�칹�Ľڵ��������
	private static final int MIN_POWERS_SIZE = MIN_POWERS.length;
	//�����������
	public static Random rand = new Random();
	public static PrintStream ps=null;
	static {
		try {
			ps = new PrintStream(new File("E:\\Algriothm-xiao\\Algrothm-demolog.txt"));
		} catch (FileNotFoundException e) {
			ps = System.out;
		}
	}
	
	
	private Node[] nodes = new Node[NODE_NUM];
	private Circle circle;
	private List<Algorithm> algorithms;
	
	//������Ӧ�㷨��ÿ��ִ�н������reader����
	private Map<Algorithm, List<Integer>> resultMap = null;
	
	public Scenario() {
		this.algorithms = new LinkedList<Algorithm>();
		this.resultMap = new HashMap<Algorithm, List<Integer>>();
	}
	
	//���һ���㷨
	public void addAlgorithm(Algorithm a) {
		//���һ���㷨��algorithms�ڣ�����һ��list���󣬶����������㷨����
		this.algorithms.add(a);
		//��������㷨��Ϊmap��key,map��value�Ǹ������㷨����reader������ÿһ���㷨��Ӧһ��list���
		this.resultMap.put(a, new LinkedList<Integer>());
		
	}
	
	//����һ�γ���
	public void initialize() {
		//�ڳ�ʼ�������Σ������ڣ�����ֲ�node�ڵ�
		deployNodes();
		//����node��λ�û���һ��Բ���ǵ����еĽڵ㶼��Բ�ڡ�
		this.circle = createCircle();
//		System.out.println("Get circle : " + this.circle);
	}
	
	//
	private void deployNodes() {
		for(int i = 0 ; i < NODE_NUM ; ++ i) {
			double randX = INIT_MAX_SIZE * rand.nextDouble(); 
			double randY = INIT_MAX_SIZE * rand.nextDouble();
  			double minPower = MIN_POWERS[i % MIN_POWERS_SIZE];
			Point pos = new Point(randX, randY);
			nodes[i] = new Node(pos, minPower);
		}
	}
	
	private Circle createCircle() {
		int nodeNum = nodes.length;
		Triangle tr = getRandomTriangle(nodes);
		
		while(tr != null) {
			Circle minCircle = tr.getMinCircle();
			double curMax = minCircle.getRedius();
			
			int index = -1;
			for(int i = 0; i < nodeNum ; ++ i) {
				double distance = nodes[i].getPosition().distanceTo(minCircle.getCenter());
				if(distance > curMax + Point.ESP) {
					index = i;
					curMax = distance;
				}
			}
			if(index == -1) {
//				System.out.println(tr);
				int cnt = 0;
				for(Node node : this.nodes) {
					//System.out.println("Node " + (++ cnt) + " distance : " + (minCircle.getRedius() - minCircle.getCenter().distanceTo(node.getPosition())));
				}
				return minCircle;
			}
			
			Map<Triangle, Point> infos = new HashMap<Triangle, Point>();
			Triangle first = new Triangle(tr.getFirst(), tr.getSecond(), nodes[index].getPosition());
			infos.put(first, tr.getThird());
			Triangle second = new Triangle(tr.getFirst(), tr.getThird(), nodes[index].getPosition());
			infos.put(second, tr.getSecond());
			Triangle third = new Triangle(tr.getSecond(), tr.getThird(), nodes[index].getPosition());
			infos.put(third, tr.getFirst());
			
			tr = null;
			//��������ĸ��ڵ����СԲ
			double redius = Double.MAX_VALUE;
			for(Triangle key : infos.keySet()) {
				Point another = infos.get(key);
				Circle outerCircle = key.getMinCircle();
				if(!outerCircle.isOutside(another) && redius > outerCircle.getRedius()) {
					tr = key;
					redius = outerCircle.getRedius();
				}
			}
		}
		
		return null;
	}
	//��node�����������ȡ������ͬ��node�ڵ�  ��ɵ�������
	private Triangle getRandomTriangle(Node[] nodes) {
		int nodeNum = nodes.length;
		Point[] triangleNodes = new Point[3];
		for(int i = 0 ; i < triangleNodes.length; ++ i) {
			int index = rand.nextInt(nodeNum - i) + i;
			triangleNodes[i] = nodes[index].getPosition();
			Node temp = nodes[index];
			nodes[index] = nodes[i];
			nodes[i] = temp;
		}
		
		return new Triangle(triangleNodes[0], triangleNodes[1], triangleNodes[2]);
	}
	
	public void calculateMinReader() {
		//���ɳ����������node�ͻ�Բ��
		this.initialize();
		if(this.circle == null)
			return ;
		//�Ѿ�ע����㷨�����ݵ�ǰ�ĳ�������reader�ĸ�����reader��λ����Ϣ
		for(Algorithm algorithm : this.algorithms) {
			//���ݵ�ǰ�㷨������ó�����������������С������reader�ֲ�
			Reader readers[] = algorithm.run(nodes, this.circle);
			System.out.println("\tUsing " + algorithm + ", need " + readers.length);
			//��ȡ���㷨�Ľ������Map��value��һ�����reader������list��
			List<Integer> results = resultMap.get(algorithm);
			//��ȡ��list֮�󣬰ѱ��ν�����뵽���棨������������С��reader������
			if(results != null)
				results.add(readers.length);
		}
	}
	
	public Node[] getNodes() {
		return this.nodes;
	}
	//��ȡreader������ƽ��ֵ
	public void printResult() {
		for(Algorithm alg : this.algorithms) {
			double sum = 0;
			List<Integer> results = this.resultMap.get(alg);
			int size = results.size();
			for(Integer value : results) {
				sum += value;
			}
			System.out.println(alg + ", test times " + size + ",average reader number : " + (sum / size));
		}
	}
	
	public void checkCircle() {
		for(int i = 0; i < nodes.length ; ++ i) {
			if(this.circle.isOutside(nodes[i].getPosition())) {
				System.out.println("Node is out side, position " + nodes[i].getPosition()
						+ ", circle " + this.circle);
			}
		}
	}
	
	public static void testCircle() {
		Scenario scenario = new Scenario();
		for(int i = 0 ; i < 1 ; ++ i) {
			Circle c2 = scenario.createCircle();
			System.out.println("Second circle : " + c2);
			for(Node node : scenario.nodes) {
				if(c2.isOutside(node.getPosition())) {
					System.out.println("!!!Outside point " + node.getPosition() + ", circle " + c2);
				}
			}
			System.out.println("========================");
		}
	}
	
	private static void testAlgorithm() {
		int count = 20;
		Scenario scenario = new Scenario();
		scenario.addAlgorithm(new GreedyAlgorithm());
//		scenario.addAlgorithm(new ParticleSwarmOptimizationAlgorithm());
		scenario.addAlgorithm(new AddOneByOneGreedyAlgorithm());
		for(int i = 0; i < count ; ++ i) {
			System.out.println("Scenario " + (i + 1) + "��: ");
			scenario.calculateMinReader();
		}
		
		scenario.printResult();
	}
	
	public static void main(String[] args) {
//		testCircle();
		//���������������
		testAlgorithm();
		
  	}
}
