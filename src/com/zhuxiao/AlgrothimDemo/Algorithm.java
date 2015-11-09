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
	
	//�������룬�ڸó����£�����ÿ��node�ڵ�Ľ��ܹ��ʴ��ڵ�����ֵʱ����Ҫ����С��reader�ڵ����
	public Reader[] run(Node[] nodes, Circle circle) {
		init();
		for(int i = INIT_NUM ;  ; ++ i) {
			//runTnternal���������ҵ�����������ʱ����null��ѭ����������reader������ֱ����������ʱ������reader�ֲ���Ϣ
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
		//��Reader[readerNum]λ��ȷ���󣬼���ÿ���ڵ�Ľ��չ���
		for (Node node : nodes) {
			node.setPower(0.0);
			for (Reader reader :readers) {
				node.newPowerFromReader(reader);
			}
		}
		//�ҳ����չ�����С�Ľڵ�(�����ж��)������С����tempPmin	
		log("==========node��ratio=============");
		for(int i=0; i<nodes.length; i++)
		{
			log("��ʼ��node"+i+"��ratio: "+nodes[i].getRatio());
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
	
	
	protected void log(String line) {
		//�����Ƿ����־
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
