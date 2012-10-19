pushTables <-
function (name,g, net=FALSE, node=FALSE, edge=FALSE) {
if (!net && !node && !edge) {
net = TRUE
node = TRUE
edge = TRUE
}
if (net) {
pushNetworkTable(name, g)
}
if (node) {
pushNodeTable(name, g)
}
if (edge) {
pushEdgeTable(name, g)
}
}
