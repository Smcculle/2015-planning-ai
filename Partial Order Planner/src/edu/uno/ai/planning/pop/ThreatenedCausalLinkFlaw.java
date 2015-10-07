package edu.uno.ai.planning.pop;

public class ThreatenedCausalLinkFlaw implements Flaw {

	public final CausalLink link;
	public final Step threat;
	
	public ThreatenedCausalLinkFlaw(CausalLink link, Step threat) {
		this.link = link;
		this.threat = threat;
	}
}
