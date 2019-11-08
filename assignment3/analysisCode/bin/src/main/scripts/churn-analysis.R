library(plyr)
library(ggplot2)

diffs <- read.csv("diffs.csv")
churnSummary <- ddply(diffs,.(File),summarise,Add=sum(Added), Rem=sum(Removed), TotalChurn = Add+Rem, Edits=length(File))
ggplot(churnSummary,aes(x=Add,y=Rem)) + geom_point(aes(size=Edits)) + geom_text(data=churnSummary[churnSummary$TotalChurn>500,],aes(label=File),nudge_x = 40)
