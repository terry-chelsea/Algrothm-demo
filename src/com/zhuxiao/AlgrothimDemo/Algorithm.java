package com.zhuxiao.AlgrothimDemo;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public abstract class Algorithm {
	private static PrintStream ps = Scenario.ps;
	private static int INIT_NUM = 1;
	protected boolean isLog = false;

	protected void init() {
		
	}
	
	protected abstract Reader[] runInternal(Node[] nodes, int readerNum, Circle circle);
	
	//根据输入，在该场景下，满足每个node节点的接受功率大于等于阈值时所需要的最小的reader节点个数
	public Reader[] run(Node[] nodes, Circle circle) {
		init();
		for(int i = INIT_NUM ;  ; ++ i) {
			//runTnternal方法：当找到不满足条件时返回null，循环继续增加reader个数，直到满足条件时，返回reader分布信息
			Reader[] readers = runInternal(nodes, i , circle);
			if(readers != null)
				return readers;
		}
	}
	
	public boolean isSatisfied(Node[] nodes, Reader[] readers, Circle circle) {
		for(Node node : nodes) {
			node.setPower(0.0);
			for(Reader reader : readers) {
				node.newPowerFromReader(reader);
			}
			if(!node.isEnoughPower()) {
				return false;
			}
		}
		for(Reader reader : readers) {
			if(circle.isOutside(reader.getPosition())) {
				System.out.println("!!!!!! reader is outside : circle = " + circle + ", point " + reader.getPosition());
			}
		}
		int i = 1;
		for(Node node : nodes) {
			log("node " + (i ++) + node.getPosition());
		}
		
		i = 0;
		for(Reader reader : readers) {
			log("reader " + (i ++) + reader.getPosition());
		}
		ps.flush();
		return true;
	}
	
	protected int getMinPower(Node[] nodes, Reader[] readers) {
		//当Reader[readerNum]位置确定后，计算每个节点的接收功率
		for (Node node : nodes) {
			node.setPower(0.0);
			for (Reader reader :readers) {
				node.newPowerFromReader(reader);
			}
		}
		//找出接收功率最小的节点(可能有多个)及该最小功率tempPmin	
		log("==========node的ratio=============");
		for(int i=0; i<nodes.length; i++)
		{
			log("初始化node"+i+"的ratio: "+nodes[i].getRatio());
		}
		double tempPmin = nodes[0].getRatio();
		int nodenum=0;
		for (int i = 1; i < nodes.length; i++) {
			Node node = nodes[i];
			double cur = node.getRatio();
			if (cur < tempPmin) {   //需要注意的是，这里是小于等于
				nodenum=i;
				tempPmin = cur;
			}
		}
//		System.out.println("最糟糕节点："+nodenum + " ,ratio " + tempPmin);
		return nodenum;
	}
	
	
	protected void log(String line) {
		//控制是否打开日志
		if(this.isLog) {
			ps.println(line);
		}
	}
	
	protected void log(String line, boolean condition) {
		if(condition)
			ps.println(line);
	}
	
/*	
	public String toString() {
		return "RandomAlgorithm";
	}*/
}
