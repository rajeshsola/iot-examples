module.exports = function(app) {
app.dataSources.mydb.autoupdate(‘Weather’, function(err) {
if (err) throw err;
	 console.log(‘Schema created for Weather model: \n’);
});	
