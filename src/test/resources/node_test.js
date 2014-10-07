function testResult() {

	print("Given String = " + result + "\n");

	var objResult = eval("(" + result + ")");
	print("Result as JS Object = " + objResult + "\n");
	
	if(objResult == null)
		return false;
	
	if(objResult.DecCol != 5.67)
		return false;
	
	var listArray = objResult.TheList;
	if(listArray.length != 2)
		return false;
	
	if(listArray[0] != 5.2)
		return false;
	if(listArray[1] != 4.5)
		return false;
	
	
	return true;
}

testResult();