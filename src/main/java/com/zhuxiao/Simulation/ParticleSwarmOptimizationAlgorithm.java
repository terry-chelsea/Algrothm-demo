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
		Reader Particle_posi[][] = new Reader[M][K];    //{Particle_posi[i][0],Particle_posi[i][1],...,Particle_posi[i][K-1]}��һ���Ż�����Ľ⣬i=0,1,...,M
		Reader Particle_v[][] = new Reader[M][K];      //��¼ÿ������ĵ�ǰ���ٶ�����(����)

		Reader Particle_Loc_best_posi[][] = new Reader[M][K];    //ÿ����ֲ���������(����)
		Reader Glo_best_Particle_posi[] = new Reader[K];        //ȫ����������(����)

		double Particle_loc_best_P[] = new double[M];      //ÿ����ľֲ�������Ӧ��(���չ���)���ɾֲ����������������	
		double Global_Pmax;         //ȫ��������Ӧ��(���չ���)

		int Best_Particle_Num;	

		//��ʼ��M����Particle_posi[0], Particle_posi[1], ...,Particle_posi[M-1]
		for (i = 0; i < M; i++) {
			for (readerNum = 0; readerNum < K; readerNum ++) {
				Point p = circle.getRandomPoint();    //��ʼ��Ⱥ��
				Particle_posi[i][readerNum] = new Reader(p);
				Particle_Loc_best_posi[i][readerNum] = new Reader(p);  //����ǰ���Ž��д��ֲ����ż���

				Particle_v[i][readerNum] = new Reader(new Point(0.0, 0.0));    //��ʼ��Ϊ0
			}
		}

		for (i = 0; i < M; i++) { //����ÿ�����ӵ���Ӧ�ȣ������չ���
			for (nodeNum = 0; nodeNum < N; nodeNum++) {
				nodes[nodeNum].setPower(0.0);
				for (readerNum = 0; readerNum < K; readerNum++) {
					nodes[nodeNum].newPowerFromReader(Particle_posi[i][readerNum]);
				}
			}

			//�ҳ�һ���н��չ�����С�Ľڵ�	
			double curMinRatio = nodes[0].getRatio();
			for (nodeNum = 1; nodeNum < N; nodeNum ++) {
				double curRatio = nodes[nodeNum].getRatio();
				if (curRatio < curMinRatio) {
					curMinRatio = curRatio;
					Particle_loc_best_P[i] = curRatio;
				}
			}
		}

		//�ҳ�ȫ��������Ӧ��(���չ���)
		Global_Pmax = Particle_loc_best_P[0];
		Best_Particle_Num = 0;
		for(i = 1; i < M; i++) {
			if (Global_Pmax < Particle_loc_best_P[i]) {
				Global_Pmax = Particle_loc_best_P[i];
				Best_Particle_Num=i;
			}
		}
		//����ȫ����������(����)
		for (readerNum = 0; readerNum < K; readerNum++) {
			Glo_best_Particle_posi[readerNum] = new Reader(Particle_Loc_best_posi[Best_Particle_Num][readerNum]);
		}

		int iter = 0;
		while (iter < ITER_TIMES) {
			//���µ�i��particle���ٶ�Particle_v[][]
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

			//���µ�i��particle��Ӧ��ÿ���ڵ���չ��ʣ��Լ����µ�i��particle����ʷ����ֵParticle_Loc_best_posi[i]
			for (i = 0; i < M; i++) {
				//�����i��particle��Ӧ��ÿ���ڵ���չ���
				for (nodeNum = 0; nodeNum < N; nodeNum++) {
					nodes[nodeNum].setPower(0.0);
					for (readerNum = 0; readerNum < K; readerNum++) {
						nodes[nodeNum].newPowerFromReader(Particle_posi[i][readerNum]);
					}
				}
				
				//�ҳ���i��particle��Ӧ����ͽ��չ��ʣ�����Particle_cur_P[i]
				double curMinRatio = nodes[0].getRatio();
				for (nodeNum = 1; nodeNum < N; nodeNum++) {
					double curRatio = nodes[nodeNum].getRatio();
					if (curRatio < curMinRatio) 
						curMinRatio = curRatio;
				}

				//���¸���ֲ�����ֵ
				if (curMinRatio > Particle_loc_best_P[i]) {
					Particle_loc_best_P[i] = curMinRatio;
					for (readerNum = 0; readerNum < K; readerNum++) {
						Particle_Loc_best_posi[i][readerNum] = new Reader(Particle_posi[i][readerNum]);
					}
				}
			}

			//����ȫ������ֵGlobal_Pmax
			//����ȫ������Particle�ı��Best_Particle_Num
			//����ȫ����������(����)Glo_best_Particle_posi[]
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
