package edu.uno.ai.planning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Result;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.table.*;

public class Main {

	public static void main(String[] args) throws Exception {
		// Test each planner on each problem.
		Table<Problem, Planner<?>, Result> results = new Table<>();
		for(Planner<?> planner : TestSuite.PLANNERS)
			results.addColumn(planner);
		for(Benchmark benchmark : TestSuite.BENCHMARKS) {
		// for (int counterrrr = 0; counterrrr <= 0; counterrrr++){
			Problem problem = benchmark.getProblem();

			results.addRow(problem);
			for(Planner<?> planner : TestSuite.PLANNERS) {
				Result result = planner.findSolutuion(problem, TestSuite.NODE_LIMIT, TestSuite.TIME_LIMIT);
				results.set(problem, planner, result);
				System.out.println(result);
				if(result.success)
					for(Step step : result.solution)
						System.out.println("  " + step);
			}
		}
		// Create output file.
		BufferedWriter out = new BufferedWriter(new FileWriter("benchmarks/results.html"));
		out.write("<html>\n<head>\n<title>Planner Benchmark Results</title>");
		out.write("\n<style>\ntable { border-collapse: collapse; }\ntable, tr, th, td { border: 1px solid black; }\ntr:nth-child(odd) { background-color: lightgray; }\nth { font-weight: bold; }\ntd { text-align: right; }\n</style>");
		out.write("\n</head>\n<body>\n<h1>Planner Benchmark Results</h1>");
		// Problems solved
		out.write("\n<h2>Problems Solved</h2>");
		Table<String, String, Double> solved = results.transform(problem -> problem.name, planner -> planner.name, result -> result.success ? 1.0 : 0.0);
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
		Table<String, String, Double> visited = results.transform(problem -> problem.name, planner -> planner.name, result -> (double) result.visited);
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
		Table<String, String, Double> expanded = results.transform(problem -> problem.name, planner -> planner.name, result -> (double) result.expanded);
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
		out.write("\n<h2>Time (ms)</h2>");
		Table<String, String, Double> time = results.transform(problem -> problem.name, planner -> planner.name, result -> (double) result.time);
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
		results.sortColumns(new Comparator<Column<Planner<?>, Result>>(){
			@Override
			public int compare(Column<Planner<?>, Result> c1, Column<Planner<?>, Result> c2) {
				double comparison = solved.get("Total", c2.label.name) - solved.get("Total", c1.label.name);
				if(comparison == 0)
					comparison = visited.get("Average", c1.label.name) - visited.get("Average", c2.label.name);
				if(comparison == 0)
					comparison = expanded.get("Average", c1.label.name) - expanded.get("Average", c2.label.name);
				if(comparison == 0)
					comparison = time.get("Average", c1.label.name) - time.get("Average", c2.label.name);
				return (int) comparison;
			}
		});
		out.write("\n<ol>");
		for(Column<Planner<?>, Result> column : results.columns())
			out.write("\n<li>" + column.label.name + "</li>");
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
