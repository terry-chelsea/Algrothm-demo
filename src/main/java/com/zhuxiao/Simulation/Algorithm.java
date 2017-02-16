package com.zhuxiao.Simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Algorithm {
	private static int INIT_NUM = 1;
	private static Logger logger = LoggerFactory.getLogger(Algorithm.class);

	protected abstract Reader[] runInternal(Node[] nodes, int readerNum, Circle circle);
	
	//�������룬�ڸó����£�����ÿ��node�ڵ�Ľ��ܹ��ʴ��ڵ�����ֵʱ����Ҫ����С��reader�ڵ����
	public Reader[] run(Node[] nodes, Circle circle) {
		for(int i = INIT_NUM ;  ; ++ i) {
			//runTnternal���������ҵ�����������ʱ����null��ѭ����������reader������ֱ����������ʱ������reader�ֲ���Ϣ
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
		//��Reader[readerNum]λ��ȷ���󣬼���ÿ���ڵ�Ľ��չ���
		for (Node node : nodes) {
			node.setPower(0.0);
			for (Reader reader :readers) {
				node.newPowerFromReader(reader);
			}
		}
		//�ҳ����չ�����С�Ľڵ�(�����ж��)������С����tempPmin	
		logger.debug("==========Node��Ratio=============");
		for(int i=0; i<nodes.length; i++) {
			logger.debug("Initialize Node {}, Ratio = {}", i, nodes[i].getRatio());
		}
		double tempPmin = nodes[0].getRatio();
		int nodenum=0;
		for (int i = 1; i < nodes.length; i++) {
			Node node = nodes[i];
			double cur = node.getRatio();
			if (cur < tempPmin) {   //��Ҫע����ǣ�������С�ڵ���
				nodenum=i;
				tempPmin = cur;
			}
		}
//		System.out.println("�����ڵ㣺"+nodenum + " ,ratio " + tempPmin);
		return nodenum;
	}
/*	
	public String toString() {
		return "RandomAlgorithm";
	}*/
}
