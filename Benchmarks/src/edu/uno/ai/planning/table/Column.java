package edu.uno.ai.planning.table;

import java.util.ArrayList;
import java.util.Iterator;

public class Column<C, V> extends Sequence<C, V> {

	private final Table<?, C, V> table;
	
	Column(Table<?, C, V> table, C label) {
		super(label);
		this.table = table;
	}
	
	@Override
	public int size() {
		return table.rows.size();
	}

	@Override
	public Iterator<V> iterator() {
		ArrayList<V> values = new ArrayList<>();
		for(Row<?, V> row : table.rows)
			values.add(table.getCell(row.label, label).value);
		return values.iterator();
	}
}
