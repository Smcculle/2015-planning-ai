package edu.uno.ai.planning.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Table<R, C, V> {

	final class Cell {
		
		public final Row<R, V> row;
		public final Column<C, V> column;
		public V value = null;
		
		Cell(Row<R, V> row, Column<C, V> column) {
			this.row = row;
			this.column = column;
		}
	}
	
	final ArrayList<Row<R, V>> rows = new ArrayList<>();
	final ArrayList<Column<C, V>> columns = new ArrayList<>();
	final ArrayList<Cell> cells = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public Iterable<Row<R, V>> rows() {
		return (Iterable<Row<R, V>>) rows.clone();
	}
	
	public void addRow(R label) {
		Row<R, V> row = new Row<R, V>(this, label);
		rows.add(row);
		for(Column<C, V> column : columns)
			cells.add(new Cell(row, column));
	}
	
	public Row<R, V> getRow(R row) {
		for(Row<R, V> r : rows)
			if(r.label.equals(row))
				return r;
		throw new IllegalArgumentException("This table does not have a row for \"" + row + "\".");
	}
	
	public void sortRows(Comparator<Row<R, V>> comparator) {
		rows.sort(comparator);
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Column<C, V>> columns() {
		return (Iterable<Column<C, V>>) columns.clone();
	}
	
	public void addColumn(C label) {
		Column<C, V> column = new Column<C, V>(this, label);
		columns.add(column);
		for(Row<R, V> row : rows)
			cells.add(new Cell(row, column));
	}
	
	public Column<C, V> getColumn(C column) {
		for(Column<C, V> c : columns)
			if(c.label.equals(column))
				return c;
		throw new IllegalArgumentException("This table does not have a column for \"" + column + "\".");
	}
	
	public void sortColumns(Comparator<Column<C, V>> comparator) {
		columns.sort(comparator);
	}
	
	final Cell getCell(Object row, Object column) {
		for(Cell cell : cells)
			if(cell.row.label.equals(row) && cell.column.label.equals(column))
				return cell;
		throw new IllegalArgumentException("This table does not have an entry for row \"" + row + "\" and column \"" + column + "\".");
	}
	
	public V get(R row, C column) {
		return getCell(row, column).value;
	}
	
	public void set(R row, C column, V value) {
		getCell(row, column).value = value;
	}
	
	public <RO, CO, VO> Table<RO, CO, VO> transform(Function<R, RO> rowTransform, Function<C, CO> columnTransform, Function<V, VO> valueTransform) {
		Table<RO, CO, VO> newTable = new Table<>();
		LinkedHashMap<C, CO> newColumns = new LinkedHashMap<>();
		for(Row<R, V> row : rows) {
			RO newRow = rowTransform.apply(row.label);
			newTable.addRow(newRow);
			for(Column<C, V> column : columns) {
				CO newColumn = newColumns.get(column.label);
				if(newColumn == null) {
					newColumn = columnTransform.apply(column.label);
					newColumns.put(column.label, newColumn);
					newTable.addColumn(newColumn);
				}
				V value = get(row.label, column.label);
				VO newValue = value == null ? null : valueTransform.apply(value);
				newTable.set(newRow, newColumn, newValue);
			}
		}
		return newTable;
	}
	
	@Override
	public String toString() {
		String str = "<table>\n<tr><th></th>";
		for(Column<C, V> column : columns)
			str += "<th>" + column.label + "</th>";
		str += "</tr>";
		for(Row<R, V> row : rows) {
			str += "\n<tr><th>" + row.label + "</th>";
			for(V value : row)
				str += "<td>" + (value == null ? "" : value) + "</td>";
			str += "</tr>";
		}
		str += "\n</table>";
		return str;
	}
}
