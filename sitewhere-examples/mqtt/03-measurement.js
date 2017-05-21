const mqtt=require('mqtt');
const os=require('os');
var conn_opts = {
	protocol : 'mqtt',
	host : 'iserver.pune.cdac.in',
	port :1883,	
	protocolId: 'MQTT', //'MQIsdp',
  	protocolVersion: 4,   //3
	keepalive: 60,
	clean: true,
	clientId: 'sitewhere.publish',
	reconnectPeriod: 1000,
	connectTimeout: 30000
	//username:'xxxx',
	//password:'xxxx'
};
var client  = mqtt.connect(conn_opts);
var now=new Date().toISOString();
var payload = {
	hardwareId: "123-MyRpiSense-4567890",
	type: "DeviceMeasurements",
	request: {
		measurements: { 
        	"rpi.loadavg1": 0,
        	"rpi.loadavg5": 0,
		"rpi.freemem": 0
        },
        updateState: true,
        eventDate: now
	}
};
var pub_opts = {
	qos:1,
	retain:false
};
var pub_interval=5000;
client.on('connect', function () {
	console.log('connected');
	setInterval( function() {
		var cpuload=os.loadavg();
		payload.request.measurements['rpi.loadvag1']=cpuload[0];
		payload.request.measurements['rpi.loadvag5']=cpuload[1];
		payload.request.measurements['rpi.freemem']=(100 - (os.freemem()/os.totalmem())*100).toFixed(2);
		payload.eventDate=new Date().toISOString();
		client.publish('SiteWhere/input/json',JSON.stringify(payload),pub_opts);
		console.log('published measurements');
	},pub_interval);
	client.end()
});

