pushEdgeTable <-
function(name, g) {
edata <- c()
etypes <- c()
for(e in 1:length(list.edge.attributes(g))) {
temp <- get.edge.attribute(g,list.edge.attributes(g)[e])
edata <- append(edata, temp)
etypes <- append(etypes,.type(unlist(temp)))
}

postForm("http://127.0.0.1:2609/cytobridge/JSONEdgeTable/", data=toJSON(list(network_name=name, table_headings=.dummy(list.edge.attributes(g)), table_types=.dummy(etypes), edge_cytobridge_ids=.dummy(get.edge.attribute(g, 'cytobid')), table_data=as.character(.dummy(edata)))),style="post")
}
