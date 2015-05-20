package quantizer;

import java.util.ArrayList;

public class QunTree {
	Vector codeBookVec;
	ArrayList<Vector>photo;
	public QunTree() {
		photo=new ArrayList<>();
	}
	public Vector getCodeBookVec() {
		return codeBookVec;
	}
	public void setCodeBookVec(Vector codeBookVec) {
		this.codeBookVec = codeBookVec;
	}
	public ArrayList<Vector> getPhoto() {
		return photo;
	}
	public void setPhoto(Vector vector) {
		this.photo.add(vector);
	}
	public QunTree LN;
	public QunTree RN;
}
