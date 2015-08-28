package edu.uno.ai.planning.table;

import java.util.ArrayList;
import java.util.Iterator;

public class Row<R, V> extends Sequence<R, V> {

	private final Table<R, ?, V> table;
	
	Row(Table<R, ?, V> table, R label) {
		super(label);
		this.table = table;
	}
	
	@Override
	public int size() {
		return table.columns.size();
	}

	@Override
	public Iterator<V> iterator() {
		ArrayList<V> values = new ArrayList<>();
		for(Column<?, V> column : table.columns)
			values.add(table.getCell(label, column.label).value);
		return values.iterator();
	}
}
