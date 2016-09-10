import java.io.*;

public class Leave implements Serializable {
	private String identifier;
	
	
	public void setIdentifier(String id){
		this.identifier = id;
	}
	
	public String getIdentifier(){
		return identifier;
	}
}