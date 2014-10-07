/**
 * 
 */

function testResult() {
	print("Given table string = " + result + "\n");

	var objResult = eval("(" + result + ")");
	print("Result as JS Object = " + objResult + "\n");
	
	if(objResult === null || objResult === undefined) {
		print("objResult undefined.\n");
		return false;
	}
	
	if(objResult.length !== 1) {
		print("Array length is not 1.\n");
		return false;
	}
	
	var table1 = objResult[0];
	var rows = table1['rows'];
	if(rows === null || rows === undefined) {
		print("rows undefined.\n");
		return false;
	} 
	
	if(table1['primaryKey'] !== 'SUID') {
		print("Primary key test failed.\n");
		return false;
	}
	
	
	return true;
}

testResult();