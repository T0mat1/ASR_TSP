package polytech.tours.di.parallel.tsp.impl1;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;

import polytech.tours.di.parallel.tsp.Algorithm;
import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.InstanceReader;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

public class MainAlgorithm implements Algorithm {

	@Override
	public Solution run(Properties config) {
		
		//read the instance
		InstanceReader instReader=new InstanceReader();
		instReader.buildInstance(config.getProperty("instance"));
		
		Instance inst = instReader.getInstance();
		long max_cpu = Long.valueOf(config.getProperty("maxcpu"));
		long max_thread = Long.valueOf(config.getProperty("maxtheads"));
		long max_tasks = Long.valueOf(config.getProperty("maxtasks"));
		
		Random rand=new Random(Long.valueOf(config.getProperty("seed")));
		
		Solution currentSolution = new Solution();
		Solution best = null;

		Coordinator coordinator = new Coordinator();
		
		long startTime=System.currentTimeMillis();
		for(int i=0; i<inst.getN(); i++){
			currentSolution.add(i);
		}
		
		while((System.currentTimeMillis()-startTime)/1_000<=max_cpu){	
			//set the objective function of the solution
			currentSolution.setOF(TSPCostCalculator.calcOF(inst.getDistanceMatrix(), currentSolution));
			
			//TODO run local search here
			

			// TODO wip executor / callable
			// y'aura une ThreadPool de la classe Executor (newFixed) avec arg max_thread en param
			/*
			ArrayList<LocalSearch> tasks=new ArrayList<>();
			ArrayList<Thread> threads=new ArrayList<>();
			Executors executor;
			
			for(int t=1;t<=inst.getN();t++){
				 tasks.add(new LocalSearch(currentSolution.get(t), currentSolution, inst));
				 threads.add(new Thread(tasks.get(tasks.size()-1)));
			}
			
			//Launch execution			
			for(int i=0; i<inst.getN(); i++) {
				LocalSearch ls = new LocalSearch(currentSolution.get(i), currentSolution, inst);
				ls.call();
			}
			*/
	
			if(best==null)
				best=currentSolution.clone();
			else if(currentSolution.getOF()<best.getOF())
				best=currentSolution.clone();
		}
		//return the solution
		return best;
	}

}
