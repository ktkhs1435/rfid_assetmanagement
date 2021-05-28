package nesslab.reader.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;


public class ReaderApi {
	// test 
	public enum Algorithm
	{
		FIXEDQ,
		DYNAMICQ,
		DYNAMICQ_ADJUST,
		DYNAMICQ_THRESH,
		MAX
	}

	public enum ModelType
	{
		NL_NONE(0),
		NL_RF100(1),
		NL_RF1000(3),
		NL_RF2000(4),//industrial usage of NL-RF1000
        NL_RF2200(5),
		Max(6);
		
		public int value;
		ModelType(int value)
		{
			this.value = value;			
		}
	}
	
	public enum ConnectType
	{
		Tcp,
		Udp,
		Serial
	}

	public enum TagType
	{
		ISO18000_6B,
		ISO18000_6C_GEN2,

	}
	
	public enum TagInformations
	{
		None(0x00000000),
		IpAddress(0x00000001),
		ReadTime(0x00000002),
		Flag(0x00000004),
		RSSI(0x00000008);
		
		public int value;
		TagInformations(int value)
		{
			this.value = value;			
		}
	}
	
	public enum CloseType
	{
		None,

		Close,
		FormClose,
		ReConnect,

		Timeout,
		FtpFileError,
		FtpSocketError,
	}

	public enum ReaderState
	{
		None(0x00000000),
		Handling(0x00000001),
		Connecting(0x00000002),
		Monitoring(0x00000004),
		Inventorying(0x00000008),
        TagReading(0x00000016);
		//All = Handling | Connecting | Monitoring | Inventorying;
		public int value;
		ReaderState(int value)
		{
			this.value = value;			
		}
	}
	
	public enum PacketType
	{
		Default,//variable text
		Variable,//variable binary - ftp
		Monitor,//{#22} Default + Fixed
		Fixed//Not implemented
	}


	public enum ReadType
	{
        MULTI,
        ONE,
        SELECT,
        INTERVAL,
        EXTERNALSWITCH,
        NONE        

	}
	
	public enum MonitorState//{#22}
	{
		None,
		Monitor,
		Start,
		Report,
		Complete
	}
	
    public enum IOType
    {
        Output,
        Input,
        Voltage_5,
        Voltage_12
    }
    
    /// <summary>
    /// AntennaSetType
    /// (Power, Register, Cycle Time, Duration Time)
    /// </summary>
    public enum AntennaSetType
    {
        //01~04 : power - RF100=x % 1 0, RF1000=x % 01 0
        //11~14 : register
        //21~24 : cycle time
        //31~34 : duration time
        Power(0),
        Register(1),
        CycleTime(2),
        DurationTime(3);
    	
		public int value;
		AntennaSetType(int value)
		{
			this.value = value;			
		}
    	
    }
    
    
    public enum ReaderEventKind
	{
		None,
		Connected,
		Disconnected,
		timeout,
		TagId,//get
		GetTagMemory,//get
        GetTagMemoryEx,//get
        SetTagMemory,
		LockTag,
		KillTag,
		Version,//get
		Buzzer,//get
		ContinueMode,//get
		QValue,//get
		Power,//get
		Session,//get
		TagInformations,//get
		AntennaState,//get
		IpAddress,//get
 		WritePort,//get
		SelectMask,//get
		SelectAction,//get
		AlgorithmParameter,//get
		Command,//get or set, general command
        OperationMode,//get (x)
		FtpBlock,
		FtpEof,
		FtpFileError,
		FtpSocketError,
		Timer,
		TagCounts,//{#22}
		TagReport,//{#22}
		TagReportCompleted,//{#22}
		FrequencyBand,
		ChannelNumber,
		FHSS,
		SetPower,
		SetAntennaState,
        GPIOState,
        GPIOPress,//GPIO Press
        GPIORelease,//GPIO Release
        GPIOMapCommand,
        GPIOBlinkTime,
        State,
        StateReport,
        StateReportTerm,
        StopOperation,
        RSSI,
        InventoryRunningTime,
        InventoryAutoRun,
        InventorySubMode,
        InventoryPurgeCycle,
        InventoryBufferClearTime,
        InventoryCompetitionTime,
        InventoryCompetitionValidCount,
        InventoryIntervalScanTime,
        InventoryIntervalIdleTime,
        InventoryOnceIdleTime,
        TagResponseCode,
        DebugMode,
        SioInputTrigger,
        SioConnected,
        SioInputStatus,
        SioOutputStatus,
        SioVersion,
        CheckSumEnable,
        LogData,
        ExtSwitchOperationMode,
        ExtSwitchAntennaSwingOption,
        ExtSwitchAntennaPort,
        ExtSwitchAntennaDuration,
        ExtSwitchSensorStatus
	}


    public enum GpioMapCommand
    {
        NORMAL_INVENTORY(0x66),
        SELECT_INVENTORY(0x67),
        INTERVAL_INVENTORY(0x69),
        SINGLE_INVENTORY(0x65),
        STOP_INVENTORY(0x33),
        REBOOT(0x72),
        VERSION(0x76),
        NONE(0x30);
    	
		public int value;
		GpioMapCommand(int value)
		{
			this.value = value;			
		}
    	
    }

   
    public String Unknown = "Unknown";    

    
    // model type
	private ModelType modelType;
	public ModelType getModelType()
	{
		return this.modelType;
	}	
	public void setModelType(ModelType value)
	{
		
		this.modelType = value;
	}	
	public boolean getIsRf1000Series()
	{
			return Rf1000Series(modelType);
	}	
	public static boolean Rf1000Series(ModelType modelType)
	{
		return modelType == ModelType.NL_RF1000 ||
			modelType == ModelType.NL_RF2000 ||
            modelType == ModelType.NL_RF2200;
	}
	
	
	
	static public String[] ModelNames;
	static public int GetModelType (String szModelName)//for later use...
	{
		int nType;
		for (nType = ModelType.NL_NONE.value; nType < ModelType.Max.value; nType++)
			if(ModelNames[(int)nType].compareTo(szModelName) == 0) break;		
		return nType;
	}

	
	private ConnectType connectType;
	public ConnectType getConnectType()
	{
		return this.connectType;
	}	
	public void setConnectType( ConnectType value)
	{
		this.connectType = value;
	}    
    
