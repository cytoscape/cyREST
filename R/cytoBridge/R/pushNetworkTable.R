pushNetworkTable <-
function (name, g) {
gdata <- c()
gtypes <- c()
for(i in 1:length(list.graph.attributes(g))) {
temp <- get.graph.attribute(g,list.graph.attributes(g)[i])
gdata <- append(gdata, temp)
gtypes <- append(gtypes, .type(temp))
}

postForm("http://127.0.0.1:2609/cytobridge/JSONNetworkTable/", data=toJSON(list(network_name=name, table_headings=.dummy(list.graph.attributes(g)), table_types=.dummy(gtypes), table_data=as.character(.dummy(gdata)))),style="post")
}
