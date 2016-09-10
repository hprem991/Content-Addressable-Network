import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;


class Boot implements Serializable {
	String flag = null, current_node = null;
	String bootIP = "localhost";
	
	HashMap<String, String> boot = new HashMap<String, String>(); // bootObjectKey -> bootObject Ip
	HashMap<String, List<String> >	nodeList = new HashMap<String, List<String> >(); // networkKey -> ListofNetworkIp 
	//	List<String> nodeList = new ArrayList<String>();


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public String getbootIP(){
		return bootIP;
//		if(boot.containsKey(identifier)){
//			return boot.get(identifier);
//		} else {
//			return "localhost";
//		} 
	}
	
//	/*****************************************************************
//	 *  FUNCTION :
//	 *  INPUT   :
//	 *  OUTPUT  :
//	 *  PURPOSE :
//	 ******************************************************************/
//	public void setId(String identifier, String ipAddr){
//		boot.put(identifier, ipAddr);
//	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	Boot(String bootIdentifier, int port){
		this.bootIP = bootIdentifier;
		this.flag = null;
		this.current_node = null;
//		try{
//			bootIP = (InetAddress.getLocalHost().getLocalHost().toString()).split("/")[1].toString();
//		} catch (Exception e){
//			System.out.println("Exception : Boot IP "+e.getMessage());
//		}
//		boot.put(identifier, bootIP);
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	void setFlag(String flag){
		this.flag = flag;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	String getFlag(){
		return flag;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public List<String> getNodes(String identifier){
		if(nodeList.containsKey(identifier))
			return nodeList.get(identifier);
		return Collections.emptyList();	
	}  

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	//  	public void addNode(String ipList){
	//	    	 nodeList.add(ipList);
	//  	}
	
	public void setNode(String domain, String ipList){
		List<String> nodes = new ArrayList<String>();
		if(nodeList.containsKey(domain)){
			nodes = nodeList.get(domain);	    	 
		} 
		nodes.add(ipList);
		nodeList.put(domain, nodes);
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public void eraseNode(String currentCanNetwork, String ipList){
		List<String> nodes = new ArrayList<String>();
		if(nodeList.containsKey(currentCanNetwork)){
			nodes = nodeList.get(currentCanNetwork);	    	 
		} 
		nodes.remove(ipList);
		nodeList.put(currentCanNetwork, nodes);
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public void setCurrent(String ipAddress){
		this.current_node = ipAddress;
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public String getCurrent(){
		return current_node;
	}
}
