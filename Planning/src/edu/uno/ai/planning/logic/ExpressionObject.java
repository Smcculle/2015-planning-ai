package edu.uno.ai.planning.logic;

/**
 * An implementation of
 * {@link edu.uno.ai.planning.logic.Expression} which provides
 * a default implementation of {@link java.lang.Object#equals(Object)}.
 * 
 * @author Stephen G. Ware
 */
public abstract class ExpressionObject implements Expression {

	@Override
	public boolean equals(Object other) {
		if(other instanceof Formula)
			return equals((Formula) other, Substitution.EMPTY);
		else
			return false;
	}
}
