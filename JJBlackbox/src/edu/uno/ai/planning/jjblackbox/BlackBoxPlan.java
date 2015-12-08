package edu.uno.ai.planning.jjblackbox;


import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public class BlackBoxPlan extends Planner {

    public BlackBoxPlan() {
        super("Blackbox Jonathan & Janak");
    }

    @Override
    protected Search makeSearch(Problem problem) {
        return new BlackboxSearch(problem);
    }
}
