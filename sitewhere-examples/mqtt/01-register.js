const mqtt=require('mqtt')
var conn_opts = {
	protocol : 'mqtt',
	host : 'localhost',	//replace with host details of MQTT broker
	port :1883,	
	protocolId: 'MQTT', 
  	protocolVersion: 4,
	keepalive: 60,
	clean: true,
	clientId: 'sitewhere.register',
	reconnectPeriod: 1000,
	connectTimeout: 30000
	//username:'xxxx',
	//password:'xxxx'
};
var client  = mqtt.connect(conn_opts);
var payload = {
	hardwareId: "123-MyRpiSense-4567890",
	type: "RegisterDevice",
	request: {
		hardwareId: "123-RpiSenseHAT-4567890",					//Unique Id
		specificationToken: "7dfd6d63-5e8d-4380-be04-fc5c73801dfb",		//Rpi
		siteToken: "bb105f8d-3150-41f5-b9d1-db04965668d3"			//Construction site
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

