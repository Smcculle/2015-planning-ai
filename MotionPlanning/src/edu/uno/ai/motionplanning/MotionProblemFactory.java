package edu.uno.ai.motionplanning;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.*;
import java.util.*;
public class MotionProblemFactory {
	public static Problem generateMotionProblem(Scenario s) {
		String name = s.getMap().getName();
		Variable[] params = new Variable[2];
		Variable from = params[0] = new Variable("location", "from");
		Variable to = params[1] = new Variable("location", "to");
		Predication atFrom = new Predication("at", from);
		Expression preconditions = new Conjunction(atFrom, new Predication("clear", to),
				new Predication("adjacent", from, to));
		Expression effects = new Conjunction(new NegatedLiteral(atFrom), new Predication("at", to));
		GridMap m = s.getMap();
		ArrayList<Step> steps=new ArrayList<>();
		ArrayList<Literal> literals=new ArrayList<>();
		Constant[] constants = new Constant[m.getWidth() * m.getHeight()];
		for (int y = 0; y < m.getHeight(); y++) {
			for (int x = 0; x < m.getWidth(); x++) {
				constants[y * m.getWidth() + x] = new Constant("location", y + "," + x);
			}
		}
		Constant start = constants[s.getStart().y * m.getWidth() + s.getStart().x];
		Constant end = constants[s.getEnd().y * m.getWidth() + s.getEnd().x];

		Operator move = new Operator("move", new ImmutableArray<Variable>(params), preconditions, effects);
		Expression goal = new Predication("at", end);

		Domain d = new Domain("motion", new Constant[0], move);
		MutableState initial = new MutableState();
		initial.impose(new Predication("at", start));
		for (int y = 0; y < m.getHeight(); y++) {
			for (int x = 0; x < m.getWidth(); x++) {
				if (m.isClear(s, y, x)) {
					Constant tile = constants[y * m.getWidth() + x];
					initial.impose(new Predication("clear",tile));
					literals.add(new Predication("at",tile));
					literals.add(new Predication("clear",tile));
					for (int dy = -1; dy <= 1; dy++) {
						for (int dx = -1; dx <= 1; dx++) {
							if (dx == 0 && dy == 0) {
								continue;
							}
							int newX = x + dx;
							int newY = y + dy;
							if (newX < 0 || newX >= m.getWidth() || newY < 0 || newY >= m.getHeight()) {
								continue;
							}
							Constant adjTile = constants[newY * m.getWidth() + newX];
							initial.impose(new Predication("adjacent", tile, adjTile));
							literals.add(new Predication("adjacent", tile, adjTile));
							HashSubstitution hs=new HashSubstitution();
							hs.set(from,tile);
							hs.set(to, adjTile);
							steps.add(move.makeStep(hs));
						}
					}
				}
			}
		}
		Problem p=new Problem(name, d, new ImmutableArray<Constant>(constants), initial, goal);
		return new StateSpaceProblem(p, new ImmutableArray<Step>(steps.toArray(new Step[steps.size()])), new ImmutableArray<Literal>(literals.toArray(new Literal[literals.size()])));
	}

}
