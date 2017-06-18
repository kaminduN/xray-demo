package my.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

public class LambdaFunctionHandler implements
		RequestHandler<Object, Object> {

	private static AmazonDynamoDB dynamoDB;

	@Override
	public Object handleRequest(Object input, Context context) {
		context.getLogger().log("input: " + input);
    
		if (input.toString().equals("{}") || input.toString().equals("")) {
			context.getLogger().log("input is empty: abort");
			return "{\"status\":\"error\",\"message\":\"input at lambda function is empty\"}";
		}

		dynamoDB = AmazonDynamoDBClientBuilder.standard()
                        .withRegion(Regions.EU_WEST_1)
                        .build();

		HashMap<String, String> mapInput = (HashMap<String, String>) input;
		Map<String, AttributeValue> nameKey = new HashMap<String, AttributeValue>();
		
    String name = mapInput.get("name");
		context.getLogger().log("name: " + name);
		nameKey.put("name", new AttributeValue().withS(name));
		
    Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<String, AttributeValueUpdate>();
		attributeUpdates.put("approval", new AttributeValueUpdate()
				.withValue(new AttributeValue().withS("approved")));
		
    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
				.withKey(nameKey).withAttributeUpdates(attributeUpdates)
				.withTableName(TableNames.TEST_TABLE);

    try{
    UpdateItemResult updateItemResult = dynamoDB
				.updateItem(updateItemRequest);
    context.getLogger().log("Result: " + updateItemResult);

    return "{'status':'done'}";
  }catch(ResourceNotFoundException ex){
        context.getLogger().log("Result: Failed"+ ex);
        throw ex;
    }
		
	}

}
