package reverseEngineering;

public class Column {

	private String name;
	
	private String type;
	
	private int index;
	
	private boolean unique;
	
	private boolean nullable;
	
	private boolean pk = false;
	
	private boolean fk = false;
	
	private String fkReference;

	public Column(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isPk() {
		return pk;
	}

	public void setPk(boolean pk) {
		this.pk = pk;
	}

	public boolean isFk() {
		return fk;
	}

	public void setFk(boolean fk) {
		this.fk = fk;
	}

	public String getFkReference() {
		return fkReference;
	}

	public void setFkReference(String fkReference) {
		this.fkReference = fkReference;
	}
}
