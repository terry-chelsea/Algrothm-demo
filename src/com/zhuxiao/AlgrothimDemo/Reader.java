package com.zhuxiao.AlgrothimDemo;

public class Reader {
	private Point pos;
	public Reader(Point p) {
		this.pos = p;
	}
	
	//复制一个reader节点
	public Reader(Reader r) {
		this.pos = new Point(r.pos);
	}
	
	public Point getPosition() {
		return this.pos;
	}
	
	public void setPosition(Point p) {
		this.pos = p;
	}

	@Override
	public String toString() {
		return "Reader [pos=" + pos + "]";
	}
	
	public static void printNodes(Reader[] readers) {
		StringBuffer sb = new StringBuffer("[");
		for(Reader reader : readers) {
			sb.append(reader).append(",");
		}
		
		System.out.println(sb.substring(0, sb.length() - 1) + "]");
	}
	
	public static void main(String [] args)
	{
		Reader[] read =new Reader[10];
		for(int i=0 ;i<read.length ;i++)
		{
			read[i] =new Reader(new Point(0.0,0.0)); 
		}
		printNodes(read);
		
	}
}
