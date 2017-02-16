package com.zhuxiao.Simulation;

import java.util.Random;

public class ReaderWithAngle extends Reader {
	private double angle;
	private int cover;
	
	public static int MAX_ANGLE = 360;
	public static int LINE_ANGLE = 180;
	private static double MIN_VALUE = 1E-3;
	
	public ReaderWithAngle(Point p, int cover) {
		super(p);
		this.cover = cover;
		this.angle = 0;
	}
	
	public ReaderWithAngle(Point p, Point node, int cover) {
		super(p);
		this.cover = cover;
		this.angle = getMaxAngle(p, node) - cover;
		if(this.angle < 0) {
			angle += MAX_ANGLE;
		} 
		angle = Math.ceil(angle) % MAX_ANGLE;
		if(this.isNodeInside(node) == false) {
			System.out.println("Angle : " + angle);
		}
	}
	
	public void setAngle(double angle) {
		this.angle = angle % MAX_ANGLE;
	}
	
	@Override
	public String toString() {
		return "ReaderWithAngle [angle=" + angle + ", cover=" + cover + "]";
	}

	public ReaderWithAngle(Point p, int cover, double angle) {
		super(p);
		this.cover = cover;
		this.angle = angle;
	}

	public int getCoverAngle() {
		return this.cover;
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	private static double getMaxAngle(Point original, Point pos) {
		boolean isBlow = original.getY() > pos.getY();
		if(isDoubleEquals(original.getY(), pos.getY()))
			return LINE_ANGLE;
		Point linePoint = new Point(original.getX() + 1, original.getY());
		double angle = getAngleWithPoints(original, linePoint, pos);
		if(isBlow)
			angle += LINE_ANGLE;
		return angle;
	}
	
	private boolean isSameDouble(double v1, double v2) {
		return Math.abs(v1 - v2) < MIN_VALUE;
	}
	
	public boolean isNodeInside(Point pos) {
		double angle = getMaxAngle(this.pos, pos);
		
		if(this.angle + cover >= MAX_ANGLE)
			return angle > this.angle || isSameDouble(angle, this.angle) || 
					angle < (this.angle + cover) % MAX_ANGLE || isSameDouble(angle , (this.angle + cover) % MAX_ANGLE);
		else
			return (angle > this.angle || isSameDouble(angle, this.angle)) && 
					(angle < this.angle + cover || isSameDouble(angle, this.angle + cover));
		
		/*		
		int realCover = this.cover;
		boolean reverse = false;
		if(cover > 180) {
			realCover = 360 - cover;
			reverse = true;
		} else if(cover >= MAX_ANGLE) {
			return true;
		} else if (cover <= 0) {
			return false;
		}
		
		Point p1 = getPointWithAngle(this.pos, this.angle);
		Point p2 = getPointWithAngle(this.pos, this.angle + this.cover);
		
		double angle1 = getAngleWithPoints(this.pos, pos, p1);
		double angle2 = getAngleWithPoints(this.pos, pos, p2);
		
		boolean inside = Math.abs(realCover - angle1 - angle2) < 1E-3;
		return reverse ? !inside : inside;
		*/
	}
	
	private static double getAngleWithPoints(Point original, Point p1, Point p2) {
		double a = original.distanceTo(p1);
		double b = original.distanceTo(p2);
		double c = p1.distanceTo(p2);
		
		// arccos((a * a + b * b - c * c) / (2 * a * b))
		return Math.toDegrees(Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)));
	}
	
	private static Point getPointWithAngle(Point original, double angle) {
		while(angle > 360)
			angle -= 180;
		double py = Math.sin(Math.toRadians(angle));
		double px = Math.cos(Math.toRadians(angle));
		if(isDoubleEquals(px , 0)) {
			px = 0.0;
		}
		if(isDoubleEquals(py, 0)) {
			py = 0.0;
		}
		return new Point(px, py);
	}
	
	private static boolean isDoubleEquals(double v1, double v2) {
		return Math.abs(v1 - v2) < MIN_VALUE;
	}
	
	public static void main(String[] args) {
		Point p = new Point(0, 0);
		Point p7 = getPointWithAngle(p, 0);

		Point p1 = getPointWithAngle(p, 45);
		
		Point p2 = getPointWithAngle(p, 90);

		Point p3 = getPointWithAngle(p, 135);

		Point p4 = getPointWithAngle(p, 180);

		Point p5 = getPointWithAngle(p, 225);
		
		Point p6 = getPointWithAngle(p, 270);
		
		Point p8 = getPointWithAngle(p, 315);

		System.out.println(p7);
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		System.out.println(p5);
		System.out.println(p6);
		System.out.println(p8);
		
		double angle = getAngleWithPoints(p, p7, p1);
		System.out.println("Angle = " + angle);
		
		ReaderWithAngle reader = new ReaderWithAngle(new Point(0, 0), 60);
		reader.angle = 90;
		System.out.println(reader.isNodeInside(new Point(1, 1)));
		System.out.println(reader.isNodeInside(new Point(-1, 1)));
		System.out.println(reader.isNodeInside(new Point(-1, -1)));
		System.out.println(reader.isNodeInside(new Point(1, -1)));

		Random rand = new Random(100);
		for(int i = 0 ; i < 1 ; ++ i) {
			Point pp1 = new Point(2.9880027938979357, -0.05215578277318415);
			Point pp2 = new Point(5.47284482729326, 14.70300341013806);
			ReaderWithAngle r = new ReaderWithAngle(pp1, pp2, 60);
			if(r.isNodeInside(pp2) == false) {
					System.out.println("pp1 = " + pp1 + ", pp2 = " + pp2);
					break;
			}
				
		}
	}
}
