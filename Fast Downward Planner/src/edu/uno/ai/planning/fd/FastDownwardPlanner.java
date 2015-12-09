package edu.uno.ai.planning.fd;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.fd.FDSearch;

public class FastDownwardPlanner extends Planner<FDSearch>{

	public FastDownwardPlanner(){
		super("FD");
	}
	
	@Override
	protected FDSearch makeSearch(Problem problem){
		MPTTranslator translator = new MPTTranslator(problem);
		return new FDSearch(translator.newProblem);
	}
}
