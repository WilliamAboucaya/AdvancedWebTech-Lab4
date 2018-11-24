package reverseEngineering;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Table {

	private String name;
	
	private SortedSet<Column> columns;
	
	public Table(String name) {
		this.name = name;
		columns = new TreeSet<>(Comparator.comparing(Column::getIndex));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SortedSet<Column> getColumns() {
		return columns;
	}

	public void setColumns(SortedSet<Column> columns) {
		this.columns = columns;
	}
	
	public void addColumn(Column column) {
		columns.add(column);
	}
	
	public Column getColumnByIndex(int index) {
		for (Column column : columns) {
			if (column.getIndex() == index) return column;
		}
		throw new IndexOutOfBoundsException("This table has no column for index " + index);
	}
	
	public Column getColumnByName(String name) {
		for (Column column : columns) {
			if (column.getName().equals(name)) return column;
		}
		throw new IndexOutOfBoundsException("This table has no column named " + name);
	}
	
	public Column getPrimaryKey() {
		for (Column column : columns) {
			if (column.isPk()) return column;
		}
		return null;
	}
	
	public String toTableSqlString() {
		StringBuilder tableScript = new StringBuilder("CREATE TABLE ");
		tableScript .append(getName())
					.append(" (");
		
		for (Column column : getColumns()) {
			tableScript.append(column.toSqlString());
		}
		
		Column primary = getPrimaryKey();
		
		if (primary != null) {
			tableScript .append("\nPRIMARY KEY (")
						.append(primary.getName())
						.append("),");
		}
		
		tableScript.delete(tableScript.length() - 1, tableScript.length());
		tableScript.append("\n);\n");
		
		return(tableScript.toString());
	}
	
	public String toSqlFKScript() {
		StringBuilder foreignKeysScript = new StringBuilder("");
		
		for (Column column : getColumns()) {
			if (column.isFk()) {
				foreignKeysScript.append("ALTER TABLE ")
								 .append(getName())
								 .append(" ADD FOREIGN KEY (")
								 .append(column.getName())
								 .append(") REFERENCES ")
								 .append(column.getFkReference())
								 .append(";\n\n");
			}
		}
		
		return(foreignKeysScript.toString());
	}
}
