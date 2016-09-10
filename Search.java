import java.io.*;

class Search implements Serializable {
	private double x_cor, y_cor;
	private String filename;
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	Search(double x, double y, String fileName){
		this.x_cor = x;
		this.y_cor = y;
		this.filename = fileName;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getXPos(){
		return x_cor;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getYPos(){
		return y_cor;
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
}