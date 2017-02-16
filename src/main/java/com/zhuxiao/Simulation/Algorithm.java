package com.zhuxiao.Simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Algorithm {
	private static int INIT_NUM = 1;
	private static Logger logger = LoggerFactory.getLogger(Algorithm.class);

	protected abstract Reader[] runInternal(Node[] nodes, int readerNum, Circle circle);
	
	//根据输入，在该场景下，满足每个node节点的接受功率大于等于阈值时所需要的最小的reader节点个数
	public Reader[] run(Node[] nodes, Circle circle) {
		for(int i = INIT_NUM ;  ; ++ i) {
			//runTnternal方法：当找到不满足条件时返回null，循环继续增加reader个数，直到满足条件时，返回reader分布信息
			Reader[] readers = runInternal(nodes, i , circle);
			if(readers != null)
				return readers;
		}
	}
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	public boolean isSatisfied(Node[] nodes, Reader[] readers, Circle circle) {
		for(Node node : nodes) {
			node.setPower(0.0);
			for(Reader reader : readers) {
				if(reader != null && reader.isNodeInside(node.getPosition())) {
					node.newPowerFromReader(reader);
				}
			}
		}
		
		for(Node node : nodes) {
			if(node.isEnoughPower() == false)
				return false;
		}
		
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
		logger.debug("==========Node的Ratio=============");
		for(int i=0; i<nodes.length; i++) {
			logger.debug("Initialize Node {}, Ratio = {}", i, nodes[i].getRatio());
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
/*	
	public String toString() {
		return "RandomAlgorithm";
	}*/
}
