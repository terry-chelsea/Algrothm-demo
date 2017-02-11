package com.zhuxiao.Simulation;

public class Node {
 	private Point pos;
	private double power;
	private double minPower;
	
	private static final double PS = 1.0;
	
	//��ʼ��һ��node�ڵ�
 	public Node(Point p, double min) {
 		this.pos = p;
 		this.minPower = min;
 		this.power = 0.0;
	}
 	
 	//����һ��node�ڵ�
 	public Node(Node n) {
 		this.pos = n.pos;
 		this.minPower = n.minPower;
 		this.power = n.getPower();
 	}
	
 	//node�ڵ������ĸ�reader�ڵ��ȡ���ʣ������ӵ���node�ڵ��Ž��ܹ��ʵ�power��������
	public void newPowerFromReader(Reader r) {
		double distinctFromReader = this.pos.distanceTo(r.getPosition());
		double eta = 0.3, lambda = 0.33, epsilong = 0.2316, pi = 3.1416;
		double Gs = Math.pow(10, 0.8); //% 8dBi
		double Gr = Math.pow(10, 0.2); //% 2dBi
		double Lp = Math.pow(10, 0.3); //% 3dB

		double Ph = eta * Gs * Gr * PS* lambda * lambda / 
				(Lp*16 * pi * pi*(distinctFromReader + epsilong)*(distinctFromReader + epsilong));
		this.power += Ph;
	}
	
	public void setPoint(Point p) {
		this.pos = p;
	}
	
	//�ýڵ��ѽ��ܵĹ����Ƿ�����ýڵ�Ĺ�����ֵ
	public boolean isEnoughPower() {
		return this.getRatio() >= 1;
	}	
	
	public Point getPosition() 
	{
		return this.pos;
	}
	
	public double getPower() {
		return this.power;
	}
	
	public double getMinPower() {
		return this.minPower;
	}
	
	public void setPower(double power) {
		this.power = power;
	}
	
	//���ظ�node�ڵ�Ľ��ܹ����빦����ֵ�ı�ֵ
	public double getRatio() {
		return this.power / this.minPower;
	}
	
	@Override
	public String toString() {
		return "Node [pos=" + pos + ", power=" + power + ", minPower="
				+ minPower + "]";
	}
	
	public static void main(String[] args) {
		Reader r1 = new Reader(new Point(1, 2));
		
		Reader r2 = new Reader(new Point(2, 3));
		
		Reader r3 = new Reader(new Point(4, 2));
		
		Node node = new Node(new Point(0,0), 1.2);
		
		node.newPowerFromReader(r1);
		node.newPowerFromReader(r2);
		node.newPowerFromReader(r3);
		
		System.out.println(node.power);
		System.out.println(node.isEnoughPower());
	}
}
