package com.zhuxiao.Simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scenario {
	// �������node�ڵ�ĳ�����С
	private static final double INIT_MAX_SIZE = 16.0;   
	 //node�ڵ�ĳ�ʼ������
	private static final int NODE_NUM = 20;
	//node�ڵ����С���ܹ���
	private static final double MIN_POWERS[] = {0.0001, 0.0002};
	//�칹�Ľڵ��������
	private static final int MIN_POWERS_SIZE = MIN_POWERS.length;
	//�����������
	public static Random rand = new Random();
	private static Logger logger = LoggerFactory.getLogger(Scenario.class);
	
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
		for(Algorithm algorithm : this.algorithms) {
			//���ɳ����������node�ͻ�Բ��
			this.initialize();
			if(this.circle == null)
				continue ;
			
			//���ݵ�ǰ�㷨������ó�����������������С������reader�ֲ�
			Reader readers[] = algorithm.run(nodes, this.circle);
			logger.info("\tUsing {}, need {} readers.", algorithm, readers.length);
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
		//���������������
		rand.setSeed(555L);
		testAlgorithm();
		
  	}
}