	//packettype
	private PacketType packetType = PacketType.Default;
	public PacketType getPacketType()
	{
		return this.packetType;		
	}	
	public void setPacketType(PacketType value)
	{
		this.packetType = value;		
	}
	
	
	//socket
	private Socket socket = null;
	private String ipAddr = "";	
    private int socketPort = 5578;    
	public String getIpAddr()
	{
		return this.ipAddr;
	}	
	public void setIpAddr(String value)
	{
		this.ipAddr = value;		
	}
	

	//antenastate
	int antennaState = 0;//undefined
	public int getAntennaState()
	{
		return antennaState;
	}	
	public void setAntennaState(int value)
	{
		antennaState = value;
	}
	
	
	private TagType tagType;
	public TagType getTagType()
	{
		return this.tagType;
	}
	
	public void setTagType(TagType value)
	{
		this.tagType = value;
	}
	
	
	//readonly object lockobject = new object();  //c#
	Object lockobject = new Object();
		
	static private Map<String, String> responses;	
	static private Map<String, String> states;

	{
		//state
		responses = new HashMap<String, String>();
		responses.put("01", "Success");
		responses.put("90", "Execut wait ready");       
        responses.put("91", "Execute duplication");
        responses.put("92", "Execute fail");
        // Error
		responses.put("05", "Tag response rsv");	//NL-RF1000 
		responses.put("0A", "Tag response over");	//NL-RF1000
        responses.put("0F", "Tag response per");
        responses.put("21", "Tag Backscatter");      
        responses.put("22", "Tag Timeout"); 		//NL-RF1000
        responses.put("23", "Tag CRC");
        responses.put("25", "Invalid Password");
        responses.put("26", "Tag not found");
        responses.put("88", "Tag function errorl");
        responses.put("99", "Invalid Tag parameter");
        responses.put("FF", "MAC value error");
        // Backscatter Error
        responses.put("00", "Other error");
        responses.put("03", "Memory overrun");
        responses.put("04", "Memory locked");
		responses.put("0B", "Insufficient power");
		responses.put("0F", "Non-specific error");
        // NL-RF2200
        responses.put("40", "Success");
        responses.put("41", "Handle Mismatc");
        responses.put("42", "Bad CRC");
        responses.put("43", "No Reply");
        responses.put("44", "Invalid password");
        responses.put("45", "Zero Kill password");
        responses.put("46", "Tag Lost");
        responses.put("47", "CMD Format error");
        responses.put("48", "Read count invalid");
        responses.put("49", "Retry count exceeded");
        responses.put("4A", "Access Fail");

		states = new HashMap<String, String>();
		states.put("1", "INIT");
		states.put("2", "READY");
		states.put("3", "QUERY");
		states.put("4", "SELECT");
		states.put("5", "PSOTSELECT");
		states.put("6", "WRITE");
		states.put("7", "BANKREAD");
		states.put("8", "LOCK");
		states.put("9", "KILL");
		states.put("10", "TEST");
		states.put("50", "TAGMOE");
		states.put("13", "XXXX");

		ModelNames = new String[(int)ModelType.Max.value];
		ModelNames[0] = "NL-NONE";
		ModelNames[1] = "NL-RF100";
		ModelNames[2] = "NL-RF1000";
		ModelNames[3] = "NL-RF2000";
        ModelNames[4] = "NL-RF2200";
	}
	
	
	static public String Responses(String code)
	{
		String value = null;
		
		if(code.isEmpty() == false)
		{			
			value = responses.get(code).toString();
		}
		
		return value.isEmpty() ? "unknown" : value;
		
		/*
		String value = null;
		//if (String.IsNullOrEmpty(code) == false)
		if(code.isEmpty() == false)
		{
			//responses.TryGetValue(code, out value);
			value = responses.get(code).toString();
		}
		//return String.IsNullOrEmpty(value) ? "unknown" : value;
		return value.isEmpty() ? "unknown" : value;
		*/ 
	}

	static public String States(String code)
	{
		String value = null;
		
		if (code.isEmpty() == false)
		{
			value = states.get(code).toString();
		}
		
		return value.isEmpty() ? "unknown" : value;
		
		/*
		String value = null;
		if (String.IsNullOrEmpty(code) == false)
		{
			states.TryGetValue(code, out value);
		}
		return String.IsNullOrEmpty(value) ? "unknown" : value;
		*/
	}
	
	
	private Object lockReaderState = new Object();
	ReaderState readerState = ReaderState.None;
	
	private void SetReaderState(ReaderState state)
	{
		synchronized (lockReaderState)
		{
            if(state == ReaderState.Inventorying)
                this.readerState.value &= ~ReaderState.TagReading.value;
            else if(state == ReaderState.TagReading)
                this.readerState.value &= ~ReaderState.Inventorying.value;

			this.readerState.value |= state.value;
		}
	}
	
    private void UnSetReaderState(ReaderState state)
	{
    	synchronized (lockReaderState)
		{
			this.readerState.value &= ~state.value;
		}
	}
    
    private ReaderState GetReaderState()
	{
		synchronized (lockReaderState)
		{
			return this.readerState;
		}
	}
    
    private boolean IsTagReading = (GetReaderState().value & ReaderState.TagReading.value) > 0 ? true : false;
        
