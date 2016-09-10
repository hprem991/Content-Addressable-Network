import java.lang.*;
import java.io.*;

class Zone implements Serializable {
	private double min_x, max_x, min_y, max_y;
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	Zone(){
		min_x = 0.0;
		max_x = 0.0;
		min_y = 0.0;
		max_y = 0.0;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void setZone(double x_min, double x_max, double y_min, double y_max){
		min_x = x_min;
		max_x = x_max;
		min_y = y_min;
		max_y = y_max;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void zone(){
		System.out.println("Min-x "+min_x+" Max-x "+max_x+" Min-y "+min_y+" Max-y "+max_y);
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getXMin(){
		return min_x;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getXMax(){
		return max_x;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getYMin(){
		return min_y;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getYMax(){
		return max_y;
	}	
}