import java.io.Serializable;

class View implements Serializable {
	private String identifier;
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	View(){
		this.identifier = null;
	}
	
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void setIdentifier(String iden){
		this.identifier = iden;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	
	public String getIdentifier(){
		return identifier;
	}
}
	