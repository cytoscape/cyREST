idUpdate <-
function(x) {
if (is.na(x)) {
cytob.suid <<- cytob.suid +  1
as.integer(cytob.suid)
} else {
x
}
}
