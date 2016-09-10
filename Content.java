import java.io.*;
import java.lang.*;
import java.util.*;

class Content implements Serializable {
	private String filename;
	private double x, y;
	String data;


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public Content(double x_cor, double y_cor){
		this.x = x_cor;
		this.y = y_cor;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void readFile(String fileName) throws IOException {
		this.filename =(new File(fileName)).getName();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			data = sb.toString();
		} finally {
			br.close();
		}
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getXPos(){
		return x;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getYPos(){
		 return y;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getFilename(){
		return filename;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getData(){
		return data;
	}
}