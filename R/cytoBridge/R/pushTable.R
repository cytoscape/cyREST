pushTable <-
function (name, df) {
data <- c()
types <- c()
for(i in names(df)) {
temp <- df[[i]]
data <- append(data, temp)
types <- append(types,.type(temp))
}
postForm("http://127.0.0.1:2609/cytobridge/JSONTable/", data=toJSON(list(table_name=name, table_headings=.dummy(names(df)), table_types=.dummy(types), row_ids=.dummy(row.names(df)), table_data=as.character(.dummy(data)))), style="post")
df
}
