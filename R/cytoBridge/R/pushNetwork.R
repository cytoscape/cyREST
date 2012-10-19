pushNetwork <-
function (name,g, tables=TRUE) {
isGN <- FALSE
if (class(g) == "graphNEL") {
g <- igraph.from.graphNEL(g)
isGN <- TRUE
}
if (!('cytobid' %in% list.graph.attributes(g))) {
cytob.suid <- 0
V(g)$cytobid <- seq(cytob.suid,cytob.suid+length(V(g))-1)
cytob.suid <- cytob.suid + length(V(g))-1
E(g)$cytobid <- seq(cytob.suid,cytob.suid+length(E(g))-1)
cytob.suid <- cytob.suid + length(E(g))-1
g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
} else {
cytob.suid <<- get.graph.attribute(g, "cytobid")

V(g)$cytobid <- lapply(V(g)$cytobid,.idUpdate)

E(g)$cytobid <- lapply(E(g)$cytobid,.idUpdate)

g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
}

postForm("http://127.0.0.1:2609/cytobridge/JSONNetwork/",data=toJSON(list(network_name=name, node_cytobridge_ids=get.vertex.attribute(g, 'cytobid'), edge_cytobridge_ids=get.edge.attribute(g, 'cytobid'),  edge_source_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,1]), edge_target_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,2]))), style="post")

if (tables) { pushTables(name, g) }

if (isGN) { g <- igraph.to.graphNEL(g) }

g
}
