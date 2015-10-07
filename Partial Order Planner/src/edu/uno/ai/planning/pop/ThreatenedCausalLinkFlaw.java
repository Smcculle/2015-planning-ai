package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;

public class ThreatenedCausalLinkFlaw implements Flaw {

	public final CausalLink link;
	public final Step threat;
	
	public ThreatenedCausalLinkFlaw(CausalLink link, Step threat) {
		this.link = link;
		this.threat = threat;
	}
	
	@Override
	public String toString() {
		return toString(Bindings.EMPTY);
	}

	@Override
	public String toString(Substitution substitution) {
		return threat.toString(substitution) + " threatens " + link.toString(substitution);
	}
}
