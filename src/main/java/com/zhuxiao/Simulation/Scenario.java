package com.zhuxiao.Simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scenario {
	// 最初放置node节点的场景大小
	private static final double INIT_MAX_SIZE = 16.0;   
	 //node节点的初始化个数
	private static final int NODE_NUM = 20;
	//node节点的最小接受功率
	private static final double MIN_POWERS[] = {0.0001, 0.0002};
	//异构的节点种类个数
	private static final int MIN_POWERS_SIZE = MIN_POWERS.length;
	//随机数生成器
	public static Random rand = new Random();
	private static Logger logger = LoggerFactory.getLogger(Scenario.class);
	
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
		for(Algorithm algorithm : this.algorithms) {
			//生成场景（随机撒node和画圆）
			this.initialize();
			if(this.circle == null)
				continue ;
			
			//根据当前算法计算出该场景下满足条件的最小个数的reader分布
			Reader readers[] = algorithm.run(nodes, this.circle);
			logger.info("\tUsing {}, need {} readers.", algorithm, readers.length);
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
			logger.info("Algorithm {}, run {} times, average reader number {}.", alg, size, (sum / size));
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
			logger.info("Second circle : " + c2);
			for(Node node : scenario.nodes) {
				if(c2.isOutside(node.getPosition())) {
					logger.warn("Outside point {}, Circle {}.", node.getPosition(), c2);
				}
			}
			logger.info("========================");
		}
	}
	
	private static void testAlgorithm() {
		int count = 13;
		Scenario scenario = new Scenario();
//		scenario.addAlgorithm(new GreedyAlgorithm());
		scenario.addAlgorithm(new RandomAngleAlgorithm());
		scenario.addAlgorithm(new GreedAngleAlgorithm());
//		scenario.addAlgorithm(new ParticleSwarmOptimizationAlgorithm());
		for(int i = 0; i < count ; ++ i) {
			logger.info("Scenario {} : ", (i + 1));
			scenario.calculateMinReader();
		}
		
		scenario.printResult();
	}
	
	public static void main(String[] args) {
//		testCircle();
		//随机函数种子设置
		rand.setSeed(555L);
		testAlgorithm();
		
  	}
}
