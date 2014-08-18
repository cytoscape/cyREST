var com = { qmino : { miredot : {}}};
com.qmino.miredot.restApiSource = {"licenceType":"FREE","projectVersion":"0.2.0-SNAPSHOT","allowUsageTracking":true,"jsonDocHidden":true,"licenceHash":"96920848577151415","licenceErrorMessage":null,"miredotVersion":"1.4","validLicence":true,"projectTitle":"RESTful API for Cytoscape v1","projectName":"org.cytoscape.rest.cy-rest","dateOfGeneration":"2014-08-18 12:48:49","jsonDocEnabled":false};
com.qmino.miredot.restApiSource.tos = {
	org_cytoscape_rest_internal_model_NetworkData_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NetworkData_in", "content": [] },
	org_cytoscape_rest_internal_model_NetworkData_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NetworkData_out", "content": [] },
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
	org_cytoscape_rest_internal_model_CyNetworkWrapper_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_CyNetworkWrapper_in", "content": [] },
	org_cytoscape_rest_internal_model_CyNetworkWrapper_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_CyNetworkWrapper_out", "content": [] },
	org_cytoscape_rest_internal_model_NodeData_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NodeData_in", "content": [] },
	org_cytoscape_rest_internal_model_NodeData_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_NodeData_out", "content": [] },
	org_cytoscape_rest_internal_model_Elements_in: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Elements_in", "content": [] },
	org_cytoscape_rest_internal_model_Elements_out: { "type": "complex", "name": "org_cytoscape_rest_internal_model_Elements_out", "content": [] }
};

