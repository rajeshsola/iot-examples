const request=require('request'),
      url=require('url');
var auth = new Buffer('admin:password').toString('base64');
var siteToken = 'bb105f8d-3150-41f5-b9d1-db04965668d3';	//construction site
var urlopts={
	url:"http://127.0.0.1:8080/sitewhere/api/sites/"+siteToken+"/alerts",
	headers : {
		'Authorization' : 'Basic ' + auth,
		'X-Sitewhere-Tenant': 'sitewhere1234567890'
	}
};
request.get(urlopts,function(error,response,body) {
  if(error)
	  console.log('error:', error); 
  console.log('statusCode:', response && response.statusCode);
  console.log('body:', JSON.parse(body,"",4));
});
