package org.californium.osfy.demo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;

public class MyCoAPServer extends CoapServer {
    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    private static CoapServer server;
    public static void main(String[] args) 
    {
        // binds on UDP port 5683
	try {
        server = new MyCoAPServer();
	}catch(SocketException e) {
		e.printStackTrace();
	}
    }
    public MyCoAPServer() throws SocketException
    {
        // "hello"
        this.add(new TemperatureResource());
        // "subpath/Another"
        CoapResource ledpath = new CoapResource("Leds");
        ledpath.add(new LedResource("red"));	//"leds/red"
	ledpath.add(new LedResource("green"));	//"leds/green"
	ledpath.add(new LedResource("blue"));	//"leds/blue"
        this.add(ledpath);

	this.add(new RtcObsResource());	//"rtc"
        this.add(new RemovableResource());	//"removeme!"
	this.add(new WritableResource());	//"writeme!"
	this.start();

    }
    //Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
    private void addEndpoints() {
    	for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
		// only binds to IPv4 addresses and localhost
		if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
			InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
			addEndpoint(new CoapEndpoint(bindToAddress));
		}
	}
    }
    public static class TemperatureResource extends CoapResource {
        public TemperatureResource() {

            // resource identifier
            super("Temperature");

            // set display name
            getAttributes().setTitle("Temperature Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
	    double t=18+Math.round(Math.random()*12);  //random value in range:18-30
            exchange.respond("Current Temperature is:"+t);
        }
    }

    public static class LedResource extends CoapResource {
	String id;
	boolean state;
	String reply="off";
        public LedResource(String id) {
            // resource identifier
	    super(id);
	    this.id=id;
  	    // set display name
            getAttributes().setTitle(id);
        }
        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("State of "+id+" led is "+reply);
        }
	@Override
        public void handlePUT(CoapExchange exchange) {
            byte[] payload = exchange.getRequestPayload();
	    String putstr;
            try {
                putstr = new String(payload, "UTF-8");
		state=putstr.equals("on")
				||putstr.equals("1");	
		reply=state?"on":"off";
                exchange.respond(CHANGED, "request payload:"+putstr);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.respond(BAD_REQUEST, "Invalid input");
            }
        }
    }
    public static class RtcObsResource extends CoapResource {
	private int dataCf = TEXT_PLAIN;
	private boolean wasUpdated = false;
	private String timestr;
        public RtcObsResource() {
            super("RtcData");
	    setObservable(true);
	    getAttributes().setTitle("observable rtc data,5 sec frequency");
	    getAttributes().addResourceType("observe");
	    getAttributes().setObservable();
	    setObserveType(Type.CON);

	    Timer timer = new Timer();
	    timer.schedule(new PeriodicTask(), 0, 5000);
        }
        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.setMaxAge(5);
	    exchange.respond(CONTENT, timestr, dataCf);		
        }
	private class PeriodicTask extends TimerTask {
		@Override
		public void run() {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date time = new Date();
			timestr=dateFormat.format(time);
			dataCf = TEXT_PLAIN;
			// Call changed to notify subscribers
			changed();
		}
	}
    }
    public static class RemovableResource extends CoapResource {
        public RemovableResource() {
            super("removeme!");
        }
	@Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("Resource delete demo!");
        }
        @Override
        public void handleDELETE(CoapExchange exchange) {
            delete();
            exchange.respond(DELETED);
        }
    }
    public static class WritableResource extends CoapResource {

        public String value = "updatable resource,try PUT/POST method";

        public WritableResource() {
            super("updateme!");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond(value);
        }

        @Override
        public void handlePUT(CoapExchange exchange) {
            byte[] payload = exchange.getRequestPayload();
            try {
                value = new String(payload, "UTF-8");
                exchange.respond(CHANGED, value);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.respond(BAD_REQUEST, "Invalid String");
            }
        }
	/** 
	 * In this example POST method keeps appending payload to existing value,
	 * where as PUT method replace current value and GET method retrieves
	 * current value.
	 **/
	@Override
	public void handlePOST(CoapExchange exchange) {
            byte[] payload = exchange.getRequestPayload();
            try {
                value += new String(payload, "UTF-8");
                exchange.respond(CHANGED, value);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.respond(BAD_REQUEST, "Invalid String");
            }
        }
    }

}
