type <-
function(l) {
if (is.logical(l)) {
"Boolean"
} else if (is.numeric(l)) {
if (all.equal(as.integer(l),l)==TRUE) {
"Integer"
} else {
"Double"
}
} else {
"String"
}
}
