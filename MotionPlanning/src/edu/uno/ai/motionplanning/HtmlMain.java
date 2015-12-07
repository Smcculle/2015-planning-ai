package edu.uno.ai.motionplanning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.uno.ai.motionplanning.Heuristics.Euclidean;
import edu.uno.ai.motionplanning.Planners.*;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Result;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.table.*;

public class HtmlMain {

	public static void main(String[] args) throws Exception {
		// Test each planner on each problem.
		Table<Scenario, MotionPlanner, MotionResults> results = new Table<>();
		ScenarioLoader sl = new ScenarioLoader(new File("./"), new File("scenarios/"));
		List<Scenario> complete = sl.loadAllScenarios();
		boolean first=true;
		MotionPlanner[] firstPlanners=null;
		for(Scenario scenario : complete) {
			results.addRow(scenario);
			MotionPlanner mp[]=new MotionPlanner[2];
			mp[0]=new AStar(scenario,new Euclidean());
			mp[1]=new AnytimeDStar(scenario, new Euclidean(), 1, true);
			if (first){
				for(MotionPlanner planner : mp) {
					results.addColumn(planner);
				}
				firstPlanners=mp;
				first=false;
			}
			for (int i=0;i<mp.length;i++){
				mp[i].run();
				results.set(scenario, firstPlanners[i], mp[i].getResult());
				System.out.println(mp[i].toResultsString());
			}
		}
		// Create output file.
		BufferedWriter out = new BufferedWriter(new FileWriter("results.html"));
		out.write("<html>\n<head>\n<title>Planner Benchmark Results</title>");
		out.write("\n<style>\ntable { border-collapse: collapse; }\ntable, tr, th, td { border: 1px solid black; }\ntr:nth-child(odd) { background-color: lightgray; }\nth { font-weight: bold; }\ntd { text-align: right; }\n</style>");
		out.write("\n</head>\n<body>\n<h1>Planner Benchmark Results</h1>");
		// Problems solved
		out.write("\n<h2>Solution Cost</h2>");
		Table<String, String, Double> solved = results.transform(problem -> problem.getName(), planner -> planner.getPlannerName(),result->(double)result.getSolutionCost());
		addTotals(solved);
		solved.sortColumns(new Comparator<Column<String, Double>>(){
			@Override
			public int compare(Column<String, Double> c1, Column<String, Double> c2) {
				if(c1.label.equals("Total"))
					return 1;
				else if(c2.label.equals("Total"))
					return -1;
				else
					return (int) (solved.get("Total", c2.label) - solved.get("Total", c1.label));
			}
		});
		Table<String, String, Integer> solvedInt = solved.transform(problem -> problem, planner -> planner, s -> s.intValue());
		out.write(solvedInt.toString());
		// Nodes visited
		out.write("\n<h2>Nodes Visited</h2>");
		Table<String, String, Double> visited = results.transform(problem -> problem.getName(), planner -> planner.getPlannerName(), result -> (double) result.getVisited());
		addAverages(visited);
		Table<String, String, Integer> visitedRounded = visited.transform(problem -> problem, planner -> planner, v -> (int) Math.round(v));
		visitedRounded.sortColumns(new Comparator<Column<String, Integer>>(){
			@Override
			public int compare(Column<String, Integer> c1, Column<String, Integer> c2) {
				if(c1.label.equals("Average"))
					return 1;
				else if(c2.label.equals("Average"))
					return -1;
				else
					return (int) (visited.get("Average", c1.label) - visited.get("Average", c2.label));
			}
		});
		out.write(visitedRounded.toString());
		// Nodes expanded
		out.write("\n<h2>Nodes Expanded</h2>");
		Table<String, String, Double> expanded = results.transform(problem -> problem.getName(), planner -> planner.getPlannerName(), result -> (double) result.getExpanded());
		addAverages(expanded);
		Table<String, String, Integer> expandedRounded = expanded.transform(problem -> problem, planner -> planner, v -> (int) Math.round(v));
		expandedRounded.sortColumns(new Comparator<Column<String, Integer>>(){
			@Override
			public int compare(Column<String, Integer> c1, Column<String, Integer> c2) {
				if(c1.label.equals("Average"))
					return 1;
				else if(c2.label.equals("Average"))
					return -1;
				else
					return (int) (expanded.get("Average", c1.label) - expanded.get("Average", c2.label));
			}
		});
		out.write(expandedRounded.toString());
		// Time
		out.write("\n<h2>Time (ns)</h2>");
		Table<String, String, Double> time = results.transform(problem -> problem.getName(), planner -> planner.getPlannerName(), result -> (double) result.getTime());
		addAverages(time);
		time.sortColumns(new Comparator<Column<String, Double>>(){
			@Override
			public int compare(Column<String, Double> c1, Column<String, Double> c2) {
				if(c1.label.equals("Average"))
					return 1;
				else if(c2.label.equals("Average"))
					return -1;
				else
					return (int) (time.get("Average", c1.label) - time.get("Average", c2.label));
			}
		});
		Table<String, String, Integer> timeRounded = time.transform(problem -> problem, planner -> planner, t -> (int) Math.round(t));
		out.write(timeRounded.toString());
		// Final rankings
		out.write("\n<h2>Final Ranking</h2>");
		results.sortColumns(new Comparator<Column<MotionPlanner, MotionResults>>(){
			@Override
			public int compare(Column<MotionPlanner, MotionResults> c1, Column<MotionPlanner, MotionResults> c2) {
				double comparison = solved.get("Total", c2.label.getPlannerName()) - solved.get("Total", c1.label.getPlannerName());
				if(comparison == 0)
					comparison = visited.get("Average", c1.label.getPlannerName()) - visited.get("Average", c2.label.getPlannerName());
				if(comparison == 0)
					comparison = expanded.get("Average", c1.label.getPlannerName()) - expanded.get("Average", c2.label.getPlannerName());
				if(comparison == 0)
					comparison = time.get("Average", c1.label.getPlannerName()) - time.get("Average", c2.label.getPlannerName());
				return (int) comparison;
			}
		});
		out.write("\n<ol>");
		for(Column<MotionPlanner, MotionResults> column : results.columns())
			out.write("\n<li>" + column.label.getPlannerName() + "</li>");
		out.write("\n<ol>");
		// Close output file.
		out.write("\n</body>");
		out.close();
	}
	
	private static void addTotals(Table<String, String, Double> table) {
		HashMap<String, Double> rowTotals = new HashMap<>();
		for(Row<String, Double> row : table.rows())
			rowTotals.put(row.label, row.sum());
		HashMap<String, Double> columnTotals = new HashMap<>();
		for(Column<String, Double> column : table.columns())
			columnTotals.put(column.label, column.sum());
		table.addRow("Total");
		table.addColumn("Total");
		for(Row<String, ?> row : table.rows())
			table.set(row.label, "Total", rowTotals.get(row.label));
		for(Column<String, ?> column : table.columns())
			table.set("Total", column.label, columnTotals.get(column.label));
	}
	
	private static void addAverages(Table<String, String, Double> table) {
		HashMap<String, Double> rowAverages = new HashMap<>();
		for(Row<String, Double> row : table.rows())
			rowAverages.put(row.label, row.average());
		HashMap<String, Double> columnAverages = new HashMap<>();
		for(Column<String, Double> column : table.columns())
			columnAverages.put(column.label, column.average());
		table.addRow("Average");
		table.addColumn("Average");
		for(Row<String, Double> row : table.rows())
			table.set(row.label, "Average", rowAverages.get(row.label));
		for(Column<String, Double> column : table.columns())
			table.set("Average", column.label, columnAverages.get(column.label));
	}
}
