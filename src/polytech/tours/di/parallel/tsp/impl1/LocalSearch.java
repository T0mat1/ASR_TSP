package polytech.tours.di.parallel.tsp.impl1;

import java.util.concurrent.Callable;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

/**
 * In these tasks, we relocate a city in every possible ways and keep only the
 * best solution
 * 
 * @author Thomas Rossi Benoit Richard
 * @version %I%, %G%
 *
 */
public class LocalSearch implements Callable<Solution> {

	private Solution currentSolution;
	private int currentCity;
	private Instance instance;

	public LocalSearch(int city, Solution newSolution, Instance newInstance) {
		currentSolution = newSolution;
		currentCity = city;
		instance = newInstance;
	}

	@Override
	public Solution call() {
		Solution bestSolution = currentSolution.clone();
		Solution tmpSolution;
		
		for (int city : currentSolution) {
			tmpSolution = currentSolution.clone();
			if (currentCity != city)
				tmpSolution.relocate(currentCity, city);
			tmpSolution.setOF(calcOF(instance, currentSolution));
			if (tmpSolution.getOF() < bestSolution.getOF())
				bestSolution = tmpSolution.clone();
		}
		return bestSolution;
	}
	
	/**
	 * internal implementation of the calculator for each thread
	 * @return the cost of a TSP solution
	 */
	public double calcOF(Instance instance, Solution s){
		double[][] tmpDistMatrix=instance.getDistanceMatrix();

		double cost=0;
		for(int i=1;i<s.size();i++){
			cost=cost+tmpDistMatrix[s.get(i-1)][s.get(i)];
		}
		cost=cost+tmpDistMatrix[s.get(s.size()-1)][s.get(0)];
		return cost;		
	}
}