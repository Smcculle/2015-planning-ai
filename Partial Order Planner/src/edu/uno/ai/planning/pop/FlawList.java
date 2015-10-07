package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.ImmutableList;

public class FlawList {

	private final ImmutableList<Flaw> flaws;
	
	private FlawList(ImmutableList<Flaw> flaws) {
		this.flaws = flaws;
	}
	
	FlawList(Step end) {
		ImmutableList<Flaw> flaws = new ImmutableList<>();
		for(Literal precondition : end.preconditions)
			flaws = flaws.add(new OpenPreconditionFlaw(end, precondition));
		this.flaws = flaws;
	}
	
	public int size() {
		return flaws.length;
	}
	
	public Flaw chooseFlaw() {
		return flaws.first;
	}
	
	public FlawList add(Flaw flaw) {
		return new FlawList(flaws.add(flaw));
	}
	
	public FlawList remove(Flaw flaw) {
		return new FlawList(remove(flaw, flaws));
	}
	
	private static final ImmutableList<Flaw> remove(Flaw flaw, ImmutableList<Flaw> flaws) {
		if(flaws.length == 0)
			return flaws;
		else if(flaws.first == flaw)
			return flaws.rest;
		else {
			ImmutableList<Flaw> rest = remove(flaw, flaws.rest);
			if(rest == flaws.rest)
				return flaws;
			else
				return rest.add(flaws.first);
		}
	}
}
