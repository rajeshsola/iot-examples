const mqtt=require('mqtt')
var conn_opts = {
	protocol : 'mqtt',
	host : 'localhost',	//replace with the host details of MQTT broker
	port :1883,	
	protocolId: 'MQTT', 
  	protocolVersion: 4,
	keepalive: 60,
	clean: true,
	clientId: 'sitewhere.location',
	reconnectPeriod: 1000,
	connectTimeout: 30000
	//username:'xxxx',
	//password:'xxxx'
};
var client  = mqtt.connect(conn_opts);
var now=new Date().toISOString();
var payload = {
	hardwareId: "123-MyRpiSenseHAT-4567890",
	type:"DeviceLocation",
	request: {
 		latitude: "18.5204",
        	longitude: "73.8567",
	        elevation: "560",
        	updateState: false,
	        eventDate: now
	}
};
var pub_opts = {
	qos:1,
	retain:false
};
client.on('connect', function () {
	console.log('connected');
	client.publish('SiteWhere/input/json',JSON.stringify(payload),pub_opts);
	client.end()
});

