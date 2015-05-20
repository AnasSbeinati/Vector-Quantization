package quantizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.plaf.metal.MetalFileChooserUI.FilterComboBoxRenderer;

public class QuantFuns {
	public static int[][]compress(String imagDir,String des,int codeBookLength,int vectorSize,Inte heightx,Inte widthx) {
		//read into 2D Array
		int[][]pixels=ImageRW.readImage(imagDir);
		int height=heightx.x=ImageRW.height;
		int width=widthx.x=ImageRW.width;
		
		//rearrange the data
		ArrayList<Vector>vectoredPhoto=getVecoredPhoto(pixels, vectorSize, height, width);
		//construct code Book
		ArrayList<Vector>codeBook=getCodeBook(vectoredPhoto, codeBookLength, vectorSize);
		//compress the image
		String[]compressedPhoto=new String[vectoredPhoto.size()];
		int i=0;
		for (Vector vector : vectoredPhoto) {
			compressedPhoto[i]=codeBook.get(getCodeBookVecor(codeBook, vector)).getCode();
            //System.out.println(codeBook.get(getCodeBookVecor(codeBook, vector)).getCode());
			i++;
		}
		//save to file
		try {
			FileOutputStream file=new FileOutputStream(new File(des));
			ObjectOutputStream oos=new ObjectOutputStream(file);
			oos.writeInt(height);
			oos.writeInt(width);
			oos.writeInt(vectorSize);
			oos.writeInt(codeBookLength);
			for (Vector vector : codeBook) {
				for (Integer integer : vector.getVector()) {
					oos.writeInt(integer);
				}
			}
			FileRW.write(oos, compressedPhoto);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File not open");
		}
		return pixels;
	}
	private static  ArrayList<Vector>getVecoredPhoto(int[][]pixels,int vectorSize,int height,int width) {
		ArrayList<Vector>photo=new ArrayList<>();
		for (int i = 0; i < height; i++) {
			int j = 0;
			while(j < width) {
				Vector vector=new Vector(vectorSize);
				ArrayList<Integer>temp=new ArrayList<>();
				int j2 = j;
			    for (; j2 <j+vectorSize; j2++) {
			    	if(j2<width)
			    		temp.add(pixels[i][j2]);
			    	else
			    		temp.add(500);
				}
				j=j2;
				vector.setVector(temp);
				photo.add(vector);
			}
		}
		return photo;
	}
    private static ArrayList<Vector>getCodeBook(ArrayList<Vector>photo,int codeBookLength,int vectorSize){
		ArrayList<Vector>codeBook=makeCodeBook(photo, codeBookLength, vectorSize);
		setCode(codeBook, codeBookLength);
		return codeBook;
	}
    private static Vector getAverageVector(ArrayList<Vector> list,int vectorSize) {
    	Vector vector=new Vector(vectorSize);
    	ArrayList<Integer>temp=new ArrayList<>();
    	for (int i=0;i<vectorSize;i++) {
			temp.add(0);
		}
    	for (Vector vector1 : list) {
			for (int i = 0; i < vectorSize; i++) {
				int ee=vector1.getVector().get(i);
				temp.set(i,temp.get(i)+ee);
			}
		}
    	for (int i = 0; i < vectorSize; i++) {
			temp.set(i,temp.get(i)/list.size());
		}
    	vector.setVector(temp);
    	return vector;
    }
    private static int getTowVectorMSE(Vector v1,Vector v2) {
    	int MSE=0,i=0;
    	for (Integer integer : v1.getVector()) {
			MSE+=Math.pow(integer-v2.getVector().get(i), 2);
			i++;
		}
    	return MSE/v1.getVector().size();
    }
    private static int getCodeBookVecor(ArrayList<Vector>codeBook,Vector vector) {
    	int vecNum=0,MSE=0,minMSE=10000000,i=-1;
    	for (Vector vector2 : codeBook) {
			MSE=getTowVectorMSE(vector2, vector);
			i++;
			if(MSE<minMSE) {
				minMSE=MSE;
				vecNum=i;
			}
		}
    	return vecNum;
    }
    private static ArrayList<Vector>subVecor(Vector vector) {
    	ArrayList<Vector>newVectors=new ArrayList<>();
    	Vector v1=new Vector(vector.getVector().size()),v2=new Vector(vector.getVector().size());
    	for (Integer integer : vector.getVector()) {
			if(integer!=0)
				v1.getVector().add(integer-1);
			else
				v1.getVector().add(0);
			if(integer!=255)
				v2.getVector().add(integer+1);
			else
				v2.getVector().add(255);
		}
    	newVectors.add(0,v1);
    	newVectors.add(1,v2);
    	return newVectors;
    }
    private static ArrayList<Vector>makeCodeBook(ArrayList<Vector>photo,int codeBookLength,int vectorSize) {
    	ArrayList<Vector>codeBook=new ArrayList<>();
    	QunTree tree=new QunTree();
    	double D=Math.log10(codeBookLength)/Math.log10(2);
    	int treeDgree=(int)Math.ceil(D);
    	Vector vector=getAverageVector(photo, vectorSize);
    	tree.setCodeBookVec(vector);
    	for (Vector vector1 : photo) {
    		tree.setPhoto(vector1);
		}
    	for (int i = 1; i <= treeDgree; i++) {
    		ArrayList<Vector>newLeafsVectors=new ArrayList<>();
			dividLeafs(tree,newLeafsVectors);
			ArrayList<QunTree>finalLeafs=getFinalLeafs(photo, newLeafsVectors);
			if(i==treeDgree) {
				for (QunTree qunTree : finalLeafs) {
					codeBook.add(qunTree.getCodeBookVec());
				}
				break;
			}
			Inte inte=new Inte();
			inte.x=0;
			setFinalTree(tree, finalLeafs, inte);
		}
    	return codeBook;
    }
    private static void dividLeafs(QunTree tree,ArrayList<Vector>list) {
    	if(tree.LN==null &&tree.RN==null) {
    		ArrayList<Vector> subVectors=subVecor(tree.getCodeBookVec());
    		tree.LN=new QunTree();
    		tree.RN=new QunTree();
    		list.addAll(subVectors);
    		tree.LN.setCodeBookVec(subVectors.get(0));
    		tree.RN.setCodeBookVec(subVectors.get(1));
    	}
    	else {
    		if(tree.LN!=null)
    			dividLeafs(tree.LN,list);
    		if(tree.RN!=null)
    			dividLeafs(tree.RN,list);
    	}
    }
    private static ArrayList<QunTree>getFinalLeafs(ArrayList<Vector>photo,ArrayList<Vector>list) {
    	ArrayList<QunTree>leafs=new ArrayList<>();
    	for (Vector vector : list) {
			QunTree tree=new QunTree();
			tree.setCodeBookVec(vector);
			leafs.add(tree);
		}
    	for (Vector vector : photo) {
			int index=getCodeBookVecor(list, vector);
			leafs.get(index).setPhoto(vector);
		}
    	for (QunTree qunTree : leafs) {
    		ArrayList<Vector> temp=qunTree.getPhoto();
    		if(temp.size()!=0)
			     qunTree.setCodeBookVec(getAverageVector(temp,list.get(0).getVector().size()));
		}
    	return leafs;
    }
    private static void setFinalTree(QunTree tree,ArrayList<QunTree>leafs,Inte i) {
    	if(tree.LN==null &&tree.RN==null) {
    		tree=leafs.get(i.x);
    		i.x++;
    	}
    	else {
    		if(tree.LN!=null)
    			setFinalTree(tree.LN,leafs,i);
    		if(tree.RN!=null)
    			setFinalTree(tree.RN,leafs,i);
    	}
    }
    private static void setCode(ArrayList<Vector>codeBook,int codeBookLength) {
    	int codeLength=(int)Math.ceil(Math.log10(codeBookLength)/Math.log10(2));
    	for (int i = 0; i < codeBook.size(); i++) {
			String code=Integer.toBinaryString(i);
			if(code.length()<codeLength) {
				String d="";
				for (int j = code.length(); j < codeLength; j++) {
					d+="0";
				}
				code=d+code;
			}
			codeBook.get(i).setCode(code);
		}
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static int[][]deCompress(String file,String newImage) {
    	int[][] pixles;
    	try {
			FileInputStream fis=new FileInputStream(new File(file));
			ObjectInputStream ois=new ObjectInputStream(fis);
			int height,width,vectorSize,codeBookLength;
			height=ois.readInt();
			width=ois.readInt();
			vectorSize=ois.readInt();
			codeBookLength=ois.readInt();
			ArrayList<Vector>codeBook=new ArrayList<>();
			for (int i = 0; i < codeBookLength; i++) {
				Vector vector=new Vector(vectorSize);
				ArrayList<Integer> list= new ArrayList<>();
				for (int j = 0; j < vectorSize; j++) {
					list.add(ois.readInt());
				}
				vector.setVector(list);
				codeBook.add(vector);
			}
			setCode(codeBook, codeBookLength);
			pixles=FileRW.read(ois, codeBook, codeBookLength, vectorSize, height, width);
			ImageRW.writeImage(pixles, newImage);
		} catch (Exception e) {}
    	return null;
    }
    static public int MSE(int[][]pixels,int[][]pixelsComp,Inte height,Inte width) {
    	int comu=0;
    	for (int i = 0; i < height.x; i++) {
			for (int j = 0; j < width.x; j++) {
				comu+=Math.pow(pixelsComp[i][j]-pixels[i][j], 2);
			}
		}
    	int MSE=comu/(height.x*width.x);
    	System.out.println("The MSE is "+MSE);
    	return MSE;
    }
}
