package quantizer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileRW {
	public static void write(ObjectOutputStream oos,String[]compressedPhoto) {
		String str="";
		int fileSize=(int)Math.ceil((compressedPhoto.length*compressedPhoto[0].length())/31);
		try {oos.writeInt(fileSize);} catch (IOException e) {}
		for (String string : compressedPhoto) {
			str+=string;
			//System.out.println(string);
			if(str.length()>=31) {
				String temp=str.substring(0,31);
				int inte=Integer.parseInt(temp,2);
				try {oos.writeInt(inte);} catch (IOException e) {
					System.out.println("Ex");
				}
				if(str.length()==31)
					str="";
				else
				    str=str.substring(31, str.length());
			}
		}
		if(str!="") {
			String dd="";
			for (int i = str.length(); i < 31; i++) {
				dd+="0";
			}
			str=dd+str;
			int inte=Integer.parseInt(str,2);
			try {oos.writeInt(inte);} catch (IOException e) {}
		}
	}
    public static int[][]read(ObjectInputStream ois,ArrayList<Vector>codeBook,int codeBookLength,int vectorSize,int height,int width) {
    	int[][]pixels=new int[height][width];
    	System.out.println("------------------------------------------------");
    	int fileSize=0,currentFileSize=0;
		try {fileSize = ois.readInt();} catch (IOException e1) {}
    	ArrayList<Vector>vectorPhoto=new ArrayList<>();
    	int codeLength=(int)Math.ceil(Math.log10(codeBookLength)/Math.log10(2));
    	String intToStr="";
    	HashMap<String,Vector>tableCodeBook=new HashMap<>();
    	for (Vector vector : codeBook) {
			tableCodeBook.put(vector.getCode(), vector);
		}
    	while (currentFileSize<fileSize) {
			try {
				int num=ois.readInt();
				currentFileSize+=4;
				String temp=Integer.toBinaryString(num);
				if(temp.length()<31){
					String d="";
					for (int j = temp.length(); j < 31; j++) {
						d+="0";
					}
					temp=d+temp;
				}
				intToStr+=temp;
				while(intToStr.length()>=codeLength)
				{
					vectorPhoto.add(tableCodeBook.get(intToStr.substring(0, codeLength)));
					//System.out.println(intToStr.substring(0, codeLength));
					if(intToStr.length()==codeLength)
						intToStr="";
					else
					    intToStr=intToStr.substring(codeLength, intToStr.length());
				}
				} catch (IOException e) {
					System.out.println("Ex");
				}
		}
    	int i=0,j=0;
    	for (Vector vector : vectorPhoto) {
			for (Integer integer : vector.getVector()) {
				if(integer!=500){
					pixels[i][j]=integer;
				    j++;
				    if(j==width) {
				    	j=0;
					    i++;
					    if(i==height)
					    	return pixels;
					}
				}
			}
		}
    	return pixels;
    }
}
