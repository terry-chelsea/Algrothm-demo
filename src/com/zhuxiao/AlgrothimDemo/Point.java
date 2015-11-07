package com.zhuxiao.AlgrothimDemo;

//��עһ����ά������
public class Point {
	public static final double ESP = 1E-8;
	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		this(p.x, p.y);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

	//����������һ����ά���꣬���㵱ǰ����������ڵ�ľ���
	public double distanceTo(Point p) {
		if(p==null){
			return 0.0;
		}
		return Math.sqrt(Math.pow((this.x - p.x), 2) + Math.pow((this.y - p.y), 2));   
	}
 	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	public static void main(String[] args) {
		Point p1 = new Point(2, 3);
		Point p2 = new Point(3, 2);
		
		System.out.println(p1.distanceTo(p2));
	}
}
