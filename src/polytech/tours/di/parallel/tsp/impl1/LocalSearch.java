package polytech.tours.di.parallel.tsp.impl1;

import java.util.concurrent.Callable;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

/**
 * In these tasks, we relocate a city in every possible ways and keep only the best solution
 * @author Thomas Rossi Benoit Richard
 * @version %I%, %G%
 *
 */
public class LocalSearch implements Callable<Solution> {
	
	private Solution currentSolution;
	private int currentCity;
	private Instance instance;
	private Coordinator coordinator;
	
	public LocalSearch(int city, Solution newSolution, Instance newInstance, Coordinator newCoordinator) {
		currentSolution = newSolution;
		currentCity = city;
		instance = newInstance;
		coordinator = newCoordinator;
	}
	
	@Override
	public Solution call() {
		Solution bestSolution = currentSolution.clone();
		for (int city : currentSolution) {
			if (currentCity != city)
				currentSolution.relocate(currentCity, city);
				currentSolution.setOF(TSPCostCalculator.calcOF(instance, currentSolution));
			if (currentSolution.getOF()<bestSolution.getOF())
				bestSolution = currentSolution.clone();
			if (!coordinator.keepRunning())
				break;
		}
		return bestSolution;
	}
	
}
