\name{pushNetworkTable}
\alias{pushNetworkTable}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
%%  ~~function to do ... ~~
}
\description{
%%  ~~ A concise (1-5 lines) description of what the function does. ~~
}
\usage{
pushNetworkTable(name, g)
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
    gdata <- c()
    gtypes <- c()
    for (i in 1:length(list.graph.attributes(g))) {
        temp <- get.graph.attribute(g, list.graph.attributes(g)[i])
        gdata <- append(gdata, temp)
        gtypes <- append(gtypes, type(temp))
    }
    postForm("http://127.0.0.1:2609/cytobridge/JSONNetworkTable/", 
        data = toJSON(list(network_name = name, table_headings = dummy(list.graph.attributes(g)), 
            table_types = dummy(gtypes), table_data = as.character(dummy(gdata)))), 
        style = "post")
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
