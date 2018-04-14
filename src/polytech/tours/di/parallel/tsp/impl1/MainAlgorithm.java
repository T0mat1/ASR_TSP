package polytech.tours.di.parallel.tsp.impl1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
		long max_thread = Long.valueOf(config.getProperty("maxthreads"));
		long max_tasks = Long.valueOf(config.getProperty("maxtasks"));
		
		Random rand=new Random(Long.valueOf(config.getProperty("seed")));
		Coordinator coordinator = new Coordinator();
		
		Solution currentSolution = new Solution();
		Solution best = null;
		

		for(int i=0; i<inst.getN(); i++){
			currentSolution.add(i);
		}
		
		long startTime=System.currentTimeMillis();		
		while((System.currentTimeMillis()-startTime)/1_000<max_cpu){
			Collections.shuffle(currentSolution,rand);
			
			//set the objective function of the solution
			currentSolution.setOF(TSPCostCalculator.calcOF(inst.getDistanceMatrix(), currentSolution));
			
			//run local search			
			ExecutorService executor = Executors.newFixedThreadPool((int) max_thread);
			ArrayList<Future <Solution>> results;
			ArrayList<Callable <Solution>> tasks = new ArrayList<>();
			for(int i=0; i<=max_tasks&&i<=currentSolution.size(); i++) {
				tasks.add(new LocalSearch(i, currentSolution, inst, coordinator));
			}
				
			//execute tasks
			try {
				results = (ArrayList<Future<Solution>>) executor.invokeAll(tasks, (long) ((max_cpu)-((System.currentTimeMillis()-startTime)/1_000)), TimeUnit.SECONDS);
				executor.shutdown();
			} catch(InterruptedException e) {
				return currentSolution;
			}
			
			try {
				double currentBestOF = currentSolution.getOF();
				for(Future<Solution> someBest : results) {
					//choisir la meilleure
					if (someBest.get().getOF() < currentBestOF) {
						best = someBest.get();
						best.setOF(TSPCostCalculator.calcOF(inst, best));
						currentBestOF = best.getOF();
					}
				}
			} catch(InterruptedException | ExecutionException e) {
				if(best==null) {
					return currentSolution;
				} else {
					return best;
				}
			}
	
			if(best==null)
				best=currentSolution.clone();
			else if(currentSolution.getOF()<best.getOF())
				best=currentSolution.clone();
		}
		//return the solution
		return best;
	}

}
