
library(ggplot2)

CompareAll <- function(srcRoot, outputPng){
  matrix <- read.csv(srcRoot)
  ggplot(matrix,aes(From,To)) + geom_tile(aes(fill=Overlap)) + theme(axis.text.x=element_text(angle=90, size=8),axis.text.y=element_text(size=8))
  ggsave(file=outputPng)
}

CompareDetailed <- function(csvFile, outputPng){
  detailed <- read.csv(csvFile,header=T)
  ggplot(detailed,aes(FromLine,ToLine)) + geom_tile(aes(fill=Match)) + theme(axis.text.x=element_text(angle=90, size=8),axis.text.y=element_text(size=8))
  ggsave(file=outputPng)
}
