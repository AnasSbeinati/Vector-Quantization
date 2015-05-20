package quantizer;

import java.util.ArrayList;

public class Vector {
	private ArrayList<Integer>vector;
	private String code;
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Vector(int vectorSize) {
		super();
		this.vector = new ArrayList<Integer>(vectorSize);
	}

	public ArrayList<Integer> getVector() {
		return vector;
	}

	public void setVector(ArrayList<Integer> vector) {
		this.vector = vector;
	}
	
}
