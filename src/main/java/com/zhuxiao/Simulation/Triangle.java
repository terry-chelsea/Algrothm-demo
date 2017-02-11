package com.zhuxiao.Simulation;

import java.util.Random;

public class Triangle {
	Point p1;
	Point p2;
	Point p3;
	
	public Triangle(Point p1, Point p2, Point p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	public Circle getMinCircle() {
		double r12 = this.p1.distanceTo(this.p2) / 2;
		double r13 = this.p1.distanceTo(this.p3) / 2;
		double r23 = this.p2.distanceTo(this.p3) / 2;
		
		Point m12 = new Point((this.p1.getX() + this.p2.getX()) / 2,
				(this.p1.getY() + this.p2.getY()) / 2);
		Point m13 = new Point((this.p1.getX() + this.p3.getX()) / 2,
				(this.p1.getY() + this.p3.getY()) / 2);
		Point m23 = new Point((this.p2.getX() + this.p3.getX()) / 2,
				(this.p2.getY() + this.p3.getY()) / 2);

		Point outerCenter = this.getOuterCenter();
		//三点平行的时候
		if(outerCenter == null) {
			double redius = r12;
			Point center = m12;
			if(redius < r13) {
				redius = r13;
				center = m13;
			}
			if(redius < r23) {
				redius = r23;
				center = m23;
			}
			
			return new Circle(center, redius);
		}
		
		double distance = outerCenter.distanceTo(this.p1);
		double redius = distance;
		Point center = outerCenter;
		//三角形如果是钝角三角形，外心的求法
		if(redius > r12 && m12.distanceTo(p3)- r12 < Point.ESP) {
			redius = r12;
			center = m12;
		}
		if(redius > r13 && m13.distanceTo(p2) - r13 < Point.ESP) {
			redius = r13;
			center = m13;
		}
		
		if(redius > r23 && m23.distanceTo(p1) - r23 < Point.ESP) {
			redius = r23;
			center = m23;
		}
		
		return new Circle(center, redius);
	}
	
	public Point getOuterCenter() {
		double y32 = this.p3.getY() - this.p2.getY();
		double y12 = this.p1.getY() - this.p2.getY();
		double x21 = this.p2.getX() - this.p1.getX();
		double x23 = this.p2.getX() - this.p3.getX();
		
		double py32 = Math.pow(this.p3.getY(), 2) - Math.pow(this .p2.getY(), 2);
		double px32 = Math.pow(this.p3.getX(), 2) - Math.pow(this.p2.getX(), 2);
		double py12 = Math.pow(this.p1.getY(), 2) - Math.pow(this.p2.getY(), 2);
		double px12 = Math.pow(this.p1.getX(), 2) - Math.pow(this.p2.getX(), 2);
		
		double x = 0;
		double y = 0;
		//三个点在一条直线上
		if((y32 == 0 && y12 == 0) || (x21 == 0 && x23 == 0) ||
				Math.abs(y32 / x23 - y12 / x21) < Point.ESP) {
			return null;
		//BC边平行于X轴
		} else if(y32 == 0) {
			x = (this.p2.getX() + this.p3.getX()) / 2;
			y = x21 * x / y12 + (py12 + px12) / (2 * y12);
		//AB边平行于X轴
		} else if(y12 == 0) {
			x = (this.p2.getX() + this.p1.getX()) / 2;
			y = x23 * x / y32 + (py32 + px32) / (2 * y32);
		} else {
			x = ((py32 + px32) / (2 * y32) - (py12 + px12) / (2 * y12)) / 
				(x21 / y12 - x23 / y32);
			y = x23 * x / y32 + (py32 + px32) / (2 * y32);
		}
		
		return new Point(x, y);
	}
	
	public Point getFirst() {
		return this.p1;
	}
	
	public Point getSecond() {
		return this.p2;
	}
	
	public Point getThird() {
		return this.p3;
	}
	
	@Override
	public String toString() {
		return "Triangle [p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + "]";
	}
	
	private static void test() {
		Random rand = new Random();
		
		for(int i = 0 ; i < 100 ; ++ i) {
			Point p1 = new Point(rand.nextInt(10), rand.nextInt(10));
			Point p2 = new Point(rand.nextInt(10), rand.nextInt(10));
			Point p3 = new Point(rand.nextInt(10), rand.nextInt(10));

			System.out.println(p1);
		
			System.out.println(p2);

			System.out.println(p3);

		
			Triangle t = new Triangle(p1, p2, p3);
			Point p = t.getOuterCenter();
		
			System.out.println(p);

			System.out.println(p.distanceTo(p1));
			System.out.println(p.distanceTo(p2));
			System.out.println(p.distanceTo(p3));
		}
		
/*		
		Point p1 = new Point(0, 0);
		Point p2 = new Point(0, 1);
		Point p3 = new Point(0, 2);
		Triangle t1 = new Triangle(p1, p2, p3);
		
		System.out.println(t1.getMinCircle());
		
		Point p11 = new Point(0, 0);
		Point p21 = new Point(0, 4);
		Point p31 = new Point(4, 0);
		Triangle t2 = new Triangle(p11, p21, p31);
		System.out.println(t2.getMinCircle());
*/		
		Point p12 = new Point(0, 0);
		Point p22 = new Point(4, 0);
		Point p32 = new Point(-4, 4);
		Triangle t3 = new Triangle(p12, p22, p32);
		System.out.println(t3.getMinCircle());
		
		double a = 1.23457893435566;
		double b = 1.23457893435566;
		System.out.println(a - b == 0);
	}
	
	public static void testLine()
	{
		Point p1=new Point(0,0);
		Point p2=new Point(0,4);
		Point p3=new Point(3,0);
		Triangle t=new Triangle(p1,p2,p3);
		Circle c=t.getMinCircle();
		System.out.println(c);
	}
	
	public static void main(String[] args) {
		testLine();
	}
}
