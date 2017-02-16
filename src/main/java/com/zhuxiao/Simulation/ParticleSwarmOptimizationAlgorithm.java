package com.zhuxiao.Simulation;

import java.util.Random;

public class ParticleSwarmOptimizationAlgorithm extends Algorithm {
	private static final int M = 20;
	private static final int ITER_TIMES = 2000;
	private static final double W = 0.7298;
	private static final double C1 = 1.49618;
	private static final double C2 = 1.49618;
	
	@Override
	public Reader[] runInternal(Node[] nodes, int num, Circle circle) {
		Random rand = Scenario.rand;
		int readerNum,nodeNum;
		int i;
		int K = num;
		int N = nodes.length;
		Reader Particle_posi[][] = new Reader[M][K];    //{Particle_posi[i][0],Particle_posi[i][1],...,Particle_posi[i][K-1]}是一个优化问题的解，i=0,1,...,M
		Reader Particle_v[][] = new Reader[M][K];      //记录每个个体的当前代速度向量(坐标)

		Reader Particle_Loc_best_posi[][] = new Reader[M][K];    //每个解局部最优向量(坐标)
		Reader Glo_best_Particle_posi[] = new Reader[K];        //全局最优向量(坐标)

		double Particle_loc_best_P[] = new double[M];      //每个解的局部最优适应度(接收功率)，由局部最优向量计算而来	
		double Global_Pmax;         //全局最优适应度(接收功率)

		int Best_Particle_Num;	

		//初始化M个解Particle_posi[0], Particle_posi[1], ...,Particle_posi[M-1]
		for (i = 0; i < M; i++) {
			for (readerNum = 0; readerNum < K; readerNum ++) {
				Point p = circle.getRandomPoint();    //初始化群体
				Particle_posi[i][readerNum] = new Reader(p);
				Particle_Loc_best_posi[i][readerNum] = new Reader(p);  //将当前最优结果写入局部最优集合

				Particle_v[i][readerNum] = new Reader(new Point(0.0, 0.0));    //初始化为0
			}
		}

		for (i = 0; i < M; i++) { //计算每个粒子的适应度，即接收功率
			for (nodeNum = 0; nodeNum < N; nodeNum++) {
				nodes[nodeNum].setPower(0.0);
				for (readerNum = 0; readerNum < K; readerNum++) {
					nodes[nodeNum].newPowerFromReader(Particle_posi[i][readerNum]);
				}
			}

			//找出一组中接收功率最小的节点	
			double curMinRatio = nodes[0].getRatio();
			for (nodeNum = 1; nodeNum < N; nodeNum ++) {
				double curRatio = nodes[nodeNum].getRatio();
				if (curRatio < curMinRatio) {
					curMinRatio = curRatio;
					Particle_loc_best_P[i] = curRatio;
				}
			}
		}

		//找出全局最优适应度(接收功率)
		Global_Pmax = Particle_loc_best_P[0];
		Best_Particle_Num = 0;
		for(i = 1; i < M; i++) {
			if (Global_Pmax < Particle_loc_best_P[i]) {
				Global_Pmax = Particle_loc_best_P[i];
				Best_Particle_Num=i;
			}
		}
		//更新全局最优向量(坐标)
		for (readerNum = 0; readerNum < K; readerNum++) {
			Glo_best_Particle_posi[readerNum] = new Reader(Particle_Loc_best_posi[Best_Particle_Num][readerNum]);
		}

		int iter = 0;
		while (iter < ITER_TIMES) {
			//更新第i个particle的速度Particle_v[][]
			for (i = 0; i < M; i++) {
				for(readerNum = 0; readerNum < K; readerNum++) {
					Point curReaderV = Particle_v[i][readerNum].getPosition();
					Point curReaderPos = Particle_posi[i][readerNum].getPosition();
					Point curBestReaderPos = Particle_Loc_best_posi[i][readerNum].getPosition();
					Point globalBestReaderPos = Glo_best_Particle_posi[readerNum].getPosition();
					
					double newX = W * curReaderV.getX() 
							+ C1 * rand.nextDouble() * (curBestReaderPos.getX() - curReaderPos.getX()) 
							+ C2 * rand.nextDouble() * (globalBestReaderPos.getX() - curReaderPos.getX());

					double newY = W * curReaderV.getY() 
							+ C1 * rand.nextDouble() * (curBestReaderPos.getY() - curReaderPos.getY()) 
							+ C2 * rand.nextDouble() * (globalBestReaderPos.getY() - curReaderPos.getY());
					
					curReaderV.setX(newX);
					curReaderV.setY(newY);
					curReaderPos.setX(curReaderPos.getX() + newX);
					curReaderPos.setY(curReaderPos.getY() + newY);
					/*
					if(circle.isOutside(curReaderPos)) {
						System.err.println(curReaderPos);
					}*/
				}
			}

			//更新第i个particle对应的每个节点接收功率，以及更新第i个particle的历史最优值Particle_Loc_best_posi[i]
			for (i = 0; i < M; i++) {
				//计算第i个particle对应的每个节点接收功率
				for (nodeNum = 0; nodeNum < N; nodeNum++) {
					nodes[nodeNum].setPower(0.0);
					for (readerNum = 0; readerNum < K; readerNum++) {
						nodes[nodeNum].newPowerFromReader(Particle_posi[i][readerNum]);
					}
				}
				
				//找出第i个particle对应的最低接收功率，存入Particle_cur_P[i]
				double curMinRatio = nodes[0].getRatio();
				for (nodeNum = 1; nodeNum < N; nodeNum++) {
					double curRatio = nodes[nodeNum].getRatio();
					if (curRatio < curMinRatio) 
						curMinRatio = curRatio;
				}

				//更新个体局部最优值
				if (curMinRatio > Particle_loc_best_P[i]) {
					Particle_loc_best_P[i] = curMinRatio;
					for (readerNum = 0; readerNum < K; readerNum++) {
						Particle_Loc_best_posi[i][readerNum] = new Reader(Particle_posi[i][readerNum]);
					}
				}
			}

			//更新全局最优值Global_Pmax
			//更新全局最优Particle的编号Best_Particle_Num
			//更新全局最优向量(坐标)Glo_best_Particle_posi[]
			for (i = 0; i < M; i++) {
				if (Particle_loc_best_P[i] > Global_Pmax) {
					Global_Pmax = Particle_loc_best_P[i];
					Best_Particle_Num = i;

					for(readerNum = 0; readerNum < K; readerNum++) {
						Glo_best_Particle_posi[readerNum] = new Reader(Particle_Loc_best_posi[i][readerNum]);
					}
				}
			}
			iter ++;
		}    //------end while
//		System.out.println("min " + super.getMinPower(nodes, Glo_best_Particle_posi));
		if(super.isSatisfied(nodes, Glo_best_Particle_posi, circle))
			return Glo_best_Particle_posi;
		else 
			return null;
	}
}
