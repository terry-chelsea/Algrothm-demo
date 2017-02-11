package com.zhuxiao.Simulation;

import java.util.Random;

public class Circle {
	private static Random rand = Scenario.rand;
	private Point center;
	private double redius;
	
	public Circle(Point center, double redius) {
		this.center = center;
		this.redius = redius;
	}

	public Point getCenter() {
		return center;
	}

	public double getRedius() {
		return redius;
	}
	
	//�ж�����������Ƿ���Բ��
	public boolean isOutside(Point p) {
		return this.center.distanceTo(p) > redius + Point.ESP;
	}
	
	//�����԰�ڻ�ȡһ����ά����
	public Point getRandomPoint() {
		double randomLength = rand.nextDouble() * this.redius;
		double randomAngle = rand.nextDouble() * 360;
		return newPoint(randomLength, randomAngle);
	}
	
	//�Ӽ�����ת��Ϊ��ά����
	public Point newPoint(double r, double angle) {
		double radian = Math.toRadians(angle);
		double newX = r * Math.cos(radian);
		double newY = r * Math.sin(radian);

		return new Point(newX + this.center.getX(), 
				newY + this.center.getY());
	}
	
	public double getAngle(Point p) {
		double x = p.getX() - this.center.getX();
		double y = p.getY() - this.center.getY();
		
		double angle = Math.toDegrees(Math.atan2(y, x));
		if(angle < 0)
			angle = 360 + angle;
		return angle;
	}
	
	public double getPointRedius(Point p) {
		double x = p.getX() - this.center.getX();
		double y = p.getY() - this.center.getY();
		
		return Math.sqrt(x * x + y * y);
	}

	@Override
	public String toString() {
		return "Circle [center=" + center + ", redius=" + redius + "]";
	}
	
	public static void main(String[] args) {
		Point center = new Point(rand.nextDouble() * 10 , rand.nextDouble() * 10);
		double reduis = rand.nextDouble() * 6;
		Circle c = new Circle(center, reduis);
		
		Point p = c.getRandomPoint();
		double angle = c.getAngle(p);
		System.out.println(angle);
	}
}
