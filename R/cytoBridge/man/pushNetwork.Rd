\name{pushNetwork}
\alias{pushNetwork}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
%%  ~~function to do ... ~~
}
\description{
%%  ~~ A concise (1-5 lines) description of what the function does. ~~
}
\usage{
pushNetwork(name, g, tables = TRUE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{name}{
%%     ~~Describe \code{name} here~~
}
  \item{g}{
%%     ~~Describe \code{g} here~~
}
  \item{tables}{
%%     ~~Describe \code{tables} here~~
}
}
\details{
%%  ~~ If necessary, more details than the description above ~~
}
\value{
%%  ~Describe the value returned
%%  If it is a LIST, use
%%  \item{comp1 }{Description of 'comp1'}
%%  \item{comp2 }{Description of 'comp2'}
%% ...
}
\references{
%% ~put references to the literature/web site here ~
}
\author{
%%  ~~who you are~~
}
\note{
%%  ~~further notes~~
}

%% ~Make other sections like Warning with \section{Warning }{....} ~

\seealso{
%% ~~objects to See Also as \code{\link{help}}, ~~~
}
\examples{
##---- Should be DIRECTLY executable !! ----
##-- ==>  Define data, use random,
##--	or do  help(data=index)  for the standard data sets.

## The function is currently defined as
function (name, g, tables = TRUE) 
{
    isGN <- FALSE
    if (class(g) == "graphNEL") {
        g <- igraph.from.graphNEL(g)
        isGN <- TRUE
    }
    if (!("cytobid" \%in\% list.graph.attributes(g))) {
        cytob.suid <- 0
        V(g)$cytobid <- seq(cytob.suid, cytob.suid + length(V(g)) - 
            1)
        cytob.suid <- cytob.suid + length(V(g)) - 1
        E(g)$cytobid <- seq(cytob.suid, cytob.suid + length(E(g)) - 
            1)
        cytob.suid <- cytob.suid + length(E(g)) - 1
        g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
    }
    else {
        cytob.suid <<- get.graph.attribute(g, "cytobid")
        V(g)$cytobid <- lapply(V(g)$cytobid, idUpdate)
        E(g)$cytobid <- lapply(E(g)$cytobid, idUpdate)
        g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
    }
    postForm("http://127.0.0.1:2609/cytobridge/JSONNetwork/", 
        data = toJSON(list(network_name = name, node_cytobridge_ids = get.vertex.attribute(g, 
            "cytobid"), edge_cytobridge_ids = get.edge.attribute(g, 
            "cytobid"), edge_source_cytobridge_ids = get.vertex.attribute(g, 
            "cytobid", get.edges(g, E(g))[, 1]), edge_target_cytobridge_ids = get.vertex.attribute(g, 
            "cytobid", get.edges(g, E(g))[, 2]))), style = "post")
    if (tables) {
        pushTables(name, g)
    }
    if (isGN) {
        g <- igraph.to.graphNEL(g)
    }
    g
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
