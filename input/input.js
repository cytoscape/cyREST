var com = { qmino : { miredot : {}}};
com.qmino.miredot.restApiSource = {"licenceType":"FREE","projectVersion":"1.0.0-beta","allowUsageTracking":true,"jsonDocHidden":true,"licenceHash":"96920848577151415","licenceErrorMessage":null,"miredotVersion":"1.4.1","validLicence":true,"projectTitle":"RESTful API for Cytoscape v1","projectName":"cyREST: RESTful API module for Cytoscape (org.cytoscape.rest.cy-rest)","dateOfGeneration":"2014-10-13 16:08:08","jsonDocEnabled":false};
com.qmino.miredot.restApiSource.tos = {
	org_cytoscape_rest_internal_model_EdgeData_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_EdgeData_in", "content": [] },
	org_cytoscape_rest_internal_model_EdgeData_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_EdgeData_out", "content": [] },
	org_cytoscape_rest_internal_model_Node_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Node_in", "content": [] },
	org_cytoscape_rest_internal_model_Node_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Node_out", "content": [] },
	org_cytoscape_rest_internal_model_CytoscapeVersion_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_CytoscapeVersion_in", "content": [] },
	org_cytoscape_rest_internal_model_CytoscapeVersion_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_CytoscapeVersion_out", "content": [] },
	org_cytoscape_rest_internal_model_ServerStatus_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_ServerStatus_in", "content": [] },
	org_cytoscape_rest_internal_model_ServerStatus_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_ServerStatus_out", "content": [] },
	org_cytoscape_rest_internal_model_Edge_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Edge_in", "content": [] },
	org_cytoscape_rest_internal_model_Edge_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Edge_out", "content": [] },
	org_cytoscape_rest_internal_model_MemoryStatus_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_MemoryStatus_in", "content": [] },
	org_cytoscape_rest_internal_model_MemoryStatus_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_MemoryStatus_out", "content": [] },
	org_cytoscape_rest_internal_model_NodeData_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NodeData_in", "content": [] },
	org_cytoscape_rest_internal_model_NodeData_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NodeData_out", "content": [] }
};

