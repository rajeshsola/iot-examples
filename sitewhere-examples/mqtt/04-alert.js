const mqtt=require('mqtt')
var conn_opts = {
	protocol : 'mqtt',
	host : 'localhost',
	port :1883,	
	protocolId: 'MQTT', 
  	protocolVersion: 4,
	keepalive: 60,
	clean: true,
	clientId: 'sitewhere.alerts',
	reconnectPeriod: 1000,
    	connectTimeout: 30000
	//username:'xxxx',
	//password:'xxxx'
};
var client  = mqtt.connect(conn_opts);
var now=new Date().toISOString();
var cpuload=os.loadavg();
var avg1=cpuload[0];
var payload= {
    hardwareId: "123-MyRpiSense-4567890",
    type:"DeviceAlert",
    request: {
        type: "rpi.cpuoverload",
        level: "Warning",
        message: "CPU Load is very high!!",
        updateState: false,
        eventDate: now,
        metadata: { 
            loadavg: avg1,
            method: "uptime"
        }
    }
}
var pub_opts = {
	qos:1,
	retain:false
};
client.on('connect', function () {
	console.log('connected');
	client.publish('SiteWhere/input/json',JSON.stringify(payload),pub_opts);
	console.log('alert sent successfully');
	client.end()
});