    AsynchronousChannelGroup channelGroup = null;
    AsynchronousSocketChannel socketChannel = null;

    
	public void ConnectSocket(String ipAddr, int port)
	{
		System.out.printf("ConnectSocket( %s  %d )   \n",ipAddr,port);	
		
		this.ipAddr = ipAddr;
        this.socketPort = port;
        
		ReaderEventKind e = ReaderEventKind.Disconnected;
	
        try { 		        			        	
            //Socket socket = new Socket(ipAddr, port);   //1
        	//socket = new Socket(ipAddr, port);
        	//socket.setKeepAlive(true);        	
        	
        	if(socketChannel != null)
        	{
        		socketChannel.close();
        	}        	
        	

        	
        	InetSocketAddress hostAddress = new InetSocketAddress(ipAddr, port);  //2
            //SocketChannel client = SocketChannel.open(hostAddress);
        	
        	 channelGroup = AsynchronousChannelGroup.withFixedThreadPool(

        		    Runtime.getRuntime().availableProcessors(),

        		    Executors.defaultThreadFactory()

        		   );

        	socketChannel = AsynchronousSocketChannel.open(channelGroup);
        	
        	
        	if (!socketChannel.isOpen()) 
        	{ 
        		throw new IOException("fail open AsynchronousSocketChannel"); 
        	}
        	else
        	{
    	    	try {
    	    		//socketChannel.configureBlocking(true);
					//socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
					//socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024); 
					socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true); 						
					//socketChannel.setOption(StandardSocketOptions.SO_LINGER, 5);

				} catch (IOException ex) 
    	    	{
					// TODO Auto-generated catch block
					ex.printStackTrace();
					System.out.println(e);
					

				}
        		
        	}
        	
        	e = ReaderEventKind.None;
            //SetReaderState(ReaderState.Connecting);
            
        	socketChannel.connect(hostAddress,null, new CompletionHandler<Void, Void>() {
        	    @Override

        	    public void completed(Void result, Void attachment) {
       	    	
       	           	OnConneted(); 
         	    	Read();      	
     	    	        	    	
        	    }

        	   @Override

        	   public void failed(Throwable e, Void attachment){
        		   //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Disconnected, "Disconnected : [Close : Not Connected]", this.closeType));
        		   Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Disconnected, "Disconnected : [Close : Not Connected]", closeType.Close));
        	   }

        	});
        	
        } catch (IOException ex) { 
            // TODO Auto-generated catch block
            ex.printStackTrace(); 
        } 
               
		
	}

          

	//read(ByteBuffer dst, A attachment, CompletionHandler<Integer, A> handler);

	//write(ByteBuffer src, A attachment, CompletionHandler<Integer, A> handler);
	
	//ByteBuffer readbuffer = ByteBuffer.allocateDirect(5*1024);
	ByteBuffer readbuffer = ByteBuffer.allocate(128);
	
	public void Read()
	{
		//socketChannel.read(ByteBuffer dst, A attachment, CompletionHandler<Integer, A> handler);	
		
		//ByteBuffer buffer = ByteBuffer.allocateDirect(5*1024);
		//ByteBuffer readbuffer = ByteBuffer.allocate(5*1024);
		
	
		socketChannel.read(readbuffer, readbuffer, new CompletionHandler<Integer, ByteBuffer>() {

    	    @Override

    	    public void completed(Integer result, ByteBuffer attachment) {
    	    	//System.out.println(" read completed ");	
    	    	
    	        try {
    	            
    	        	System.out.println(attachment);    	            
    	        	
    	            ByteBuffer byteBuffer = ByteBuffer.allocate(readbuffer.position());
    	            
    	            byteBuffer = readbuffer;
    	                	            
    	            Analyze(byteBuffer);               
 
    	            readbuffer.clear();
    	            byteBuffer.clear();
    	            
   	            	socketChannel.read(readbuffer, readbuffer, this);

    	            
    	           } 
    	        	catch(Exception e) 
    	        	{
    	        		System.out.println(e);    	
    	        	}

    	    }

    	   @Override

    	   public void failed(Throwable e, ByteBuffer attachment){

    		   System.out.println(" read failed ");
    		   System.out.println(e);
    		   
    		   if(socketChannel.isOpen())
    		   {
    			   //Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Disconnected, "Disconnected : [Close : Not Connected]", closeType.Close));
    			   closeType = closeType.Close;
    			   ReaderEventKind re = ReaderEventKind.Disconnected;
    	    		String message = "Disconnected : [Close : Not Connected]";        	
    	            
    	    		Visited.raiseEvent(this, new ReaderEventArgs(re, message, closeType));
    		   }
    	   }
    	});
		
		
		/*
		ByteBuffer readbuffer = ByteBuffer.allocate(5*1024);
		
		socketChannel.read(readbuffer, null, new CompletionHandler<Integer, Void>() {

    	    @Override

    	    public void completed(Integer result, Void attachment) {
    	    	System.out.println(readbuffer);
    	    	readbuffer.clear();
    	    }

    	   @Override

    	   public void failed(Throwable e, Void attachment){

    	   }
    	});
		 */
	}

	//byte[] rcvBuf;
	int nTotalSend;
	int nTotalReceive;
	
	private void Analyze(ByteBuffer byteBuffer)
	{			
		
		byte[] rcvBuf = byteBuffer.array();
		System.out.println("Analyze() - rcvBuf length  = "+ rcvBuf.length);
				
		if (packetType == PacketType.Default)
		{

                //if (connectType == ConnectType.Tcp)
                //{
					//System.out.println("rcvBuf length  = "+ rcvBuf.length);
				
                    for (int i = 0; i < rcvBuf.length; i++)
                    {
                    	System.out.println("rcvBuf = ["+ i +"]  "+ rcvBuf[i]);	
                    	
                        if (rcvBuf[i] == '>')
                        {
   
                            // skip single char command-echo : 'f', '3', ...
                            //int idx_etx = Array.indexOf(rcvBuf, (byte)0x0A, i);
                        	
                            String s = new String(rcvBuf);
                            //int idx_etx = s.indexOf((byte)0x0A, i);
                            int idx_etx = s.indexOf((byte)0x0A);
                             
                            System.out.println("idx_etx = " + idx_etx);
                            
                            if (idx_etx >= i + 4)
                            {
                            	//System.out.println("test[1] = " + i + "  "+idx_etx);
                                //if (idx_etx <= nTotalReceive)
                                {                                	
                                    if (rcvBuf[idx_etx - 1] == 0x0D)
                                    {              
                                    	
                                    	String data = s.substring(i, idx_etx - i + 1);                               	
                                   	    //String data = Encoding.ASCII.GetString(rcvBuf, i, idx_etx - i + 1);
                                        
                                        byte[] arr = new byte[idx_etx - i + 1];
                                        //Array.Copy(rcvBuf, i, arr, 0, idx_etx - i + 1);
                                        System.arraycopy(rcvBuf, i, arr, 0, idx_etx - i + 1);
                                        ParseEvent(data, arr);
                                        //ParseEvent(data, Encoding.ASCII.GetBytes(data));
                                         
                                        i = idx_etx;
                                       
                                    }
                                }
                            }                            
                        }
                        
                        
                        
                    }
                }
                else//{#19} ConnectType.Serial
                {
 
                //    this.sbPacket.Append(Encoding.ASCII.GetString(packet));

                //    foreach (string parsed_packet in ParsePacket(ref sbPacket))
                //    {
                //        ParseEvent(parsed_packet, Encoding.ASCII.GetBytes(parsed_packet));
                //    }
                }

                currentEvent = ReaderEventKind.None;
                //rcvBuf.Initialize();                
                //rcvBuf = new byte[128];
                
                
                

		}
	
		/*
		else if (packetType == PacketType.Variable)
		{
			CommandType category = (CommandType)rcvBuf[0];
			switch (category)
			{
				case CommandType.ftp:
					ftp();
					break;
			}
		}
		else if (packetType == PacketType.Monitor) 
		{//{#22}
			if (monitorState == MonitorState.Monitor) 
			{
				ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagCounts, string.Empty, rcvBuf, nTotalReceive));
				monitorState = MonitorState.Start;
				watch.Start();
			}
			else if (monitorState == MonitorState.Start) 
			{
				monitorState = MonitorState.Report;
			}
			else if (monitorState == MonitorState.Report) 
			{
				bool bComplete = nTotalReceive != 54;
				if (bComplete)
					UnSetReaderState(ReaderState.Monitoring);
				ReaderEventKind kind = bComplete ? ReaderEventKind.TagReportCompleted : ReaderEventKind.TagReport;
				ReaderEvent(this, new ReaderEventArgs(kind, rcvBuf, nTotalReceive));
			}
		}
		else
		{
		}
		*/
	//}

	String _returnvalue = "";
	
	 private void ParseEvent(String txt, byte[] tmpbuff)
     {
		 System.out.println("ParseEvent()");
		 System.out.println("txt = " + txt.toString() + "length = " + txt.length());
	 
         //if(txt.substring(0, 1) == ">") // && txt.Substring(txt.Length - 1, 1) == "\n")
		 if(txt.substring(0,1).equals(">"))
         {
             String returncmd = txt.substring(2,3);             
             System.out.println("returncmd  = " + returncmd + "   IsTagReading = " + this.IsTagReading);
             switch (returncmd)
             {
                 case "T": // Inventory , Read
                     if(this.IsTagReading)  // Tag Reading
                     {                    	 
                         this.readerState.value &= ~ReaderState.TagReading.value;
                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, string.Empty, tmpbuff, tmpbuff.Length));
                         Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, "", tmpbuff, tmpbuff.length));
                     }
                     else // Tag Inventory
                     {
                         //if (txt.substring(txt.length() - 5, 1) == "|")
                         if (txt.substring(txt.length() - 5, txt.length() - 4).equals("|"))
                         {
                             if (ApprovalCheckSum(txt.substring(0, txt.length() - 2)) == true)
                             {
                                 Byte[] data = new Byte[tmpbuff.length - 3];
                                 //Array.Copy(tmpbuff, 0, data, 0, tmpbuff.length - 5);
                                 System.arraycopy(tmpbuff, 0, data, 0, tmpbuff.length - 5);
                                 //Array.Copy(new Byte[] { 0x0D, 0x0A }, 0, data, data.length - 2, 2);
                                 System.arraycopy(new Byte[] { 0x0D, 0x0A }, 0, data, data.length - 2, 2);
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagId, String.Empty, data, data.length));
                                 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.TagId, "", tmpbuff, tmpbuff.length));
                             }
                         }
                         else
                         {
                             ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagId, string.Empty, tmpbuff, tmpbuff.Length));
                        	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.TagId, "", tmpbuff, tmpbuff.length));
                             ////System.Diagnostics.Debug.WriteLine(DateTime.Now.ToString());                            
                             
                         }
                     }
                     break;
                 case "G": // GPIO
                     try
                     {
                         //if (txt.substring(5, 2) == "99")
                    	 if (txt.substring(5, 7).equals("99"))
                         {
                             ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOPress, string.Empty, tmpbuff, tmpbuff.Length));
                        	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOPress, "", tmpbuff, tmpbuff.length));
                         }
                         //else if (txt.substring(5, 2) == "02")
                         else if (txt.substring(5, 7).equals("02"))
                         {
                             ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIORelease, string.Empty, tmpbuff, tmpbuff.Length));
                        	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIORelease, "", tmpbuff, tmpbuff.length));
                         }
                         //else if (txt.substring(2, 1) == "2") // GPIO State
                         else if (txt.substring(2, 3).equals("2")) // GPIO State
                         {
                             ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOState, string.Empty, tmpbuff, tmpbuff.Length));
                        	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOState, "", tmpbuff, tmpbuff.length));
                         }
                     }
                     catch (Exception e)
                     {
                         ////ReaderEvent(this, new ReaderEventArgs(currentEvent, string.Empty, tmpbuff, tmpbuff.Length));
                    	 Visited.raiseEvent(this, new ReaderEventArgs(currentEvent, "", tmpbuff, tmpbuff.length));
                     }
                     break;
                 case "S": // Stop Operation
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.StopOperation, string.Empty, tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.StopOperation, "", tmpbuff, tmpbuff.length));
                     break;
                 case "R": // RSSI
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.RSSI, string.Empty, tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.RSSI, "", tmpbuff, tmpbuff.length));
                     break;
                 case "H": // Heart Beat
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.StateReport, "HeartBeat", tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.StateReport, "", tmpbuff, tmpbuff.length));
                     break;
                 case "C": // Response Code
                     //if (txt.substring(3, 2) == "01")
                     if (txt.substring(3, 5).equals("01"))
                     {
                         //UnSetReaderState(ReaderState.Inventorying);
                         this.readerState.value &= ~ReaderState.Inventorying.value;

                         synchronized (lockobject)
                         {
                             _returnvalue = txt;
                         }

                         //_resetevent.Set();
                     }
                     //if (currentEvent == ReaderEventKind.GetTagMemory ||
                     //    currentEvent == ReaderEventKind.SetTagMemory ||
                     //    currentEvent == ReaderEventKind.LockTag ||
                     //    currentEvent == ReaderEventKind.KillTag)
                     //    ReaderEvent(this, new ReaderEventArgs(currentEvent, string.Empty, tmpbuff, tmpbuff.Length));
                     //else
                     String responseMessage = "";
                     
                     try 
                     { 
                    	 //responseMessage = responses[txt.substring(3, 2)];
                    	 responseMessage = responses.get(txt.substring(3, 5)); 
                     }
                     catch (Exception e)
                     { 
                    	 System.out.println("Exception  = " + e);
                    	 responseMessage = "Unknown"; 
                     }
                     
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responseMessage, tmpbuff, tmpbuff.Length));
                     Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responseMessage, tmpbuff, tmpbuff.length));
                     break;
                 case "B": // Block Read
                     //if (txt.substring(txt.length() - 5, 1) == "|")
                	 if (txt.substring(txt.length() - 5, txt.length() - 4) == "|")
                     {
                         if (ApprovalCheckSum(txt.substring(0, txt.length() - 2)) == true)
                         {
                             Byte[] data = new Byte[tmpbuff.length - 3];
                             //Array.Copy(tmpbuff, 0, data, 0, tmpbuff.Length - 5);
                             System.arraycopy(tmpbuff, 0, data, 0, tmpbuff.length - 5);
                             //Array.Copy(new Byte[] { 0x0D, 0x0A }, 0, data, data.Length - 2, 2);
                             System.arraycopy(new Byte[] { 0x0D, 0x0A }, 0, data, data.length - 2, 2);
                             ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, string.Empty, data, data.Length));
                             Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, "", tmpbuff, tmpbuff.length));
                         }
                     }
                     else
                     {
                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, string.Empty, tmpbuff, tmpbuff.Length));
                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemory, "", tmpbuff, tmpbuff.length));
                     }                        
                     break;
                 case "*":
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemoryEx, string.Empty, tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GetTagMemoryEx, "", tmpbuff, tmpbuff.length));
                	 
                     break;
                 case "^":
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.CheckSumEnable, string.Empty, tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.CheckSumEnable, "", tmpbuff, tmpbuff.length));
                     break;
                 case "Z": // Log Data
                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.LogData, string.Empty, tmpbuff, tmpbuff.Length));
                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.LogData, "", tmpbuff, tmpbuff.length));
                     break;
                 default:
                     if (txt.startsWith(">#PPAC"))
                     {
                         //switch (txt.substring(6, 2))
                    	 switch (txt.substring(6, 8))
                         {
                             case "IN": // Sio Connected
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.SioConnected, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.SioConnected, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "IS": // Sio Input Status
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.SioInputStatus, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.SioInputStatus, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "OS": // Sio Output Status
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.SioOutputStatus, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.SioOutputStatus, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "BT": // Sio Input Event - Rising Edge
                             case "BF": // Sio Input Event - Falling Edge
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.SioInputTrigger, txt.Substring(7, 1) == "T" ? "Signal Rising Edge" : "Signal Falling Edge", tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.SioInputTrigger, txt.substring(7, 8) == "T" ? "Signal Rising Edge" : "Signal Falling Edge", tmpbuff, tmpbuff.length));
                                 break;
                             case "GV": // Sio Firmware Version
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.SioVersion, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.SioVersion, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "SS": // Sensor Port Status
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchSensorStatus, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchSensorStatus, "", tmpbuff, tmpbuff.length));
                                 break;
                         }
                     }
                     else
                     {
                         //returncmd = txt.substring(1, 1);
                         returncmd = txt.substring(1, 2);
                         switch (returncmd)
                         {
                             case "v":
                                 //if (txt.substring(0, 3) == ">v9")
                            	 if (txt.substring(0, 3).equals(">v9"))
                                 {
                                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.State, states[txt.Trim().Substring(3)], tmpbuff, tmpbuff.Length));
                            		 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.State, states.get(txt.substring(0, 4)), tmpbuff, tmpbuff.length));
                                 }
                                 else
                                 {
                                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Version, string.Empty, tmpbuff, tmpbuff.Length));
                                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Version, "", tmpbuff, tmpbuff.length));
                                 }
                                 break;
                             case ".":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.StateReportTerm, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.StateReportTerm, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "e":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.AntennaState, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.AntennaState, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "p":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Power, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Power, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "s":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Session, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Session, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "b":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Buzzer, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Buzzer, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "y":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagInformations, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.TagInformations, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "A":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryAutoRun, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryAutoRun, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "O":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventorySubMode, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventorySubMode, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "t":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryRunningTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryRunningTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "0":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryPurgeCycle, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryPurgeCycle, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "!":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryBufferClearTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryBufferClearTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "6":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryCompetitionTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryCompetitionTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "7":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryCompetitionValidCount, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryCompetitionValidCount, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "8":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryIntervalScanTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryIntervalScanTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "9":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryIntervalIdleTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryIntervalIdleTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "3":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryOnceIdleTime, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.InventoryOnceIdleTime, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "r":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.IpAddress, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.IpAddress, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "2":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.WritePort, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.WritePort, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "x":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.OperationMode, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.OperationMode, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "F":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.FrequencyBand, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.FrequencyBand, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "n":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ChannelNumber, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ChannelNumber, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "o":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.FHSS, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.FHSS, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "c":
                                 //ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ContinueMode, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ContinueMode, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "q":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.QValue, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.QValue, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "Q":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.AlgorithmParameter, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.AlgorithmParameter, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "%":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.Power, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.Power, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "D":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.DebugMode, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.DebugMode, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "J":
                             case "G":
                                 ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOState, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOState, "", tmpbuff, tmpbuff.length));
                                 break;
                             case "C":
                                 //if (txt.substring(1, 2) == "CI")
                            	 if (txt.substring(1, 3).equals("CI"))
                                 {
                                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOMapCommand, string.Empty, tmpbuff, tmpbuff.Length));
                                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOMapCommand, "", tmpbuff, tmpbuff.length));
                                 }
                                 else
                                 {
                                     ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOBlinkTime, string.Empty, tmpbuff, tmpbuff.Length));
                                	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.GPIOBlinkTime, "", tmpbuff, tmpbuff.length));
                                 }
                                 break;
                             case "w":
                                 //switch (txt.substring(2, 1)) 
                            	 switch (txt.substring(2, 3))
                                 {
                                     case "a":
                                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchOperationMode, string.Empty, tmpbuff, tmpbuff.Length));
                                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchOperationMode, "", tmpbuff, tmpbuff.length));
                                         break;
                                     case "b":
                                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaSwingOption, string.Empty, tmpbuff, tmpbuff.Length));
                                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaSwingOption, "", tmpbuff, tmpbuff.length));
                                         break;
                                     case "c":
                                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaPort, string.Empty, tmpbuff, tmpbuff.Length));
                                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaPort, "", tmpbuff, tmpbuff.length));
                                         break;
                                     case "d":
                                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaDuration, string.Empty, tmpbuff, tmpbuff.Length));
                                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchAntennaDuration, "", tmpbuff, tmpbuff.length));
                                         break;
                                     case "s":
                                         ////ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchSensorStatus, string.Empty, tmpbuff, tmpbuff.Length));
                                    	 Visited.raiseEvent(this, new ReaderEventArgs(ReaderEventKind.ExtSwitchSensorStatus, "", tmpbuff, tmpbuff.length));
                                         break;
                                 }
                                 break;
                             default:
                                 ////ReaderEvent(this, new ReaderEventArgs(currentEvent, string.Empty, tmpbuff, tmpbuff.Length));
                            	 Visited.raiseEvent(this, new ReaderEventArgs(currentEvent, "", tmpbuff, tmpbuff.length));
                                 break;
                         }
                     }

                     //ReaderEvent(this, new ReaderEventArgs(currentEvent, string.Empty, tmpbuff, tmpbuff.Length));
                     break;
             }
         }
     }
	
	 
     /// <summary>
     /// Approval Checksum
     /// </summary>
     /// <param name="hexstring"></param>
     /// <param name="checksum"></param>
     /// <returns></returns>
     public Boolean ApprovalCheckSum(String value)
     {
    	 return true;
    	 /*
         if (value.index("|") > 5)        	 
         {
             String[] values = value.Split(new String[] { "|" }, StringSplitOptions.None);
             Byte[] data = Encoding.ASCII.GetBytes(values[0]);

             Byte checksum = 0x00;

             for (int i = 0; i < data.Length; i++)
             {
                 checksum ^= data[i];
             }

             if (BitConverter.ToString(new Byte[] { checksum }) == values[1])
             {
                 return true;
             }
             else
             {
                 return false;
             }
         }
         else
         {
             return false;
         }
         */
     }
     
     
	public synchronized void Write(ByteBuffer buffer)
	{
	

		socketChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {			
    	    @Override

    	    public void completed(Integer result, Void attachment) {
    	    	System.out.println(" write completed ");	
    	    	writebuffer.clear();
    	    }
    	   @Override

    	   public void failed(Throwable e, Void attachment){
    		   System.out.println(" write failed ");
    	   }

    	});

		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}
	
	SocketHandler ReadSocketHandler = new SocketHandler();
	 
	public void OnConneted()
	{
		System.out.println("OnConneted()");
			
		ReaderEventKind e = ReaderEventKind.Disconnected;
		String message = Unknown;
		
		//Thread thread = new Thread(socketHandler);
		
		
		this.IsRunningThread = true;
		
		//if(!ReadSocketHandler.isInterrupted())
		//ReadSocketHandler.start();
		
		
		 e = ReaderEventKind.Connected;
         message = e.toString();
         
         //UnSetReaderState(ReaderState.Connecting);
                         
		Visited.raiseEvent(this, new ReaderEventArgs(e,message,null,0));

		
	}
	
	CloseType closeType = CloseType.None;

	public void Close(CloseType closeType)
	{
		this.closeType = closeType;
       // boolean isrunning = this.IsRunningThread;
       // this.IsRunningThread = false;

        System.out.println("API.Close(1)");
        
        if(channelGroup!=null && !channelGroup.isShutdown()) {
 		   System.out.println("API.Close(2)");
 		   
 	    try 
 	    {
 	    	channelGroup.shutdownNow();

			if(channelGroup.isShutdown())
			{
				System.out.println("API.Close(3)");			
			}

			ReaderEventKind e = ReaderEventKind.Disconnected;
    		String message = "Disconnected : [Close : Not Connected]";        	
            
    		Visited.raiseEvent(this, new ReaderEventArgs(e, message, this.closeType));                     
		} 
 	    catch (IOException e)
 	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 	   }

	}
	
     private boolean IsRunningThread = false;
     
     
	public class SocketHandler extends Thread
	{
		public SocketHandler()
		{			
			
		}
		
		public void run()
		{ 
        	
	        	System.out.println("SocketHandler_Thread.run");
	        	System.out.println(IsRunningThread);
	        	
                while (IsRunningThread)
                {
                	Read();

                }
		}
	}
	


	public class PACKET
	{
		public boolean bResponseRequired;
		public byte[] packet;
		public PACKET(byte[] payload, int length, boolean bResponseRequired)
		{
			this.bResponseRequired = bResponseRequired;
			//PACKET FORMAT : [0x3e],payload,[0x0d],[0x0a]
			packet = new byte[length + 3];//include start, end signal
			packet[0] = 0x3e;
			//Array.Copyof(payload, 0, packet, 1, length); // C#
			System.arraycopy(payload, 0, packet, 1, length); //java
			packet[length + 1] = 0x0d;
			packet[length + 2] = 0x0a;
		}
	}

		
		//List<PACKET> packets = new List<PACKET>();  //C#
		LinkedList<PACKET> packets = new LinkedList<PACKET>();  //java
		//private object lockOfPackets = new object();
		
		PACKET packet;	
		byte[] addpacket;
		ByteBuffer writebuffer;
		
		void AddPacket(byte[] payload, int length, boolean bResponseRequired)
		{
				
			//PACKET packet = new PACKET(payload, length, bResponseRequired);
			System.out.println("AddPacket");
			//packet = new PACKET(payload, length, bResponseRequired);		
			
			//this.bResponseRequired = bResponseRequired;
			//PACKET FORMAT : [0x3e],payload,[0x0d],[0x0a]
			addpacket = new byte[length + 3]; //include start, end signal
			addpacket[0] = 0x3e;
			//Array.Copyof(payload, 0, packet, 1, length); // C#
			System.arraycopy(payload, 0, addpacket, 1, length); //java
			//System.arraycopy(payload, 0, addpacket, 1, length); //java
			addpacket[length + 1] = 0x0d;
			addpacket[length + 2] = 0x0a;
			
			
			//packets.add(packet);
			
			writebuffer = ByteBuffer.wrap(addpacket);			
			Write(writebuffer);
		}

		ReaderEventKind currentEvent = ReaderEventKind.None;

		public void ReadTagId(TagType tagType, ReadType readType)		
		{
			System.out.println("ReadTagId");	    
			//System.out.printf("ReadTagId = %d %d",tagType,readType);
			
			SetReaderState(ReaderState.Inventorying);			
			currentEvent = ReaderEventKind.TagId;
						
			int result = tagType.compareTo(TagType.ISO18000_6B);
			
			System.out.printf("compareTo = %d ",result);
			
			if(result == 0)
			{			
				System.out.printf("TagType = %d ",TagType.ISO18000_6B.ordinal());
			}
			else				
			{
				byte type = (byte)(
						readType.ordinal() == ReadType.MULTI.ordinal() ? 'f' :
						readType.ordinal() == ReadType.ONE.ordinal() ? 'e' :
                        readType.ordinal() == ReadType.SELECT.ordinal() ? 'g' :
                        readType.ordinal() == ReadType.INTERVAL.ordinal() ? 'i' :
                        readType.ordinal() == ReadType.EXTERNALSWITCH.ordinal() ? 'W' :
                        'f' // Interval
						);
					AddPacket(new byte[] { type }, 1, false);				
			}
			
		}
		
		public void StopOperation()
		{
			System.out.println("StopOperation()");
			
			//UnSetReaderState(ReaderState.Inventorying);
			currentEvent = ReaderEventKind.None;
			AddPacket(new byte[] { (byte)'3' }, 1, false);
		}
		

		  public Event<ReaderEventArgs> Visited = new Event<ReaderEventArgs>();
			  
			  public final class Event<TEventArgs extends EventArgs> {
			  	// Event Handler List
			  	private ArrayList<IEventHandler<TEventArgs>> observerList = new ArrayList<IEventHandler<TEventArgs>>();
			  	
			  	// Raise Event
			  	public void raiseEvent(Object sender, TEventArgs e) {
			  		for(IEventHandler<TEventArgs> handler : this.observerList) {
			  			handler.OnReaderEvent(sender, e);
			  		}
			  	}
			  	
			  	// Add Event Handler
			  	public void addEventHandler(IEventHandler<TEventArgs> handler) {
			  		this.observerList.add(handler);
			  	}
			  	
			  	// Remove Event Handler
			  	public void removeEventHandler(IEventHandler<TEventArgs> handler) {
			  		this.observerList.remove(handler);
			  	}
			  }
	
			  public interface IEventHandler<TEventArgs extends EventArgs> {
			  	public void OnReaderEvent(Object sender, TEventArgs e);
			  } 
			  
			  public class EventArgs {

			  } 
			  
			  
			  public class ReaderEventArgs extends EventArgs {
				  
					private ReaderEventKind kind;
					private String message;
					private byte[] payload;
					private CloseType closeType = CloseType.None;
					int length;
					
				  	public ReaderEventArgs(ReaderEventKind kind, String message, byte[] payload, int length ) {
				  		this.kind = kind;
				  		this.message = message;
				  		this.payload = payload;
				  		this.length =  length;
				  	}
				  	
				  	public ReaderEventArgs(ReaderEventKind kind, String message, CloseType closeType ) {
				  		this.kind = kind;
				  		this.message = message;
				  		this.closeType =  closeType;
				  	}
				  	
					public ReaderEventArgs(ReaderEventKind kind, String message, byte[] packet, int index, int length)
					{
						this.kind = kind;
						this.message = message;
						if (packet != null) {
							//PACKET FORMAT : [0x3e],payload,[0x0d],[0x0a]
							payload = new byte[length-3-index];//exclude preceding data before [0x3e]
							//Array.(packet, index+1, payload, 0, length-3-index);
							System.arraycopy(packet, index+1, payload, 0, length-3-index);
						}
					}
				  
					public ReaderEventArgs(ReaderEventKind kind, byte[] packet, int length)//{#22}
					{
						this.kind = kind;
						this.message = "";
						
						if (packet != null) {
							payload = new byte[length];
							//Array.Copy(packet, 0, payload, 0, length);
							System.arraycopy(packet, 0, payload, 0, length);
						}
					}
					
				  	public ReaderEventKind getkind() {
				  		return this.kind;
				  	}
				  	
				  	public String getmessage() {
				  		return this.message;
				  	}
				  	
				  	public byte[] getpayload() {
				  		return this.payload;
				  	}
				  	
				  	public CloseType getcloseType() {
				  		return this.closeType;
				  	}
				  
				  	public int getlength() {
				  		return this.length;
				  	}
				
			  } 
			    

			  
			  
			  
				public void GetAntennaState()
				{
					System.out.println("GetAntennaState()");
					currentEvent = ReaderEventKind.AntennaState;
					byte[] payload = new byte[] { (byte)'y', (byte)' ', (byte)'e' };
					AddPacket(payload, payload.length, true);
				}
				
				public void GetPower()
				{
					System.out.println("GetPower()");
					currentEvent = ReaderEventKind.Power;
					byte[] payload = new byte[] { (byte)'y', (byte)' ', (byte)'p' };
					AddPacket(payload, payload.length, true);
				}
				
		        public void GetVersion(int type)
		        {
		        	System.out.println("GetVersion(" + type + ")");
		            currentEvent = ReaderEventKind.Version;
		            String cmd = "y v " + type;
		            //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		            byte[] payload = cmd.getBytes();
		            AddPacket(payload, payload.length, true);
		        }

				public void SetAntennaState(int value)
				{
					System.out.println("SetAntennaState(" + value + ")");
		            if (value < 50)//for sure
		                antennaState = value;
					String cmd = "x e " + value;
					//byte[] payload = Encoding.ASCII.GetBytes(cmd);
					byte[] payload = cmd.getBytes();
		            AddPacket(payload, payload.length, true);
				}
				
			     /// <summary>
		        /// Sets the power. (General Power)
		        /// </summary>
		        /// <param name="value">The value.</param>
				public void SetPower(int value)
				{
					System.out.println("SetPower(" + value + ")");
					String cmd = "x p " + value;
					//byte[] payload = Encoding.ASCII.GetBytes(cmd);
					byte[] payload = cmd.getBytes();
		            AddPacket(payload, payload.length, true);
				}
				
		        /// <summary>
		        /// Sets the power.
		        /// </summary>
		        /// <param name="port">The port.</param>
		        /// <param name="value">The value.</param>
				public void SetPower(int port, String value)
				{
					//port number
					//01~04 : power - RF100=x % 1 0, RF1000=x % 01 0
					//11~14 : register
					//21~24 : cycle time
					//31~34 : duration time
					//String number = port.tostring();
					String number = null;
					
					number.valueOf(port);
					
					if (getIsRf1000Series())
		            {
						if (number.length() == 1)//Antenna Power = 1~4
						{
							number = "0" + number;
						}
					}
					String cmd = "x % " + number + " " + value;
					//byte[] payload = Encoding.ASCII.GetBytes(cmd);
					byte[] payload = cmd.getBytes();
		            AddPacket(payload, payload.length, true);
				}
				
				
				 /// <summary>
		        /// Reads the tag memory.
		        /// bank : Reserved(0), EPC(1), TID(2), User(3)
		        /// </summary>
		        /// <param name="type">The type.</param>
		        /// <param name="bank">The bank.</param>
		        /// <param name="location">The location.</param>
		        /// <param name="length">The length.</param>
		        /// <param name="password">The password.</param>
				public void ReadTagMemory(TagType type, int bank, int location, int length, String password)
				{
		            currentEvent = ReaderEventKind.GetTagMemory;

		           // if ((readerState & ReaderState.Inventorying) > 0)
		            {
		                //if(ReaderEvent != null)
		                //    ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responses["91"], Encoding.ASCII.GetBytes(">9C91\r\n"), 7));
		            }
		            //else
		            {
		                //SetReaderState(ReaderState.TagReading);

		                switch (type)
		                {
		                    case ISO18000_6B:
		                        break;
		                    case ISO18000_6C_GEN2:
		                        String cmd = "r " + bank + " " + location + " " + length;
		                        //if (String.IsNullOrEmpty(password) == false)
		                        if (password.isEmpty() == false)
		                        {
		                            cmd += (" " + password);
		                        }
		                        //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		                        byte[] payload = cmd.getBytes();
		                        AddPacket(payload, payload.length, true);
		                        break;
		                }
		            }
				}
				
		        /// <summary>
		        /// Writes the tag memory.
		        /// data : HEX values
		        /// </summary>
		        /// <param name="type">The type.</param>
		        /// <param name="bank">The bank.</param>
		        /// <param name="location">The location.</param>
		        /// <param name="data">The data.</param>
		        /// <param name="password">The password.</param>
				public void WriteTagMemory(TagType type, int bank, int location, String data, String password)
				{
		            currentEvent = ReaderEventKind.SetTagMemory;

		           // if ((readerState & ReaderState.Inventorying) > 0)
		            {
		           //     if (ReaderEvent != null)
		           //         ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responses["91"], Encoding.ASCII.GetBytes(">9C91\r\n"), 7));
		            }
		           // else
		            {
		                switch (type)
		                {
		                    case ISO18000_6B:
		                        break;
		                    case ISO18000_6C_GEN2:
		                        String cmd = "w " + bank + " " + location + " " + data;
		                        //if (String.IsNullOrEmpty(password) == false)
		                        if (password.isEmpty() == false)
		                        {
		                            cmd += (" " + password);
		                        }
		                        //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		                        byte[] payload = cmd.getBytes();
		                        //Debug.WriteLine(BitConverter.ToString(payload));
		                        AddPacket(payload, payload.length, true);
		                        break;
		                }
		            }
				}
		        /// <summary>
		        /// Writes the tag memory.
		        /// tagId : TEXT
		        /// </summary>
		        /// <param name="tagId">The tag id.</param>
		        /// <param name="password">The password.</param>
				public void WriteTagMemory(String tagId, String password)
				{
		            currentEvent = ReaderEventKind.SetTagMemory;

		           // if ((readerState & ReaderState.Inventorying) > 0)
		            {
		           //     if (ReaderEvent != null)
		           //         ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responses["91"], Encoding.ASCII.GetBytes(">9C91\r\n"), 7));
		            }
		          //  else
		            {
		                int bank = 1;//EPC Memory bank
		                int location = 1;//PC~
		                String cmd = "w " + bank + " " + location + " " + MakeEpcHexFromText(tagId);
		                //if (String.IsNullOrEmpty(password) == false)
		                if (password.isEmpty() == false)
		                {
		                    cmd += (" " + password);
		                }
		                //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		                //Debug.WriteLine(BitConverter.ToString(payload));
		                byte[] payload = cmd.getBytes();
		                AddPacket(payload, payload.length, true);
		            }
				}
				
		        /// <summary>
		        /// Locks the tag.
		        /// </summary>
		        /// <param name="mask">The mask.</param>
		        /// <param name="action">The action.</param>
		        /// <param name="password">The password.</param>
				public void LockTag(String mask, String action, String password)
				{
		            currentEvent = ReaderEventKind.LockTag;

		           // if ((readerState & ReaderState.Inventorying) > 0)
		            {
		           //     if (ReaderEvent != null)
		           //         ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responses["91"], Encoding.ASCII.GetBytes(">9C91\r\n"), 7));
		            }
		           // else
		            {
		                String cmd = "l " + mask + " " + action;
		                //if (String.IsNullOrEmpty(password) == false)
		                if (password.isEmpty() == false)
		                {
		                    cmd += (" " + password);
		                }
		                //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		                //Debug.WriteLine(BitConverter.ToString(payload));
		                byte[] payload = cmd.getBytes();
		                AddPacket(payload, payload.length, true);
		            }
				}
				
		        /// <summary>
		        /// Kills the tag.
		        /// </summary>
		        /// <param name="password">The password.</param>
				public void KillTag(String password)
				{
					currentEvent = ReaderEventKind.KillTag;

		           // if ((readerState & ReaderState.Inventorying) > 0)
		            {
		           //     if (ReaderEvent != null)
		           //         ReaderEvent(this, new ReaderEventArgs(ReaderEventKind.TagResponseCode, responses["91"], Encoding.ASCII.GetBytes(">9C91\r\n"), 7));
		            }
		           // else
		            {
		                String cmd = "k " + password;
		                //byte[] payload = Encoding.ASCII.GetBytes(cmd);
		                //Debug.WriteLine(BitConverter.ToString(payload));
		                byte[] payload = cmd.getBytes();
		                AddPacket(payload, payload.length, true);
		            }
				}
				
				
				public String MakeEpcHexFromText(String tagId)
				{
					String hex = "abc";
					return hex;
				}
				
				public String MakeTextFromHex(String hex)
				{
					String text = "abc";
					return text;
				}
		
		
}









