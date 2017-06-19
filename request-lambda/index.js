const AWSXRay = require('aws-xray-sdk');
var AWS = AWSXRay.captureAWS(require('aws-sdk'));

exports.handler = function(event, context, callback) {

	console.log('REQUEST RECEIVED:\n', JSON.stringify(event));


	var sns = new AWS.SNS();
	var username = event.name;

	var s3 = new AWS.S3();
    var param = {Bucket: process.env.BUCKET_NAME, Key: username, Body: 'me me me'};
	s3.upload(param, function(err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else console.log(data);           // successful response

        console.log('actually done!');
        context.done();
    });

	// Notify
  var params = {
    Message: 'Received name "' + username  + '".',
    Subject: 'User: ' + username,
    TopicArn: process.env.TOPIC_ARN
  };

  sns.publish(params, function(err, data) {
		if (err) {
		  console.log(err, err.stack);
		  callback(err);
		}
		else {
		  console.log(data);
		  callback(null, {"name": username});
		}
  });

}
