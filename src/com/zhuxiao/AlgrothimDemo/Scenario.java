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
	// 最初放置node节点的场景大小
	private static final double INIT_MAX_SIZE = 16.0;   
	 //node节点的初始化个数
	private static final int NODE_NUM = 20;
	//node节点的最小接受功率
	private static final double MIN_POWERS[] = {0.0001, 0.0002, 0.0003, 0.0004};
	//异构的节点种类个数
	private static final int MIN_POWERS_SIZE = MIN_POWERS.length;
	//随机数生成器
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
	
	//保存相应算法的每次执行结果，即reader个数
	private Map<Algorithm, List<Integer>> resultMap = null;
	
	public Scenario() {
		this.algorithms = new LinkedList<Algorithm>();
		this.resultMap = new HashMap<Algorithm, List<Integer>>();
	}
	
	//添加一种算法
	public void addAlgorithm(Algorithm a) {
		//添加一个算法到algorithms内，它是一个list对象，对象类型是算法类型
		this.algorithms.add(a);
		//把输入的算法作为map的key,map的value是该输入算法返回reader个数。每一个算法对应一个list结果
		this.resultMap.put(a, new LinkedList<Integer>());
		
	}
	
	//部署一次场景
	public void initialize() {
		//在初始（正方形）区域内，随机分布node节点
		deployNodes();
		//根据node的位置画出一个圆，是的所有的节点都在圆内。
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
			//求出包含四个节点的最小圆
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
	//从node数组里面随机取三个不同的node节点  组成的三角形
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
		//生成场景（随机撒node和画圆）
		this.initialize();
		if(this.circle == null)
			return ;
		//已经注册的算法，根据当前的场景计算reader的个数和reader的位置信息
		for(Algorithm algorithm : this.algorithms) {
			//根据当前算法计算出该场景下满足条件的最小个数的reader分布
			Reader readers[] = algorithm.run(nodes, this.circle);
			System.out.println("\tUsing " + algorithm + ", need " + readers.length);
			//获取该算法的结果集（Map的value是一个存放reader个数的list）
			List<Integer> results = resultMap.get(algorithm);
			//获取到list之后，把本次结果加入到里面（即满足结果的最小的reader个数）
			if(results != null)
				results.add(readers.length);
		}
	}
	
	public Node[] getNodes() {
		return this.nodes;
	}
	//获取reader个数的平均值
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
			System.out.println("Scenario " + (i + 1) + "　: ");
			scenario.calculateMinReader();
		}
		
		scenario.printResult();
	}
	
	public static void main(String[] args) {
//		testCircle();
		//随机函数种子设置
		testAlgorithm();
		
  	}
}
