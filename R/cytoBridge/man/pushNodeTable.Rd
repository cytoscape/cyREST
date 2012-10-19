\name{pushNodeTable}
\alias{pushNodeTable}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
%%  ~~function to do ... ~~
}
\description{
%%  ~~ A concise (1-5 lines) description of what the function does. ~~
}
\usage{
pushNodeTable(name, g)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{name}{
%%     ~~Describe \code{name} here~~
}
  \item{g}{
%%     ~~Describe \code{g} here~~
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
function (name, g) 
{
    vdata <- c()
    vtypes <- c()
    for (v in 1:length(list.vertex.attributes(g))) {
        temp <- get.vertex.attribute(g, list.vertex.attributes(g)[v])
        vdata <- append(vdata, temp)
        vtypes <- append(vtypes, type(unlist(temp)))
    }
    postForm("http://127.0.0.1:2609/cytobridge/JSONNodeTable/", 
        data = toJSON(list(network_name = name, table_headings = dummy(list.vertex.attributes(g)), 
            table_types = dummy(vtypes), node_cytobridge_ids = dummy(get.vertex.attribute(g, 
                "cytobid")), table_data = as.character(dummy(vdata)))), 
        style = "post")
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