com.qmino.miredot.restApiSource.enums = {

};
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_in"].content = [ 
	{
		"name": "source",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }
	},
	{
		"name": "target",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_out"].content = [ 
	{
		"name": "source",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }
	},
	{
		"name": "target",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_in"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_in"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_out"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_in"].content = [ 

];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_out"].content = [ 
	{
		"name": "apiVersion",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }
	},
	{
		"name": "cytoscapeVersion",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_ServerStatus_in"].content = [ 
	{
		"name": "apiVersion",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }
	},
	{
		"name": "memoryStatus",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_in"]
	},
	{
		"name": "numberOfCores",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_ServerStatus_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_ServerStatus_out"].content = [ 
	{
		"name": "apiVersion",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }
	},
	{
		"name": "memoryStatus",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_out"]
	},
	{
		"name": "numberOfCores",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_ServerStatus_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_in"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_in"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_out"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_in"].content = [ 

];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_out"].content = [ 
	{
		"name": "usedMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }
	},
	{
		"name": "totalMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }
	},
	{
		"name": "maxMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }
	},
	{
		"name": "freeMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_in"].content = [ 
	{
		"name": "id",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_out"].content = [ 
	{
		"name": "id",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_out"].comment = null;
com.qmino.miredot.restApiSource.interfaces = [
	{
		"beschrijving": "List of all available layout algorithm names. This <strong>does not include yFiles</strong> algorithms due to license issues.",
		"url": "/v1/apply/layouts",
		"http": "GET",
		"title": "Get list of available layout algorithm names",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "string" } }, "comment": "List of layout algorithm names."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "160838297",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Add new edge(s) to the network. Body should include an array of new node names. <pre>\n [\n        {\n                \"source\": SOURCE_NODE_SUID,\n                \"target\": TARGET_NODE_SUID,\n                \"directed\": (Optional boolean value.  Default is True),\n                \"interaction\": (Optional.  Will be used for Interaction column.)\n        } ...\n ]\n </pre>",
		"url": "/v1/networks/{networkId}/edges",
		"http": "POST",
		"title": "Add edge(s) to existing network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUIDs of the new edges with source and target SUIDs."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-868911793",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "In general, a network has one view. But if there are multiple views, this deletes all of them.",
		"url": "/v1/networks/{networkId}/views/",
		"http": "DELETE",
		"title": "Delete all network views",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1980812267",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/views/",
		"http": "POST",
		"title": "Create view for the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUID for the new Network View."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-674835444",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "DELETE",
		"title": "Delete all groups in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1244466579",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Cytoscape can have multiple views per network model. This feature is not exposed to end-users, but you can access it from this API.",
		"url": "/v1/networks/{networkId}/views/count",
		"http": "GET",
		"title": "Get number of views for the given network model",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Number of views for the network model"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-262013305",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Generate a PNG network image as stream. Default size is 600 px.",
		"url": "/v1/networks/{networkId}/views/{viewId}.png",
		"http": "GET",
		"title": "Get PNG image of a network view",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "PNG image stream."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1288037935",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "viewId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network View SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [{"name": "h", "defaultValue": "600", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Optional height of the image. Width will be set automatically.", "jaxrs": "QUERY"}],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/{groupNodeId}/collapse",
		"http": "GET",
		"title": "Collapse group nodes",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-976711221",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "groupNodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Group node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "DELETE",
		"title": "Delete all nodes in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1904302687",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID.", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/count",
		"http": "GET",
		"title": "Get number of groups in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Number of groups in the network"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1385114415",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}/adjEdges",
		"http": "GET",
		"title": "Get adjacent edges for a node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "List of connected edges (as SUID)"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1532171535",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Add new node(s) to the network. Body should include an array of new node names. <br/> <pre>\n [ \"nodeName1\", \"nodeName2\", ... ]\n </pre> <br /> Node name will be used for \"name\" column.",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "POST",
		"title": "Add node(s) to existing network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUID of the new node(s) with the name."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1195970986",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}/pointer",
		"http": "GET",
		"title": "Get network pointer (nested network SUID)",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Nested network SUID"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-936620209",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID.", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "target node SUID.", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns/{columnName}",
		"http": "PUT",
		"title": "Update values in a column",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-219122051",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type: \"defaultnode\", \"defaultedge\" or \"defaultnetwork\"", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Target column name", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "GET",
		"title": "Get matching nodes",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "List of matched node SUIDs. If no parameter is given, returns all node SUIDs."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-351293045",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Node table column name to be used for search.", "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Search query.", "jaxrs": "QUERY"}
                ],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "This returns a view for the network.",
		"url": "/v1/networks/{networkId}/views/first",
		"http": "GET",
		"title": "Convenience method to get the first view object.",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1601429362",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "To use this API, you need to know SUID of the CyNetworkView, in addition to CyNetwork SUID.",
		"url": "/v1/networks/{networkId}/views/{viewId}",
		"http": "GET",
		"title": "Get a network view",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "View in Cytoscape.js JSON. Currently, view information is (x, y) location only."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "465131160",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "viewId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network View SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/version",
		"http": "GET",
		"title": "Get Cytoscape and REST API version",
		"tags": ["Server status"],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_out"], "comment": "Cytoscape version and REST API version"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1344679833",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/views/",
		"http": "GET",
		"title": "Get SUID of all network views",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "Array of all network view SUIDs"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1320550666",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/count",
		"http": "GET",
		"title": "Get number of nodes in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Number of nodes in the network with given SUID"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-154879171",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/",
		"http": "POST",
		"title": "Create a new network from Cytoscape.js JSON or Edgelist",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUID of the new network"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1378568763",
		"inputs": {
                "PATH": [],
                "QUERY": [
                    {"name": "collection", "defaultValue": "Posted: ", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Name of new network collection", "jaxrs": "QUERY"},
                    {"name": "source", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. \"url\"", "jaxrs": "QUERY"},
                    {"name": "format", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "\"edgelist\" or \"json\"", "jaxrs": "QUERY"}
                ],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/",
		"http": "GET",
		"title": "Cytoscape RESTful API server status",
		"tags": ["Server status"],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json;charset=utf-8"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_ServerStatus_out"], "comment": "Summary of server status"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "If REST API Module is not working."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1637304040",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/count",
		"http": "GET",
		"title": "Get number of networks in current session",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Number of networks in current Cytoscape session"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1936626815",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges/{edgeId}/{type}",
		"http": "GET",
		"title": "Get source/target node of an edge",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUID of the source/target node"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "519563717",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Edge SUID", "jaxrs": "PATH"},
                    {"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "\"source\" or \"target\"", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/views/first",
		"http": "DELETE",
		"title": "Delete a view whatever found first.",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1956573753",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}/neighbors",
		"http": "GET",
		"title": "Get first neighbors of the node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "Neighbors of the node as a list of SUIDs."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1214382742",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target network SUID.", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Node SUID.", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/session/name",
		"http": "GET",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1394644823",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "This API is for updating default node/edge/network data table at once. If not specified, SUID will be used for mapping.",
		"url": "/v1/networks/{networkId}/tables/{tableType}",
		"http": "PUT",
		"title": "Update table data",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "438214378",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}",
		"http": "GET",
		"title": "Get a node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"], "comment": "Node with associated row data."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1092581726",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "GET",
		"title": "Get all groups in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "List of all groups in the network"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-484652426",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges",
		"http": "DELETE",
		"title": "Delete all edges in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-751167354",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "To update the column name, you need to provide the parameters in the body: <pre>\n {\n                \"oldName\": OLD_COLUMN_NAME,\n                \"newName\": NEW_COLUMN_NAME\n }\n </pre> Both parameters are required.",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "PUT",
		"title": "Update a column name",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "189048185",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type: \"defaultnode\", \"defaultedge\" or \"defaultnetwork\"", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Apply edge bundling with default parameters. Currently optional parameters are not supported.",
		"url": "/v1/apply/edgebundling/{networkId}",
		"http": "GET",
		"title": "Apply Edge Bundling to a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-623876800",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/tables/count",
		"http": "GET",
		"title": "Get number of global tables",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Number of global tables."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-26110140",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}.csv",
		"http": "GET",
		"title": "Get a table as CSV",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["text/plain"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table in CSV format"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1996936384",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"}
                ],
                "QUERY": [{"name": "format", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "If body is empty, it simply creates new network from current selection. Otherwise, select from the list of SUID.",
		"url": "/v1/networks/{networkId}",
		"http": "POST",
		"title": "Create a subnetwork from selected nodes and edges",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "SUID of the new network."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1889261857",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Create a new group from a list of nodes. The Body should be in the following format: <pre>\n        {\n                \"name\": (New group node name),\n                \"nodes\": [\n                        nodeSUID1, nodeSUID2, ...\n                ]\n        }\n </pre>",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "POST",
		"title": "Create a new group",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "New group node's SUID"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1579310846",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges/{edgeId}/isDirected",
		"http": "GET",
		"title": "Get edge directionality",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "boolean" }, "comment": "true if the edge is directed."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "748072261",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target edge SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get list of all Visual Style names. This may not be unique.",
		"url": "/v1/apply/styles",
		"http": "GET",
		"title": "Get list of all Visual Style names",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "string" } }, "comment": "List of Visual Style names."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1450138716",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Returns list of networks as an array of network SUID.",
		"url": "/v1/networks/",
		"http": "GET",
		"title": "Get SUID list of networks",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json; charset=UTF-8"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "Matched networks as list of SUIDs. If no query is given, returns all network SUIDs."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1099067042",
		"inputs": {
                "PATH": [],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Network table column name to be used for search.", "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Search query.", "jaxrs": "QUERY"}
                ],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/",
		"http": "GET",
		"title": "Get available REST API versions",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "map", "typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "string" } } }, "comment": "List of available REST API versions. Currently, v1 is the only available version."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1567347803",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/{groupId}",
		"http": "DELETE",
		"title": "Delete a group",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1469098445",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "groupNodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Group node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges/{edgeId}",
		"http": "GET",
		"title": "Get an edge",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"], "comment": "Edge with associated row data"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "288600029",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Edge SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges/count",
		"http": "GET",
		"title": "Get number of edges in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "number of edges in the network"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1189989470",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}",
		"http": "DELETE",
		"title": "Delete a node in the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1499255901",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns/{columnName}",
		"http": "DELETE",
		"title": "Delete a column in a table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-48657391",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type: \"defaultnode\", \"defaultedge\" or \"defaultnetwork\"", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Name of the column to be deleted", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/rows/{primaryKey}",
		"http": "GET",
		"title": "Get a row in a table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Row in the table"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1678146497",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
                    {"name": "primaryKey", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Name of primary key column", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "GET",
		"title": "Get all columns in the table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "All columns in the table"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-2070327705",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/rows",
		"http": "GET",
		"title": "Get all rows in the table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "All rows in the table"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-805850066",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges/{edgeId}",
		"http": "DELETE",
		"title": "Delete an edge in the network.",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-118074146",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "SUID of the edge to be deleted", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Run System.gc(). In general, this is not necessary.",
		"url": "/v1/gc",
		"http": "GET",
		"title": "Force to run garbage collection to free up memory",
		"tags": ["Server status"],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1471173272",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/",
		"http": "DELETE",
		"title": "Delete all networks in current session",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1245674789",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}",
		"http": "GET",
		"title": "Get a default table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "The Table in JSON"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1923426397",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns/{columnName}",
		"http": "GET",
		"title": "Get all values in the column",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "All values in the column"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1653037714",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Column name", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/apply/styles/{styleName}/{networkId}",
		"http": "GET",
		"title": "Apply Visual Style to a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1624881815",
		"inputs": {
                "PATH": [
                    {"name": "styleName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Visual Style name (title)", "jaxrs": "PATH"},
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target network SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Create new, empty column in an assigned table. This accepts the following object OR allay of this objects: <pre>\n                {\n                        \"name\":\"COLUMN NAME\",\n                        \"type\":\"data type, Double, String, Boolean, Long, Integer\",\n                        \"immutable\": \"Optional: boolean value to specify immutable or not\",\n                        \"list\": \"Optional.  If true, return create List column for the given type.\"\n                }\n </pre>",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "POST",
		"title": "Create new column(s) in the table",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-67693391",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type: \"defaultnode\", \"defaultedge\" or \"defaultnetwork\"", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/{nodeId}",
		"http": "GET",
		"title": "Get group for a node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "A group where the node belongs to"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-875567376",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Networks SUID", "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/groups/{groupNodeId}/expand",
		"http": "GET",
		"title": "Expand group nodes",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "824273598",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "groupNodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Group node SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}",
		"http": "DELETE",
		"title": "Delete a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "878202141",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/",
		"http": "GET",
		"title": "Get all tables assigned to the network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "All tables in JSON"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "578785109",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges",
		"http": "GET",
		"title": "Get matching edges",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "List of matched edge SUIDs. If no parameter is given, returns all edge SUIDs."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "662589552",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Edge table column name to be used for search.", "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Optional. Search query.", "jaxrs": "QUERY"}
                ],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Fit an existing network view to current window.",
		"url": "/v1/apply/fit/{networkId}",
		"http": "GET",
		"title": "Fit network to the window",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "-1608791223",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/rows/{primaryKey}/{columnName}",
		"http": "GET",
		"title": "Get a value in the cell",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "object" }, "comment": "Value in the cell. String, Boolean, Number, or List."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1976423404",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
                    {"name": "primaryKey", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Name of primary key column", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Name of the column", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/apply/layouts/{algorithmName}/{networkId}",
		"http": "GET",
		"title": "Apply layout to a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "1226484092",
		"inputs": {
                "PATH": [
                    {"name": "algorithmName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Name of layout algorithm (\"circular\", \"force-directed\", etc.)", "jaxrs": "PATH"},
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Target network SUID", "jaxrs": "PATH"}
                ],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Generate a PNG image as stream. Default size is 600 px.",
		"url": "/v1/networks/{networkId}/views/first.png",
		"http": "GET",
		"title": "Get PNG image of a network view",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["image/png"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "PNG image stream."},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 500, "comment": "The API call has not succeeded."}
            ],
		"hash": "144494073",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"}],
                "QUERY": [{"name": "h", "defaultValue": "600", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Optional height of the image. Width will be set automatically.", "jaxrs": "QUERY"}],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	}];
com.qmino.miredot.projectWarnings = [
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAXRS_MISSING_PATH_PARAM",
		"description": "A @PathParam is used in the method signature, but not found in the service URL",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	}];
com.qmino.miredot.processErrors  = [
	{
		"class": "org.cytoscape.rest.internal.datamapper.VisualStyleMapper",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualMappingFunction",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.MappingFactoryManager",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualMappingFunctionFactory",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.NetworkFullResource",
		"method": "getNetworks",
		"exception": "java.lang.NullPointerException",
		"exceptionmessage": null,
		"stacktrace": "com.qmino.miredot.model.objectmodel.TypeConstructionInfoContainer@TypeConstructionInfoContainer.java:274|com.qmino.miredot.model.objectmodel.TypeConstructionInfoContainer@TypeConstructionInfoContainer.java:243|com.qmino.miredot.model.objectmodel.JavaTypeFactory@JavaTypeFactory.java:44|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:213|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.NetworkResource",
		"method": "getNetwork",
		"exception": "java.lang.NullPointerException",
		"exceptionmessage": null,
		"stacktrace": "com.qmino.miredot.model.objectmodel.TypeConstructionInfoContainer@TypeConstructionInfoContainer.java:66|com.qmino.miredot.model.objectmodel.TypeConstructionInfoContainer@TypeConstructionInfoContainer.java:41|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:213|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getDefaultValue",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "updateStyleName",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getStyleNames",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getStylCount",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "deleteStyle",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "deleteAllStyles",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "deleteMapping",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getDefaults",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getMappings",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getMapping",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "updateDefaults",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getStyle",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "getStyleFull",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "createStyle",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.resource.StyleResource",
		"method": "addMappings",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2002|com.qmino.miredot.construction.reflection.AnnotationHelper@AnnotationHelper.java:187|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:207|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:112|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.serializer.ContinuousMappingSerializer",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/mappings/ContinuousMapping",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.serializer.DiscreteMappingSerializer",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/mappings/DiscreteMapping",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.serializer.PassthroughMappingSerializer",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/mappings/PassthroughMapping",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	},
	{
		"class": "org.cytoscape.rest.internal.serializer.VisualStyleSerializer",
		"method": "?",
		"exception": "java.lang.NoClassDefFoundError",
		"exceptionmessage": "org/cytoscape/view/vizmap/VisualStyle",
		"stacktrace": "java.lang.Class@Class.java:-2|java.lang.Class@Class.java:2570|java.lang.Class@Class.java:2690|java.lang.Class@Class.java:1467|com.qmino.miredot.construction.reflection.RestInterfaceHandler@RestInterfaceHandler.java:96|com.qmino.miredot.construction.reflection.RestModelFactory@RestModelFactory.java:45|com.qmino.miredot.maven.Mojo@Mojo.java:168|org.apache.maven.plugin.DefaultBuildPluginManager@DefaultBuildPluginManager.java:132|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:208|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:153|org.apache.maven.lifecycle.internal.MojoExecutor@MojoExecutor.java:145|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:116|org.apache.maven.lifecycle.internal.LifecycleModuleBuilder@LifecycleModuleBuilder.java:80|org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder@SingleThreadedBuilder.java:51|org.apache.maven.lifecycle.internal.LifecycleStarter@LifecycleStarter.java:120|org.apache.maven.DefaultMaven@DefaultMaven.java:347|org.apache.maven.DefaultMaven@DefaultMaven.java:154|org.apache.maven.cli.MavenCli@MavenCli.java:582|org.apache.maven.cli.MavenCli@MavenCli.java:214|org.apache.maven.cli.MavenCli@MavenCli.java:158|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:-2|sun.reflect.NativeMethodAccessorImpl@NativeMethodAccessorImpl.java:57|sun.reflect.DelegatingMethodAccessorImpl@DelegatingMethodAccessorImpl.java:43|java.lang.reflect.Method@Method.java:606|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:289|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:229|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:415|org.codehaus.plexus.classworlds.launcher.Launcher@Launcher.java:356|"
	}];

