package com.zhuxiao.Simulation;

public class ReaderWithAngle extends Reader {
	private double angle;
	private int cover;
	
	public static int MAX_ANGLE = 360;
	public static int LINE_ANGLE = 180;
	private static double MOVE_STEP = 1;
	
	public ReaderWithAngle(Point p, int cover) {
		super(p);
		this.cover = cover;
		this.angle = 0;
	}
	
	public ReaderWithAngle(Point p, int cover, double angle) {
		super(p);
		this.cover = cover;
		this.angle = angle;
	}

	public boolean hasNext() {
		return this.angle < MAX_ANGLE;
	}
	
	public Reader next() {
		this.angle += MOVE_STEP;
		return this;
	}
	
	public int getCoverAngle() {
		return this.cover;
	}
	
	public boolean isNodeInside(Point pos) {
		Point p1 = getPointWithAngle(this.pos, this.angle);
		Point p2 = getPointWithAngle(this.pos, this.angle + this.cover);
		
		double angle1 = getAngleWithPoints(this.pos, pos, p1);
		double angle2 = getAngleWithPoints(this.pos, pos, p2);
		
		return Math.abs(cover - angle1 - angle2) < 1E-6;
	}
	
	private static double getAngleWithPoints(Point original, Point p1, Point p2) {
		double a = original.distanceTo(p1);
		double b = original.distanceTo(p2);
		double c = p1.distanceTo(p2);
		
		// arccos((a * a + b * b - c * c) / (2 * a * b))
		return Math.toDegrees(Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)));
	}
	
	private static Point getPointWithAngle(Point original, double angle) {
		double py = Math.sin(Math.toRadians(angle));
		double px = Math.cos(Math.toRadians(angle));
		if(Math.abs(px) < 1E-10) {
			px = 0.0;
		}
		if(Math.abs(py) < 1E-10) {
			py = 0.0;
		}
		return new Point(px, py);
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
		
		ReaderWithAngle reader = new ReaderWithAngle(new Point(0, 0), 180);
		System.out.println(reader.isNodeInside(new Point(-0.1, -0.1)));
	}
}
