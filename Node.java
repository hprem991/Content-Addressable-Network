import java.lang.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;


class Node {

	Peer peer;
	Boot bootObject;
	String currentIp;
	int defaultPort;


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 ******************************************************************/
	
	Node(String identifier, int port)  {
		try {		
			bootObject = new Boot(identifier, port);
			bootObject.setCurrent(identifier);
			bootObject.setFlag("request");
			bootObject = (Boot)startClient(identifier , port, bootObject);
		} catch(Exception e) {
			if(bootObject == null) {
				try {
					bootObject = new Boot(getLocalIp(), port);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			} else {
				System.out.println("FATAL FAILUE in NODE "+e.getMessage());
			}
		}
		System.out.println("Boot Ip is "+bootObject.getbootIP());
	}



	/*****************************************************************
	 *  FUNCTION : DEPRECATED COMPLEX DYNAMIC BOOT  
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 ******************************************************************/
	
//	Node(String identifier, int port)  {
//		try { 
//			String validIp = getValidBootIp(identifier, port);
//			String local = getLocalIp();
//			if((validIp.equals(getLocalIp())) && ((bootObject == null))){
//				bootObject = new Boot(getLocalIp(), port);
//			} else  {
//				bootObject = new Boot(identifier, port);
//				Socket clientSocket = new Socket("localhost", port);
//				ObjectOutputStream request = new ObjectOutputStream(clientSocket.getOutputStream());
//				ObjectInputStream response = new ObjectInputStream(clientSocket.getInputStream());
//				bootObject.setCurrent(identifier);
//				bootObject.setFlag("request");
//				bootObject = (Boot)startClient(identifier , port, bootObject);
//			}
//		} catch(Exception e) {
//			if(bootObject == null) {
//				try {
//					bootObject = new Boot(getLocalIp(), port);
//				} catch (UnknownHostException e1) {
//					e1.printStackTrace();
//				}
//			} else {
//				System.out.println("FATAL FAILUE in NODE "+e.getMessage());
//			}
//		}
//		System.out.println("Boot Ip is "+bootObject.getbootIP());
//	}


	/*****************************************************************
	 *  FUNCTION : DEPRECATED SIMPLE BOOT  
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 ******************************************************************/
	

	//	Node(String identifier, int port)  {
	//		bootObject = new Boot(identifier, port);
	//	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public String getValidBootIp(String id, int port) throws UnknownHostException{
		String myhostIp = getLocalIp();
		try {
			int timeOut = (int)TimeUnit.SECONDS.toMillis(5);
			if(InetAddress.getByName(id).isReachable(timeOut)){
				Socket clientSocket = new Socket(id, port);
				clientSocket.close();
				return id;
			} 
		} catch(Exception e){
			System.out.println("FATAL ERROR :- Failed to Connect with BootStrap" +e.getMessage());
		}
		return myhostIp;
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 * @throws UnknownHostException 
	 ******************************************************************/

	public String getLocalIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public void startServer(final int port) {
		(new Thread() {
			@Override
			public void run () {
				try {
					ServerSocket welcomeSocket = new ServerSocket(port);
					System.out.println("SERVER STARTED...");

					while (true) {    
						Socket server = welcomeSocket.accept();
						System.out.println("SOCKET ESTABLISHED...");
						// Create input and output streams to client
						ObjectOutputStream response = new ObjectOutputStream(server.getOutputStream());
						ObjectInputStream request = new ObjectInputStream(server.getInputStream());
						try {
							System.out.println("SERVER: PROCESSING REQUEST...");
							Object obj = request.readObject();

							if(obj instanceof Leave) { // Search 
								Leave leave = (Leave)obj;
								if(peer.getMyIp().equals(null)){
									System.out.println("SERVER : LEAVING PEER DOES NOT EXIST");
									response.writeObject(leave);
									peer.view();
								} else if(peer.getMyIp().equals(leave.getIdentifier())){
									System.out.println("SERVER : VIEW PEER IDEN");
									response.writeObject(leave);
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										System.out.println("EFFECTED PEERS :-" +entry.getKey());										
									}
								} else {
									//REROUTE
									response.writeObject(leave);
									// Forward to all neighbour
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										startClient(entry.getKey(), port, leave);
									}
								}
							} else if(obj instanceof View) { // View 
								View view = (View)obj;
								if(peer.getMyIp().equals(null)){
									System.out.println("SERVER : VIEW PEER NULL");
									response.writeObject(view);
									peer.view();
								} else if(peer.getMyIp().equals(view.getIdentifier())){
									System.out.println("SERVER : VIEW PEER IDEN");
									response.writeObject(view);
									peer.view();
								} else {
									//REROUTE
									response.writeObject(view);
									// Forward to all neighbour
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										startClient(entry.getKey(), port, view);
									}
								}
							} else if(obj instanceof Search) { // Search 
								Search search = (Search)obj;
								if(compareZone(search.getXPos(),search.getYPos())){
									System.out.println("SERVER : SEARCH COMPARED ZONE PASSED  ...");
									response.writeObject(search);
									peer.getData(search.getFilename());						
								} else {
									//REROUTE;
									System.out.println("SERVER : RE-ROUTE SEARCH...");
									response.writeObject(search);
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										startClient(entry.getKey(), port, search);
									}

								}
							} else if(obj instanceof Content) { // Insert
								Content content = (Content)obj;
								if(compareZone(content.getXPos(),content.getYPos())){
									System.out.println("SERVER : INSERT COMPARED ZONE PASSED ...");
									response.writeObject(content);
									peer.setData(content);
								} else {
									//REROUTE;
									System.out.println("SERVER : RE-ROUTE INSERT...");
									response.writeObject(content);
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										startClient(entry.getKey(), port, content);
									}	
								}
							} else if(obj instanceof Peer) { // Join
								// TODO : Do Peer Object Processing
								Peer tempPeer = (Peer)obj;
								// Once I gets the peer object, i will check if the peer.x,peer.y lies in myzone
								// if it lies, I will check all my zone size and compare it will all the neighbouring zone size.
								// Collect max-zone(optimised) , current zone and split.
								// calculate all the neighbours and update itself as well as send periodic update.

								// if the peer does not lie in my zone, then get all the neighbouring list and route through each one of them.
								//System.out.println("SERVER PEER : B4 Comparision "+tempPeer.getPostionX()+ "Check "+peer.getZone());
								if(noPeersAvailable()){
									System.out.println("SERVER : FIRST NODE");
									tempPeer.getZone().setZone(0, 10, 0, 10);
									peer = tempPeer;
									response.writeObject(tempPeer);
									peer.view();
								}
								else if(compareZone(tempPeer.getPostionX(),tempPeer.getPostionY())){
									System.out.println("SERVER SERVING PEER : "+tempPeer.getMyIp());
									Peer node = getmaxZone(peer); 
									Split(node, tempPeer);
									calculateNeighbours(node, tempPeer); 
									System.out.println("SERVER RESPONDING PEER -->: "+node.neighbours.size());
									response.writeObject(tempPeer);
									tempPeer.view();
									peer.view();
								} else {
									System.out.println("########SERVER PEER : OUTSIDE ZONE "+peer.neighbours.size());
									response.writeObject(tempPeer);
									response.flush();
									request.close();
									for(Map.Entry<String, Zone> entry : peer.neighbours.entrySet())
									{
										String currentIp = entry.getKey();
										System.out.println("SERVER: RE-ROUTING "+currentIp);
										startClient(currentIp, port, tempPeer);
									}
								}
							} else if (obj instanceof Boot) {
								//TODO : BootStrap Processing
								System.out.println("SERVER: BOOTSTRAPING ..."+request.toString());
								Boot responseObject = (Boot)obj;
								System.out.println("SERVER: Reading  FLAG "+responseObject.getFlag());
								if(responseObject.getFlag().toLowerCase().equals("request")){
									System.out.println("SERVER: RESPONDING --> : Request");
									response.writeObject(bootObject); // sending my current_instance (iplist)
								} else if(responseObject.getFlag().toLowerCase().equals("update")){
									System.out.println("SERVER: RESPONDING --> : Update");
									bootObject = responseObject;
									response.writeObject(bootObject);
								} else if(responseObject.getFlag().toLowerCase().equals("delete")){
									System.out.println("SERVER: RESPONDINF --> Delete");
									bootObject = responseObject;
									response.writeObject(bootObject);
								} else {
									System.out.println("SERVER: INVALID BOOTSTRAP !!!");
									response.writeObject(bootObject);
								}
							} else {
								System.out.println("SERVER: UNABLE TO PROCESS  !!!");
							}
						} catch (Exception e){
							System.err.println("SERVER: Stack Trace: " + e.getStackTrace());
							System.err.println("SERVER: To String: " + e.toString());
						}
						response.flush();
					}
				} catch (Exception e) {
					System.err.println("Server Error: " + e.getMessage());
					System.err.println("Localized: " + e.getLocalizedMessage());
					System.err.println("Stack Trace: " + e.getStackTrace());
					System.err.println("To String: " + e.toString());
				}
			}
		}).start();
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	protected boolean noPeersAvailable(){
		return (peer == null);
	}
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	protected void Split(Peer node, Peer tempPeer) {
		if(peer.isSquare()){
			System.out.println("SERVER: SPLITTING Square");
			double mid_x = (peer.getZone().getXMax() - peer.getZone().getXMin()) / 2;
			if(peer.getPostionX() > mid_x){
				peer.getZone().setZone(mid_x, peer.getZone().getXMax(), 
						peer.getZone().getYMin(), peer.getZone().getYMax());
				tempPeer.getZone().setZone(peer.getZone().getXMin(), mid_x,
						peer.getZone().getYMin(), peer.getZone().getYMax());
			} else {
				peer.getZone().setZone(peer.getZone().getXMin(), mid_x,
						peer.getZone().getYMin(), peer.getZone().getYMax());
				tempPeer.getZone().setZone(mid_x, peer.getZone().getXMax(), 
						peer.getZone().getYMin(), peer.getZone().getYMax());
			}
		} else {
			double mid_y = (peer.getZone().getYMax() - peer.getZone().getYMin()) / 2;
			if(peer.getPostionY() > mid_y){
				peer.getZone().setZone(peer.getZone().getXMin(), peer.getZone().getXMax(), 
						mid_y, peer.getZone().getYMax());
				tempPeer.getZone().setZone(peer.getZone().getXMin(), peer.getZone().getXMax(), 
						peer.getZone().getYMin(), mid_y);
			} else {
				peer.getZone().setZone(peer.getZone().getXMin(), peer.getZone().getXMax(), 
						peer.getZone().getYMin(), mid_y);
				tempPeer.getZone().setZone(peer.getZone().getXMin(), peer.getZone().getXMax(),
						mid_y, peer.getZone().getYMax());
			}
		}
		//}
	}
	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/



	protected void calculateNeighbours(Peer node, Peer tempPeer) {
		// TODO Auto-generated method stub
		HashMap<String, Zone> currentNeighbours = new HashMap<String, Zone>();
		currentNeighbours = node.neighbours;
		node.neighbours.clear();
		//		System.out.println("SERVER: PARENTS Neighbours Before "+node.neighbours.size());
		//		System.out.println("SERVER: NODE Neighbours Before "+tempPeer.neighbours.size());
		for(Map.Entry<String, Zone> entry : currentNeighbours.entrySet())
		{
			System.out.println("SERVER: FINDING NEIGHBOURS");
			String currentIp = entry.getKey();
			Zone curentZone = entry.getValue();
			//			System.out.println("SERVER: My Zone Value "+curentZone.getXMax());
			if(doXOverlaps(curentZone,tempPeer)){
				if(verifyNeighbourhood(curentZone.getYMin(),curentZone.getYMax(),
						tempPeer.getZone().getYMin(),tempPeer.getZone().getYMax()))
					tempPeer.neighbours.put(currentIp, curentZone); //Neighbours
			} else if(doYOverlaps(curentZone, tempPeer)) {
				if(verifyNeighbourhood(curentZone.getXMin(),curentZone.getXMax(),
						tempPeer.getZone().getXMin(),tempPeer.getZone().getXMax()))
					tempPeer.neighbours.put(currentIp, curentZone); //Neighbours
			}else if(doXOverlaps(curentZone,node)){
				if(verifyNeighbourhood(curentZone.getYMin(),curentZone.getYMax(),
						node.getZone().getYMin(),node.getZone().getYMax()))
					node.neighbours.put(currentIp, curentZone); //Neighbours
			} else if(doYOverlaps(curentZone, node)) {
				if(verifyNeighbourhood(curentZone.getXMin(),curentZone.getXMax(),
						node.getZone().getXMin(),node.getZone().getXMax()))
					node.neighbours.put(currentIp, curentZone); //Neighbours
			}
		}
		tempPeer.neighbours.put(node.getMyIp(), node.getZone());
		node.neighbours.put(tempPeer.getMyIp(), tempPeer.getZone());
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	private boolean verifyNeighbourhood(double min, double max, double min2, double max2) {
		return ( ((min < min2) && (max > max2)) || ((min < max2) && (max > max2)) ||
				((min > min2) && (max < max2)));
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	private boolean doXOverlaps(Zone myZone, Peer tempPeer) {
		return ((Math.abs(myZone.getXMax() - tempPeer.getZone().getXMin()) < 0.01) || 
				(Math.abs(myZone.getXMin() - tempPeer.getZone().getXMax()) < 0.01));
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT    :
	 *  OUTPUT   :
	 *  PURPOSE  :
	 ******************************************************************/

	private boolean doYOverlaps(Zone myZone, Peer tempPeer) {
		return ((Math.abs(myZone.getYMax() - tempPeer.getZone().getYMin()) < 0.01) || 
				(Math.abs(myZone.getYMin() - tempPeer.getZone().getYMax()) < 0.01));
	}

	/*****************************************************************
	 *  FUNCTION : COMPAREZONE
	 *  INPUT    :
	 *  OUTPUT   :
	 *  PURPOSE  :
	 ******************************************************************/

	protected boolean compareZone(double x_cor, double y_cor){
		return (x_cor >= peer.getZone().getXMin() && x_cor <= peer.getZone().getXMax() &&
				y_cor >= peer.getZone().getXMin() && y_cor <= peer.getZone().getYMax());		
	}


	/*****************************************************************
	 *  FUNCTION : GETMAXZONE
	 *  INPUT  	 :
	 *  OUTPUT   :
	 *  PURPOSE  :
	 ******************************************************************/
	protected Peer getmaxZone(Peer peer) {
		return peer; // Need to add the max peer calculation
	}

	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public double calculateXPos(String str){
		int myValue = 0;
		for(int index = 1; index < (str.length() - 2);){
			myValue += Character.getNumericValue(str.charAt(index));
			index += 2;
		}
		return (myValue % 10);
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/
	public double calculateYPos(String str){
		int myValue = 0;
		for(int index = 0; index < (str.length() - 2);){
			myValue += Character.getNumericValue(str.charAt(index));
			index += 2;
		}
		return (myValue % 10);
	}



	/*****************************************************************
	 *  FUNCTION : STARTCLIENT 
	 *  INPUT    :
	 *  OUTPUT   :
	 *  PURPOSE  :
	 ******************************************************************/

	public Object startClient(String ipaddress, int port, Object obj) {
		try {
			Socket clientSocket = new Socket(ipaddress, port);
			ObjectOutputStream request = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream response = new ObjectInputStream(clientSocket.getInputStream());

			System.out.println("CLIENT :- REQUESTING SERVER ...Addr "+ipaddress+" Prt "+port);
			request.writeObject(obj);
			System.out.println("CLIENT :- READING SERVER RESPONSE");
			obj = response.readObject();
			System.out.println("CLIENT :- After Read SOCKET ");
			clientSocket.close();
		} catch (Exception e) {
			System.err.println("Client Error: " + e.getMessage());
			System.err.println("Localized: " + e.getLocalizedMessage());
			System.err.println("Stack Trace: " + e.getStackTrace());
		}
		return obj;
	}


	/*****************************************************************
	 *  FUNCTION : JOIN
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public void join(String networkIdentifier, int port){

		System.out.println("CLIENT : Joining Request ...");	
		bootObject.setFlag("request"); 
		String hostIp = "localhost";

		System.out.println("********** JOINING BOOTSTRAP ********");
		bootObject = (Boot)startClient(bootObject.getbootIP(), port, bootObject); 
		try {
			List<String> domainIpList = bootObject.getNodes(networkIdentifier);
			hostIp = getLocalIp();
			if(!hostIp.equals(currentIp) || domainIpList.isEmpty()){
				bootObject.setCurrent(hostIp);
				bootObject.setFlag("update");
				bootObject.setNode(networkIdentifier, hostIp);
				bootObject = (Boot)startClient(bootObject.getbootIP(), port, bootObject);
			}
			int counter = 0;
			while((hostIp.equals(currentIp) && counter++ < domainIpList.size()) || (currentIp == null)) {
				System.out.println("Host Ip "+hostIp+" currentIp "+currentIp+" Counter "+counter);
				currentIp = domainIpList.get(counter);
				System.out.println("*** CURRENT "+currentIp+" Counter "+counter);
			}
		} catch (Exception e){
			currentIp = hostIp;
			System.out.println("Exception :Failed getting system IP Address "+e.getMessage());
		}

		Peer node = new Peer(networkIdentifier);

		node.setId();
		node.setPosition(Math.random()*10,Math.random()*10);
		startClient(currentIp, port, node);
	}

	/*****************************************************************
	 *  FUNCTION : INSERT
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public void insert(String currentCanNetwork, String keyword, int port){
		// Get the hashKey of the node where we are going to store the value
		// Calculate its co-ordinate values
		// Route the content to the address of the calculated co-ordinate
		try{
			String fileName = getClass().getResource(keyword).getFile();
			if(new File(fileName).exists()){
				Content peerContent = new Content(calculateXPos(keyword), calculateYPos(keyword));
				peerContent.readFile(fileName);
				//System.out.println("CLIENT :-> INSERTING FILE "+keyword+" Address "+peer.getMyIp());
				List<String> ip = bootObject.getNodes(currentCanNetwork);
				if(ip.isEmpty()) {
					System.out.println(" No nodes found to insert file");
					//startClient("localhost", port , peerContent);
				} else {
					startClient(ip.get(0), port , peerContent);
				}
			} else {
				System.out.println("ERROR :  File Does Not Exist !!!");
			}
		} catch (Exception e){
			System.out.println("CLIENT : Exception Occoured during File Insert "+e.getMessage());
		}
	}

	/*****************************************************************
	 *  FUNCTION : SEARCH
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public void search(String currentCanNetwork, String keyword, int port){
		// Get the hashKey of the node where we are going to store the value
		// Calculate its co-ordinate values
		// Route the content to the address of the calculated co-ordinate
		try{
			Search peerContent = new Search(calculateXPos(keyword), calculateYPos(keyword), keyword);
			//System.out.println("CLIENT :-> SEARCHING FILE "+keyword+" Address "+peer.getMyIp());
			List<String> ip = bootObject.getNodes(currentCanNetwork);
			if(ip.isEmpty()) {
				System.out.println(" No nodes found in network for file search");
				//startClient("localhost", port , peerContent);
			} else {
				startClient(ip.get(0), port , peerContent);
			}
		} catch (Exception e){
			System.out.println("CLIENT : Exception Occoured during File Insert "+e.getMessage());
		}
	}

	/*****************************************************************
	 *  FUNCTION : VIEW
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public void View(String currentCanNetwork, String identifier, int port){
		// If identifer is given then route and call view of identifier
		// if not given, then call view on each of the the bootObject identifier.

		if(identifier.equals(null)){
			List <String> peers = bootObject.getNodes(currentCanNetwork);
			for(int index=0;index<=peers.size();index++){
				startClient(peers.get(index), port, new View());
			}
		} else {
			View view =  new View();
			view.setIdentifier(identifier);
			startClient(identifier, port, view);
		}
	}


	/*****************************************************************
	 *  FUNCTION : LEAVE
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 ******************************************************************/

	public void leave(String currentCanNetwork,String currentNode, int port){
		Leave leave = new Leave();
		leave.setIdentifier(currentCanNetwork);
		startClient(peer.getMyIp(), port, leave);
		bootObject.eraseNode(currentCanNetwork, currentIp);
		//Show each of the nodes neighbour
	}


	/*****************************************************************
	 *  FUNCTION :
	 *  INPUT   :
	 *  OUTPUT  :
	 *  PURPOSE :
	 * @throws UnknownHostException 
	 ******************************************************************/

	public static void main(String arguments[]) throws UnknownHostException
	{

		String currentCanNetwork = "localhost";
		int defaultPort = 9090;
		String defaultbootkey = "localhost";
		Peer currentNode = null;
		String input = null;

		if(arguments.length < 1){
			try {
				defaultbootkey = (InetAddress.getLocalHost().toString()).split("/")[1].toString(); // This is where bootIp is located
				System.out.println("Try BootStrap IP "+defaultbootkey);
			} catch (Exception e){
				System.out.println("ERROR :- Failed to get the host Ip "+ e.getMessage());
			}
		} else if(arguments.length == 1) {
			defaultbootkey = arguments[0];	
		} else if(arguments.length == 2) {
			defaultPort = Integer.parseInt(arguments[1]);	
		}

		System.out.println("************ SERVER NODE ******** Listening at Port :->"+defaultPort);

		Node node = new Node(defaultbootkey, defaultPort);

		System.out.println("PLEASE TYPE ANY OF THE FOLLOWING INPUT :- "+node.bootObject.getbootIP());
		System.out.println("join <networkName>");
		System.out.println("insert <fileName>");
		System.out.println("search <fileName>");
		System.out.println("view <identifier>");
		System.out.println("leave");

		node.startServer(defaultPort);

		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String words[];
			while((input=br.readLine())!=null){
				words = input.split("\\s+");
				System.out.println("input Value "+words.length);
				if(words[0].toLowerCase().equals("join")){
					if(words.length == 1){ // User Input join <ENTER>
						node.join(currentCanNetwork, defaultPort); 
					} else if(words.length == 2){// User Input join <Identifier> <ENTER>
						currentCanNetwork = words[1];
						node.join(currentCanNetwork, defaultPort);
					} else if(words.length == 3){ // User Input join <identifier> <port> <ENTER>
						currentCanNetwork = words[1];
						node.join(currentCanNetwork, Integer.parseInt(words[2]));
					} 
				} else if(input.split(" ")[0].toLowerCase().equals("insert")){
					node.insert(currentCanNetwork, input.split(" ")[1], defaultPort);
				} else if(input.split(" ")[0].toLowerCase().equals("search")){
					node.search(currentCanNetwork, input.split(" ")[1], defaultPort);
				} else if(input.split(" ")[0].toLowerCase().equals("view")){
					String viewList[] = input.split("\\s+");
					if(viewList.length < 2)
						node.View(currentCanNetwork,"null", defaultPort);
					else 
						node.View(currentCanNetwork,viewList[1],defaultPort);
				} else if(input.split(" ")[0].toLowerCase().equals("leave")){
					node.leave(currentCanNetwork, currentNode.getMyIp(), defaultPort); 
				} else {
					System.out.println(input);
				}
			}				
		}catch(IOException io){
			io.printStackTrace();
		}	
	}
}