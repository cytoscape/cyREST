pushNodeTable <-
function (name, g) {
vdata <- c()
vtypes <- c()
for(v in 1:length(list.vertex.attributes(g))) {
temp <- get.vertex.attribute(g,list.vertex.attributes(g)[v])
vdata <- append(vdata, temp)
vtypes <- append(vtypes,.type(unlist(temp)))
}

postForm("http://127.0.0.1:2609/cytobridge/JSONNodeTable/", data=toJSON(list(network_name=name, table_headings=.dummy(list.vertex.attributes(g)), table_types=.dummy(vtypes), node_cytobridge_ids=.dummy(get.vertex.attribute(g, 'cytobid')), table_data=as.character(.dummy(vdata)))),style="post")
}
