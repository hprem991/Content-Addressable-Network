import java.io.*;
import java.util.*;
import java.net.*;
import java.security.*;

class Peer implements Serializable {

	private String ipAddress, hostname;

	private String hashKey;
	HashMap<String, String> id= new HashMap<String, String>(); // Identifier -> Ip Address

	public HashMap<String, Zone> neighbours = new HashMap<String, Zone>();	// ipMapping -> Zone 

	public HashMap<String, List<String> > neighboursList = new HashMap<String, List<String> >();
	
	HashMap<String, String> content = new HashMap<String, String>(); //  Hash(fileName) -> Content

	private double x_pos,y_pos;

	private Zone myZone;


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getIp(String identifier){
		if(id.containsKey(identifier)){
			return id.get(identifier);
		} else {
			return "NULL";
		} 
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public HashMap<String, List<String> > getNeighbours(){
		return neighboursList;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void getData(String filename){
		if(content.containsKey(calculateHashCode(filename)))
			view();
		System.out.println("Failure");
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getMyIp(){
		return ipAddress;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getPostionX(){
		return x_pos;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public double getPostionY(){
		return y_pos;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getHostName(){
		return hostname;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public String getHash(){
		return hashKey;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public Zone getZone(){
		return myZone;
	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void setId(){
		id.put(calculateHashCode(ipAddress), ipAddress);
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void setPosition(double x, double y){
		this.x_pos = x;
		this.y_pos = y;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void setData(Content data){
		content.put(calculateHashCode(data.getFilename()), data.getData());
		view();
	}

	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
//	public void setNeighbours(String ipAddress, Zone newZone){
//		if(neighboursList.containsKey(ipAddress)){
//			List<Zone> myZone = neighboursList. .get(ipAddress);
//			neighboursList.put(ipAddress, (neighboursList.get(ipAddress)).addAll((Collection<? extends String>) newZone));
//		}
//	}
	
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	private String calculateHashCode(String hashKey){
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(hashKey.getBytes()); 
			return ConvertByteToString(messageDigest.digest());
		} catch (Exception e){
			System.out.println("Exception in HashKey Compute " +e.getMessage());
			return hashKey.toString();
		}
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	private String ConvertByteToString(byte[] digest) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			stringBuffer.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public boolean isSquare(){
		return ((myZone.getXMax() - myZone.getXMin()) == (myZone.getYMax() - myZone.getYMin()));
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	public void view(){
		System.out.println("******* DISPLAYING PEER INFORMATION ******");
		System.out.println("IDENTIFIER -> "+this.hashKey);	
		System.out.println("IP-ADDRESS -> "+this.getMyIp());
		System.out.println("X -> "+this.getPostionX()+" Y-> "+this.getPostionY());	
		for(Map.Entry<String, String> entry : this.content.entrySet())
		{
			System.out.println("DATA -> "+entry.getKey());	
		}

		for(Map.Entry<String, Zone> entry : this.neighbours.entrySet())
		{
			System.out.println("NEIGHBOUR'S IP -> "+entry.getKey());	
		}	
		System.out.print("ZONE -> ");
		this.myZone.zone();
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	******************************************************************/
	
	Peer(String str){
		try {
			ipAddress = (InetAddress.getLocalHost().getLocalHost().toString()).split("/")[1].toString();
			hostname = (InetAddress.getLocalHost().getLocalHost().toString()).split("/")[0].toString();
			myZone = new Zone();
		} catch (Exception e){
			System.out.println("Failed getting system IP Address "+e.getMessage());
		}
	}

}