com.qmino.miredot.restApiSource.enums = {

};
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NetworkData_in"].content = [ 

];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NetworkData_out"].content = [ 

];
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
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_in"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_in"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_out"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_in"].content = [ 

];
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
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_in"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_in"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_EdgeData_out"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_MemoryStatus_in"].content = [ 

];
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
		"name": "freeMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }
	},
	{
		"name": "maxMemory",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "number" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CyNetworkWrapper_in"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NetworkData_in"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CyNetworkWrapper_out"].content = [ 
	{
		"name": "data",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NetworkData_out"]
	},
	{
		"name": "elements",
		"comment": null,
		"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Elements_out"]}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_in"].content = [ 
	{
		"name": "id",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_NodeData_out"].content = [ 
	{
		"name": "id",
		"comment": null,
		"typeValue": { "type": "simple", "typeValue": "string" }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Elements_in"].content = [ 
	{
		"name": "edges",
		"comment": null,
		"typeValue": { "type": "collection", "typeValue":com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_in"] }
	},
	{
		"name": "nodes",
		"comment": null,
		"typeValue": { "type": "collection", "typeValue":com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_in"] }}
];
com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Elements_out"].content = [ 
	{
		"name": "edges",
		"comment": null,
		"typeValue": { "type": "collection", "typeValue":com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"] }
	},
	{
		"name": "nodes",
		"comment": null,
		"typeValue": { "type": "collection", "typeValue":com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"] }}
];
com.qmino.miredot.restApiSource.interfaces = [
	{
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/edges",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-421327531",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get all edges as an array of SUIDs",
		"url": "/v1/networks/{networkId}/edges",
		"http": "GET",
		"title": "Get all edges as an array of SUIDs",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "923506858",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "SUID of the network edges belong to.", "jaxrs": "PATH"}],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}
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
		"url": "/v1/apply/layouts",
		"http": "GET",
		"title": "Get list of available layout algorithm names",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "string" } }, "comment": "List of layout algorithm names."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1834952121",
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
		"beschrijving": "Get number of networks in the current session",
		"url": "/v1/networks/count",
		"http": "GET",
		"title": "Get number of networks in the current session",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Number of networks in current Cytoscape session"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-634574713",
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
		"beschrijving": "Get network pointer (nested network SUID)",
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
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Nested network SUID"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-304874027",
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
		"url": "/v1/styles/",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1549995158",
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
		"url": "/v1/styles/{name}/mappings/{vpName}",
		"http": "DELETE",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-721585162",
		"inputs": {
                "PATH": [
                    {"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "vpName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get the entire view object as JSON.",
		"url": "/v1/networks/{networkId}/views/{viewId}",
		"http": "GET",
		"title": "Get the entire view object as JSON",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "965983784",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "viewId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}
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
		"url": "/v1/styles/{name}/defaults",
		"http": "PUT",
		"title": null,
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
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-647806670",
		"inputs": {
                "PATH": [
                    {"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get adjacent edges for a node",
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
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "List of edge SUIDs"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "166305813",
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
		"beschrijving": "Create a new group node",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "POST",
		"title": "Create a new group node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "New group node's SUID"},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-1799436299",
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
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-1332038601",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network ID", "jaxrs": "PATH"},
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
		"url": "/v1/styles/count",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "989433820",
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
		"beschrijving": "Delete all edges in a network",
		"url": "/v1/networks/{networkId}/edges",
		"http": "DELETE",
		"title": "Delete all edges in a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1030998720",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "target network ID", "jaxrs": "PATH"}],
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
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1527380556",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Expand group nodes",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1739225067",
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
		"url": "/v1/networks/{networkId}/tables/{tableType}",
		"http": "GET",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-102919358",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get all groups for a network",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "GET",
		"title": "Get all groups for a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "List of all groups for the network"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1025189847",
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
		"url": "/v1/styles/{name}/mappings",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "387947386",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get source or target node of a edge",
		"url": "/v1/networks/{networkId}/edges/{edgeId}/{type}",
		"http": "GET",
		"title": "Get source or target node of a edge",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "SUID of the source/target node"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1301847679",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Edge SUID", "jaxrs": "PATH"},
                    {"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "source or target", "jaxrs": "PATH"}
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
		"beschrijving": "Get number of views for the given network model",
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
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Number of views for the network model"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1964627191",
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
		"beschrijving": "Get number of groups for a network",
		"url": "/v1/networks/{networkId}/groups/count",
		"http": "GET",
		"title": "Get number of groups for a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Number of groups for the network"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1808323548",
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
		"url": "/v1/styles/{name}",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1341331300",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [{"name": "format", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/v1/styles/{name}/mappings",
		"http": "DELETE",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1364921833",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get number of nodes in the network",
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
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Number of nodes in the network with given SUID"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-7684489",
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
		"beschrijving": "Add a new node to existing network",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "POST",
		"title": "Add a new node to existing network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "1643555248",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Delete a network",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1146269987",
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
		"url": "/v1/networks/{networkId}/tables/{tableType}/rows/{primaryKey}",
		"http": "GET",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Row in the table"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1164026759",
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
		"beschrijving": "Get a cell entry",
		"url": "/v1/networks/{networkId}/tables/{tableType}/rows/{primaryKey}/{columnName}",
		"http": "GET",
		"title": "Get a cell entry",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "object" }, "comment": "Value in the cell. String, Boolean, Number, or List."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1820248334",
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
		"beschrijving": "Delete a column from a table",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns/{columnName}",
		"http": "DELETE",
		"title": "Delete a column from a table",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1930982837",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
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
		"beschrijving": "Delete all network views",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1406351781",
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
		"url": "/v1/networks/{networkId}/tables/{tableType}.csv",
		"http": "GET",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Entire table as CSV"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-219483066",
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
		"beschrijving": "Delete all nodes in a network",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "DELETE",
		"title": "Delete all nodes in a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "2110833243",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "target network ID.", "jaxrs": "PATH"}],
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
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1941087502",
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
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}",
		"http": "PUT",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-295307612",
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
		"url": "/v1/styles/",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-981334352",
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get the edge is directed or not.",
		"url": "/v1/networks/{networkId}/edges/{edgeId}/isDirected",
		"http": "GET",
		"title": "Get the edge is directed or not",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "boolean" }, "comment": "True if the edge is directed."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1195656523",
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
		"beschrijving": "",
		"url": "/v1/styles/{name}/mappings/{vp}",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-197013538",
		"inputs": {
                "PATH": [
                    {"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "vp", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Delete a group",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1910917382",
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
		"url": "/v1/styles/{name}/defaults/{vpName}",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-509457607",
		"inputs": {
                "PATH": [
                    {"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "vpName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "388692504",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get a network in Cytoscape.js format",
		"url": "/v1/networks/{networkId}",
		"http": "GET",
		"title": "Get a network in Cytoscape",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CyNetworkWrapper_out"], "comment": "Network with all associated table"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-184064712",
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
		"beschrijving": "Delete all groups for a network",
		"url": "/v1/networks/{networkId}/groups/",
		"http": "DELETE",
		"title": "Delete all groups for a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-73449728",
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
		"beschrijving": "Create a subnetwork from selected nodes & edges if body is empty, it simply creates new network from current selection. Otherwise, select from the list of SUID.",
		"url": "/v1/networks/{networkId}",
		"http": "POST",
		"title": "Create a subnetwork from selected nodes & edges if body is empty, it simply creates new network from current selection",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-689842777",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}],
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
                { "httpCode": 500, "comment": "If REST API Module is not working."}
            ],
		"hash": "-816892100",
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
		"url": "/v1/networks/{networkId}/views/first",
		"http": "DELETE",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "33703369",
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
		"url": "/v1/styles/{name}",
		"http": "DELETE",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-2044533260",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Convenience method to get the first view object.",
		"url": "/v1/networks/{networkId}/views/first",
		"http": "GET",
		"title": "Convenience method to get the first view object",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "625211134",
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
		"beschrijving": "Get all tables assigned for the network.",
		"url": "/v1/networks/{networkId}/tables/",
		"http": "GET",
		"title": "Get all tables assigned for the network",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1499226971",
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
		"url": "/v1/styles/{name}/defaults",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1187752202",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Create network from Cytoscape.js style JSON.",
		"url": "/v1/networks/",
		"http": "POST",
		"title": "Create network from Cytoscape",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "797081153",
		"inputs": {
                "PATH": [],
                "QUERY": [
                    {"name": "collection", "defaultValue": "Posted: ", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Name of network collection.", "jaxrs": "QUERY"},
                    {"name": "source", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"},
                    {"name": "format", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}
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
		"url": "/v1/styles/{name}",
		"http": "PUT",
		"title": null,
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-306342718",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "java.io.InputStream" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Get a node",
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
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Node_out"], "comment": "Node with associated table data"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1775338664",
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
		"beschrijving": "Collapse group nodes",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "2118322104",
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
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "GET",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "string" }, "comment": null},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1424710177",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get Cytoscape and API version.",
		"url": "/v1/version",
		"http": "GET",
		"title": "Get Cytoscape and API version",
		"tags": ["Server status"],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CytoscapeVersion_out"], "comment": "Cytoscape version and REST API version"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-2017671443",
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
		"beschrijving": "Get an edge",
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
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_Edge_out"], "comment": "Edge with associated table data"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1138446877",
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
		"beschrijving": "Get all nodes as an array of SUIDs",
		"url": "/v1/networks/{networkId}/nodes",
		"http": "GET",
		"title": "Get all nodes as an array of SUIDs",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-90375739",
		"inputs": {
                "PATH": [{"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}
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
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "map", "typeValue": { "type": "simple", "typeValue": "object" } }, "comment": "List of available REST API versions. Currently, v1 is the only available version."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1462257647",
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
		"url": "/v1/apply/styles",
		"http": "GET",
		"title": "Get list of all Visual Style names.",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "string" } }, "comment": "List of Style names."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-692451602",
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
		"beschrijving": "Apply Visual Style to a network.",
		"url": "/v1/apply/styles/{styleName}/{networkId}",
		"http": "GET",
		"title": "Apply Visual Style to a network",
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "simple", "typeValue": "javax.ws.rs.core.Response" }, "comment": "Success message."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1723305879",
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
		"beschrijving": "",
		"url": "/v1/networks/{networkId}/tables/{tableType}/columns",
		"http": "PUT",
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "1918709114",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Original name of the column to be updated.", "jaxrs": "PATH"}
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
		"beschrijving": "Get group for a node",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1251258595",
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
		"beschrijving": "Get all networks in current session",
		"url": "/v1/networks/",
		"http": "GET",
		"title": "Get all networks in current session",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":com.qmino.miredot.restApiSource.tos["org_cytoscape_rest_internal_model_CyNetworkWrapper_out"] }, "comment": ""},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "219567930",
		"inputs": {
                "PATH": [],
                "QUERY": [
                    {"name": "column", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"},
                    {"name": "query", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"},
                    {"name": "format", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "QUERY"}
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
		"url": "/v1/styles/{name}/mappings",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "788652041",
		"inputs": {
                "PATH": [{"name": "name", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "Delete all networks",
		"url": "/v1/networks/",
		"http": "DELETE",
		"title": "Delete all networks",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1612705621",
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
		"beschrijving": "Delete an edge in a network.",
		"url": "/v1/networks/{networkId}/edges/{edgeId}",
		"http": "DELETE",
		"title": "Delete an edge in a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1686154984",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "network ID", "jaxrs": "PATH"},
                    {"name": "edgeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "SUID of an edge to be deleted", "jaxrs": "PATH"}
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
		"title": null,
		"tags": [],
		"authors": ["kono"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [
                { "httpCode": 200, "comment": "The service call has completed successfully."},
                { "httpCode": 412, "comment": "Invalid JSON input."}
            ],
		"hash": "-545714813",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": "Network SUID", "jaxrs": "PATH"},
                    {"name": "tableType", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Table type (defaultnode, defaultedge or defaultnetwork)", "jaxrs": "PATH"},
                    {"name": "columnName", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": "Target column name.", "jaxrs": "PATH"}
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
		"beschrijving": "Force to run garbage collection to free up memory",
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
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1670119572",
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
		"beschrijving": "Delete a node in a network",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}",
		"http": "DELETE",
		"title": "Delete a node in a network",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": [],
		"roles": [],
		"output": {},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "1227630557",
		"inputs": {
                "PATH": [
                    {"name": "networkId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"},
                    {"name": "nodeId", "typeValue": { "type": "simple", "typeValue": "number" }, "comment": null, "jaxrs": "PATH"}
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
		"beschrijving": "Get number of edges in the network",
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
		"output": {"typeValue": { "type": "simple", "typeValue": "number" }, "comment": "number of edges in the network"},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1042794788",
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
		"beschrijving": "Get first neighbors of node",
		"url": "/v1/networks/{networkId}/nodes/{nodeId}/neighbors",
		"http": "GET",
		"title": "Get first neighbors of node",
		"tags": [],
		"authors": [],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"output": {"typeValue": { "type": "collection", "typeValue":{ "type": "simple", "typeValue": "number" } }, "comment": "Neighbors as a list of SUIDs."},
		"statusCodes": [{ "httpCode": 200, "comment": "The service call has completed successfully."}],
		"hash": "-1417550820",
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
	}];
com.qmino.miredot.projectWarnings = [
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
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
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAXRS_MISSING_CONSUMES",
		"description": "Interface specifies a JAXRS-BODY parameter, but does not specify a Consumes value.",
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
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
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
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
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
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAXRS_MISSING_PATH_PARAM",
		"description": "A @PathParam is used in the method signature, but not found in the service URL",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
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
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": null,
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
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
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
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
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
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
		"description": "Missing parameter documentation",
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
		"category": "JAVADOC_MISSING_AUTHORS",
		"description": "No author(s) specified for interface.",
		"failedBuild": false,
		"interface": null,
		"entity": null
	}];
com.qmino.miredot.processErrors  = [
];

