package edu.uno.ai.planning.jjblackbox;

import edu.uno.ai.planning.Step;

public class PlanGraphEncodingModel {
    public Step step;
    public int time;
    public PlanGraphEncodingModel(Step step, int time){
        this.step = step;
        this.time = time;
    }
}
